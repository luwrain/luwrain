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

package org.luwrain.core;

import java.util.*;
import java.nio.file.*;

abstract class EnvironmentBase implements EventConsumer
{
    protected final EventQueue eventQueue = new EventQueue();
    protected Speech speech = null;
    protected final Braille braille = new Braille();
    protected Strings strings;
    protected final SoundsPlayer sounds = new SoundsPlayer();
    protected HashMap<String, Path> paths;
    protected String lang;
    protected boolean needForIntroduction = false;
    protected boolean introduceApp = false;

    //True means the event is processed and there is no need to process it again;
    abstract protected boolean onEvent(Event event);
    abstract protected void introduce(EventLoopStopCondition stopCondition);

    protected void eventLoop(EventLoopStopCondition stopCondition)
    {
	NullCheck.notNull(stopCondition, "stopCondition");
	while(stopCondition.continueEventLoop())
	{
	    needForIntroduction = false;
	    introduceApp = false;
	    final Event event = eventQueue.takeEvent();
	    if (event == null)
		continue;
	    if (!onEvent(event))
	    {
		eventQueue.onceAgain(event);
		continue;
	    }
	    event.markAsProcessed();
	    if (!eventQueue.hasAgain())
		introduce(stopCondition);
	}
    }

    @Override public void enqueueEvent(Event e)
    {
	eventQueue.putEvent(e);
    }

    void playSound(int code)
    {
	sounds.play(code);
    }

    protected void noAppsMessage()
    {
	speech.silence(); 
	playSound(Sounds.NO_APPLICATIONS);
	speech.speak(strings.noLaunchedApps(), 0, 0);
    }

    protected void areaInaccessibleMessage()
    {
	speech.silence();
	playSound(Sounds.EVENT_NOT_PROCESSED);
    }

    void eventNotProcessedMessage()
    {
	speech.silence();
	playSound(Sounds.EVENT_NOT_PROCESSED);
    }

    protected void failureMessage()
    {
	speech.silence();
	playSound(Sounds.EVENT_NOT_PROCESSED);
    }

    protected void printMemInfo()
    {
	final Runtime runtime = Runtime.getRuntime();
	final java.text.NumberFormat format = java.text.NumberFormat.getInstance();
	final long maxMemory = runtime.maxMemory();
	final long allocatedMemory = runtime.totalMemory();
	final long freeMemory = runtime.freeMemory();
	Log.debug("core", "Memory usage information:");
	Log.debug("core", "free memory: " + format.format(freeMemory / 1048576) + "M");
	Log.debug("core", "allocated memory: " + format.format(allocatedMemory / 1046576) + "M");
	Log.debug("core", "max memory: " + format.format(maxMemory / 1048576) + "M");
    }

    protected void setAreaIntroduction()
    {
	needForIntroduction = true;
    }

    protected void setAppIntroduction()
    {
	needForIntroduction = true;
	introduceApp = true;
    }

    Speech getSpeech()
    {
	return speech;
    }

    Braille getBraille()
    {
	return   braille;
    }

    Map<String, Path> getPaths()
    {
	return paths;
    }

    String getLang()
    {
	return lang;
    }
}
