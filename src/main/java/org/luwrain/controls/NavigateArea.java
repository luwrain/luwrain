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

package org.luwrain.controls;

//TODO:Tab shift respecting on up-down movements;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain .util.*;

/**
 * The area with basic navigation operations. This abstract class
 * implements the proper behaviour for navigation over the static content
 * of the area. There is no data container and it is implied that the
 * user should implement method {@code getLine()} and {@code
 * getLineCount()}. Supported operations include arrow keys, Page up/Page
 * down and Home/End. The copy to clipboard operation is supported as
 * well.
 *
 * @see SimpleArea
 */
public abstract class NavigateArea implements Area, HotPointControl
{
    protected ControlEnvironment environment;
    private final Region region = new Region(new LinesRegionProvider(this));
    private int hotPointX = 0;
    private int hotPointY = 0;

    public NavigateArea(ControlEnvironment environment)
    {
	this.environment = environment;
	NullCheck.notNull(environment, "environment");
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	if (!event.isCommand() || event.isModified())
	    return false;
	switch (event.getCommand())
	{
	case HOME:
	    return onHome(event);
	case END:
	    return onEnd(event);
	case ALTERNATIVE_HOME:
	    return onAltHome(event);
	case ALTERNATIVE_END:
	    return onAltEnd(event);
	case ARROW_DOWN:
	    return onArrowDown(event);
	case ARROW_UP:
	    return onArrowUp(event);
	case ARROW_RIGHT:
	    return onArrowRight(event);
	case ARROW_LEFT:
	    return onArrowLeft(event);
	case ALTERNATIVE_ARROW_RIGHT:
	    return onAltRight(event);
	case ALTERNATIVE_ARROW_LEFT:
	    return onAltLeft(event);
	case PAGE_DOWN:
	    return onPageDown(event);
	case PAGE_UP:
	    return onPageUp(event);
	}
	return false;
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	switch(event.getCode())
	{
	case MOVE_HOT_POINT:
	    if (event instanceof MoveHotPointEvent)
	    {
		final MoveHotPointEvent moveHotPoint = (MoveHotPointEvent)event;
	    setHotPoint(moveHotPoint.getNewHotPointX(), moveHotPoint.getNewHotPointY());
	    return true;
	    }
	    return false;
	default:
	    return region.onEnvironmentEvent(event, hotPointX, hotPointY);
	}
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	return region.onAreaQuery(query, hotPointX, hotPointY);
    }

