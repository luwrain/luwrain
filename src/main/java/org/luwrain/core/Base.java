/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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
import java.util.concurrent.*;//
import java.io.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import static org.luwrain.core.NullCheck.*;

abstract class Base implements EventConsumer
{
    static final Logger LOGGER =LogManager.getLogger();

    
    static final String
	LOG_COMPONENT = "core",
	PROP_ICONS_VOLUME = "luwrain.sounds.iconsvol";

    enum AnnouncementType {AREA, APP};

    interface StopCondition
    {
	boolean continueEventLoop();
    }

    protected final CmdLine cmdLine;
    protected final  Registry registry;
    final Luwrain luwrain;
    protected final PropertiesRegistry props;
    protected final HelpSections helpSects;
    protected final String lang;

    private final Thread mainCoreThread;
    protected final InterfaceManager interfaces = new InterfaceManager(this);
    protected final ExtensionsManager extensions = new ExtensionsManager(this, interfaces);
    protected final ObjRegistry objRegistry = new ObjRegistry();
    final TempFiles tempFiles = new TempFiles();
    protected final CommandManager commands = new CommandManager();//FIXME:must be merged into objRegistry
    protected final EventQueue eventQueue = new EventQueue();
    protected final MainStopCondition mainStopCondition = new MainStopCondition();
    private EventResponse eventResponse = null;

    protected final WorkersTracking workers = new WorkersTracking();
    protected final JobsManager jobs = new JobsManager(interfaces.systemObj, extensions);
    protected final I18nImpl i18n = new I18nImpl();
    protected final Speech speech;
    protected final org.luwrain.core.speech.SpeakingText speakingText = new org.luwrain.core.speech.SpeakingText(extensions);
    protected final BrailleImpl braille = new BrailleImpl();
    protected final org.luwrain.core.sound.SoundIcons sounds;
    protected final org.luwrain.core.sound.Manager soundManager;

    final FileTypes fileTypes = new FileTypes();
    final FileContentType contentTypes = new FileContentType();
    private final Clipboard clipboard = new Clipboard();
    protected AnnouncementType announcement = null;

    protected Base(CmdLine cmdLine, Registry registry, PropertiesRegistry props, String lang)
    {
	notNull(cmdLine, "cmdLine");
	notNull(registry, "registry");
	notNull(props, "props");
	notEmpty(lang, "lang");
	this.cmdLine = cmdLine;
	this.luwrain = interfaces.systemObj;
	this.registry = registry;
	this.props = props;
	this.props.setLuwrainObj(interfaces.systemObj);
	this.lang = lang;
	this.helpSects = new HelpSections(registry);
	this.speech = new Speech(cmdLine, registry);
	this.sounds = new org.luwrain.core.sound.SoundIcons(registry, props.getFileProperty(Luwrain.PROP_DIR_SOUNDS));
	this.soundManager = new org.luwrain.core.sound.Manager(objRegistry, interfaces.systemObj);
	this.mainCoreThread = Thread.currentThread();
    }

    abstract protected void onEvent(Event event);
    abstract protected void processEventResponse(EventResponse eventResponse);
    abstract void message(String text, Luwrain.MessageType messageType);
    abstract protected void announce(StopCondition stopCondition);

    protected void eventLoop(StopCondition stopCondition)
    {
	notNull(stopCondition, "stopCondition");
	while(stopCondition.continueEventLoop())
	{
	    try {
		this.announcement = null;
		this.eventResponse = null;
		final Event event = eventQueue.pickEvent();
		if (event == null)
		    continue;
		onEvent(event);
		event.markAsProcessed();
		if (this.eventResponse != null)
		{
		    processEventResponse(eventResponse);
		    this.eventResponse = null;
		} else
		    announce(stopCondition);
	    }
	    catch(Throwable e)
	    {
		error(e, "event processing failure");
	    }
	}
    }

    @Override public void enqueueEvent(Event e)
    {
	eventQueue.putEvent(e);
    }

    final void playSound(Sounds sound)
    {
	if (sound == null)
	{
	    sounds.stop();
	    return;
	}
	final String volumeStr = props.getProperty(PROP_ICONS_VOLUME);
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

    final void playSound(File file)
    {
	notNull(file, "file");
	final String volumeStr = props.getProperty(PROP_ICONS_VOLUME);
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
	sounds.play(file, volume);
    }

    protected void noAppsMessage()
    {
	speech.silence(); 
	playSound(Sounds.NO_APPLICATIONS);
	speech.speak(i18n.getStaticStr("NoLaunchedApps"), 0, 0);
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
	/*
	mainCoreThreadOnly();
	final LoadedExtension ext = extensions.getDynamicExtensionById(extId);
	if (ext == null)
	    return false;
	objRegistry.deleteByExt(ext.ext);
	//FIXME:workers
	commands.deleteByInstance(ext.luwrain);
	return extensions.unloadDynamicExtension(ext.ext);
	*/
	return true;
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
		luwrain.message(e.getClass().getName() + ":" + e.getMessage(), Luwrain.MessageType.ERROR);
		error(e, "unexpected exception in apps");
	    }
	}
    }

            static void fatal(String msg)
    {
	Log.debug(LOG_COMPONENT, msg);
    }


    static void error(String msg)
    {
	System.err.println("ERROR: " + msg);
	Log.error(LOG_COMPONENT, msg);
    }

    static void error(Throwable e, String comment)
    {
	Log.error(LOG_COMPONENT, "Exception " + e.getClass().getName());
	if (e.getMessage() != null && !e.getMessage().isEmpty())
	    Log .error(LOG_COMPONENT, "Message: " + e.getMessage());
	if (comment != null && !comment.isEmpty())
	    Log.error(LOG_COMPONENT, "Comment: " + comment);
	final StringWriter w = new StringWriter();
	final PrintWriter p = new PrintWriter(w);
	e.printStackTrace(p);
	w.flush();
	p.flush();
	for(String s: w.toString().split(System.lineSeparator(), -1))
	    Log.error(LOG_COMPONENT, s);
	if (comment != null && !comment.isEmpty())
	{
	    System.err.println("ERROR: " + comment);
	    System.err.println("Exception: " + e.getClass().getName());
	    if (e.getMessage() != null && !e.getMessage().isEmpty())
		System.err.println("Message: " + e.getMessage());
	} else
	{
	    System.err.println("Exception in the LUWRAIN core " + e.getClass().getName());
	    if (e.getMessage() != null && !e.getMessage().isEmpty())
		System.err.println("Message: " + e.getMessage());
	}
    }

    static void error(Throwable e)
    {
	error(e, null);
    }

    static void warn(String msg)
    {
	LOGGER.warn(msg);
    }

            static void info(String msg)
    {
	LOGGER.info(msg);
    }

        static void debug(String msg)
    {
	LOGGER.debug(msg);
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
