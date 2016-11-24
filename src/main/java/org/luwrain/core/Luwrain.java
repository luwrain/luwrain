/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
import java.io.*;
import java.nio.file.*;

import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.speech.Channel;

/**
 * The main bridge for applications and extensions purposed for communication with
 * LUWRAIN core. This class is a central object to be used by
 * applications and extensions to call system routines. Applications and
 * extensions never have the access with the level deeper than this
 * class. The packages like {@code org.luwrain.controls} or 
 * {@code org.luwrain.popups} always wrap the instance of {@code Luwrain} class
 * (meaning, are unable to give more access to system core than provided
 * with given instance of {@code Luwrain} class).
 * <p>
 * The core creates new instance of this class for each newly launched
 * application or loaded extension. Therefore, the environment is always
 * aware which application oor extension has issued the particular
 * request. Applications get the object associated with them through
 * {@code onLaunch()} method. Extensions get the corresponding instance
 * through the argument for the methods they override (it is always the
 * same instance provided this way just for convenience). Everybody is
 * encouraged to keep provided instance in secret. 
 * <p>
 * It could be slightly confusing that the extension and the applications
 * launched by this extension get different instances of {@code Luwrain}
 * class, but it's necessary to distinguish multiple instances of the
 * particular application (while an extension can be loaded only once).
 * <p>
 * Various instance of {@code Luwrain} class may provide different level
 * of access.  It is necessary to make extensions using more accurate and
 * transparent.
 */
public final class Luwrain implements EventConsumer, org.luwrain.base.CoreProperties
{
    public enum ReloadComponents {
	ENVIRONMENT_SOUNDS,
    };

    public static final int PITCH_HIGH = 25;
    public static final int PITCH_NORMAL = 0;
    public static final int PITCH_LOW = -25;
    public static final int PITCH_HINT = -25;
    public static final int PITCH_MESSAGE = -25;

    /** The message has no any typical semantics*/
    static public final int MESSAGE_REGULAR = 0;

    /** The message implies a confirmation of a successful action*/
    static public final int MESSAGE_OK = 1;

    /** The message implies a successful finishing of an operation continuous in time*/
    static public final int MESSAGE_DONE = 2;

    /** The message notifies the user that the object is unable to perform the requested operation*/
    static public final int MESSAGE_NOT_READY = 3;

    /** The message must be issued with a sound announcement indicating the critical error*/
    static public final int MESSAGE_ERROR = 4;

    private final Environment environment;
    private String charsToSkip = "";

    Luwrain(Environment environment)
    {
	NullCheck.notNull(environment, "environment");
	this.environment = environment;
	Registry registry = environment.registry();
	RegistryKeys keys = new RegistryKeys();
	if (registry.getTypeOf(keys.speechCharsToSkip()) == Registry.STRING)
	    charsToSkip = registry.getString(keys.speechCharsToSkip());
    }

    public RegionContent currentAreaRegion(boolean issueErrorMessages)
    {
	final Area area = environment.getValidActiveArea(issueErrorMessages);
	if (area == null)
	    return null;
	final RegionQuery query = new RegionQuery();
	if (!area.onAreaQuery(query) || !query.hasAnswer())
	    return null;
	return query.getAnswer();
    }

    public String currentAreaWord(boolean issueErrorMessages)
    {
	return environment.currentAreaWordIface(issueErrorMessages);
    }

    //Never returns null, returns user home dir if area doesn't speak about that
    public String currentAreaDir()
    {
	final Area area = environment.getValidActiveArea(false);
	if (area == null)
	    return getPathProperty("luwrain.dir.userhome").toString();
	final CurrentDirQuery query = new CurrentDirQuery();
	if (!area.onAreaQuery(query) || !query.hasAnswer())
	    return getPathProperty("luwrain.dir.userhome").toString();
	return query.getAnswer();
    }

    @Override public void enqueueEvent(Event e)
    {
	NullCheck.notNull(e, "e");
	if (e instanceof ThreadSyncEvent)
	{
	    final ThreadSyncEvent threadSync = (ThreadSyncEvent)e;
	    threadSync.setInstanceObj(this);
	}
	environment.enqueueEvent(e);
    }