    @Override public Action[] getAreaActions()
    {
	return new Action[0];
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
	    if (count == 1)
		environment.hint(environment.staticStr(LangStatic.NO_LINES_BELOW) + " " + getLineNotNull(0), Hints.NO_LINES_BELOW); else
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
	    if (count == 1)
		environment.hint(environment.staticStr(LangStatic.NO_LINES_ABOVE) + " " + getLineNotNull(0), Hints.NO_LINES_ABOVE); else
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
	if (hotPointX == newLine.length())
	    environment.hint(hotPointY + 1 >= count?Hints.END_OF_TEXT:Hints.END_OF_LINE); else
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
	    hotPointX = newLine.length();
	} else
	    --hotPointX;
	environment.onAreaNewHotPoint(this);
	final String newLine = getLineNotNull(hotPointY);
	if (hotPointX == newLine.length())
	    environment.hint(Hints.END_OF_LINE); else
	    environment.sayLetter(newLine.charAt(hotPointX));
	return true;
    }

    private boolean onPageDown(KeyboardEvent event)
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	if (hotPointY + 1 >= count)
	{
	    environment.hint(Hints.NO_LINES_BELOW);
	    return true;
	}
	final int height = environment.getAreaVisibleHeight(this);
	if (hotPointY + height >= count)
	    hotPointY = count - 1; else
	    hotPointY  += height;
	hotPointX = 0;
	environment.onAreaNewHotPoint(this);
	introduceLine(hotPointY, getLineNotNull(hotPointY));
	return true;
    }

    private boolean onPageUp(KeyboardEvent event)
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	if (hotPointY == 0)
	{
	    environment.hint(Hints.NO_LINES_ABOVE);
	    return true;
	}
	final int height = environment.getAreaVisibleHeight(this);
	if (hotPointY > height)
	    hotPointY -= height; else
	    hotPointY = 0;
	hotPointX = 0;
	environment.onAreaNewHotPoint(this);
	introduceLine(hotPointY, getLineNotNull(hotPointY));
	return true;
    }

    private boolean onAltRight(KeyboardEvent event)
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	final String line = getLineNotNull(hotPointY);
	if (line.isEmpty())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	hotPointX = hotPointX <= line.length()?hotPointX:line.length();
	if (hotPointX >= line.length())
	{
	    environment.hint(Hints.END_OF_LINE);
	    return true;
	}
	WordIterator it = new WordIterator(line, hotPointX);
	if (!it.stepForward())
	{
	    environment.hint(Hints.END_OF_LINE);
	    return true;
	}
	hotPointX = it.pos();
	if (it.announce().length() > 0)
	    environment.say(it.announce()); else
	    environment.hint(Hints.END_OF_LINE);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAltLeft(KeyboardEvent event )
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	final String line = getLineNotNull(hotPointY);
	if (line.isEmpty())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	hotPointX = hotPointX <= line.length()?hotPointX:line.length();
	if (hotPointX <= 0)
	{
	    environment.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	WordIterator it = new WordIterator(line, hotPointX);
	if (!it.stepBackward())
	{
	    environment.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	hotPointX = it.pos();
	environment.say(it.announce());
	environment.onAreaNewHotPoint(this);
	return true;
    }

    public void introduceLine(int index, String line)
    {
	if (line == null || line.isEmpty())
	    environment.hint(Hints.EMPTY_LINE); else
	    environment.say(line);
    }

    public void resetState(boolean introduce)
    {
	EnvironmentEvent.resetRegionPoint(this);
	hotPointX = 0;
	hotPointY = 0;
	environment.onAreaNewHotPoint(this);
	if (introduce)
	{
	    final String line = getLineNotNull(0);
	    if (!line.isEmpty())
		introduceLine(0, line); else
		environment.hint(Hints.EMPTY_LINE);
	}
    }

    @Override public void beginHotPointTrans()
    {
	//FIXME:
    }

    @Override public void endHotPointTrans()
    {
	//FIXME:
    }

    /**
     * Sets the hot point to the new position. The provided coordinates are
     * adjusted to real area size and the user may not take care about
     * exceeding area bounds.
     *
     * @param x The x coordinate of the new position
     * @param y The y coordinate of the new position
     */
    public void setHotPoint(int x,int y)
    {
	final int count = getValidLineCount();
	if (y < 0)
	    hotPointY = 0; else
	    if (y >= count)
		hotPointY = count - 1; else
		hotPointY = y;
	final String line = getLineNotNull(hotPointY);
	if (x < 0)
	    hotPointX = 0; else
	    if (x >= line.length())
		hotPointX = line.length(); else
		hotPointX = x;
	environment.onAreaNewHotPoint(this);
    }

    @Override public void setHotPointX(int value)
    {
	final String line = getLineNotNull(hotPointY);
	if (value < 0)
	    hotPointX = 0; else
	    if (value >= line.length())
		hotPointX = line.length(); else
		hotPointX = value;
	environment.onAreaNewHotPoint(this);
    }

    @Override public void setHotPointY(int value)
    {
	final int count = getValidLineCount();
	if (value < 0)
	    hotPointY = 0; else
	    if (value >= count)
		hotPointY = count - 1; else
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

    protected int getValidLineCount()
    {
	final int count = getLineCount();
	return count >= 1?count:1;
    }

    protected String getLineNotNull(int index)
    {
	final String line = getLine(index);
	return line != null?line:"";
    }
}
