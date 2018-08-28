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

//LWR_API 1.0

//LWR_API 1.0

package org.luwrain.controls;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class CenteredArea implements org.luwrain.core.Area
{
    protected final ControlEnvironment context;
    protected String areaName = "";
    protected int screenWidth;
    protected int screenHeight;

    protected String[] lines = new String[0];
    protected int hotPointX = 0;
    protected int hotPointY = 0;

    public CenteredArea(ControlEnvironment context)
    {
	NullCheck.notNull(context, "context");
	this.context = context;
	this.screenWidth = context.getScreenWidth();
	this.screenHeight = context.getScreenHeight();
    }

    public CenteredArea(ControlEnvironment context, String areaName)
    {
	NullCheck.notNull(context, "context");
	NullCheck.notNull(areaName, "areaName");
	this.context = context;
	this.areaName = areaName;
	this.screenWidth = context.getScreenWidth();
	this.screenHeight = context.getScreenHeight();
    }

    @Override public int getHotPointX()
    {
	return getLeft() + hotPointX;
    }

    @Override public int getHotPointY()
    {
	return getTop() + hotPointY;
    }

    @Override public int getLineCount()
    {
	final int res = getTop() + lines.length;
	return res > 0?res:1;
    }

    @Override public String getLine(int index)
    {
	if (index < 0)
	    throw new IllegalArgumentException("index (" + index + ") may not be negative");
	final int top = getTop();
	if (index < top)
	    return "";
	if (index >= top + lines.length)
	    return "";
	final int localIndex = index - top;
	final int left = getLeft();
	final StringBuilder b = new StringBuilder();
	for(int i = 0;i < left;++i)
	    b.append(" ");
	return new String(b) + lines[localIndex];
    }

    @Override public String getAreaName()
    {
	return areaName;
    }

    public void setAreaName(String newName)
    {
	NullCheck.notNull(newName, "newName");
	this.areaName = areaName;
	context.onAreaNewName(this);
    }

    @Override public boolean onInputEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	return false;
    }

    @Override public boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getCode() == EnvironmentEvent.Code.FONT_SIZE_CHANGED)
	{
	    this.screenWidth = context.getScreenWidth();
	    this.screenHeight = context.getScreenHeight();
	    context.onAreaNewContent(this);
	    context.onAreaNewHotPoint(this);
	    return true;
	}
	return false;
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	return false;
    }

    @Override public Action[] getAreaActions()
    {
	return new Action[0];
    }

    public void setLines(String[] newLines)
    {
	NullCheck.notNullItems(newLines, "newLines");
	this.lines = newLines;
	context.onAreaNewContent(this);
	context.onAreaNewHotPoint(this);
    }

    public String[] getLines()
    {
	return lines.clone();
    }

    public int getLocalHotPointX()
    {
	return hotPointX;
    }

    public int getLocalHotPointY()
    {
	return hotPointY;
    }

    public void setLocalHotPointX(int value)
    {
	if (value < 0)
	    throw new IllegalArgumentException("value (" + value + ") may not be negative");
	this.hotPointX = value;
	context.onAreaNewHotPoint(this);
    }

    public void setLocalHotPointY(int value)
    {
	if (value < 0)
	    throw new IllegalArgumentException("value (" + value + ") may not be negative");
	this.hotPointY = value;
	context.onAreaNewHotPoint(this);
    }

    public int getMaxLineLen()
    {
	int res = 0;
	for(String s: lines)
	    if (res < s.length())
		res = s.length();
	return res;
    }

    public int getLeft()
    {
	final int res = (screenWidth / 2) - (getMaxLineLen() / 2);
	return res >= 0?res:0;
    }

    public int getTop()
    {
	final int res = (screenHeight / 2) - (lines.length / 2);
	return res >= 0?res:0;
    }
}
