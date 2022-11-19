/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

public class HotPointShift implements HotPointControl
{
    protected final HotPointControl control;
    private int offsetX = 0;
    private int offsetY = 0;

    public HotPointShift(HotPointControl control, int offsetX, int offsetY)
    {
	NullCheck.notNull(control, "control");
	if (offsetX < 0)
	    throw new IllegalArgumentException("offsetX can't be negative");
	if (offsetY < 0)
	    throw new IllegalArgumentException("offsetY can't be negative");
	this.control = control;
	this.offsetX = offsetX;
	this.offsetY = offsetY;
    }

    @Override public void beginHotPointTrans()
    {
	control.beginHotPointTrans();
    }

    @Override public void endHotPointTrans()
    {
	control.endHotPointTrans();
    }

    @Override public int getHotPointX()
    {
	final int value = control.getHotPointX();
	return value >= offsetX?value - offsetX:0;
    }

    @Override public void setHotPointX(int value)
    {
	control.setHotPointX(value + offsetX);
    }

    @Override public int getHotPointY()
    {
	final int value = control.getHotPointY();
	return value >= offsetY?value - offsetY:0;
    }

    @Override public void setHotPointY(int value)
    {
	control.setHotPointY(value + offsetY);
    }

    public int getOffsetX() { return offsetX; }
    public int getOffsetY() { return offsetY; }

    public void setOffsetX(int value)
    {
	if (value < 0)
	    throw new IllegalArgumentException("value can't be negative");
	offsetX = value;
    }

    public void setOffsetY(int value)
    {
	if (value < 0)
	    throw new IllegalArgumentException("value can't be negative");
	offsetY = value;
    }
}