    /**
     * Returns a path to the directory where the application may safely store
     * its auxiliary data. The returned directory, if it it isn't {@code null},
     * always exists and always belongs to the current user. Meaning,
     * different users get different directories for the same
     * application. Application must be identified with some short string. We
     * discourage using application names starting with "luwrain.", because
     * such names are usually used by the applications from LUWRAIN standard
     * distribution.
     *
     * @param appName A short string for application identification, the same application name will result in the same directory
     * @return The application data directory or {@code null} if the directory cannot be created
     */
    public Path getAppDataDir(String appName)
    {
	NullCheck.notEmpty(appName, "appName");
	if (appName.indexOf("/") >= 0)
	    throw new IllegalArgumentException("appName contains illegal characters");
	final Path res = getPathProperty("luwrain.dir.appdata").resolve(appName);
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

    public     void closeApp()
    {
	environment.closeAppIface(this);
    }

    public Registry getRegistry()
    {
	return environment.registry();
    }

    public Object getSharedObject(String id)
    {
	NullCheck.notNull(id, "id");
	return environment.getSharedObjectIface(id);
    }

    public void hint(String text)
    {
	NullCheck.notNull(text, "text");
	say(text, PITCH_HINT);
    }

    public void hint(String text, int code)
    {
	NullCheck.notNull(text, "text");
	final Sounds sound = Hints.hintToSoundMap(code);
	if (sound != null)
	    playSound(sound);
	if (environment.onStandardHint(code))
	    hint(text);
    }

    public boolean hint(int code)
    {
	final LangStatic staticStrId = Hints.hintToStaticStrMap(code);
	if (staticStrId == null)
	    return false;
	hint(i18n().staticStr(staticStrId), code);
	return true;
    }

    public I18n i18n()
    {
	return environment.i18nIface();
    }

    public void crash(Exception e)
    {
	NullCheck.notNull(e, "e");
	e.printStackTrace();
	final Luwrain instance = this;
	runInMainThread(()->environment.launchAppCrash(this, e));
    }

    public void launchApp(String shortcutName)
    {
	NullCheck.notNull(shortcutName, "shortcutName");
	environment.launchAppIface(shortcutName, new String[0]);
    }

    public void launchApp(String shortcutName, String[] args)
    {
	NullCheck.notNull(shortcutName, "shortcutName");
	NullCheck.notNullItems(args, "args");
	environment.launchAppIface(shortcutName, args != null?args:new String[0]);
    }

    /*
    public LaunchContext launchContext()
    {
	return environment.launchContextIface();
    }
    */

    public void message(String text)
    {
	NullCheck.notNull(text, "text");
	environment.getBraille().textToSpeak(text);
	environment.message(text, MESSAGE_REGULAR);
    }

    public void message(String text, int semantic)
    {
	NullCheck.notNull(text, "text");
	environment.message(text, semantic);
    }

    /**
     * Notifies the environment that the area gets new position of the hot
     * point. This method causes updating of the visual position of the hot
     * point on the screen for low vision users.  Please keep in mind that
     * this method doesn't produce any speech announcement of the new
     * position and you should do that on your own, depending on the
     * behaviour of your application.
     *
     * @param area The area which gets new position of the hot point
     */
    public void onAreaNewHotPoint(Area area)
    {
	NullCheck.notNull(area, "area");
	environment.onAreaNewHotPointIface(this, area);
    }

    /**
     * Notifies the environment that the area gets new content. This method
     * causes updating of the visual representation of the area content on
     * the screen for low vision users.  Please keep in mind that this method
     * doesn't produce any speech announcement of the changes and you should
     * do that on your own, depending on the behaviour of your application.
     *
     * @param area The area which gets new content
     */
    public void onAreaNewContent(Area area)
    {
	NullCheck.notNull(area, "area");
	environment.onAreaNewContentIface(this, area);
    }

    /**
     * Notifies the environment that the area gets new name. This method
     * causes updating of the visual title of the area on the screen for low
     * vision users.  Please keep in mind that this method doesn't produce
     * any speech announcement of name changes and you should do that on your
     * own, depending on the behaviour of your application.
     *
     * @param area The area which gets new name
     */
    public void onAreaNewName(Area area)
    {
	NullCheck.notNull(area, "area");
	environment.onAreaNewNameIface(this, area);
    }

    public void onAreaNewBackgroundSound(Area area)
    {
	NullCheck.notNull(area, "area");
	environment.onAreaNewBackgroundSound(this, area);
    }


    //May return -1 if area is not shown on the screen;
    public int getAreaVisibleHeight(Area area)
    {
	NullCheck.notNull(area, "area");
	return environment.getAreaVisibleHeightIface(this, area);
    }

    public int getAreaVisibleWidth(Area area)
    {
	NullCheck.notNull(area, "area");
	return environment.getAreaVisibleWidthIface(this, area);
    }

    public int getScreenWidth()
    {
	return environment.getScreenWidthIface();
    }

    public int getScreenHeight()
    {
	return environment.getScreenHeightIface();
    }

    public void announceActiveArea()
    {
	environment.announceActiveAreaIface();
    }

    public void setClipboard(RegionContent content)
    {
	NullCheck.notNull(content, "content");
	environment.setClipboard(content);
    }

    public RegionContent getClipboard()
    {
	return environment.getClipboard();
    }



    //Doesn't produce any announcement
    public void onNewAreaLayout()
    {
	environment.onNewAreaLayoutIface(this);
    }

    public void openFile(String fileName)
    {
	NullCheck.notNull(fileName, "fileName");
	String[] s = new String[1];
	s[0] = fileName;
	environment.openFiles(s);
    }

    public void openFiles(String[] fileNames)
    {
	NullCheck.notNullItems(fileNames, "fileNames");
	environment.openFiles(fileNames);
    }

    /**
    /**
    * Plays one of the system sounds.  This method takes an identifier of
    * the system sound, stops any previous playing, if there was any, and
    * plays. The exact sound is selected depending on user's
    * settings. Please node that sounds playing isn't interfering with
    * speech. 
    *
    * @param sound The identifier of the sound to play
    */
    public void playSound(Sounds sound)
    {
	NullCheck.notNull(sound, "sound");
	environment.playSound(sound);
    }

    public void popup(Popup popup)
    {
	NullCheck.notNull(popup, "popup");
	environment.popupIface(popup);
    }

    public boolean runCommand(String command)
    {
	NullCheck.notNull(command, "command");
	return environment.runCommand(command);
    }

    public void say(String text)
    {
	NullCheck.notNull(text, "text");
	environment.getBraille().textToSpeak(text);
	environment.getSpeech().speak(preprocess(text), 0, 0);
    }

    public void say(String text, Sounds sound)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(sound, "sound");
	playSound(sound);
	say(text);
    }

