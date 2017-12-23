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

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class EmbeddedSingleLineEdit implements SingleLineEdit.Model
{
    protected final ControlEnvironment context;
    protected final SingleLineEdit edit;
    protected final EmbeddedEditLines lines;
    protected final HotPointControl hotPoint;
    protected final ShiftedRegionPoint regionPoint;
    protected int posX;
    protected int posY;

    /**
     * @param context The control context for this edit
     * @param lines The object to provide and to accept edited text
     * @param hotPoint The object to provide and to set real hot point position in area (without any shift)
     * @param posX The X position of this edit in the area
     * @param posY The Y position of this edit in the area
     */
    public EmbeddedSingleLineEdit(ControlEnvironment context, EmbeddedEditLines lines,
				  HotPointControl hotPoint, 
				  int posX, int posY)
    {
	NullCheck.notNull(context, "context");
	NullCheck.notNull(lines, "lines");
	NullCheck.notNull(hotPoint, "hotPoint");
		if (posX < 0 || posY < 0)
	    throw new IllegalArgumentException("posX (" + posX + ") and posY (" + posY + ") may not be negative");
			this.context = context;
	this.lines = lines;
	this.hotPoint = hotPoint;
	this.posX = posX;
	this.posY = posY;
	this.regionPoint = null;
	edit = new SingleLineEdit(context, this);
    }

    public SingleLineEdit getEditObj()
    {
	return edit;
    }

    public boolean isPosCovered(int x, int y)
    {
	return posY == y && x >= posX;
    }

    public void setNewPos(int x, int y)
    {
	if (x < 0 || y < 0)
	    throw new IllegalArgumentException("x (" + x + ") and y (" + y + ") may not be negative");
	posX = x;
	posY = y;
	if (regionPoint != null)
	    regionPoint.setOffset(x, y);
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	return edit.onKeyboardEvent(event);
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	return edit.onEnvironmentEvent(event);
    }

    public boolean onAreaQuery(AreaQuery query)
    {
	return edit.onAreaQuery(query);
	}

    @Override public String getLine()
    {
	return lines.getEmbeddedEditLine(posX, posY);
    }

    @Override public void setLine(String text)
    {
	lines.setEmbeddedEditLine(posX, posY, text);
    }

    @Override public int getHotPointX()
    {
	int value = hotPoint.getHotPointX();
	return value >= posX?value - posX:0;
    }

    @Override public void setHotPointX(int value)
    {
	hotPoint.setHotPointX(value + posX);
    }

    @Override public String getTabSeq()
    {
	return "\t";//FIXME:
    }

    protected class ShiftedRegionPoint implements AbstractRegionPoint
    {
	protected final AbstractRegionPoint regionPoint;
	protected int offsetX = 0;
	protected int offsetY = 0;

	public ShiftedRegionPoint(AbstractRegionPoint regionPoint)
	{
	    NullCheck.notNull(regionPoint, "regionPoint");
	    this.regionPoint = regionPoint;
	}

	public ShiftedRegionPoint(AbstractRegionPoint regionPoint, int offsetX, int offsetY)
	{
	    NullCheck.notNull(regionPoint, "regionPoint");
	    if (offsetX < 0 || offsetY < 0)
		throw new IllegalArgumentException("offsetX (" + offsetX + ") and offsetY (" + offsetY + ") may not be negative");
	    this.regionPoint = regionPoint;
	    this.offsetX = offsetX;
	    this.offsetY = offsetY;
	}

	@Override public boolean onEnvironmentEvent(EnvironmentEvent event, int hotPointX, int hotPointY)
	{
	    NullCheck.notNull(event, "event");
	    if (hotPointX < 0 || hotPointY < 0)
		throw new IllegalArgumentException("hotPointX and hotPointY must be greater or equal to zero");
	    if (event.getType() == EnvironmentEvent.Type.REGULAR)
		switch(event.getCode())
		{
		case REGION_POINT:
		    set(hotPointX, hotPointY);
		    return true;
		}
	    return false;
	}

	public boolean isInitialized()
	{
	    if (regionPoint.getHotPointY() != offsetY || regionPoint.getHotPointX() < offsetX)
		return false;
	    return regionPoint.isInitialized();
	}

	public void set(int hotPointX, int hotPointY)
	{
	    if (hotPointX < 0 || hotPointY < 0)
		throw new IllegalArgumentException("hotPointX and hotPointY must be greater or equal to zero");
	    regionPoint.set(hotPointX + offsetX, hotPointY + offsetY);
	}

	@Override public int getHotPointX()
	{
	    return Math.max(regionPoint.getHotPointX() - offsetX, 0);
	}

	@Override public int getHotPointY()
	{
	    return Math.max(regionPoint.getHotPointY() - offsetY, 0);
	}

	public void reset()
	{
	    regionPoint.reset();
	}

	public void setOffset(int x, int y)
	{
	    if (x < 0 || y < 0)
		throw new IllegalArgumentException("x (" + x + ") and y (" + y + ") may not be negative");
	    this.offsetX = x;
	    this.offsetY = y;
	}
    }
}
