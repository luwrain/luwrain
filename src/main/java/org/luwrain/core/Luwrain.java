/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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
public final class Luwrain
{
    public static final int PITCH_HIGH = org.luwrain.speech.BackEnd.HIGH;
    public static final int PITCH_NORMAL = org.luwrain.speech.BackEnd.NORMAL;
    public static final int PITCH_LOW = org.luwrain.speech.BackEnd.LOW;
    public static final int RATE_HIGH = org.luwrain.speech.BackEnd.HIGH;
    public static final int RATE_NORMAL = org.luwrain.speech.BackEnd.NORMAL;
    public static final int RATE_LOW = org.luwrain.speech.BackEnd.LOW;

    private Environment environment;

    public Luwrain(Environment environment)
    {
	this.environment = environment;
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
    }

    public void enqueueEvent(Event e)
    {
	environment.enqueueEvent(e);
    }

    public void launchApp(Application app)
    {
	environment.launchApp(app);
    }

public     void closeApp()
    {
	environment.closeApp(this);
    }

    //Not for popup areas, only standard areas of applications;
    //Introduces new area in contrast with onAreaNewContent, onAreaNewHotPoint and onAreaNewName  
    public void setActiveArea(Area area)
    {
	environment.setActiveArea(this, area);
    }

    //Never produces any speech output automatically;
    public void onAreaNewHotPoint(Area area)
    {
	environment.onAreaNewHotPoint(area);
    }

    //Never produces any speech output automatically;
    public void onAreaNewContent(Area area)
    {
	environment.onAreaNewContent(area);
    }

    //Never produces any speech output automatically;
    public void onAreaNewName(Area area)
    {
	environment.onAreaNewName(area);
    }

    //May return -1 if area is not shown on the screen;
    public int getAreaVisibleHeight(Area area)
    {
	return environment.getAreaVisibleHeight(area);
    }

    public void quit()
    {
	environment.quit();
    }

    public void message(String text)
    {
	environment.message(text);
    }

    /**
     * @param name The desired popup name, can be null if default value is required
     * @param prefix The desired input prefix, can be null if default value is required
     * @param defaultValue The desired default value, can be null to use the user home directory path
     */
    public File openPopup(String name,
			  String prefix,
			  File defaultValue)
    {
	return environment.openPopup(this, name, prefix, defaultValue);
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

    public void popup(Popup popup)
    {
	environment.popup(popup);
    }

    public Registry getRegistry()
    {
	return environment.registry();
    }

    /*
    public Object getPimManager()
    {
	return environment.getPimManager();
    }
    */

    public void setClipboard(String[] value)
    {
	environment.setClipboard(value);
    }

    public String[] getClipboard()
    {
	return environment.getClipboard();
    }

    public void say(String text)
    {
	silence();
	if (text != null)
	    environment.speech().say(text);
    }

    public void say(String text, int pitch)
    {
	silence();
	if (text != null)
	    environment.speech().say(text, pitch);
    }

    public void say(String text,
		    int pitch,
		    int rate)
    {
	silence();
	if (text != null)
	    environment.speech().say(text, pitch, rate);
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
	final String value = lang().hasSpecialNameOfChar(letter);
	if (value == null)
	{
	    silence();
	    environment.speech().sayLetter(letter);
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
	default:
	    silence();
	    environment.speech().sayLetter(letter, pitch);
	}
    }

    public void sayLetter(char letter,
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
	default:
	    silence();
	    environment.speech().sayLetter(letter, pitch, rate);
	}
    }

    public void hint(String text)
    {
	say(text, PITCH_LOW);
    }

    public void hint(String text, int code)
    {
	if (environment.onStandardHint(code))
	    hint(text);
    }

    public boolean hint(int code)
    {
	String msg = "";
	switch (code)
	{
	case Hints.SPACE:
	    msg = lang().staticStr(LangStatic.SPACE);
	    break;
	case Hints.TAB:
	    msg = lang().staticStr(LangStatic.TAB);
	    break;
	case Hints.EMPTY_LINE:
	    msg = lang().staticStr(LangStatic.EMPTY_LINE);
	    break;
	case Hints.BEGIN_OF_LINE:
	    msg = lang().staticStr(LangStatic.BEGIN_OF_LINE);
	    break;
	case Hints.END_OF_LINE:
	    msg = lang().staticStr(LangStatic.END_OF_LINE);
	    break;
	case Hints.BEGIN_OF_TEXT:
	    msg = lang().staticStr(LangStatic.BEGIN_OF_TEXT);
	    break;
	case Hints.END_OF_TEXT:
	    msg = lang().staticStr(LangStatic.END_OF_TEXT);
	    break;
	case Hints.NO_LINES_ABOVE:
	    msg = lang().staticStr(LangStatic.NO_LINES_ABOVE);
	    break;
	case Hints.NO_LINES_BELOW:
	    msg = lang().staticStr(LangStatic.NO_LINES_BELOW);
	    break;
	case Hints.NO_ITEMS_ABOVE:
	    msg = lang().staticStr(LangStatic.NO_ITEMS_ABOVE);
	    break;
	case Hints.NO_ITEMS_BELOW:
	    msg = lang().staticStr(LangStatic.NO_ITEMS_BELOW);
	    break;
	default:
	    return false;
	}
	hint(msg, code);
	return true;
    }

    public void silence()
    {
	environment.speech().silence();
    }

    public String staticString(int code)
    {
	return lang().staticStr(code);
    }

    public Lang lang()
    {
	return environment.lang();
    }

    public void playSound(int code)
    {
	environment.playSound(code);
    }
}
