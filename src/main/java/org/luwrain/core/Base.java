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

package org.luwrain.core;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.util.*;
import org.luwrain.core.ExtensionsManager.LoadedExtension;
import org.luwrain.script.core.ScriptCore;
import org.luwrain.script.hooks.ChainOfResponsibilityHook;

abstract class Base implements EventConsumer
{
    static final String LOG_COMPONENT = "core";

    interface StopCondition
    {
	boolean continueEventLoop();
    }

    enum AnnouncementType {AREA, APP};

    protected final CmdLine cmdLine;
    protected final  Registry registry;
    public final Luwrain luwrain;
    final PropertiesRegistry props;
    final HelpSections helpSects;
    protected final String lang;

    private final Thread mainCoreThread;
    protected final InterfaceManager interfaces = new InterfaceManager(this);
    final ExtensionsManager extensions = new ExtensionsManager(interfaces);
         final ObjRegistry objRegistry = new ObjRegistry();
    final CommandManager commands = new CommandManager();//FIXME:must be merged into objRegistry
    protected final EventQueue eventQueue = new EventQueue();
    protected final MainStopCondition mainStopCondition = new MainStopCondition();
    private EventResponse eventResponse = null;

    protected final WorkersTracking workers = new WorkersTracking();
    final JobsTracking jobs = new JobsTracking(getObjForEnvironment(), objRegistry);
    protected final I18nImpl i18n = new I18nImpl();
    final Speech speech;
    final org.luwrain.core.speech.SpeakingText speakingText = new org.luwrain.core.speech.SpeakingText(extensions);
    final BrailleImpl braille = new BrailleImpl();
    protected final org.luwrain.core.sound.SoundIcons sounds;
    protected final org.luwrain.core.sound.Manager soundManager;

    final FileTypes fileTypes = new FileTypes();
    final FileContentType contentTypes = new FileContentType();
    private final Clipboard clipboard = new Clipboard();
    protected AnnouncementType announcement = null;

    protected Base(CmdLine cmdLine, Registry registry,
			      PropertiesRegistry props, String lang)
    {
	NullCheck.notNull(cmdLine, "cmdLine");
	NullCheck.notNull(registry, "registry");
	NullCheck.notNull(props, "props");
	NullCheck.notEmpty(lang, "lang");
	this.cmdLine = cmdLine;
	this.registry = registry;
	this.props = props;
	this.props.setLuwrainObj(getObjForEnvironment());
	this.lang = lang;
	this.helpSects = new HelpSections(registry);
	this.speech = new Speech(cmdLine, registry);
	this.sounds = new org.luwrain.core.sound.SoundIcons(registry, props.getFileProperty(Luwrain.PROP_DIR_SOUNDS));
	this.soundManager = new org.luwrain.core.sound.Manager(objRegistry, getObjForEnvironment());
	this.mainCoreThread = Thread.currentThread();
	this.luwrain = getObjForEnvironment();
    }

    //True means the event is processed and there is no need to process it again;
    abstract protected boolean onEvent(Event event);
        abstract protected void processEventResponse(EventResponse eventResponse);
        abstract void message(String text, Luwrain.MessageType messageType);
    abstract protected void announce(StopCondition stopCondition);


