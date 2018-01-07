
package org.luwrain.core;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.net.*;
import javax.sound.sampled.*;


import org.luwrain.base.*;
import org.luwrain.core.*;

class WavePlayers
{
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
		Log.error(Environment.LOG_COMPONENT, "unable to play audio file" + fileName + ":" + e.getClass().getName() + ":" + e.getMessage());
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
}
