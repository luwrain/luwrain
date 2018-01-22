/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

import org.luwrain.base.*;

final class WavePlayers
{
    static private final String LOG_COMPONENT = Base.LOG_COMPONENT;
    
    static class Simple implements Runnable
    {
	private static final int BUF_SIZE = 512;
	private final String fileName;
	private final int volumePercent;
	private boolean interruptPlayback = false;
	boolean finished = false;
	private SourceDataLine audioLine = null;

	Simple(String fileName, int volumePercent)
	{
	    NullCheck.notEmpty(fileName, "fileName");
	    this.fileName = fileName;
	    this.volumePercent = volumePercent;
	}

	@Override public void run()
	{
	    AudioInputStream audioInputStream = null;
	    try {
		try {
		    final File soundFile = new File(fileName);
		    if (!soundFile.exists())
			return;
		    audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		    final AudioFormat format = audioInputStream.getFormat();
		    final DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		    audioLine = (SourceDataLine) AudioSystem.getLine(info);
		    audioLine.open(format);
		    audioLine.start();
		    if (volumePercent >= 0 || volumePercent < 100)
			org.luwrain.util.SoundUtils.setLineMasterGanePercent(audioLine, volumePercent);
		    int bytesRead = 0;
		    final byte[] buf = new byte[BUF_SIZE];
		    while (bytesRead != -1 && !interruptPlayback)
		    {
			bytesRead = audioInputStream.read(buf, 0, buf.length);
			if (bytesRead >= 0)
			    audioLine.write(buf, 0, bytesRead);
		    }
		    audioLine.drain();
		}
		finally {
		    synchronized(this)
		    {
			if (audioLine != null)
			{
			    audioLine.close();
			    audioLine = null;
			}
			if (audioInputStream != null)
			{
			    audioInputStream.close();
			    audioInputStream = null;
			}
		    }
		    finished = true;
		}
	    } //try
	    catch(UnsupportedAudioFileException | IOException | LineUnavailableException e)
	    {
		Log.error(Core.LOG_COMPONENT, "unable to play audio file" + fileName + ":" + e.getClass().getName() + ":" + e.getMessage());
	    }
	}

	void stopPlaying()
	{
	    interruptPlayback = true;
	    synchronized(this)
	    {
		if (audioLine != null)
		    audioLine.stop();
	    }
	}
    }

    static private final class PlayerInstance implements MediaResourcePlayer.Instance
    {
		private static final int NOTIFY_MSEC_COUNT=500;
	private static final int BUF_SIZE = 512;

	    private final ExecutorService executor = Executors.newSingleThreadExecutor();
		private final MediaResourcePlayer.Listener listener;

	private boolean interruptPlayback = false;
	//	private SourceDataLine audioLine = null;
	private FutureTask<Boolean> futureTask = null;

	PlayerInstance(MediaResourcePlayer.Listener listener)
	{
	    NullCheck.notNull(listener, "listener");
	    this.listener = listener;
	}

	@Override public MediaResourcePlayer.Result play(URL url, MediaResourcePlayer.Params params)
	{
	    NullCheck.notNull(url, "url");
	    NullCheck.notNull(params, "params");
	    NullCheck.notNull(params.flags, "params.flags");
	    if (params.playFromMsec < 0)
		throw new IllegalArgumentException("params.playFromMsec (" + params.playFromMsec + ") may not be negative");
	    if (params.volume < 0 || params.volume > 100)
		throw new IllegalArgumentException("params.volume (" + params.volume + ") must be between 0 and 100 inclusively");
	    interruptPlayback = false;
	    final AudioInputStream audioInputStream;
	    try {
		audioInputStream = AudioSystem.getAudioInputStream(url.openStream());
	    } 
	    catch(UnsupportedAudioFileException | IOException e)
	    {
		Log.error(LOG_COMPONENT, "unable to play " + url.toString() + ":" + e.getClass().getName() + ":" + e.getMessage());
		return new MediaResourcePlayer.Result();
	    }

	    //	    kaka
	    final AudioFormat format=audioInputStream.getFormat();
	    futureTask = new FutureTask(()->{
SourceDataLine line = null;
		    try {
final DataLine.Info info=new DataLine.Info(SourceDataLine.class,format);
			synchronized(this)
			{
			    line = (SourceDataLine)AudioSystem.getLine(info);
			    // FloatControl volume=(FloatControl)line.getControl(FloatControl.Type.MASTER_GAIN); 
			    line.open(format);
			    line.start();
			}
			long totalBytes=0;
			// skip if task need it
			if(params.playFromMsec > 0)
			{
			    // bytes count from msec pos, 8000 is a 8 bits in byte and 1000 ms in second
			    long skipBytes=mSecToBytesSamples(format, params.playFromMsec);
			    audioInputStream.skip(skipBytes);
			    totalBytes+=skipBytes;
			}
			long lastNotifiedMsec=totalBytes;
			long notifyBytesCount=mSecToBytesSamples(format, NOTIFY_MSEC_COUNT);
			int bytesRead=0;
			byte[] buf=new byte[BUF_SIZE];
			while(bytesRead!=-1&&!interruptPlayback)
			{
			    bytesRead=audioInputStream.read(buf,0,buf.length);
			    if(bytesRead>=0) synchronized(this)
					     {
						 line.write(buf,0,bytesRead);
						 totalBytes+=bytesRead;
					     }
			    if (totalBytes > lastNotifiedMsec + notifyBytesCount)
			    {
				lastNotifiedMsec = totalBytes;
				listener.onPlayerTime(PlayerInstance.this, (long)bytesSamplesTomSec(format, totalBytes));
				//Log.debug("player","SoundPlayer: step"+(long)bytesSamplesTomSec(totalBytes));
			    }
			}
			line.drain();
		    }
		    catch (Exception e)
		    {
			e.printStackTrace();
			listener.onPlayerFinish(PlayerInstance.this);
			return false;
		    } finally
		    {
			if(line != null)
			    line.close();
		    }
		    //Log.debug("player","SoundPlayer: finish");
		    listener.onPlayerFinish(PlayerInstance.this);
		    return true;
		});
	    executor.execute(futureTask);
	    return new MediaResourcePlayer.Result();
	}

	static private long mSecToBytesSamples(AudioFormat format, float msec)
	{
	    NullCheck.notNull(format, "format");
	    return (long)(format.getSampleRate()*format.getSampleSizeInBits()*msec/8000);
	}
	static private float bytesSamplesTomSec(AudioFormat format, long samples)
	{
	    NullCheck.notNull(format, "format");
	    return (8000f*samples/format.getSampleRate()*format.getSampleSizeInBits());
	}
	@Override public void stop()
	{
	    interruptPlayback=true;
	}
    }

    static class Player implements MediaResourcePlayer
    {
	@Override public Instance newMediaResourcePlayer(Listener listener)
	{
	    NullCheck.notNull(listener, "listener");
	    return new PlayerInstance(listener);
	}

	@Override public String getSupportedMimeType()
	{
	    return ContentTypes.SOUND_WAVE_DEFAULT;
	}

	@Override public String getExtObjName()
	{
	    return "wav";
	}
    }
}
