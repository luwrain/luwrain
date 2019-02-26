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
import java.util.concurrent.atomic.*;
import java.util.concurrent.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.base.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.speech.Channel;

final class LuwrainImpl implements Luwrain
{
    private final Core core;

    LuwrainImpl(Core core)
    {
	NullCheck.notNull(core, "core");
	this.core = core;
    }

    @Override public CmdLine getCmdLine()
    {
	core.mainCoreThreadOnly();
	return core.cmdLine;
    }

    @Override public String getActiveAreaText(AreaTextType type, boolean issueErrorMessages)
    {
	NullCheck.notNull(type, "type");
	core.mainCoreThreadOnly();
	final Area activeArea = core.getValidActiveArea(issueErrorMessages);
	if (activeArea == null)
	    return null;
	return new AreaText(activeArea).get(type);
    }

    @Override public String getActiveAreaAttr(AreaAttr attr)
    {
	NullCheck.notNull(attr, "attr");
	core.mainCoreThreadOnly();
	final Area area = core.getValidActiveArea(false);
	if (area == null)
	    return attr == AreaAttr.DIRECTORY?getFileProperty("luwrain.dir.userhome").toString():null;
	switch(attr)
	{
	case DIRECTORY:
	    {
		final CurrentDirQuery query = new CurrentDirQuery();
		if (!AreaQuery.ask(area, query))
		    return getFileProperty("luwrain.dir.userhome").toString();
		return query.getAnswer();
	    }
	case UNIREF:
	    {
		final UniRefAreaQuery query = new UniRefAreaQuery();
		if (!AreaQuery.ask(area, query))
		    return null;
		return query.getAnswer();
	    }
	case UNIREF_UNDER_HOT_POINT:
	    {
		final UniRefHotPointQuery query = new UniRefHotPointQuery();
		if (!AreaQuery.ask(area, query))
		    return null;
		return query.getAnswer();
	    }
	case URL:
	    {
		final UrlAreaQuery query = new UrlAreaQuery();
		if (AreaQuery.ask(area, query))
		    return query.getAnswer();
		final UniRefAreaQuery query2 = new UniRefAreaQuery();
		if (!AreaQuery.ask(area, query2))
		    return null;
		return extractUrl(query2.getAnswer());
	    }
	case URL_UNDER_HOT_POINT:
	    {
		final UrlHotPointQuery query = new UrlHotPointQuery();
		if (AreaQuery.ask(area, query))
		    return query.getAnswer();
		final UniRefHotPointQuery query2 = new UniRefHotPointQuery();
		if (!AreaQuery.ask(area, query2))
		    return null;
		return extractUrl(query2.getAnswer());
	    }
	default:
	    return null;
	}
    }

    @Override public void sendBroadcastEvent(EnvironmentEvent e)
    {
	NullCheck.notNull(e, "e");
	core.enqueueEvent(e);
    }

    @Override public void sendInputEvent(KeyboardEvent e)
    {
	NullCheck.notNull(e, "e");
	core.enqueueEvent(e);
    }

    @Override public boolean xQuit()
    {
	core.mainCoreThreadOnly();
	core.quit();
	return true;
    }

    @Override public Path getAppDataDir(String appName)
    {
	NullCheck.notEmpty(appName, "appName");
	core.mainCoreThreadOnly();
	if (appName.indexOf("/") >= 0)
	    throw new IllegalArgumentException("appName contains illegal characters");
	final Path res = getFileProperty("luwrain.dir.appdata").toPath().resolve(appName);
	try {
	    Files.createDirectories(res);
	    return res;
	}
	catch(IOException e)
	{
	    Log.error("core", "unable to prepare application data directory:" + res.toString() + ":" + e.getClass().getName() + ":" + e.getMessage());
	    return null;
	}
    }

    @Override public     void closeApp()
    {
	core.mainCoreThreadOnly();
	core.closeAppIface(this);
    }

