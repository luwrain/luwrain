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


import java.util.Set;
import java.io.File;

import org.luwrain.os.OperatingSystem;
import org.luwrain.hardware.*;
import org.luwrain.core.events.*;
import org.luwrain.speech.Channel;

/**
 * The main gate to Luwrain core for applications. This class is the
 * single point which all applications have for the access to system
 * functions.  All other classes considered as parts of Luwrain API
 * (e.g. from packages {@code org.luwrain.controls} or {@code
 * org.luwrain.popups}) only wrap the object of this class. In other
 * words, this class is an interface for interaction with environment
 * features.
 * <p>
 * On every application launch environment creates new instance of this
 * class which is provided to the application object through {@code
 * Application.onLaunch()} method.  All methods of this class dealing with the
 * environment always give the {@code this} reference. Therefore, the
 * environment is always aware which application the request is come
 * from. With this behaviour {@code Luwrain} class does the
 * identification function and applications should try to keep the reference
 * to this class in secret.
 */
public final class Luwrain implements EventConsumer
{
    public enum ReloadComponents {
	ENVIRONMENT_SOUNDS,
};

    public static final int PITCH_HIGH = 25;
    public static final int PITCH_NORMAL = 0;
    public static final int PITCH_LOW = -25;
    public static final int PITCH_HINT = -25;
    public static final int PITCH_MESSAGE = -25;

    /*
    public static final int RATE_HIGH = org.luwrain.speech.BackEnd.HIGH;
    public static final int RATE_NORMAL = org.luwrain.speech.BackEnd.NORMAL;
    public static final int RATE_LOW = org.luwrain.speech.BackEnd.LOW;
    */

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

    private Environment environment;
    private String charsToSkip = "";

    public Luwrain(Environment environment)
    {
	this.environment = environment;
	NullCheck.notNull(environment, "environment");
	Registry registry = environment.registry();
	RegistryKeys keys = new RegistryKeys();
	if (registry.getTypeOf(keys.speechCharsToSkip()) == Registry.STRING)
	    charsToSkip = registry.getString(keys.speechCharsToSkip());
    }

    public HeldData currentAreaRegion(boolean issueErrorMessages)
    {
	return environment.currentAreaRegionIface(issueErrorMessages);
    }

    //Never returns null, returns user home dir if area doesn't speak about that
    public String currentAreaDir()
    {
	return environment.currentAreaDirIface();
    }


    @Override public void enqueueEvent(Event e)
    {
	if (e != null && (e instanceof ThreadSyncEvent))
	{
	    final ThreadSyncEvent threadSync = (ThreadSyncEvent)e;
	    threadSync.setInstanceObj(this);
	}
	environment.enqueueEvent(e);
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
	return environment.getSharedObjectIface(id);
    }

    public void hint(String text)
    {
	say(text, PITCH_HINT);
    }

    public void hint(String text, int code)
    {
	final int soundId = Hints.hintToSoundMap(code);
	if (soundId >= 0)
	    playSound(soundId);
	if (environment.onStandardHint(code))
	    hint(text);
    }

    public boolean hint(int code)
    {
	final int staticStrNum = Hints.hintToStaticStrMap(code);
	if (staticStrNum < 0)
	    return false;
	hint(i18n().staticStr(staticStrNum), code);
	return true;
    }

    public I18n i18n()
    {
	return environment.i18nIface();
    }

    public void launchApp(String shortcutName)
    {
	environment.launchAppIface(shortcutName, new String[0]);
    }

    public void launchApp(String shortcutName, String[] args)
    {
	environment.launchAppIface(shortcutName, args != null?args:new String[0]);
    }

    public LaunchContext launchContext()
    {
	return environment.launchContextIface();
    }

    public void message(String text)
    {
	environment.message(text, MESSAGE_REGULAR);
    }

    public void message(String text, int semantic)
    {
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
	environment.onAreaNewNameIface(this, area);
    }

    //May return -1 if area is not shown on the screen;
    public int getAreaVisibleHeight(Area area)
    {
	return environment.getAreaVisibleHeightIface(this, area);
    }

    public int getAreaVisibleWidth(Area area)
    {
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

    public void onNewAreaLayout()
    {
	environment.onNewAreaLayoutIface(this);
    }

    public void openFile(String fileName)
    {
	String[] s = new String[1];
	s[0] = fileName;
	environment.openFiles(s);
    }

    public void openFiles(String[] fileNames)
    {
	environment.openFiles(fileNames);
    }

    public OperatingSystem os()
    {
	return environment.os();
    }

    public void playSound(int code)
    {
	environment.playSound(code);
    }

    public void popup(Popup popup)
    {
	environment.popupIface(popup);
    }

    public boolean runCommand(String command)
    {
	return environment.runCommand(command);
    }

    public void say(String text)
    {
	silence();
	environment.getSpeech().speak(preprocess(text), 0, 0);
    }

    public void say(String text, int pitch)
    {
	silence();
	if (text != null)
	    environment.getSpeech().speak(preprocess(text), pitch, 0);
    }

    public void say(String text,
			      int pitch, int rate)
    {
	silence();
	if (text != null)
	    environment.getSpeech().speak(preprocess(text), pitch, rate);
    }

    public void sayLetter(char letter)
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
	{
	    silence();
	    environment.getSpeech().speakLetter(letter, 0, 0);
	} else
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
	{
	    silence();
	    environment.getSpeech().speakLetter(letter, pitch, 0);
	} else
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
	{
	    silence();
	    environment.getSpeech().speakLetter(letter, pitch, rate);
	} else
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
	environment.setActiveAreaIface(this, area);
    }

    /*
    public void setClipboard(String[] value)
    {
	environment.setClipboard(value);
    }
    */

    private String staticString(int code)
    {
	return i18n().staticStr(code);
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

    public UniRefInfo getUniRefInfo(String uniRef)
    {
	return environment.getUniRefInfoIface(uniRef);
    }

    public boolean openUniRef(String uniRef)
    {
	return environment.openUniRefIface(uniRef);
    }

    public Hardware getHardware()
    {
	return environment.getHardware();
    }

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



    public org.luwrain.player.Player getPlayer()
    {
	return environment.getPlayer();
    }

    public void runInMainThread(Runnable runnable)
    {
	NullCheck.notNull(runnable, "runnable");
environment.enqueueEvent(new RunnableEvent(runnable));
    }

    public void reloadComponent(ReloadComponents component)
    {
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
}
