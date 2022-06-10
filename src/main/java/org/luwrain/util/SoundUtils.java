/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

//LWR_API 1.0

package org.luwrain.util;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioFormat.Encoding;

import org.luwrain.core.*;

public class SoundUtils 
{
    static public byte[] createWaveHeader(int sampleRate, int bitsPerSample, int numChannels, int dataLen)
    {
	final int byteRate;
	switch(bitsPerSample)
	{
	case 8:
	    byteRate = sampleRate * numChannels;
	    break;
	case 16:
	    byteRate = 2 * sampleRate * numChannels;
	    break;
	default:
	    return null;
	}
	final int totalDataLen = dataLen + 36;
	byte[] header = new byte[44];
	header[0] = 'R';  // RIFF/WAVE header
	header[1] = 'I';
	header[2] = 'F';
	header[3] = 'F';
	header[4] = (byte) (totalDataLen & 0xff);
	header[5] = (byte) ((totalDataLen >> 8) & 0xff);
	header[6] = (byte) ((totalDataLen >> 16) & 0xff);
	header[7] = (byte) ((totalDataLen >> 24) & 0xff);
	header[8] = 'W';
	header[9] = 'A';
	header[10] = 'V';
	header[11] = 'E';
	header[12] = 'f';  // 'fmt ' chunk
	header[13] = 'm';
	header[14] = 't';
	header[15] = ' ';
	header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
	header[17] = 0;
	header[18] = 0;
	header[19] = 0;
	header[20] = 1;  // format = 1
	header[21] = 0;
	header[22] = (byte) numChannels;
	header[23] = 0;
	header[24] = (byte) (sampleRate & 0xff);
	header[25] = (byte) ((sampleRate >> 8) & 0xff);
	header[26] = (byte) ((sampleRate >> 16) & 0xff);
	header[27] = (byte) ((sampleRate >> 24) & 0xff);
	header[28] = (byte) (byteRate & 0xff);
	header[29] = (byte) ((byteRate >> 8) & 0xff);
	header[30] = (byte) ((byteRate >> 16) & 0xff);
	header[31] = (byte) ((byteRate >> 24) & 0xff);
	header[32] = (byte) (numChannels * bitsPerSample / 8);  // block align
	header[33] = 0;
	header[34] = (byte)bitsPerSample;
	header[35] = 0;
	header[36] = 'd';
	header[37] = 'a';
	header[38] = 't';
	header[39] = 'a';
	header[40] = (byte) (dataLen & 0xff);
	header[41] = (byte) ((dataLen >> 8) & 0xff);
	header[42] = (byte) ((dataLen >> 16) & 0xff);
	header[43] = (byte) ((dataLen >> 24) & 0xff);
	return header;
    }

    static public byte[] createWaveHeader(javax.sound.sampled.AudioFormat audioFormat, int dataLen)
    {
	NullCheck.notNull(audioFormat, "audioFormat");
	return createWaveHeader((int)audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), audioFormat.getChannels(), dataLen);
    }

    static public AudioFormat createAudioFormat(String params)
    {
	Encoding encoding = Encoding.PCM_SIGNED;
	float sampleRate = 24000;
	int channels = 2;
	int bitsInSample = 16;
	boolean bigEndian = false;
	for (String p: params.split(",", -1))
	{
	    if (p.isEmpty())
		continue;
	    switch(p.trim().toLowerCase())
	    {
	    case "mono":
		channels = 1;
		continue;
	    case "stereo":
		channels = 2;
		continue;
	    case "8bit":
	    case "8bits":
		bitsInSample = 8;
	    continue;
	    case "16bit":
	    case "16bits":
		bitsInSample = 16;
	    continue;
	    case "bigendian":
		bigEndian = true;
		continue;
	    case "littleEndian":
		bigEndian = false;
		continue;
	    case "signed":
		encoding = Encoding.PCM_SIGNED;
		continue;
	    case "unsigned":
		encoding = Encoding.PCM_UNSIGNED;
		continue;
	    }
	    try {
		sampleRate = Float.parseFloat(p.trim());
	    }
	    catch(NumberFormatException e)
	    {
		continue;
	    }
	}
	return new AudioFormat(encoding,
			       sampleRate, 
			       bitsInSample, //sampleSizeInBits
			       channels, //channels
			       (1 * bitsInSample / 8), //frameSize
			       sampleRate, //frameRate
			       bigEndian);
    }

    static public boolean setLineMasterGanePercent(SourceDataLine line, int percent)
    {
	NullCheck.notNull(line, "line");
	if (percent < 0 || percent > 100)
	    throw new IllegalArgumentException("The percent number (" + percent + ") may not be less than 0 or greater than 100");
	if (!line.isControlSupported(FloatControl.Type.MASTER_GAIN))
	    return false;
	final FloatControl control = (FloatControl)line.getControl(FloatControl.Type.MASTER_GAIN);
	final float min = control.getMinimum();
	final float max;
	if (control.getMinimum() < 0)
	    max = Math.min(control.getMaximum(), 0); else
	    max = control.getMaximum();
	final float value = min + (max - min) * ((float)percent / 100);
	control.setValue(value);
	return true;
    }
}
