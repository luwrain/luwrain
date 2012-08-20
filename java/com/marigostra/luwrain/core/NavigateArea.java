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

public abstract class NavigateArea implements Area
{
    private int hotPointX = 0;
    private int hotPointY = 0;

    public void onKeyboardEvent(KeyboardEvent event)
    {
	if (!event.isCommand())
	{
	    onUserKeyboardEvent(event);
	    return;
	}
	final int cmd = event.getCommand();
	if (cmd == KeyboardEvent.ARROW_DOWN)
	{
	    if (hotPointY + 1 >= getLineCount())
		return;
	    hotPointY++;
	    if (hotPointX > getLine(hotPointY).length())
		hotPointX = getLine(hotPointY).length();
	    Environment.dispatcher().onAreaNewHotPoint(this, hotPointX, hotPointY);
	    Speech.say(getLine(hotPointY));
	    return;
	}
	if (cmd == KeyboardEvent.ARROW_UP)
	{
	    if (hotPointY == 0)
		return;
	    hotPointY--;
	    if (hotPointX > getLine(hotPointY).length())
		hotPointX = getLine(hotPointY).length();
	    Environment.dispatcher().onAreaNewHotPoint(this, hotPointX, hotPointY);
	    Speech.say(getLine(hotPointY));
	    return;
	}
	if (cmd == KeyboardEvent.ARROW_RIGHT)
	{
	    if (hotPointX >= getLine(hotPointY).length())
	    {
		if (hotPointY + 1 >= getLineCount())
		    return;
		hotPointY++;
		hotPointX = 0;
	    } else
		hotPointX++;
	    Environment.dispatcher().onAreaNewHotPoint(this, hotPointX, hotPointY);
	    if (hotPointX == getLine(hotPointY).length())
		Speech.say(Langs.staticValue(Langs.END_OF_LINE)); else
		Speech.sayLetter(getLine(hotPointY).charAt(hotPointX));
	    return;
	}
	if (cmd == KeyboardEvent.ARROW_LEFT)
	{
	    if (hotPointX == 0)
	    {
		if (hotPointY == 0)
		    return;
		hotPointY--;
		hotPointX = getLine(hotPointY).length();
	    } else
		hotPointX--;
	    Environment.dispatcher().onAreaNewHotPoint(this, hotPointX, hotPointY);
	    if (hotPointX == getLine(hotPointY).length())
		Speech.say(Langs.staticValue(Langs.END_OF_LINE)); else
		Speech.sayLetter(getLine(hotPointY).charAt(hotPointX));
	    return;
	}
	onUserKeyboardEvent(event);
    }

    public void onUserKeyboardEvent(KeyboardEvent event)
    {
	//Nothing here;
    }

    public void newHotPointRequest(int x,int y)
    {
	if (x < 0 || y < 0)
	    return;
	int newX = x, newY = y;
	if (newY >= getLineCount())
	    newY = getLineCount() - 1;
	if (newY < 0)//With proper getLineCount() never happens;
	    newY = 0;
	if (newX > getLine(newY).length())
	    newX = getLine(newY).length();
	hotPointX = newX;
	hotPointY = newY;
	Environment.dispatcher().onAreaNewHotPoint(this, newX, newY);
    }

public int getHotPointX()
    {
	return hotPointX;
    }

public int getHotPointY()
    {
	return hotPointY;
    }
}
