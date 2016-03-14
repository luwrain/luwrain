/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.sounds;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

class EnvironmentSoundPlayer implements Runnable
{
    private static final int BUF_SIZE = 16;//Large this value causes delay on interruption;
    private String fileName;
    boolean interruptPlayback = false;
    boolean finished = false;

    EnvironmentSoundPlayer(String fileName)
    {
	this.fileName = fileName;
    }

    public void run()
    {
	File soundFile = new File(fileName);
	if (!soundFile.exists())
	    return;
	AudioInputStream audioInputStream = null;
	try {
	    audioInputStream = AudioSystem.getAudioInputStream(soundFile);
	}
	catch (UnsupportedAudioFileException e)
	{
	    //FIXME:Log warning;
	    return;
	}
	catch (IOException e)
	{
	    //FIXME:log warning;
	    return;
	}
	AudioFormat format = audioInputStream.getFormat();
	SourceDataLine audioLine = null;
	DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
	try {
	    audioLine = (SourceDataLine) AudioSystem.getLine(info);
	    audioLine.open(format);
	}
	catch (LineUnavailableException e)
	{
	    //FIXME:Log warning;
	    return;
	}
	catch (Exception e)
	{
	    //FIXME:Log exception;
	    return;
	}
	audioLine.start();
	int bytesRead = 0;
	byte[] buf = new byte[BUF_SIZE];
	try {
	    while (bytesRead != -1 && !interruptPlayback)
	    {
		bytesRead = audioInputStream.read(buf, 0, buf.length);
		if (bytesRead >= 0)
		    audioLine.write(buf, 0, bytesRead);
	    }
	    if (interruptPlayback)
		audioLine.flush();
	}
	catch (IOException e)
	{
	    //FIXMe:Log message;
	    return;
	}
	finally
	{
	    audioLine.drain();
	    audioLine.close();
	    finished = true;
	}
    }
}
