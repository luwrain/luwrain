/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.controls;

//TODO:Tab shift respecting on up-down movements;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain .util.*;

/**
 * An area with basic navigation operations. This abstract class
 * implements the usual behaviour for navigation over the static text
 * in the area. There is no data container, so it's implied that
 * user should implement method {@code getLine()} and {@code getLineCount()}.
 * <p>
 * The supported operations include arrow keys, Page up/down
 * and Home/End. The copying to clipboard is supported as well.
 *
 * @see SimpleArea
 */
public abstract class NavigationArea implements Area, HotPointControl, ClipboardTranslator.Provider, RegionTextQueryTranslator.Provider
{
    static private final String LOG_COMPONENT = "core";

    protected final ControlEnvironment context;
    protected final RegionPoint regionPoint = new RegionPoint();
    protected final ClipboardTranslator clipboardTranslator = new ClipboardTranslator(this, regionPoint);
    protected final RegionTextQueryTranslator regionTextQueryTranslator = new RegionTextQueryTranslator(this, regionPoint);
    protected int hotPointX = 0;
    protected int hotPointY = 0;

    public NavigationArea(ControlEnvironment context)
    {
	NullCheck.notNull(context, "context");
	this.context = context;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (!event.isSpecial() || event.isModified())
	    return false;
	switch (event.getSpecial())
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
	if (event.getType() != EnvironmentEvent.Type.REGULAR)
	    return false;
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
	    if (clipboardTranslator.onEnvironmentEvent(event, hotPointX, hotPointY))
		return true;
	    return regionTextQueryTranslator.onEnvironmentEvent(event, hotPointX, hotPointY);
	}
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	return regionTextQueryTranslator.onAreaQuery(query, hotPointX, hotPointY);
    }

    @Override public Action[] getAreaActions()
    {
	return new Action[0];
    }

    protected boolean onHome(KeyboardEvent event)
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	final String line = getLineNotNull(hotPointY);
	if (line.isEmpty())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return true;
	}
	if (hotPointX > 0)
	{
	    hotPointX = 0;
	    context.onAreaNewHotPoint(this);
	} 
	context.sayLetter(line.charAt(0));
	return true;
    }

    protected boolean onEnd(KeyboardEvent event)
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	final String line = getLineNotNull(hotPointY);
	if (line.isEmpty())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return true;
	}
	if (hotPointX < line.length())
	{
	    hotPointX = line.length();
	    context.onAreaNewHotPoint(this);
	} 
	context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	return true;
    }

    protected boolean onAltHome(KeyboardEvent event)
    {
	if (hotPointX >= 1 || hotPointY >= 1)
	{
	    hotPointX = 0;
	    hotPointY = 0;
	    context.onAreaNewHotPoint(this);
	}
	context.setEventResponse(DefaultEventResponse.hint(Hint.BEGIN_OF_TEXT));
	return true;
    }

    protected boolean onAltEnd(KeyboardEvent event)
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
	    context.onAreaNewHotPoint(this);
	}
	context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_TEXT));
	return true;
    }

    protected boolean onArrowDown(KeyboardEvent event)
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	if (hotPointY + 1 >= count)
	{
	    if (count == 1)
		context.setEventResponse(DefaultEventResponse.hint(Hint.NO_LINES_BELOW, context.staticStr(LangStatic.NO_LINES_BELOW) + " " + getLineNotNull(0))); else
		context.setEventResponse(DefaultEventResponse.hint(Hint.NO_LINES_BELOW));
	    return true;
	}
	++hotPointY;
	final String nextLine = getLineNotNull(hotPointY);
	//FIXME:do proper next line transition according to possible tab shifts;hotPointX = proper new position respecting tab sequences;
	hotPointX = hotPointX <= nextLine.length()?hotPointX:nextLine.length();
	context.onAreaNewHotPoint(this);
	announceLine(hotPointY, nextLine);
	return true;
    }

    protected boolean onArrowUp(KeyboardEvent event)
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	if (hotPointY == 0)
	{
	    if (count == 1)
		context.setEventResponse(DefaultEventResponse.hint(Hint.NO_LINES_ABOVE, context.staticStr(LangStatic.NO_LINES_ABOVE) + " " + getLineNotNull(0))); else
		context.setEventResponse(DefaultEventResponse.hint(Hint.NO_LINES_ABOVE));
	    return true;
	}
	--hotPointY;
	final String prevLine = getLineNotNull(hotPointY);
	//FIXME:do proper next line transition according to possible tab shifts;hotPointX = proper new position respecting tab sequences;
	hotPointX = hotPointX <= prevLine.length()?hotPointX:prevLine.length();
	context.onAreaNewHotPoint(this);
	announceLine(hotPointY, prevLine);
	return true;
    }

    protected boolean onArrowRight(KeyboardEvent event)
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	final String line = getLineNotNull(hotPointY);
	hotPointX = hotPointX <= line.length()?hotPointX:line.length();
	if (hotPointX == line.length())
	{
	    if (hotPointY + 1 >= count)
	    {
		context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_TEXT));
		return true;
	    }
	    ++hotPointY;
	    hotPointX = 0;
	} else
	    ++hotPointX;
	context.onAreaNewHotPoint(this);
	final 	    String newLine = getLineNotNull(hotPointY);
	if (hotPointX == newLine.length())
	    context.setEventResponse(DefaultEventResponse.hint(hotPointY + 1 >= count?Hint.END_OF_TEXT:Hint.END_OF_LINE)); else
	    context.sayLetter(newLine.charAt(hotPointX));
	return true;
    }

    protected boolean onArrowLeft(KeyboardEvent event )
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	final String line = getLineNotNull(hotPointY);
	hotPointX = hotPointX <= line.length()?hotPointX:line.length();
	if (hotPointX == 0)
	{
	    if (hotPointY == 0)
	    {
		context.setEventResponse(DefaultEventResponse.hint(Hint.BEGIN_OF_TEXT));
		return true;
	    }
	    --hotPointY;
	    final String newLine = getLineNotNull(hotPointY);
	    hotPointX = newLine.length();
	} else
	    --hotPointX;
	context.onAreaNewHotPoint(this);
	final String newLine = getLineNotNull(hotPointY);
	if (hotPointX == newLine.length())
	    context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE)); else
	    context.sayLetter(newLine.charAt(hotPointX));
	return true;
    }

    protected boolean onPageDown(KeyboardEvent event)
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	if (hotPointY + 1 >= count)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.NO_LINES_BELOW));
	    return true;
	}
	final int height = context.getAreaVisibleHeight(this);
	if (hotPointY + height >= count)
	    hotPointY = count - 1; else
	    hotPointY  += height;
	hotPointX = 0;
	context.onAreaNewHotPoint(this);
	announceLine(hotPointY, getLineNotNull(hotPointY));
	return true;
    }

    protected boolean onPageUp(KeyboardEvent event)
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	if (hotPointY == 0)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.NO_LINES_ABOVE));
	    return true;
	}
	final int height = context.getAreaVisibleHeight(this);
	if (hotPointY > height)
	    hotPointY -= height; else
	    hotPointY = 0;
	hotPointX = 0;
	context.onAreaNewHotPoint(this);
	announceLine(hotPointY, getLineNotNull(hotPointY));
	return true;
    }

    protected boolean onAltRight(KeyboardEvent event)
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	final String line = getLineNotNull(hotPointY);
	if (line.isEmpty())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return true;
	}
	hotPointX = hotPointX <= line.length()?hotPointX:line.length();
	if (hotPointX >= line.length())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	    return true;
	}
	WordIterator it = new WordIterator(line, hotPointX);
	if (!it.stepForward())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	    return true;
	}
	hotPointX = it.pos();
	if (it.announce().length() > 0)
	    context.say(it.announce()); else
	    context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	context.onAreaNewHotPoint(this);
	return true;
    }

    protected boolean onAltLeft(KeyboardEvent event )
    {
	final int count = getValidLineCount();
	hotPointY = hotPointY < count?hotPointY:count - 1;
	final String line = getLineNotNull(hotPointY);
	if (line.isEmpty())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return true;
	}
	hotPointX = hotPointX <= line.length()?hotPointX:line.length();
	if (hotPointX <= 0)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.BEGIN_OF_LINE));
	    return true;
	}
	WordIterator it = new WordIterator(line, hotPointX);
	if (!it.stepBackward())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.BEGIN_OF_LINE));
	    return true;
	}
	hotPointX = it.pos();
	context.say(it.announce());
	context.onAreaNewHotPoint(this);
	return true;
    }

    public void announceLine(int index, String line)
    {
	if (line == null || line.isEmpty())
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE)); else
	    context.say(line);
    }

    public void reset(boolean announce)
    {
	regionPoint.reset();
	hotPointX = 0;
	hotPointY = 0;
	context.onAreaNewHotPoint(this);
	if (announce)
	{
	    final String line = getLineNotNull(0);
	    if (!line.isEmpty())
		announceLine(0, line); else
		context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	}
    }

    /**
     * Redraws content and updates hot point position.
     */
    public void redraw()
    {
	context.onAreaNewContent(this);
	final int lineCount = getValidLineCount();
	if (hotPointY >= lineCount)
	{
	    hotPointY = lineCount - 1;
	    final String line = getLineNotNull(hotPointY);
	    if (hotPointX > line.length())
		hotPointX = line.length();
	    context.onAreaNewHotPoint(this);
	    return;
	}
	final String line = getLineNotNull(hotPointY);
	if (hotPointX > line.length())
	{
	    hotPointX = line.length();
	    context.onAreaNewHotPoint(this);
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
	context.onAreaNewHotPoint(this);
    }

    @Override public void setHotPointX(int value)
    {
	final String line = getLineNotNull(hotPointY);
	if (value < 0)
	    hotPointX = 0; else
	    if (value >= line.length())
		hotPointX = line.length(); else
		hotPointX = value;
	context.onAreaNewHotPoint(this);
    }

    @Override public void setHotPointY(int value)
    {
	final int count = getValidLineCount();
	if (value < 0)
	    hotPointY = 0; else
	    if (value >= count)
		hotPointY = count - 1; else
		hotPointY = value;
	context.onAreaNewHotPoint(this);
    }

    @Override public int getHotPointX()
    {
	return hotPointX;
    }

    @Override public int getHotPointY()
    {
	return hotPointY;
    }

    @Override public String onRegionTextQuery(int fromX, int fromY, int toX, int toY)
    {
	return new LinesRegionTextQueryProvider(this).onRegionTextQuery(fromX, fromY, toX, toY);
    }

    @Override public boolean onClipboardCopyAll()
    {
	return new LinesClipboardProvider(this, ()->context.getClipboard()).onClipboardCopyAll();
    }

    @Override public boolean onClipboardCopy(int fromX, int fromY, int toX, int toY, boolean withDeleting)
    {
	return new LinesClipboardProvider(this, ()->context.getClipboard()).onClipboardCopy(fromX, fromY, toX, toY, withDeleting);
    }

    @Override public boolean onDeleteRegion(int fromX, int fromY, int toX, int toY)
    {
	return new LinesClipboardProvider(this, ()->context.getClipboard()).onDeleteRegion(fromX, fromY, toX, toY);
    }

    protected int getValidLineCount()
    {
	final int count = getLineCount();
	if (count <= 0)
	    throw new RuntimeException("The area of class " + getClass().getName() + " tries to have the number of lines (" + count + ") less than 1");
	return count;
    }

    protected String getLineNotNull(int index)
    {
	final String line = getLine(index);
	return line != null?line:"";
    }
}
