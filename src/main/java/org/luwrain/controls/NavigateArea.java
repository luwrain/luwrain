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
    //    private final String areaBeginMessage = Langs.staticValue(Langs.AREA_BEGIN);
    //    private final String areaEndMessage = Langs.staticValue(Langs.AREA_END);
    ///    private final String firstLineMessage = Langs.staticValue(Langs.THE_FIRST_LINE);
    //    private final String lastLineMessage = Langs.staticValue(Langs.THE_LAST_LINE);
    //    private final String lineEndMessage = Langs.staticValue(Langs.END_OF_LINE);
    //    private final String emptyLineMessage  = Langs.staticValue(Langs.EMPTY_LINE);

    private CopyCutInfo copyCutInfo;
    private int hotPointX = 0;
    private int hotPointY = 0;

    public NavigateArea(ControlEnvironment environment)
    {
	this.environment = environment;
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
	this.copyCutInfo = new CopyCutInfo(this);
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	if (!event.isCommand() || event.isModified())
	    return false;
	switch (event.getCommand())
	{
	case KeyboardEvent.HOME:
	    return onHome(event);
	case KeyboardEvent.END:
	    return onEnd(event);
	case KeyboardEvent.ALTERNATIVE_HOME:
	    return onAltHome(event);
	case KeyboardEvent.ALTERNATIVE_END:
	    return onAltEnd(event);
	case KeyboardEvent.ARROW_DOWN:
	    return onArrowDown(event);
	case KeyboardEvent.ARROW_UP:
	    return onArrowUp(event);
	case KeyboardEvent.ARROW_RIGHT:
	    return onArrowRight(event);
	case KeyboardEvent.ARROW_LEFT:
	    return onArrowLeft(event);
	//FIXME:PageUp;
	//FIXME:PageDown;
	}
	return false;
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
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

    private boolean onHome(KeyboardEvent event)
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	final String line = getLineNotNull(hotPointY);
	if (line.isEmpty())
	{
	    environment.hint(Hints.EMPTY_LINE);
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

    private boolean onEnd(KeyboardEvent event)
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	final String line = getLineNotNull(hotPointY);
	if (line.isEmpty())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX < line.length())
	{
	    hotPointX = line.length();
	    environment.onAreaNewHotPoint(this);
	} 
	environment.hint(Hints.END_OF_LINE);
	return true;
    }

    private boolean onAltHome(KeyboardEvent event)
    {
	if (hotPointX >= 1 || hotPointY >= 1)
	{
	    hotPointX = 0;
	    hotPointY = 0;
	    environment.onAreaNewHotPoint(this);
	}
	environment.hint(Hints.BEGIN_OF_TEXT);
	return true;
    }

    private boolean onAltEnd(KeyboardEvent event)
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	final String line = getLineNotNull(hotPointY);
	hotPointX = hotPointX <= line.length()?hotPointX:line.length();
	if (hotPointY + 1 < count || hotPointX < line.length())
	{
	    final String lastLine = getLineNotNull(count - 1);
	    hotPointX = lastLine.length();
	    hotPointY = count - 1;
	    environment.onAreaNewHotPoint(this);
	}
	environment.hint(Hints.END_OF_TEXT);
	return true;
    }

    private boolean onArrowDown(KeyboardEvent event)
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	if (hotPointY + 1 >= count)
	{
	    environment.hint(Hints.NO_LINES_BELOW);
	    return true;
	}
	++hotPointY;
	final String nextLine = getLineNotNull(hotPointY);
	//FIXME:do proper next line transition according to possible tab shifts;hotPointX = proper new position respecting tab sequences;
	hotPointX = hotPointX <= nextLine.length()?hotPointX:nextLine.length();
	environment.onAreaNewHotPoint(this);
	introduceLine(hotPointY, nextLine);
	return true;
    }

    private boolean onArrowUp(KeyboardEvent event)
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	if (hotPointY == 0)
	{
	    environment.hint(Hints.NO_LINES_ABOVE);
	    return true;
	}
	--hotPointY;
	final String prevLine = getLineNotNull(hotPointY);
	//FIXME:do proper next line transition according to possible tab shifts;hotPointX = proper new position respecting tab sequences;
	hotPointX = hotPointX <= prevLine.length()?hotPointX:prevLine.length();
	environment.onAreaNewHotPoint(this);
	introduceLine(hotPointY, prevLine);
	return true;
    }

    private boolean onArrowRight(KeyboardEvent event)
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	final String line = getLineNotNull(hotPointY);
	hotPointX = hotPointX <= line.length()?hotPointX:line.length();
	if (hotPointX == line.length())
	{
	    if (hotPointY + 1 >= count)
	    {
		environment.hint(Hints.END_OF_TEXT);
		return true;
	    }
	    ++hotPointY;
	    hotPointX = 0;
	} else
	    ++hotPointX;
	environment.onAreaNewHotPoint(this);
	final 	    String newLine = getLineNotNull(hotPointY);
	if (hotPointX == line.length())
	    environment.hint(hotPointY + 1 == count?Hints.END_OF_TEXT:Hints.END_OF_LINE); else
	    environment.sayLetter(newLine.charAt(hotPointX));
	return true;
    }

    private boolean onArrowLeft(KeyboardEvent event )
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	final String line = getLineNotNull(hotPointY);
	hotPointX = hotPointX <= line.length()?hotPointX:line.length();
	if (hotPointX == 0)
	{
	    if (hotPointY == 0)
	    {
		environment.hint(Hints.BEGIN_OF_TEXT);
		return true;
	    }
	    --hotPointY;
	    final String newLine = getLineNotNull(hotPointY);
	    hotPointX = line.length();
	} else
	    --hotPointX;
	environment.onAreaNewHotPoint(this);
	final String newLine = getLineNotNull(hotPointY);
	if (hotPointX == line.length())
	    environment.hint(Hints.END_OF_LINE); else
	    environment.sayLetter(line.charAt(hotPointX));
	return true;
    }

    public void introduceLine(int index, String line)
    {
	if (line == null || line.isEmpty())
	    environment.hint(Hints.EMPTY_LINE); else
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

    /*
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
    */

    @Override public boolean onCopy(int fromX, int fromY, int toX, int toY)
    {
	if (toY >= getValidLineCount())
	    return false;
	if (fromY == toY)
	{
	    final String line = getLineNotNull(fromY);
	    if (line.isEmpty())
		return false;
	    final int fromPos = fromX < line.length()?fromX:line.length();
	    final int toPos = toX < line.length()?toX:line.length();
	    if (fromPos >= toPos)
		throw new IllegalArgumentException("fromPos should be less than toPos");
	    String[] res = new String[1];
	    res[0] = line.substring(fromPos, toPos);
	    environment.say(res[0]);
	    environment.setClipboard(res);
	    return true;
	}
	Vector<String> res = new Vector<String>();
	String line = getLineNotNull(fromY);
	res.add(line.substring(fromX < line.length()?fromX:line.length()));
	for(int i = fromY + 1;i < toY;++i)
	    res.add(getLineNotNull(i));
	line = getLineNotNull(toY);
	res.add(line.substring(0, toX <line.length()?toX:line.length()));
	environment.say(environment.langStaticString(Langs.COPIED_LINES) + res.size());
	environment.setClipboard(res.toArray(new String[res.size()]));
	return true;
    }

    @Override public boolean onCut(int fromX, int fromY, int toX, int toY)
    {
	return false;
    }

    private int getValidLineCount()
    {
	final int count = getLineCount();
	return count >= 1?count:1;
    }

private String getLineNotNull(int index)
{
    final String line = getLine(index);
    return line != null?line:"";
}
}