    @Override public Registry getRegistry()
    {
	return core.registry();
    }

    @Override public I18n i18n()
    {
	return core.i18nIface();
    }

    @Override public void crash(org.luwrain.app.crash.App app)
    {
	NullCheck.notNull(app, "app");
	runUiSafely(()->core.launchAppCrash(app));
    }

        @Override public void crash(Exception e)
    {
	NullCheck.notNull(e, "e");
	e.printStackTrace();
	final Luwrain instance = this;
	runUiSafely(()->core.launchAppCrash(this, e));
    }

    @Override public void launchApp(String shortcutName)
    {
	NullCheck.notNull(shortcutName, "shortcutName");
	runUiSafely(()->core.launchAppIface(shortcutName, new String[0]));
    }

    @Override public void launchApp(String shortcutName, String[] args)
    {
	NullCheck.notNull(shortcutName, "shortcutName");
	NullCheck.notNullItems(args, "args");
	runUiSafely(()->core.launchAppIface(shortcutName, args != null?args:new String[0]));
    }

    @Override public void message(String text)
    {
	NullCheck.notNull(text, "text");
	if (text.trim().isEmpty())
	    return;
	runUiSafely(()->{
		core.braille.textToSpeak(text);
		core.message(text, MessageType.REGULAR);
	    });
    }

