/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

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

package org.luwrain.speech;

import java.util.Set;
import java.io.OutputStream;
import javax.sound.sampled.AudioFormat;

import org.luwrain.core.Registry;

public interface Channel
{
    static public final int DEFAULT_PARAM_VALUE = 50;
    public enum Features {
	CAN_SYNTH_TO_STREAM,
	CAN_SYNTH_TO_SPEAKERS,
	CAN_NOTIFY_WHEN_FINISHED,
    };

    public enum PuncMode {
	ALL,
	NONE,
    };

    public interface Listener 
    {
	//Called only on successful finishing, not on cancelling 
	void onFinished(long id);
    }

    boolean initByRegistry(Registry registry, String path);
    boolean initByArgs(String[] args);
    void close();
    Voice[] getVoices();
    String getChannelName();
    Set<Features>  getFeatures();
    boolean isDefault();
    String getCurrentVoiceName();
    void setCurrentVoice(String name);
    int getDefaultPitch();
    void setDefaultPitch(int value);
    int getDefaultRate();
    void setDefaultRate(int value);
    PuncMode getCurrentPuncMode();
    void setCurrentPuncMode(PuncMode mode);

    /**
     * Synthesize speech with sending the result to speakers directly. This
     * methods is always executed asynchronously returning control to the
     * caller immediately.  Partial implementation may ignore
     * {@code listener} argument, if it doesn't supports notifying about finishing
     * the work (synthesizing a speech and its complete playing in computer
     * speakers).
     *
     * @param text A text to speak
     * @param listener A listener object to catch the moment of finishing the speaking (may be null and may be ignord)
     * @param relPitch Relative value of desired pitch (0 means to use default)
     * @param relPitch Relative value of desired rate (0 means to use default)
     * @param cancelPrevious Cancel previous text to speak, if there is any
     * @return An identifier of the accepted task
     */
    long speak(String text, Listener listener,
	       int relPitch, int relRate,
boolean cancelPrevious);

    long speakLetter(char letter, Listener listener, int relPitch, int relRate, boolean cancelPrevious);

    //Cancels speaking, listener will never get onFinished call
    void silence();
    AudioFormat[] getSynthSupportedFormats();
    boolean synth(String text, int pitch, int rate, AudioFormat format, OutputStream stream);

    static int adjustParamValue(int value)
    {
	if (value < 0)
	    return 0;
	if (value > 100)
	    return 100;
	return value;
    }
}
