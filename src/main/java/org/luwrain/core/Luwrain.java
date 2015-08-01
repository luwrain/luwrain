/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

import java.io.File;

import org.luwrain.os.OperatingSystem;

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
public final class Luwrain implements CommandEnvironment,EventConsumer
{
    public static final int PITCH_HIGH = org.luwrain.speech.BackEnd.HIGH;
    public static final int PITCH_NORMAL = org.luwrain.speech.BackEnd.NORMAL;
    public static final int PITCH_LOW = org.luwrain.speech.BackEnd.LOW;
    public static final int PITCH_HINT = 25;
    public static final int PITCH_MESSAGE = 25;
    public static final int RATE_HIGH = org.luwrain.speech.BackEnd.HIGH;
    public static final int RATE_NORMAL = org.luwrain.speech.BackEnd.NORMAL;
    public static final int RATE_LOW = org.luwrain.speech.BackEnd.LOW;

    public static final int MESSAGE_REGULAR = 0;
    public static final int MESSAGE_OK = 1;
    public static final int MESSAGE_ERROR = 2;

    private Environment environment;
    private String charsToSkip = "";

    public Luwrain(Environment environment)
    {
	this.environment = environment;
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
	Registry registry = environment.registry();
	RegistryKeys keys = new RegistryKeys();
	if (registry.getTypeOf(keys.speechCharsToSkip()) == Registry.STRING)
	    charsToSkip = registry.getString(keys.speechCharsToSkip());
    }

    @Override public void enqueueEvent(Event e)
    {
	environment.enqueueEvent(e);
    }

    public     void closeApp()
    {
	environment.closeApp(this);
    }

    //May return -1 if area is not shown on the screen;
    public int getAreaVisibleHeight(Area area)
    {
	return environment.getAreaVisibleHeight(area);
    }

    @Override public String[] getClipboard()
    {
	return environment.getClipboard();
    }

    @Override public Registry getRegistry()
    {
	return environment.registry();
    }

    public Object getSharedObject(String id)
    {
	return environment.getSharedObject(id);
    }

    @Override public void hint(String text)
    {
	say(text, PITCH_HINT);
    }

    @Override public void hint(String text, int code)
    {
	switch (code)
	{
	case Hints.NO_ITEMS_ABOVE:
	    playSound(Sounds.NO_ITEMS_ABOVE);
	    break;
	case Hints.NO_ITEMS_BELOW:
	    playSound(Sounds.NO_ITEMS_BELOW);
	    break;
	case Hints.NO_LINES_ABOVE:
	    playSound(Sounds.NO_LINES_ABOVE);
	    break;
	case Hints.NO_LINES_BELOW:
	    playSound(Sounds.NO_LINES_BELOW);
	    break;
	case Hints.BEGIN_OF_TREE:
	    playSound(Sounds.NO_ITEMS_ABOVE);
	    break;
	case Hints.END_OF_TREE:
	    playSound(Sounds.NO_ITEMS_BELOW);
	    break;
	}
	if (environment.onStandardHint(code))
	    hint(text);
    }

    @Override public boolean hint(int code)
    {
	String msg = "";
	switch (code)
	{
	case Hints.SPACE:
	    msg = i18n().staticStr(LangStatic.SPACE);
	    break;
	case Hints.TAB:
	    msg = i18n().staticStr(LangStatic.TAB);
	    break;
	case Hints.EMPTY_LINE:
	    msg = i18n().staticStr(LangStatic.EMPTY_LINE);
	    break;
	case Hints.BEGIN_OF_LINE:
	    msg = i18n().staticStr(LangStatic.BEGIN_OF_LINE);
	    break;
	case Hints.END_OF_LINE:
	    msg = i18n().staticStr(LangStatic.END_OF_LINE);
	    break;
	case Hints.BEGIN_OF_TEXT:
	    msg = i18n().staticStr(LangStatic.BEGIN_OF_TEXT);
	    break;
	case Hints.END_OF_TEXT:
	    msg = i18n().staticStr(LangStatic.END_OF_TEXT);
	    break;
	case Hints.NO_LINES_ABOVE:
	    msg = i18n().staticStr(LangStatic.NO_LINES_ABOVE);
	    break;
	case Hints.NO_LINES_BELOW:
	    msg = i18n().staticStr(LangStatic.NO_LINES_BELOW);
	    break;
	case Hints.NO_ITEMS_ABOVE:
	    msg = i18n().staticStr(LangStatic.NO_ITEMS_ABOVE);
	    break;
	case Hints.NO_ITEMS_BELOW:
	    msg = i18n().staticStr(LangStatic.NO_ITEMS_BELOW);
	    break;
	case Hints.BEGIN_OF_TREE:
	    msg = i18n().staticStr(LangStatic.BEGIN_OF_TREE);
	    break;
	case Hints.END_OF_TREE:
	    msg = i18n().staticStr(LangStatic.END_OF_TREE);
	    break;
	default:
	    return false;
	}
	hint(msg, code);
	return true;
    }

    @Override public I18n i18n()
    {
	return environment.i18n();
    }

    @Override public void launchApp(String shortcutName)
    {
	environment.launchApp(shortcutName, new String[0]);
    }

    @Override public void launchApp(String shortcutName, String[] args)
    {
	environment.launchApp(shortcutName, args != null?args:new String[0]);
    }

    @Override public LaunchContext launchContext()
    {
	return environment.launchContext();
    }

    @Override public void message(String text)
    {
	environment.message(text, MESSAGE_REGULAR);
    }

    @Override public void message(String text, int semantic)
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
	environment.onAreaNewHotPoint(area);
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
	environment.onAreaNewContent(area);
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
	environment.onAreaNewName(area);
    }

    @Override public void openFile(String fileName)
    {
	String[] s = new String[1];
	s[0] = fileName;
	environment.openFiles(s);
    }

    @Override public void openFiles(String[] fileNames)
    {
	environment.openFiles(fileNames);
    }

    public OperatingSystem os()
    {
	return environment.os();
    }

    @Override public void playSound(int code)
    {
	environment.playSound(code);
    }

    public void popup(Popup popup)
    {
	environment.popup(popup);
    }

    @Override public boolean runCommand(String command)
    {
	return environment.runCommand(command);
    }

    @Override public void say(String text)
    {
	silence();
	if (text != null)
	    environment.speech().say(preprocess(text));
    }

    @Override public void say(String text, int pitch)
    {
	silence();
	if (text != null)
	    environment.speech().say(preprocess(text), pitch);
    }

    @Override public void say(String text,
			      int pitch,
			      int rate)
    {
	silence();
	if (text != null)
	    environment.speech().say(preprocess(text), pitch, rate);
    }

    @Override public void sayLetter(char letter)
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
	    environment.speech().sayLetter(letter);
	} else
	    hint(value); 
    }

    @Override public void sayLetter(char letter, int pitch)
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
	    environment.speech().sayLetter(letter, pitch);
	} else
	    hint(value); 
    }

    @Override public void sayLetter(char letter,
				    int pitch,
				    int rate)
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
	    environment.speech().sayLetter(letter, pitch, rate);
	} else
	    hint(value); 
    }

    @Override public void silence()
    {
	environment.speech().silence();
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
	environment.setActiveArea(this, area);
    }

    @Override public void setClipboard(String[] value)
    {
	environment.setClipboard(value);
    }

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
	return environment.getUniRefInfo(uniRef);
    }

    public boolean openUniRef(String uniRef)
    {
	return environment.openUniRef(uniRef);
    }
}
