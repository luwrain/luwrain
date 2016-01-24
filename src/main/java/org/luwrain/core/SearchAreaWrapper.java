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

import org.luwrain.core.events.*;
import org.luwrain.util.*;

class SearchAreaWrapper implements Area, AreaWrapper
{
    private Area area;
    private Environment environment;
    private AreaWrappingBase wrappingBase;
    private int hotPointX = 0;
    private int hotPointY = 0;
    private String expression = "";//What we already have found

    SearchAreaWrapper(Area area, Environment environment,
			     AreaWrappingBase wrappingBase)
    {
	this.area = area;
	this.environment = environment;
	this.wrappingBase = wrappingBase;
	NullCheck.notNull(area, "area");
	NullCheck.notNull(environment, "environment");
	NullCheck.notNull(wrappingBase, "wrappingBase");
	hotPointX = area.getHotPointX();
	hotPointY = area.getHotPointY();
	if (hotPointX < 0)
	    hotPointX = 0;
	if (hotPointY < 0)
	    hotPointY = 0;
	environment.message("Режим поиска", Luwrain.MESSAGE_REGULAR);
    }

    @Override public String getAreaName()
    {
	return "Режим поиска: " + area.getAreaName();//FIXME:
    }

    @Override public int getHotPointX()
    {
	return hotPointX + expression.length();
    }

    @Override public int getHotPointY()
    {
	return hotPointY;
    }

    @Override public int getLineCount()
    {
	return area.getLineCount();
    }

    @Override public String getLine(int index)
    {
	return area.getLine(index);
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event.isCommand() && !event.isModified())
	    switch(event.getCommand())
	    {
	    case KeyboardEvent.TAB:
		return onNewChar('\0');
	    case KeyboardEvent.ESCAPE:
		closeSearch(false);
		return true;
	    case KeyboardEvent.ENTER:
		return closeSearch(true);
	    case KeyboardEvent.ARROW_LEFT:
	    case KeyboardEvent.ARROW_RIGHT:
	    case KeyboardEvent.ARROW_UP:
	    case KeyboardEvent.ARROW_DOWN:
		return announceCurrentLine();
	    default:
		return false;
	    }
	return onNewChar(event.getCharacter());
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	return area.onEnvironmentEvent(event);
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	return area.onAreaQuery(query);
    }

    @Override public Action[] getAreaActions()
    {
	return area.getAreaActions();
    }

    private boolean onNewChar(char c)
    {
	final String lookFor = c != '\0'?expression + Character.toLowerCase(c):expression;
	if (c == '\0')
	{
	    if (expression.isEmpty())
		return false;
	    ++hotPointX;
	}
	if (hotPointY > getLineCount())
	{
	    environment.playSound(Sounds.MESSAGE_NOT_READY);
	    return true;
	}
	String line = getLine(hotPointY);
	if (line != null && hotPointX <line.length())
	{
	    line = line.toLowerCase();
	    line = line.substring(hotPointX);
	    final int pos = line.indexOf(lookFor);
	    if (pos >= 0)
	    {
		hotPointX += pos;
		expression = lookFor;
		environment.onAreaNewHotPointIface(null, this);
		environment.message(getLine(hotPointY).substring(hotPointX), Luwrain.MESSAGE_REGULAR);
		return true;
	    }
	} //On the current line
	for(int i = hotPointY + 1;i < getLineCount();++i)
	{
	    line = getLine(i);
	    if (line == null)
		continue;
	    line = line.toLowerCase();
	    final int pos = line.indexOf(lookFor);
	    if (pos < 0)
		continue;
	    hotPointX = pos;
	    hotPointY = i;
	    environment.message(line.substring(pos), Luwrain.MESSAGE_REGULAR);
	    expression = lookFor;
	    environment.onAreaNewHotPointIface(null, this);
	    return true;
	}
	environment.playSound(Sounds.MESSAGE_NOT_READY);
	return true;
    }

    private boolean closeSearch(boolean accept)
    {
	if (accept )
	{
	    if (!area.onEnvironmentEvent(new MoveHotPointEvent(hotPointX, hotPointY)))
		return false;
	    environment.setAreaIntroduction();
	} else
	    environment.message("Поиск отменён", Luwrain.MESSAGE_REGULAR);
	wrappingBase.resetReviewWrapper();
	environment.onNewScreenLayout();
	return true;
    }

    private boolean announceCurrentLine()
    {
	if (hotPointY >= area.getLineCount())
	    return false;
	final String line = area.getLine(hotPointY);
	if (line == null)//Security wrapper should make this impossible
	    return false;
	new Luwrain(environment).say(line);//FIXME:
	return true;
    }
}
