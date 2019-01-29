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
import java.nio.file.*;

import org.luwrain.util.*;

abstract class Base implements org.luwrain.base.EventConsumer
{
    static final String LOG_COMPONENT = "core";

    interface StopCondition
    {
	boolean continueEventLoop();
    }

    protected final CmdLine cmdLine;
    protected final  Registry registry;
    final PropertiesRegistry props;
    final HelpSections helpSects;
    protected final String lang;

    private final Thread mainCoreThread;
    protected final InterfaceManager interfaces = new InterfaceManager(this);
    final org.luwrain.core.extensions.Manager extensions = new org.luwrain.core.extensions.Manager(interfaces);
         final ObjRegistry objRegistry = new ObjRegistry();
    final CommandManager commands = new CommandManager();//FIXME:must be merged into objRegistry
    protected final org.luwrain.core.script.Core script = new org.luwrain.core.script.Core(interfaces);
    protected final EventQueue eventQueue = new EventQueue();
    protected final MainStopCondition mainStopCondition = new MainStopCondition();
    private EventResponse eventResponse = null;

    protected final WorkersTracking workers = new WorkersTracking();
    final CommandLineToolsTracking commandLineTools = new CommandLineToolsTracking(objRegistry);
    protected final I18nImpl i18n = new I18nImpl();
    final Speech speech;
    final org.luwrain.core.speech.SpeakingText speakingText = new org.luwrain.core.speech.SpeakingText(extensions);
    final Braille braille = new Braille();
    protected final org.luwrain.core.sound.EnvironmentSounds sounds;
    protected final SoundManager soundManager;

