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

package org.luwrain.controls;

//TODO:Tab shift respecting on up-down movements;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain .util.*;

public abstract class NavigateArea implements Area, HotPointInfo, CopyCutRequest
{
    private ControlEnvironment environment;
    private final String areaBeginMessage = Langs.staticValue(Langs.AREA_BEGIN);
    private final String areaEndMessage = Langs.staticValue(Langs.AREA_END);
    private final String firstLineMessage = Langs.staticValue(Langs.THE_FIRST_LINE);
    private final String lastLineMessage = Langs.staticValue(Langs.THE_LAST_LINE);
    private final String lineEndMessage = Langs.staticValue(Langs.END_OF_LINE);
    private final String emptyLineMessage  = Langs.staticValue(Langs.EMPTY_LINE);

    private CopyCutInfo copyCutInfo;
    private int hotPointX = 0;
    private int hotPointY = 0;

    public NavigateArea(ControlEnvironment environment)
    {
	this.environment = environment;
	this.copyCutInfo = new CopyCutInfo(this);
    }

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
		    environment.say(areaBeginMessage);
		    return true;
		}
		hotPointX = 0;
		hotPointY = 0;
		environment.onAreaNewHotPoint(this);
		environment.say(areaBeginMessage);
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
		    environment.say(areaEndMessage);
		    return true;
		}
		line = getLine(getLineCount() - 1);
		if (line == null)
		    line = new String();
		hotPointX = line.length();
		hotPointY = getLineCount() - 1;
		if (hotPointY < 0)//Incorrect getLineCount() behaviour;
		    hotPointY = 0;
		environment.onAreaNewHotPoint(this);
		    environment.say(areaEndMessage);
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
		environment.say(lastLineMessage);
		return true;
	    }
	    hotPointY++;
	    String line = getLine(hotPointY);
	    if (line == null)
		line = new String();
	    //FIXME:hotPointX = proper new position respecting tab sequences;
	    if (hotPointX > line.length())
		hotPointX = line.length();
	    environment.onAreaNewHotPoint(this);
	    introduceLine(hotPointY);
	    return true;
	}

	//Arrow up;
	if (cmd == KeyboardEvent.ARROW_UP)
	{
	    fixHotPoint();
	    if (hotPointY == 0)
	    {
		environment.say(firstLineMessage);
		return true;
	    }
	    hotPointY--;
	    String line = getLine(hotPointY);
	    if (line == null)
		line = "";
	    //FIXME:hotPointX = proper position respecting tab sequences;
	    if (hotPointX > line.length())
		hotPointX = line.length();
	    environment.onAreaNewHotPoint(this);
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
		    environment.say(areaEndMessage);
		    return true;
		}
		hotPointY++;
		hotPointX = 0;
	    } else
		hotPointX++;
	    environment.onAreaNewHotPoint(this);
	    String line = getLine(hotPointY);
	    if (line == null)
		line = new String();
	    if (hotPointX == line.length())
	    {
		if (hotPointY + 1 == getLineCount())
		    environment.say(areaEndMessage); else
		    environment.say(lineEndMessage);
	    } else
		environment.sayLetter(line.charAt(hotPointX));
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
		    environment.say(areaBeginMessage);
		    return true;
		}
		hotPointY--;
		String line = getLine(hotPointY);
		if (line == null)
		    line = new String();
		hotPointX = line.length();
	    } else
		hotPointX--;
	    environment.onAreaNewHotPoint(this);
	    String line = getLine(hotPointY);
	    if (line == null)
		line = new String();
	    if (hotPointX == line.length())
		environment.say(lineEndMessage); else
		environment.sayLetter(line.charAt(hotPointX));
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
		environment.say(emptyLineMessage);
		return true;
	    }
	    if (hotPointX > 0)
	    {
		hotPointX = 0;
		environment.onAreaNewHotPoint(this);
	    } 
	    environment.sayLetter(line.charAt(0));
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
		environment.say(emptyLineMessage);
		return true;
	    }
	    if (hotPointX < line.length())
	    {
		hotPointX = line.length();
		environment.onAreaNewHotPoint(this);
	    } 
	    environment.say(lineEndMessage);
	    return true;
	}

	//FIXME:PageUp;
	//FIXME:PageDown;

	return false;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	switch(event.getCode())
	{
	case EnvironmentEvent.COPY_CUT_POINT:
	    return copyCutInfo.doCopyCutPoint(hotPointX, hotPointY);
	case EnvironmentEvent.COPY:
	    return copyCutInfo.doCopy(hotPointX, hotPointY);
	default:
	    return false;
	}
    }

    public void introduceLine(int index)
    {
	final String line = getLine(index);
	if (line == null || line.isEmpty())
	    environment.say(Langs.staticValue(Langs.EMPTY_LINE)); else
	    environment.say(line);
    }

    public void setHotPoint(int x,int y)
    {
	if (x >= 0)
	    hotPointX = x;
	if (y >= 0)
	    hotPointY = y;
	environment.onAreaNewHotPoint(this);
    }

    @Override public void setHotPointX(int value)
    {
	if (value < 0)
	    return;
	hotPointX = value;
	environment.onAreaNewHotPoint(this);
    }

    @Override public void setHotPointY(int value)
    {
	if (value < 0)
	    return;
	hotPointY = value;
	environment.onAreaNewHotPoint(this);
    }

    @Override public int getHotPointX()
    {
	return hotPointX;
    }

    @Override public int getHotPointY()
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

    @Override public boolean onCopy(int fromX, int fromY, int toX, int toY)
    {
	if (toY >= getLineCount())
	    return false;
	if (fromY == toY)
	{
	    final String line = getLine(fromY);
	    if (line == null || line.isEmpty())
		return false;
	    int fromPos = fromX < line.length()?fromX:line.length();
	    int toPos = toX < line.length()?toX:line.length();
	    if (fromPos >= toPos)
		return false;
	    String[] res = new String[1];
	    res[0] = line.substring(fromPos, toPos);
	    environment.say(res[0]);
	    environment.setClipboard(res);
	    return true;
	}
	Vector<String> res = new Vector<String>();
	String line = getLine(fromY);
	if (line == null)
	    return false;
	res.add(line.substring(fromX < line.length()?fromX:line.length()));
	for(int i = fromY + 1;i < toY;++i)
	{
	    line = getLine(i);
	    if (line == null)
		return false;
	    res.add(line);
	}
	line = getLine(toY);
	if (line == null)
	    return false;
	res.add(line.substring(0, toX <line.length()?toX:line.length()));
	environment.say(Langs.staticValue(Langs.COPIED_LINES) + res.size());
	environment.setClipboard(res.toArray(new String[res.size()]));
	return true;
    }

    @Override public boolean onCut(int fromX, int fromY, int toX, int toY)
    {
	return false;
    }
}
