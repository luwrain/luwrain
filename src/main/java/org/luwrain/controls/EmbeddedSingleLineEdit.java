/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.controls;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class EmbeddedSingleLineEdit implements SingleLineEdit.Model
{
    protected final ControlContext context;
    protected final SingleLineEdit edit;
    protected final EmbeddedEditLines lines;
    protected final HotPointControl hotPoint;
    protected final ShiftedRegionPoint regionPoint;
    protected int offsetX;
    protected int offsetY;

    /**
     * @param context The control context for this edit
     * @param lines The object to provide and to accept edited text
     * @param hotPoint The object to provide and to set real hot point position in area (without any shift)
     * @param regionPOint The object to track region point position
     * @param offsetX The X position of this edit in the area
     * @param offsetY The Y position of this edit in the area
     */
    public EmbeddedSingleLineEdit(ControlContext context, EmbeddedEditLines lines,
				  HotPointControl hotPoint,  AbstractRegionPoint regionPoint,
				  int offsetX, int offsetY)
    {
	NullCheck.notNull(context, "context");
	NullCheck.notNull(lines, "lines");
	NullCheck.notNull(hotPoint, "hotPoint");
	NullCheck.notNull(regionPoint, "regionPoint");
	if (offsetX < 0 || offsetY < 0)
	    throw new IllegalArgumentException("offsetX (" + offsetX + ") and offsetY (" + offsetY + ") may not be negative");
	this.context = context;
	this.lines = lines;
	this.hotPoint = hotPoint;
	this.offsetX = offsetX;
	this.offsetY = offsetY;
	this.regionPoint = new ShiftedRegionPoint(regionPoint, offsetX, offsetY);
	edit = new SingleLineEdit(context, this, this.regionPoint);
    }

    public SingleLineEdit getEditObj()
    {
	return edit;
    }

    public boolean isPosCovered(int x, int y)
    {
	return this.offsetY == y && x >= this.offsetX;
    }

    public void setNewOffset(int x, int y)
    {
	if (x < 0 || y < 0)
	    throw new IllegalArgumentException("x (" + x + ") and y (" + y + ") may not be negative");
	this.offsetX = x;
	this.offsetY = y;
	    this.regionPoint.setOffset(x, y);
    }

    public boolean onInputEvent(InputEvent event)
    {
	return edit.onInputEvent(event);
    }

    public boolean onSystemEvent(EnvironmentEvent event)
    {
	return edit.onSystemEvent(event);
    }

    public boolean onAreaQuery(AreaQuery query)
    {
	return edit.onAreaQuery(query);
	}

    @Override public String getLine()
    {
	return lines.getEmbeddedEditLine(offsetX, offsetY);
    }

    @Override public void setLine(String text)
    {
	lines.setEmbeddedEditLine(offsetX, offsetY, text);
    }

    @Override public int getHotPointX()
    {
	return Math.max(hotPoint.getHotPointX() - offsetX, 0);
    }

    @Override public void setHotPointX(int value)
    {
	hotPoint.setHotPointX(value + offsetX);
    }

    @Override public String getTabSeq()
    {
	return "\t";//FIXME:
    }

    static protected final class ShiftedRegionPoint implements AbstractRegionPoint
    {
	protected final AbstractRegionPoint regionPoint;
	protected int offsetX = 0;
	protected int offsetY = 0;

	ShiftedRegionPoint(AbstractRegionPoint regionPoint, int offsetX, int offsetY)
	{
	    NullCheck.notNull(regionPoint, "regionPoint");
	    if (offsetX < 0 || offsetY < 0)
		throw new IllegalArgumentException("offsetX (" + offsetX + ") and offsetY (" + offsetY + ") may not be negative");
	    this.regionPoint = regionPoint;
	    this.offsetX = offsetX;
	    this.offsetY = offsetY;
	}

	@Override public boolean onSystemEvent(EnvironmentEvent event, int hotPointX, int hotPointY)
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
