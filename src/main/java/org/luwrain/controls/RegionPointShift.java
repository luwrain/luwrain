/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

public class RegionPointShift implements AbstractRegionPoint
{
    protected final AbstractRegionPoint regionPoint;
    protected int offsetX = 0;
    protected int offsetY = 0;

    public RegionPointShift(AbstractRegionPoint regionPoint, int offsetX, int offsetY)
    {
	NullCheck.notNull(regionPoint, "regionPoint");
	if (offsetX < 0 || offsetY < 0)
	    throw new IllegalArgumentException("offsetX (" + String.valueOf(offsetX) + ") and offsetY (" + String.valueOf(offsetY) + ") must be greater or equal to zero");
	this.regionPoint = regionPoint;
	this.offsetX = offsetX;
	this.offsetY = offsetY;
    }

    @Override public int getHotPointX()
    {
	final int value = regionPoint.getHotPointX();
	return value >= offsetX?value - offsetX:0;
    }

    @Override public int getHotPointY()
    {
	final int value = regionPoint.getHotPointY();
	return value >= offsetY?value - offsetY:0;
    }

    @Override public boolean onSystemEvent(SystemEvent event, int hotPointX, int hotPointY)
    {
	NullCheck.notNull(event, "event");
	return regionPoint.onSystemEvent(event, hotPointX + offsetX, hotPointY + offsetY);
    }

    @Override public boolean isInitialized()
    {
	return regionPoint.isInitialized() && regionPoint.getHotPointX() >= offsetX && regionPoint.getHotPointY() >= offsetY;
    }

    @Override public void set(int hotPointX, int hotPointY)
    {
	regionPoint.set(hotPointX + offsetX, hotPointY + offsetY);
    }

    @Override public void reset()
    {
	regionPoint.reset();
    }

    public int getOffsetX()
    {
	return offsetX;
    }

    public void setOffsetX(int offsetX)
    {
		if (offsetX < 0)
	    throw new IllegalArgumentException("offsetX (" + String.valueOf(offsetX) + ") may not be negative");
	offsetX = offsetX;
    }

    public int getOffsetY()
    {
	return offsetY;
    }

    public void setOffsetY(int offsetY)
    {
	if (offsetY < 0)
	    throw new IllegalArgumentException("offsetY (" + String.valueOf(offsetY) + ") may not be negative");
	this.offsetY = offsetY;
    }

    public void setOffset(int offsetX, int offsetY)
    {
	if (offsetX < 0 || offsetY < 0)
	    throw new IllegalArgumentException("offsetX (" + String.valueOf(offsetX) + ") and offsetY (" + String.valueOf(offsetY) + ") must be greater or equal to zero");
	this.offsetX = offsetX;
	this.offsetY = offsetY;
    }
}
