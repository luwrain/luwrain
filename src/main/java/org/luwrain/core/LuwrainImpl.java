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

import org.luwrain.base.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.speech.Channel;

final class LuwrainImpl implements Luwrain
{
    private final Core core;
    private String charsToSkip = "";

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

    @Override public void xQuit()
    {
	core.mainCoreThreadOnly();
	core.quit();
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
		core.getSpeech().speak(preprocess(text), 0, 0);
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
		core.getSpeech().speak(preprocess(text), pitch, 0);
	    });
    }

    @Override public void say(String text, int pitch, int rate)
    {
	NullCheck.notNull(text, "text");
	runUiSafely(()->core.getSpeech().speak(preprocess(text), pitch, rate));
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
		    core.getSpeech().speakLetter(letter, 0, 0); else
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
		    core.getSpeech().speakLetter(letter, pitch, 0); else
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
		    core.getSpeech().speakLetter(letter, pitch, rate); else
		    say(value, Speech.PITCH_HINT);
	    });
    }

    @Override public void silence()
    {
	runUiSafely(()->core.getSpeech().silence());
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

    @Override public Channel getAnySpeechChannelByCond(Set<Channel.Features> cond)
    {
	NullCheck.notNull(cond, "cond");
	core.mainCoreThreadOnly();
	return core.getSpeech().getAnyChannelByCond(cond);
    }

    @Override public Channel[] getSpeechChannelsByCond(Set<Channel.Features> cond)
    {
	NullCheck.notNull(cond, "cond");
	core.mainCoreThreadOnly();
	return core.getSpeech().getChannelsByCond(cond);
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
	return  core.getSpeech().getRate();
    }

    @Override public void xSetSpeechRate(int value)
    {
	core.mainCoreThreadOnly();
	core.getSpeech().setRate(value);
    }

    @Override public int xGetSpeechPitch()
    {
	core.mainCoreThreadOnly();
	return core.getSpeech().getPitch();
    }

    @Override public void xSetSpeechPitch(int value)
    {
	core.mainCoreThreadOnly();
	core.getSpeech().setPitch(value);
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

    @Override public java.util.concurrent.Callable runScriptInFuture(org.luwrain.core.script.Context context, String text)
    {
	NullCheck.notNull(context, "context");
	core.mainCoreThreadOnly();
	return core.script.execFuture(this, context, text);
    }

    @Override public String loadScriptExtension(String text) throws org.luwrain.core.extensions.DynamicExtensionException
    {
	NullCheck.notNull(text, "text");
	core.mainCoreThreadOnly();
	return core.loadScriptExtension(text);
    }

    @Override public boolean unloadDynamicExtension(String extId)
    {
	NullCheck.notEmpty(extId, "extId");
	core.mainCoreThreadOnly();
	return core.unloadDynamicExtension(extId);
    }

    @Override public void xExecScript(String text)
    {
	NullCheck.notNull(text, "text");
	core.mainCoreThreadOnly();
	core.script.exec(text);
    }

        @Override public String getSpokenText(String text, SpokenTextType type)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(type, "type");
	return core.text.translate(text, type);
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

    private String preprocess(String s)
    {
	StringBuilder b = new StringBuilder();
	for(int i = 0;i < s.length();++i)
	{
	    final char c = s.charAt(i);
	    int k;
	    for(k = 0;k < charsToSkip.length();++k)
		if (c == charsToSkip.charAt(k))
		    break;
	    if (k >= charsToSkip.length())
		b.append(c);
	}
	return b.toString();
    }

    static private String extractUrl(String uniRef)
    {
	NullCheck.notNull(uniRef, "uniRef");
	if (!uniRef.startsWith("url:"))
	    return null;
	return uniRef.substring("url:".length());
    }
}