    @Override public void message(String text, MessageType messageType)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(messageType, "messageType");
	if (text.trim().isEmpty())
	    return;
	runUiSafely(()->{
		core.braille.textToSpeak(text);
		core.message(text, messageType);
	    });
    }

    @Override public void message(String text, Sounds sound)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(sound, "sound");
	if (text.trim().isEmpty())
	    return;
	runUiSafely(()->{
		core.braille.textToSpeak(text);
		core.message(text, sound);
	    });
    }

    @Override public void onAreaNewHotPoint(Area area)
    {
	NullCheck.notNull(area, "area");
	core.mainCoreThreadOnly();
	core.onAreaNewHotPointIface(this, area);
    }

    @Override public void onAreaNewContent(Area area)
    {
	NullCheck.notNull(area, "area");
	core.mainCoreThreadOnly();
	core.onAreaNewContentIface(this, area);
    }

    @Override public void onAreaNewName(Area area)
    {
	NullCheck.notNull(area, "area");
	core.mainCoreThreadOnly();
	core.onAreaNewNameIface(this, area);
    }

    @Override public void onAreaNewBackgroundSound(Area area)
    {
	NullCheck.notNull(area, "area");
	core.mainCoreThreadOnly();
	core.onAreaNewBackgroundSound(this, area);
    }

    @Override public int getAreaVisibleHeight(Area area)
    {
	NullCheck.notNull(area, "area");
	core.mainCoreThreadOnly();
	return core.getAreaVisibleHeightIface(this, area);
    }

    @Override public int getAreaVisibleWidth(Area area)
    {
	NullCheck.notNull(area, "area");
	core.mainCoreThreadOnly();
	return core.getAreaVisibleWidthIface(this, area);
    }

    @Override public int getScreenWidth()
    {
	core.mainCoreThreadOnly();
	return core.getScreenWidthIface();
    }

    @Override public int getScreenHeight()
    {
	core.mainCoreThreadOnly();
	return core.getScreenHeightIface();
    }

    @Override public void announceActiveArea()
    {
	core.mainCoreThreadOnly();
	core.announceActiveAreaIface();
    }

    @Override public Clipboard getClipboard()
    {
	return core.getClipboard();
    }

    @Override public void onNewAreaLayout()
    {
	core.mainCoreThreadOnly();
	core.onNewAreaLayoutIface(this);
    }

    @Override public void openFile(String fileName)
    {
	NullCheck.notNull(fileName, "fileName");
	runUiSafely(()->{
		String[] s = new String[1];
		s[0] = fileName;
		core.openFiles(s);
	    });
    }

    @Override public void openFiles(String[] fileNames)
    {
	NullCheck.notNullItems(fileNames, "fileNames");
	runUiSafely(()->core.openFiles(fileNames));
    }

    @Override public boolean openHelp(String sectName)
    {
	NullCheck.notEmpty(sectName, "sectName");
	core.mainCoreThreadOnly();
	final String url = core.helpSects.getSectionUrl(sectName);
	if (url.isEmpty())
	    return false;
	launchApp("reader", new String[]{url});
	return true;
    }


    @Override public String suggestContentType(java.net.URL url, ContentTypes.ExpectedType expectedType)
    {
	NullCheck.notNull(url, "url");
	NullCheck.notNull(expectedType, "expectedType");
	return core.contentTypes.suggestContentType(url, expectedType);
    }

    @Override public String suggestContentType(java.io.File file, ContentTypes.ExpectedType expectedType)
    {
	NullCheck.notNull(file, "file");
	NullCheck.notNull(expectedType, "expectedType");
	return core.contentTypes.suggestContentType(file, expectedType);
    }

    @Override public void playSound(Sounds sound)
    {
	NullCheck.notNull(sound, "sound");
	runUiSafely(()->core.playSound(sound));
    }

    @Override public void popup(Popup popup)
    {
	NullCheck.notNull(popup, "popup");
	core.mainCoreThreadOnly();
	core.popupIface(popup);
    }

    @Override public boolean runCommand(String command)
    {
	NullCheck.notNull(command, "command");
	core.mainCoreThreadOnly();
	return core.runCommand(command);
    }

    @Override public org.luwrain.base.CommandLineTool.Instance runCommandLineTool(String name, String[] args, org.luwrain.base.CommandLineTool.Listener listener)
    {
	NullCheck.notNull(name, "name");
	NullCheck.notNullItems(args, "args");
	NullCheck.notNull(listener, "listener");
	core.mainCoreThreadOnly();
	return core.commandLineTools.run(name, args, listener);
    }

    @Override public void say(String text)
    {
	NullCheck.notNull(text, "text");
	runUiSafely(()->{
		core.braille.textToSpeak(text);
		core.speech.speak(core.speakingText.processRegular(text), 0, 0);
	    });
    }

    @Override public void say(String text, Sounds sound)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(sound, "sound");
	runUiSafely(()->{
		playSound(sound);
		say(text);
	    });
    }

    @Override public void say(String text, int pitch)
    {
	NullCheck.notNull(text, "text");
	runUiSafely(()->{
		core.braille.textToSpeak(text);
		core.speech.speak(core.speakingText.processRegular(text), pitch, 0);
	    });
    }

    @Override public void say(String text, int pitch, int rate)
    {
	NullCheck.notNull(text, "text");
	runUiSafely(()->core.speech.speak(core.speakingText.processRegular(text), pitch, rate));
    }

    @Override public void sayLetter(char letter)
    {
	core.braille.textToSpeak("" + letter);
	runUiSafely(()->{
		switch(letter)
		{
		case ' ':
		    sayHint(Hint.SPACE);
		    return;
		case '\t':
		    sayHint(Hint.TAB);
		    return;
		}
		final String value = i18n().hasSpecialNameOfChar(letter);
		if (value == null)
		    core.speech.speakLetter(letter, 0, 0); else
		    say(value, Speech.PITCH_HINT);//FIXME:
	    });
    }

    @Override public void sayLetter(char letter, int pitch)
    {
	runUiSafely(()->{
		switch(letter)
		{
		case ' ':
		    sayHint(Hint.SPACE);
		    return;
		case '\t':
		    sayHint(Hint.TAB);
		    return;
		}
		final String value = i18n().hasSpecialNameOfChar(letter);
		if (value == null)
		    core.speech.speakLetter(letter, pitch, 0); else
		    say(value, Speech.PITCH_HINT);
	    });
    }

    @Override public void speakLetter(char letter,
				      int pitch, int rate)
    {
	runUiSafely(()->{
		switch(letter)
		{
		case ' ':
		    sayHint(Hint.SPACE);
		    return;
		case '\t':
		    sayHint(Hint.TAB);
		    return;
		}
		final String value = i18n().hasSpecialNameOfChar(letter);
		if (value == null)
		    core.speech.speakLetter(letter, pitch, rate); else
		    say(value, Speech.PITCH_HINT);
	    });
    }

    @Override public void silence()
    {
	runUiSafely(()->core.speech.silence());
    }

    @Override public void setActiveArea(Area area)
    {
	NullCheck.notNull(area, "area");
	core.mainCoreThreadOnly();
	core.setActiveAreaIface(this, area);
    }

    @Override public String staticStr(LangStatic id)
    {
	NullCheck.notNull(id, "id");
	core.mainCoreThreadOnly();
	return i18n().staticStr(id);
    }

    @Override public UniRefInfo getUniRefInfo(String uniRef)
    {
	NullCheck.notNull(uniRef, "uniRef");
	core.mainCoreThreadOnly();
	if (uniRef.isEmpty())
	    return null;
	return core.uniRefProcs.getInfo(uniRef);
    }

    @Override public boolean openUniRef(String uniRef)
    {
	NullCheck.notNull(uniRef, "uniRef");
	core.mainCoreThreadOnly();
	return core.openUniRefIface(uniRef);
    }

    @Override public boolean openUniRef(UniRefInfo uniRefInfo)
    {
	NullCheck.notNull(uniRefInfo, "uniRefInfo");
	core.mainCoreThreadOnly();
	return core.uniRefProcs.open(uniRefInfo.getValue());
    }

    @Override public org.luwrain.browser.Browser createBrowser()
    {
	core.mainCoreThreadOnly();
	return core.interaction.createBrowser();
    }

        @Override public org.luwrain.interaction.graphical.Pdf createPdfPreview(org.luwrain.interaction.graphical.Pdf.Listener listener, File file) throws Exception
    {
	NullCheck.notNull(listener, "listener");
	NullCheck.notNull(file, "file");
		core.mainCoreThreadOnly();
		return core.interaction.createPdfPreview(new org.luwrain.interaction.graphical.Pdf.Listener(){
			@Override public void onInputEvent(KeyboardEvent event)
			{
			    NullCheck.notNull(event, "event");
			    runUiSafely(()->listener.onInputEvent(event));
			}
		    }, file);
    }

    @Override public void runUiSafely(Runnable runnable)
    {
	NullCheck.notNull(runnable, "runnable");
	if (!core.isMainCoreThread())
	    runInMainThread(runnable); else
	    runnable.run();
    }

    @Override public Object runLaterSync(Callable callable)
    {
	NullCheck.notNull(callable, "callable");
	final Core.CallableEvent event = new Core.CallableEvent(callable);
	core.enqueueEvent(event);
	try {
	    event.waitForBeProcessed();
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	    return null;
	}
	return event.getResult();
    }

    @Override public Object callUiSafely(Callable callable)
    {
	NullCheck.notNull(callable, "callable");
	if (core.isMainCoreThread())
	{
	    try {
		return callable.call(); 
	    }
	    catch(Throwable e)
	    {
		Log.error(Core.LOG_COMPONENT, "exception on processing of CallableEvent:" + e.getClass().getName() + ":" + e.getMessage());
		return null;
	    }
	} else
	    return runLaterSync(callable);
    }

    @Override public int xGetSpeechRate()
    {
	core.mainCoreThreadOnly();
	return  core.speech.getRate();
    }

    @Override public void xSetSpeechRate(int value)
    {
	core.mainCoreThreadOnly();
	core.speech.setRate(value);
    }

    @Override public int xGetSpeechPitch()
    {
	core.mainCoreThreadOnly();
	return core.speech.getPitch();
    }

    @Override public void xSetSpeechPitch(int value)
    {
	core.mainCoreThreadOnly();
	core.speech.setPitch(value);
    }

    @Override public String[] getAllShortcutNames()
    {
	core.mainCoreThreadOnly();
	return core.objRegistry.getShortcutNames();
    }

    @Override public java.io.File getFileProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	//FIXME:	core.mainCoreThreadOnly();
	return core.props.getFileProperty(propName);
    }

    @Override public OsCommand runOsCommand(String cmd, String dir,
					    OsCommand.Output output, OsCommand.Listener listener)
    {
	NullCheck.notEmpty(cmd, "cmd");
	NullCheck.notNull(dir, "dir");
	core.mainCoreThreadOnly();
	return core.os.runOsCommand(cmd, (!dir.isEmpty())?dir:getFileProperty("luwrain.dir.userhome").getAbsolutePath(), output, listener);
    }

    @Override public String getProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	//FIXME:	core.mainCoreThreadOnly();
	return core.props.getProperty(propName);
    }

    @Override public void setEventResponse(EventResponse eventResponse)
    {
	NullCheck.notNull(eventResponse, "eventResponse");
	core.mainCoreThreadOnly();
	core.setEventResponse(eventResponse);
    }

    @Override public FilesOperations getFilesOperations()
    {
	core.mainCoreThreadOnly();
	return core.os.getFilesOperations();
    }

    @Override public org.luwrain.player.Player getPlayer()
    {
	return core.player;
    }

    @Override public org.luwrain.base.MediaResourcePlayer[] getMediaResourcePlayers()
    {
	core.mainCoreThreadOnly();
	final List<org.luwrain.base.MediaResourcePlayer> res = new LinkedList();
	res.add(core.wavePlayer);
	for(org.luwrain.base.MediaResourcePlayer p: core.objRegistry.getMediaResourcePlayers())
	    res.add(p);
	return res.toArray(new org.luwrain.base.MediaResourcePlayer[res.size()]);
    }

    @Override public String[] xGetLoadedSpeechFactories()
    {
	core.mainCoreThreadOnly();
	return new String[0];
    }

    @Override public boolean runWorker(String workerName)
    {
	NullCheck.notEmpty(workerName, "workerName");
	core.mainCoreThreadOnly();
	return core.workers.runExplicitly(workerName);
    }

    @Override public void executeBkg(java.util.concurrent.FutureTask task)
    {
	NullCheck.notNull(task, "task");
	core.mainCoreThreadOnly();
	//FIXME:maintaining the registry of executed tasks with their associations to Luwrain objects
	java.util.concurrent.Executors.newSingleThreadExecutor().execute(task);
    }

    @Override public boolean registerExtObj(ExtensionObject extObj)
    {
	NullCheck.notNull(extObj, "extObj");
	core.mainCoreThreadOnly();
	if (this != core.getObjForEnvironment())
	    throw new RuntimeException("registerExtObj() may be called only for privileged interfaces");
	return core.objRegistry.add(null, extObj);
    }

    @Override public java.util.concurrent.Callable runScriptInFuture(org.luwrain.core.script.Context context, File dataDir, String text)
    {
	NullCheck.notNull(context, "context");
	NullCheck.notNull(dataDir, "dataDir");
	NullCheck.notNull(text, "text");
	core.mainCoreThreadOnly();
	return core.script.execFuture(this, dataDir, context, text);
    }

    @Override public String loadScriptExtension(String text) throws org.luwrain.core.extensions.DynamicExtensionException
    {
	NullCheck.notNull(text, "text");
	core.mainCoreThreadOnly();
	return core.loadScriptExtension(core.props.getFileProperty("luwrain.dir.data"), text);
    }

    @Override public org.luwrain.speech.Channel loadSpeechChannel(String engineName, String params) throws org.luwrain.speech.SpeechException
    {
	NullCheck.notEmpty(engineName, "engineName");
	NullCheck.notNull(params, "params");
	return core.speech.loadChannel(engineName, params);
    }

    @Override public boolean unloadDynamicExtension(String extId)
    {
	NullCheck.notEmpty(extId, "extId");
	core.mainCoreThreadOnly();
	return core.unloadDynamicExtension(extId);
    }

    @Override public void xExecScript(File dataDir, String text)
    {
	NullCheck.notNull(dataDir, "dataDir");
	NullCheck.notNull(text, "text");
	core.mainCoreThreadOnly();
	core.script.exec(dataDir, text);
    }

        @Override public String getSpokenText(String text, SpokenTextType type)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(type, "type");
	return core.i18n.getSpokenText(text, type);
    }

        @Override public void xRunHooks(String hookName, HookRunner runner)
    {
	NullCheck.notEmpty(hookName, "hookName");
	NullCheck.notNull(runner, "runner");
	core.extensions.runHooks(hookName, runner);
    }

        @Override public boolean xRunHooks(String hookName, Object[] args, HookStrategy strategy)
    {
	NullCheck.notEmpty(hookName, "hookName");
	NullCheck.notNull(args, "args");
	NullCheck.notNull(strategy, "strategy");
	final AtomicBoolean execRes = new AtomicBoolean(false);
	final AtomicReference<RuntimeException> error = new AtomicReference();
	xRunHooks(hookName, (hook)->{
		try {
		final Object res = hook.run(args);
		switch(strategy)
		{
		case CHAIN_OF_RESPONSIBILITY:
		    if (res == null)
					    		return Luwrain.HookResult.CONTINUE;
		    if (!(res instanceof Boolean))
					    		return Luwrain.HookResult.CONTINUE;
		    if (((Boolean)res).booleanValue())
		    {
			execRes.set(true);
			return Luwrain.HookResult.BREAK;
		    }
		case ALL:
		default:
		    		return Luwrain.HookResult.CONTINUE;
		}
		}
		catch(Throwable e)
		{
		    if (!(e instanceof RuntimeException))
		    {
			Log.error("core", "throwable during hook execution:" + e.getClass().getName() + ":" + e.getMessage());
			return HookResult.CONTINUE;
		    }
		    final RuntimeException runtimeEx = (RuntimeException)e;
		    error.set(runtimeEx);
		    switch(strategy)
		    {
		    case CHAIN_OF_RESPONSIBILITY:
			return HookResult.BREAK;
		    case ALL:
		    default:
			return HookResult.CONTINUE;
		    }
		}
	    });
	switch(strategy)
	{
	case CHAIN_OF_RESPONSIBILITY:
	    	if (error.get() != null)
	    throw error.get();
	    return execRes.get();
	case ALL:
	default:
	    return error.get() == null;
    }
    }

    @Override public OsInterface xGetOsInterface()
    {
	return core.os.getInterface();
    }

            @Override public boolean xCreatePropertyHook(String propName, String hookName)
    {
	NullCheck.notEmpty(propName, "propName");
	NullCheck.notEmpty(hookName, "hookName");
	return core.props.createHook(propName, hookName);
    }

    private void sayHint(Hint hint)
    {
	NullCheck.notNull(hint, "hint");
	final LangStatic staticStrId = EventResponses.hintToStaticStrMap(hint);
	if (staticStrId == null)
	    return;
	say(i18n().staticStr(staticStrId), Speech.PITCH_HINT);
    }

    private void runInMainThread(Runnable runnable)
    {
	NullCheck.notNull(runnable, "runnable");
	core.enqueueEvent(new Core.RunnableEvent(runnable));
    }

    static private String extractUrl(String uniRef)
    {
	NullCheck.notNull(uniRef, "uniRef");
	if (!uniRef.startsWith("url:"))
	    return null;
	return uniRef.substring("url:".length());
    }
}
