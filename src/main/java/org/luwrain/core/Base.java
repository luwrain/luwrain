/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
import java.nio.file.*;

abstract class Base implements org.luwrain.base.EventConsumer
{
    interface StopCondition
    {
	boolean continueEventLoop();
    }

    static class PopupStopCondition implements Base.StopCondition
    {
	private final Base.StopCondition popupCondition;
	private boolean cancelled = false;

	PopupStopCondition(Base.StopCondition popupCondition)
	{
	    NullCheck.notNull(popupCondition, "popupCondition");
	    this.popupCondition = popupCondition;
	}

	@Override public boolean continueEventLoop()
	{
	    return !cancelled && InitialEventLoopStopCondition.shouldContinue && popupCondition.continueEventLoop();
	}

	void cancel()
	{
	    cancelled = true;
	}
    }

    protected final CmdLine cmdLine;
    protected final  Registry registry;
    protected final EventQueue eventQueue = new EventQueue();
    private EventResponse eventResponse = null;
    protected Speech speech = null;
    protected final Braille braille = new Braille();
    private RegionContent clipboard = null;
    protected final SoundsPlayer sounds = new SoundsPlayer();
    protected final SoundManager soundManager;
    protected final org.luwrain.base.CoreProperties coreProps;
    protected final String lang;
    protected boolean needForIntroduction = false;
    protected boolean introduceApp = false;

    protected Base(CmdLine cmdLine, Registry registry,
			      org.luwrain.base.CoreProperties coreProps, String lang)
    {
	NullCheck.notNull(cmdLine, "cmdLine");
	NullCheck.notNull(registry, "registry");
	NullCheck.notNull(coreProps, "coreProps");
	NullCheck.notEmpty(lang, "lang");
	this.cmdLine = cmdLine;
	this.registry = registry;
	this.coreProps = coreProps;
	this.lang = lang;
	this.soundManager = new SoundManager(registry, coreProps);
    }

    //True means the event is processed and there is no need to process it again;
    abstract protected boolean onEvent(Event event);
    abstract protected void introduce(StopCondition stopCondition);
    public abstract Luwrain getObjForEnvironment();
    abstract protected void processEventResponse(EventResponse eventResponse);

    protected void eventLoop(StopCondition stopCondition)
    {
	NullCheck.notNull(stopCondition, "stopCondition");
	while(stopCondition.continueEventLoop())
	{
	    needForIntroduction = false;
	    introduceApp = false;
	    eventResponse = null;
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
	    {
		if (eventResponse != null)
		{
		    processEventResponse(eventResponse);
		    eventResponse = null;
		} else
		    introduce(stopCondition);
	    }
	}
    }

    @Override public void enqueueEvent(Event e)
    {
	eventQueue.putEvent(e);
    }

    public void playSound(Sounds sound)
    {
	NullCheck.notNull(sound, "sound");
	sounds.play(sound);
    }

    protected void noAppsMessage()
    {
	speech.silence(); 
	playSound(Sounds.NO_APPLICATIONS);
	speech.speak(getObjForEnvironment().i18n().getStaticStr("NoLaunchedApps"), 0, 0);
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

    public void setAreaIntroduction()
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

    org.luwrain.base.CoreProperties getCoreProperties()
    {
	return coreProps;
    }

    String getLang()
    {
	return lang;
    }

    RegionContent getClipboard()
    {
	if (clipboard == null)
	    return new RegionContent();
	return clipboard;
    }

    void setClipboard(RegionContent newClipboard)
    {
	NullCheck.notNull(newClipboard, "newClipboard");
	this.clipboard = newClipboard;
    }


    void setEventResponse(EventResponse eventResponse)
    {
	NullCheck.notNull(eventResponse, "eventResponse");
	this.eventResponse = eventResponse;
    }


}