    final FileTypes fileTypes = new FileTypes();
    final FileContentType contentTypes = new FileContentType();
    private final Clipboard clipboard = new Clipboard();
    protected boolean needForIntroduction = false;
    protected boolean introduceApp = false;

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
	this.lang = lang;
	this.helpSects = new HelpSections(registry);
	this.speech = new Speech(cmdLine, registry);
	this.sounds = new org.luwrain.core.sound.EnvironmentSounds(registry, props.getFileProperty("luwrain.dir.sounds"));
	this.soundManager = new SoundManager(registry, props);
	this.mainCoreThread = Thread.currentThread();
    }

    //True means the event is processed and there is no need to process it again;
    abstract protected boolean onEvent(Event event);
        abstract protected void processEventResponse(EventResponse eventResponse);
        abstract void message(String text, Luwrain.MessageType messageType);
    abstract protected void introduce(StopCondition stopCondition);


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
	needForIntroduction = true;
    }

    protected void setAppIntroduction()
    {
	needForIntroduction = true;
	introduceApp = true;
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

    String loadScriptExtension(File dataDir, String text) throws org.luwrain.core.extensions.DynamicExtensionException
    {
	NullCheck.notNull(text, "text");
	mainCoreThreadOnly();
	final org.luwrain.core.script.Core.ExecResult execRes = script.exec(dataDir, text);
	if (!execRes.isOk())
	    throw new org.luwrain.core.extensions.DynamicExtensionException(execRes.getException());
	final org.luwrain.core.extensions.LoadedExtension loadedExt = extensions.addDynamicExtension(execRes.getExtension(), execRes.getLuwrain());
	if (loadedExt == null)
	{
	    interfaces.release(execRes.getLuwrain());
	    throw new org.luwrain.core.extensions.DynamicExtensionException("Trying to load twice the same extension");
	}
	objRegistry.takeObjects(loadedExt);
	for(Command c: loadedExt.commands)//FIXME:
	    commands.add(execRes.getLuwrain(), c);
	return loadedExt.id;
    }

    String loadScriptExtensionFromFile(File dataDir, File file) throws org.luwrain.core.extensions.DynamicExtensionException
    {
	NullCheck.notNull(file, "file");
	final String text;
	try {
	    text = FileUtils.readTextFileSingleString(file, "UTF-8");
	}
	catch(IOException e)
	{
	    throw new org.luwrain.core.extensions.DynamicExtensionException(e);
	}
	return loadScriptExtension(dataDir, text);
    }

    String loadTextExtension(String text, File baseDir) throws org.luwrain.core.extensions.DynamicExtensionException
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(baseDir, "baseDir");
	mainCoreThreadOnly();
	final org.luwrain.core.extensions.TextExtension textExt = new org.luwrain.core.extensions.TextExtension(baseDir);
	final Luwrain luwrain = interfaces.requestNew(textExt);
	Luwrain toRelease = luwrain;
	try {
	    try {
		textExt.load(text);
	    }
	    catch(Exception e)
	    {
		throw new org.luwrain.core.extensions.DynamicExtensionException(e);
	    }
	    final org.luwrain.core.extensions.LoadedExtension loadedExt = extensions.addDynamicExtension(textExt, luwrain);
	    if (loadedExt == null)
		throw new org.luwrain.core.extensions.DynamicExtensionException("Trying to load twice the same extension");
	    toRelease = null;
	    objRegistry.takeObjects(loadedExt);
	    for(Command c: loadedExt.commands)//FIXME:
		commands.add(luwrain, c);
	    return loadedExt.id;
	}
	finally {
	    if (toRelease != null)
		interfaces.release(toRelease);
	}
    }

    String loadTextExtensionFromFile(File file, File baseDir) throws org.luwrain.core.extensions.DynamicExtensionException
    {
	NullCheck.notNull(file, "file");
	NullCheck.notNull(baseDir, "baseDir");
	final String text;
	try {
	    text = FileUtils.readTextFileSingleString(file, "UTF-8");
	}
	catch(IOException e)
	{
	    throw new org.luwrain.core.extensions.DynamicExtensionException(e);
	}
	return loadTextExtension(text, baseDir);
    }


    boolean runFunc(Luwrain luwrain, String name)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notEmpty(name, "name");
	if (name.startsWith("jsfile:"))
	{
	    final String scriptName = name.substring("jsfile:".length());
	    if (scriptName.isEmpty())
		return false;
	    final String text;
	    try {
		text = FileUtils.readTextFileSingleString(new File(scriptName), "UTF-8");
	    }
	    catch(IOException e)
	    {
		Log.error(LOG_COMPONENT, "unable to run the function \'" + name + "\':" + e.getClass().getName() + ":" + e.getMessage());
		return false;
	    }
	    final org.luwrain.core.script.Context context = new org.luwrain.core.script.Context();
	    final Callable callable = script.execFuture(luwrain, props.getFileProperty("luwrain.dir.data"), context, text);
	    try {
		callable.call();
	    }
	    catch(Throwable e)
	    {
		Log.error(LOG_COMPONENT, "unable to run the function \'" + name + "\':" + e.getClass().getName() + ":" + e.getMessage());
		return false;
	    }
	    return true;
	}
	final java.util.function.Consumer func = (java.util.function.Consumer)ClassUtils.newInstanceOf(name, java.util.function.Consumer.class);
	if (func == null)
	    return false;
	try {
	    func.accept(luwrain);
	}
	catch(Throwable e)
	{
	    		Log.error(LOG_COMPONENT, "unable to run the function \'" + name + "\':" + e.getClass().getName() + ":" + e.getMessage());
			return false;
	}
	return true;
    }

    File[] getInstalledPacksDirs()
    {
	final File packsDir = props.getFileProperty("luwrain.dir.packs");
	if (!packsDir.exists() || !packsDir.isDirectory())
	    return new File[0];
	final File[] files = packsDir.listFiles();
	if (files == null)
	    return new File[0];
	final List<File> res = new LinkedList();
	for(File f: files)
	    if (f != null && f.exists() && f.isDirectory())
		res.add(f);
	return res.toArray(new File[res.size()]);
    }

        boolean unloadDynamicExtension(String extId)
    {
	NullCheck.notEmpty(extId, "extId");
	mainCoreThreadOnly();
	final org.luwrain.core.extensions.LoadedExtension ext = extensions.getDynamicExtensionById(extId);
	if (ext == null)
	    return false;
	objRegistry.deleteByExt(ext.ext);
	//FIXME:workers
	commands.deleteByInstance(ext.luwrain);
	return extensions.unloadDynamicExtension(ext.ext);
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