    protected void eventLoop(StopCondition stopCondition)
    {
	NullCheck.notNull(stopCondition, "stopCondition");
	while(stopCondition.continueEventLoop())
	{
	    try {
		this.announcement = null;
		this.eventResponse = null;
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
		    if (this.eventResponse != null)
		    {
			processEventResponse(eventResponse);
			this.eventResponse = null;
		    } else
			announce(stopCondition);
		}
	    }
	    catch(Throwable e)
	    {
		Log.error(LOG_COMPONENT, "event processing failure: " + e.getClass().getName() + ":" + e.getMessage());
	    }
	}
    }

    @Override public void enqueueEvent(Event e)
    {
	eventQueue.putEvent(e);
    }

    public final void playSound(Sounds sound)
    {
	if (sound == null)
	{
	    sounds.stop();
	    return;
	}
	final String volumeStr = props.getProperty("luwrain.sounds.iconsvol");
	int volume = 100;
	try {
	    if (!volumeStr.trim().isEmpty())
	    volume = Integer.parseInt(volumeStr);
	}
	catch(NumberFormatException e)
	{
	    volume = 100;
	}
	if (volume < 0)
	    volume = 0;
	if (volume > 100)
	    volume = 100;
	sounds.play(sound, volume);
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
	this.announcement = AnnouncementType.AREA;
    }

    protected void setAppIntroduction()
    {
	this.announcement = AnnouncementType.APP;
	    }

    String getLang()
    {
	return lang;
    }

    Clipboard getClipboard()
    {
	return clipboard;
    }

    void setEventResponse(EventResponse eventResponse)
    {
	NullCheck.notNull(eventResponse, "eventResponse");
	this.eventResponse = eventResponse;
    }

    boolean isMainCoreThread()
    {
	return Thread.currentThread() == mainCoreThread;
    }

    void mainCoreThreadOnly()
    {
	if (!isMainCoreThread())
	    throw new RuntimeException("Not in the main thread of LUWRAIN core (current thread is \'" + Thread.currentThread().getName() + "\'");
    }

    public Luwrain getObjForEnvironment()
    {
	return interfaces.objForEnvironment;
    }

    String loadScript(ScriptFile scriptFile) throws ExtensionException
    {
	NullCheck.notNull(scriptFile, "scriptFile");
	mainCoreThreadOnly();
	final ScriptExtension ext = new ScriptExtension(scriptFile.toString());
	ext.init(interfaces.requestNew(ext));
	final ScriptCore scriptCore = new ScriptCore(ext.getLuwrainObj());
	ext.setScriptCore(scriptCore);
	try {
	    scriptCore.load(scriptFile);
	}
	catch(Throwable e)
	{
	    interfaces.release(ext.getLuwrainObj());
	    throw new ExtensionException(e);
	}
	final LoadedExtension loadedExt = extensions.addDynamicExtension(ext, ext.getLuwrainObj());
	if (loadedExt == null)
	{
	    interfaces.release(ext.getLuwrainObj());
	    throw new ExtensionException("Trying to load twice the same extension");
	}
	objRegistry.takeObjects(loadedExt);
	for(Command c: loadedExt.commands)
	    commands.add(ext.getLuwrainObj(), c);
	return loadedExt.id;
    }

    File[] getInstalledPacksDirs()
    {
	final File packsDir = props.getFileProperty("luwrain.dir.packs");
	if (!packsDir.exists() || !packsDir.isDirectory())
	    return new File[0];
	final File[] files = packsDir.listFiles();
	if (files == null)
	    return new File[0];
	final List<File> res = new ArrayList<>();
	for(File f: files)
	    if (f != null && f.exists() && f.isDirectory())
		res.add(f);
	return res.toArray(new File[res.size()]);
    }

        boolean unloadDynamicExtension(String extId)
    {
	NullCheck.notEmpty(extId, "extId");
	mainCoreThreadOnly();
	final LoadedExtension ext = extensions.getDynamicExtensionById(extId);
	if (ext == null)
	    return false;
	objRegistry.deleteByExt(ext.ext);
	//FIXME:workers
	commands.deleteByInstance(ext.luwrain);
	return extensions.unloadDynamicExtension(ext.ext);
    }

    void unsafeOperation(Runnable runnable)
    {
	NullCheck.notNull(runnable, "runnable");
	unsafeAreaOperation(runnable);
    }

    //To be deleted
    void unsafeAreaOperation(Runnable runnable)
    {
	NullCheck.notNull(runnable, "runnable");
	try {
	    runnable.run();
	}
	catch(Throwable e)
	{
	    //	    if (e instanceof Exception)
	    //		getObjForEnvironment().crash((Exception)e); else
	    {
		getObjForEnvironment().message(e.getClass().getName() + ":" + e.getMessage(), Luwrain.MessageType.ERROR);
		Log.error(LOG_COMPONENT, "unexpected exception in applications:" + e.getClass().getName() + ":" + e.getMessage());
		e.printStackTrace();
	    }
	}
    }

    static protected final class MainStopCondition implements StopCondition
    {
	private boolean shouldContinue = true;//FIXME:No static members

	@Override public boolean continueEventLoop()
	{
	    return shouldContinue;
	}

	void stop()
	{
	    shouldContinue = false;
	}
	    }

    static class PopupStopCondition implements Base.StopCondition
    {
	private final StopCondition parentCondition;
	private final Base.StopCondition popupCondition;
	private boolean cancelled = false;

	PopupStopCondition(StopCondition parentCondition, StopCondition popupCondition)
	{
	    NullCheck.notNull(parentCondition, "parentCondition");
	    NullCheck.notNull(popupCondition, "popupCondition");
	    this.parentCondition = parentCondition;
	    this.popupCondition = popupCondition;
	}

	@Override public boolean continueEventLoop()
	{
	    return !cancelled && parentCondition.continueEventLoop() && popupCondition.continueEventLoop();
	}

	void cancel()
	{
	    cancelled = true;
	}
    }

}
