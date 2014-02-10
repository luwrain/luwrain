/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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

//FIXME:ControlEnvironment interface support;

package org.luwrain.core;

//TODO:Tab shift respecting on up-down movements;

import org.luwrain.core.events.*;

public abstract class NavigateArea implements Area
{
    private final String areaBeginMessage = Langs.staticValue(Langs.AREA_BEGIN);
    private final String areaEndMessage = Langs.staticValue(Langs.AREA_END);
    private final String firstLineMessage = Langs.staticValue(Langs.THE_FIRST_LINE);
    private final String lastLineMessage = Langs.staticValue(Langs.THE_LAST_LINE);
    private final String lineEndMessage = Langs.staticValue(Langs.END_OF_LINE);
    private final String emptyLineMessage  = Langs.staticValue(Langs.EMPTY_LINE);

    private int hotPointX = 0;
    private int hotPointY = 0;

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (!event.isCommand() || event.withAlt())
	    return false;
	final int cmd = event.getCommand();
	if (event.withControl() && !event.withAlt() && !event.withShift())
	{
	    //FIXME:left-right over the words;

	    //Ctrl+home;
	    if (cmd == KeyboardEvent.HOME)
	    {
		//No need to call fixHotPoint();
		if (hotPointX < 1 && hotPointY < 1)
		{
		    Speech.say(areaBeginMessage, Speech.PITCH_HIGH);
		    return true;
		}
		hotPointX = 0;
		hotPointY = 0;
		Luwrain.onAreaNewHotPoint(this);
		Speech.say(areaBeginMessage, Speech.PITCH_HIGH);
		return true;
	    }

	    //Ctrl+end;
	    if (cmd == KeyboardEvent.END)
	    {
		fixHotPoint();
		String line = getLine(hotPointY);
		if (line == null)
		    line = new String();
		if (hotPointY + 1 >= getLineCount() && hotPointX >= line.length())
		{
		    Speech.say(areaEndMessage, Speech.PITCH_HIGH);
		    return true;
		}
		line = getLine(getLineCount() - 1);
		if (line == null)
		    line = new String();
		hotPointX = line.length();
		hotPointY = getLineCount() - 1;
		if (hotPointY < 0)//Incorrect getLineCount() behaviour;
		    hotPointY = 0;
		Luwrain.onAreaNewHotPoint(this);
		    Speech.say(areaEndMessage, Speech.PITCH_HIGH);
		    return true;
	    }
	    return false;
	} //If with control;