    public void say(String text, int pitch)
    {
	NullCheck.notNull(text, "text");
	environment.getBraille().textToSpeak(text);
	environment.getSpeech().speak(preprocess(text), pitch, 0);
    }

    public void say(String text,
		    int pitch, int rate)
    {
	NullCheck.notNull(text, "text");
	environment.getSpeech().speak(preprocess(text), pitch, rate);
    }

    public void sayLetter(char letter)
    {
	environment.getBraille().textToSpeak("" + letter);
	switch(letter)
	{
	case ' ':
	    hint(Hints.SPACE);
	    return;
	case '\t':
	    hint(Hints.TAB);
	    return;
	}
	final String value = i18n().hasSpecialNameOfChar(letter);
	if (value == null)
	    environment.getSpeech().speakLetter(letter, 0, 0); else
	    hint(value); 
    }

    public void sayLetter(char letter, int pitch)
    {
	switch(letter)
	{
	case ' ':
	    hint(Hints.SPACE);
	    return;
	case '\t':
	    hint(Hints.TAB);
	    return;
	}
	final String value = i18n().hasSpecialNameOfChar(letter);
	if (value == null)
	    environment.getSpeech().speakLetter(letter, pitch, 0); else
	    hint(value); 
    }

    public void speakLetter(char letter,
			    int pitch, int rate)
    {
	switch(letter)
	{
	case ' ':
	    hint(Hints.SPACE);
	    return;
	case '\t':
	    hint(Hints.TAB);
	    return;
	}
	final String value = i18n().hasSpecialNameOfChar(letter);
	if (value == null)
	    environment.getSpeech().speakLetter(letter, pitch, rate); else
	    hint(value); 
    }

    public void silence()
    {
	environment.getSpeech().silence();
    }

    /**
     * Sets the new active area of the application. This method asks the
     * environment to choose another visible area as an active area of the
     * application. This operation is applicable only to regular areas, not
     * for popup areas, for which it is pointless. In contrast to
     * {@code onAreaNewHotPoint()}, {@code onAreaNewName()} and 
     * {@code onAreaNewContent()} methods, this one produces proper introduction of
     * the area being activated.
     *
     * @param area The area to choose as an active
     */
    public void setActiveArea(Area area)
    {
	NullCheck.notNull(area, "area");
	environment.setActiveAreaIface(this, area);
    }

    public String staticStr(LangStatic id)
    {
	NullCheck.notNull(id, "id");
	return i18n().staticStr(id);
    }

    public UniRefInfo getUniRefInfo(String uniRef)
    {
	NullCheck.notNull(uniRef, "uniRef");
	return environment.getUniRefInfoIface(uniRef);
    }

    public boolean openUniRef(String uniRef)
    {
	NullCheck.notNull(uniRef, "uniRef");
	return environment.openUniRefIface(uniRef);
    }

    public boolean openUniRef(UniRefInfo uniRefInfo)
    {
	NullCheck.notNull(uniRefInfo, "uniRefInfo");
	NullCheck.notNull(uniRefInfo, "uniRefInfo");
	return openUniRef(uniRefInfo.value());
    }

    /*
    public Hardware getHardware()
    {
	return environment.getHardware();
    }
    */

