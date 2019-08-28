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

//LWR_API 1.0

package org.luwrain.speech;

import java.util.Set;
import java.io.OutputStream;
import javax.sound.sampled.AudioFormat;

import org.luwrain.core.*;

public interface Channel
{
    public enum Flags{
    };

    static public final class SyncParams
    {
	private int pitch = 0;
	private int rate = 0;
	public int getPitch()
	{
	    return pitch;
	}
	public void setPitch(int pitch)
	{
	    this.pitch = pitch;
	}
	public int getRate()
	{
	    return rate;
	}
	public void setRate(int rate)
	{
	    this.rate = rate;
	}
    }

    static public final class Result
    {
	public enum Type {
	    OK,
	    FAILED,
	    NOT_IMPLEMENTED,
	};
	private final Type type;
	private final Exception exception;
	public Result(Type type, Exception exception)
	{
	    NullCheck.notNull(type, "type");
	    this.type = type;
	    this.exception = exception;
	}
	public Result(Type type)
	{
	    this(type, null);
	}
	public Result()
	{
	    this(Type.OK);
	}
	public Type getType()
	{
	    return type;
	}
	public Exception getException()
	{
	    return exception;
	}
    }

    public interface Listener 
    {
	//Called only on successful finishing, not on cancelling 
	void onFinished(long id);
    }

    void close();
    Voice[] getVoices();
    String getChannelName();

    String getVoiceName();
    void setVoice(String name);

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

    Result synth(String text, OutputStream stream, AudioFormat format, SyncParams params, Set<Flags> flags);

    //Cancels speaking, listener will never get onFinished call
    void silence();
    AudioFormat[] getSynthSupportedFormats();
}