	//Arrow down;
	if (cmd == KeyboardEvent.ARROW_DOWN)
	{
	    fixHotPoint();
	    if (hotPointY + 1 >= getLineCount())
	    {
		Speech.say(lastLineMessage, Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointY++;
	    String line = getLine(hotPointY);
	    if (line == null)
		line = new String();
	    //FIXME:hotPointX = proper new position respecting tab sequences;
	    if (hotPointX > line.length())
		hotPointX = line.length();
	    Luwrain.onAreaNewHotPoint(this);
	    introduceLine(hotPointY);
	    return true;
	}

	//Arrow up;
	if (cmd == KeyboardEvent.ARROW_UP)
	{
	    fixHotPoint();
	    if (hotPointY == 0)
	    {
		Speech.say(firstLineMessage, Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointY--;
	    String line = getLine(hotPointY);
	    if (line == null)
		line = "";
	    //FIXME:hotPointX = proper position respecting tab sequences;
	    if (hotPointX > line.length())
		hotPointX = line.length();
	    Luwrain.onAreaNewHotPoint(this);
	    introduceLine(hotPointY);
	    return true;
	}

	//Arrow right;
	if (cmd == KeyboardEvent.ARROW_RIGHT)
	{
	    fixHotPoint();
	    if (hotPointX >= getLine(hotPointY).length())
	    {
		if (hotPointY + 1 >= getLineCount())
		{
		    Speech.say(areaEndMessage, Speech.PITCH_HIGH);
		    return true;
		}
		hotPointY++;
		hotPointX = 0;
	    } else
		hotPointX++;
	    Luwrain.onAreaNewHotPoint(this);
	    String line = getLine(hotPointY);
	    if (line == null)
		line = new String();
	    if (hotPointX == line.length())
	    {
		if (hotPointY + 1 == getLineCount())
		    Speech.say(areaEndMessage, Speech.PITCH_HIGH); else
		    Speech.say(lineEndMessage, Speech.PITCH_HIGH);
	    } else
		Speech.sayLetter(line.charAt(hotPointX));
	    return true;
	}

	    //Arrow left;
	if (cmd == KeyboardEvent.ARROW_LEFT)
	{
	    fixHotPoint();
	    if (hotPointX == 0)
	    {
		if (hotPointY == 0)
		{
		    Speech.say(areaBeginMessage, Speech.PITCH_HIGH);
		    return true;
		}
		hotPointY--;
		String line = getLine(hotPointY);
		if (line == null)
		    line = new String();
		hotPointX = line.length();
	    } else
		hotPointX--;
	    Luwrain.onAreaNewHotPoint(this);
	    String line = getLine(hotPointY);
	    if (line == null)
		line = new String();
	    if (hotPointX == line.length())
		Speech.say(lineEndMessage, Speech.PITCH_HIGH); else
		Speech.sayLetter(line.charAt(hotPointX));
	    return true;
	}

	//Home;
	if (cmd == KeyboardEvent.HOME)
	{
	    fixHotPoint();
	    String line = getLine(hotPointY);
	    if (line == null)
		line = new String();
	    if (line.isEmpty())
	    {
		Speech.say(emptyLineMessage, Speech.PITCH_HIGH);
		return true;
	    }
	    if (hotPointX > 0)
	    {
		hotPointX = 0;
		Luwrain.onAreaNewHotPoint(this);
	    } 
	    Speech.sayLetter(line.charAt(0));
	    return true;
	}

	//End;
	if (cmd == KeyboardEvent.END)
	{
	    fixHotPoint();
	    String line = getLine(hotPointY);
	    if (line == null)
		line = new String();
	    if (line.isEmpty())
	    {
		Speech.say(emptyLineMessage, Speech.PITCH_HIGH);
		return true;
	    }
	    if (hotPointX < line.length())
	    {
		hotPointX = line.length();
		Luwrain.onAreaNewHotPoint(this);
	    } 
	    Speech.say(lineEndMessage, Speech.PITCH_HIGH);
	    return true;
	}

	//FIXME:PageUp;
	//FIXME:PageDown;

	return false;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	return false;
    }

    public void introduceLine(int index)
    {
	final String line = getLine(index);
	if (line == null || line.isEmpty())
	    Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH); else
	    Speech.say(line);
    }

    public void setHotPoint(int x,int y)
    {
	if (x < 0 || y < 0)
	    return;
	int newX = x, newY = y;
	if (newY >= getLineCount())
	    newY = getLineCount() - 1;
	if (newY < 0)//With proper getLineCount() never happens;
	    newY = 0;
	String line = getLine(newY);
	if (newX > line.length())
	    newX = line.length();
	if (hotPointX == newX && hotPointY == newY)
	    return;
	hotPointX = newX;
	hotPointY = newY;
	Luwrain.onAreaNewHotPoint(this);
    }

    public int getHotPointX()
    {
	return hotPointX;
    }

    public int getHotPointY()
    {
	return hotPointY;
    }

    protected void fixHotPoint()
    {
	int x = hotPointX, y = hotPointY;
	if (y >= getLineCount())
	    y = getLineCount() - 1;
	if (y < 0)
	    y = 0;
	String line = getLine(y);
	if (line == null)
	    line = new String();
	if (x > line.length())
	    x = line.length();
	setHotPoint(x, y);
    }
}