    public org.luwrain.browser.Browser createBrowser()
    {
	return environment.createBrowserIface(this);
    }

    public Channel getAnySpeechChannelByCond(Set<Channel.Features> cond)
    {
	NullCheck.notNull(cond, "cond");
	return environment.getSpeech().getAnyChannelByCond(cond);
    }

    public Channel[] getSpeechChannelsByCond(Set<Channel.Features> cond)
    {
	NullCheck.notNull(cond, "cond");
	return environment.getSpeech().getChannelsByCond(cond);
    }

    public void runInMainThread(Runnable runnable)
    {
	NullCheck.notNull(runnable, "runnable");
	environment.enqueueEvent(new RunnableEvent(runnable));
    }

    public void reloadComponent(ReloadComponents component)
    {
	NullCheck.notNull(component, "component");
	environment.reloadComponent(component);
    }

    public int getSpeechRate()
    {
	return  environment.getSpeech().getRate();
    }

    public void setSpeechRate(int value)
    {
	environment.getSpeech().setRate(value);
    }

    public int getSpeechPitch()
    {
	return environment.getSpeech().getPitch();
    }

    public void setSpeechPitch(int value)
    {
	environment.getSpeech().setPitch(value);
    }

    public String[] getAllShortcutNames()
    {
	return environment.getAllShortcutNames();
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

    @Override public java.nio.file.Path getPathProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	return environment.getCoreProperties().getPathProperty(propName);
    }

    OsCommand runOsCommand(String cmd)
    {
	NullCheck.notEmpty(cmd, "cmd");
	return runOsCommand(cmd, "", null, null);
    }

    OsCommand runOsCommand(String cmd, String dir)
    {
	NullCheck.notEmpty(cmd, "cmd");
	NullCheck.notNull(dir, "dir");
	return runOsCommand(cmd, dir, null, null);
    }

    OsCommand runOsCommand(String cmd, String dir, OsCommand.Output output)
    {
	NullCheck.notEmpty(cmd, "cmd");
	NullCheck.notNull(dir, "dir");
	return runOsCommand(cmd, dir, output, null);
    }

    OsCommand runOsCommand(String cmd, String dir,
			   OsCommand.Output output, OsCommand.Listener listener)
    {
	NullCheck.notEmpty(cmd, "cmd");
	NullCheck.notNull(dir, "dir");
	return environment.os().runOsCommand(cmd, (!dir.isEmpty())?dir:getPathProperty("luwrain.dir.userhome").toString(), output, listener);
    }

    @Override public String getProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	if (propName.startsWith("luwrain.speech.channel."))
	{
	    final String arg = propName.substring("luwrain.speech.channel.".length());
	    final String[] args = arg.split("\\.", -1);
	    if (args.length != 2 || 
		args[0].isEmpty() || args[1].isEmpty())
		return "";
	    int n = 0;
	    try {
		n = Integer.parseInt(args[0]);
	    }
	    catch(NumberFormatException e)
	    {
		return "";
	    }
	    final Channel[] channels = environment.getSpeech().getAllChannels();
	    if (n >= channels.length)
		return "";
	    final Channel channel = channels[n];
	    switch(args[1])
	    {
	    case "name":
		return channel.getChannelName();
	    case "class":
		return channel.getClass().getName();
	    case "default":
		return environment.getSpeech().isDefaultChannel(channel)?"1":"0";
	    case "cansynthtospeakers":
		return channel.getFeatures().contains(Channel.Features.CAN_SYNTH_TO_SPEAKERS)?"1":"0";
	    case "cansynthtostream":
		return channel.getFeatures().contains(Channel.Features.CAN_SYNTH_TO_STREAM)?"1":"0";
	    case "cannotifywhenfinished":
		return channel.getFeatures().contains(Channel.Features.CAN_NOTIFY_WHEN_FINISHED)?"1":"0";
	    default:
		return "";
	    }
	}
	switch(propName)
	{
	case "luwrain.braille.active":
	    return environment.getBraille().isActive()?"1":"0";
	case "luwrain.braille.driver":
	    return environment.getBraille().getDriver();
	case "luwrain.braille.error":
	    return environment.getBraille().getErrorMessage();
	case "luwrain.braille.displaywidth":
	    return "" + environment.getBraille().getDisplayWidth();
	case "luwrain.braille.displayheight":
	    return "" + environment.getBraille().getDisplayHeight();
	default:
	    if (propName.startsWith("luwrain.os.") || propName.startsWith("luwrain.hardware."))
	    {
		final String res = environment.os().getProperty(propName);
		return res != null?res:"";
	    }
	    return environment.getCoreProperties().getProperty(propName);
	}
    }
}
