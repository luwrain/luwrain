/*
   Copyright 2012 Michael Pozhidaev <msp@altlinux.org>

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

package com.marigostra.luwrain.core;

import com.marigostra.luwrain.core.events.*;

public abstract class NavigateArea implements Area
{
    private int hotPointX = 0;
    private int hotPointY = 0;

    public void onKeyboardEvent(KeyboardEvent event)
    {
	if (!event.isCommand() || event.withAlt())
	{
	    onUserKeyboardEvent(event);
	    return;
	}
	final int cmd = event.getCommand();
	if (event.withControl() && !event.withAlt() && !event.withShift())
	{
	    //FIXME:left-right over the words;
	    if (cmd == KeyboardEvent.HOME)
	    {
		//No need to call fixHotPoint();
		if (hotPointX < 1 && hotPointY < 1)
		{
		    Speech.say(Langs.staticValue(Langs.AREA_BEGIN), Speech.PITCH_HIGH);
		    return;
		}
		hotPointX = 0;
		hotPointY = 0;
		Dispatcher.onAreaNewHotPoint(this);
		Speech.say(Langs.staticValue(Langs.AREA_BEGIN), Speech.PITCH_HIGH);
		return;
	    }
	    if (cmd == KeyboardEvent.END)
	    {
		fixHotPoint();
		String line = getLine(hotPointY);
		if (line == null)
		    line = new String();
		if (hotPointY + 1 == getLineCount() && hotPointX == line.length())
		{
		    Speech.say(Langs.staticValue(Langs.AREA_END), Speech.PITCH_HIGH);
		    return;
		}
		hotPointX = line.length();
		hotPointY = getLineCount() - 1;
		if (hotPointY < 0)
		    hotPointY = 0;
		Dispatcher.onAreaNewHotPoint(this);
		    Speech.say(Langs.staticValue(Langs.AREA_END), Speech.PITCH_HIGH);
		    return;
	    }
	    onUserKeyboardEvent(event);
	    return;
	}
	if (cmd == KeyboardEvent.ARROW_DOWN)
	{
	    fixHotPoint();
	    if (hotPointY + 1 >= getLineCount())
	    {
		Speech.say(Langs.staticValue(Langs.THE_LAST_LINE), Speech.PITCH_HIGH);
		return;
	    }
	    hotPointY++;
	    String line = getLine(hotPointY);
	    if (line == null)
		line = new String();
	    if (hotPointX > line.length())
		hotPointX = line.length();
	    Dispatcher.onAreaNewHotPoint(this);
	    introduceLine(hotPointY);
	    return;
	}
	if (cmd == KeyboardEvent.ARROW_UP)
	{
	    fixHotPoint();
	    if (hotPointY == 0)
	    {
		Speech.say(Langs.staticValue(Langs.THE_FIRST_LINE), Speech.PITCH_HIGH);
		return;
	    }
	    hotPointY--;
	    String line = getLine(hotPointY);
	    if (line == null)
		line = "";
	    if (hotPointX > line.length())
		hotPointX = line.length();
	    Dispatcher.onAreaNewHotPoint(this);
	    introduceLine(hotPointY);
	    return;
	}
	if (cmd == KeyboardEvent.ARROW_RIGHT)
	{
	    fixHotPoint();
	    if (hotPointX >= getLine(hotPointY).length())
	    {
		if (hotPointY + 1 >= getLineCount())
		{
		    Speech.say(Langs.staticValue(Langs.AREA_END), Speech.PITCH_HIGH);
		    return;
		}
		hotPointY++;
		hotPointX = 0;
	    } else
		hotPointX++;
	    Dispatcher.onAreaNewHotPoint(this);
	    String line = getLine(hotPointY);
	    if (line == null)
		line = new String();
	    if (hotPointX == line.length())
	    {
		if (hotPointY + 1 == getLineCount())
		    Speech.say(Langs.staticValue(Langs.AREA_END), Speech.PITCH_HIGH); else
		    Speech.say(Langs.staticValue(Langs.END_OF_LINE), Speech.PITCH_HIGH);
								      } else
		Speech.sayLetter(line.charAt(hotPointX));
	    return;
	}
	if (cmd == KeyboardEvent.ARROW_LEFT)
	{
	    fixHotPoint();
	    if (hotPointX == 0)
	    {
		if (hotPointY == 0)
		{
		    Speech.say(Langs.staticValue(Langs.AREA_BEGIN), Speech.PITCH_HIGH);
		    return;
		}
		hotPointY--;
		String line = getLine(hotPointY);
		if (line == null)
		    line = new String();
		hotPointX = line.length();
	    } else
		hotPointX--;
	    Dispatcher.onAreaNewHotPoint(this);
	    String line = getLine(hotPointY);
	    if (line == null)
		line = new String();
	    if (hotPointX == line.length())
		Speech.say(Langs.staticValue(Langs.END_OF_LINE), Speech.PITCH_HIGH); else
		Speech.sayLetter(line.charAt(hotPointX));
	    return;
	}
	if (cmd == KeyboardEvent.HOME)
	{
	    fixHotPoint();
	    String line = getLine(hotPointY);
	    if (line == null)
		line = new String();
	    if (line.isEmpty())
	    {
		Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
		return;
	    }
	    if (hotPointX > 0)
	    {
		hotPointX = 0;
		Dispatcher.onAreaNewHotPoint(this);
	    } 
	    Speech.sayLetter(line.charAt(0));
	    return;
	}
	if (cmd == KeyboardEvent.END)
	{
	    fixHotPoint();
	    String line = getLine(hotPointY);
	    if (line == null)
		line = new String();
	    if (line.isEmpty())
	    {
		Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
		return;
	    }
	    if (hotPointX < line.length())
	    {
		hotPointX = line.length();
		Dispatcher.onAreaNewHotPoint(this);
	    } 
	    Speech.say(Langs.staticValue(Langs.END_OF_LINE), Speech.PITCH_HIGH);
	    return;
	}
	//FIXME:PageUp;
	//FIXME:PageDown;

	onUserKeyboardEvent(event);
    }

    public void onUserKeyboardEvent(KeyboardEvent event)
    {
	//Nothing here;
    }

    public void introduceLine(int index)
    {
	String line = getLine(index);
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
	Dispatcher.onAreaNewHotPoint(this);
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
	String line = getLine(y);
	if (line == null)
	    line = new String();
	if (x > line.length())
	    x = line.length();
	setHotPoint(x, y);
    }
}
