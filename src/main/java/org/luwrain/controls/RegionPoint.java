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
import org.luwrain.core.events.*;

public class RegionPoint implements AbstractRegionPoint
{
    protected int hotPointX = -1;
    protected int hotPointY = -1;

    public boolean onSystemEvent(SystemEvent event, int hotPointX, int hotPointY)
    {
	NullCheck.notNull(event, "event");
	if (hotPointX < 0 || hotPointY < 0)
	    throw new IllegalArgumentException("hotPointX and hotPointY must be greater or equal to zero");
	if (event.getType() == SystemEvent.Type.REGULAR)
	    switch(event.getCode())
	    {
	    case REGION_POINT:
		this.hotPointX = hotPointX;
		this.hotPointY = hotPointY;
		return true;
	    }
	return false;
    }

    public boolean isInitialized()
    {
	return hotPointX >= 0 && hotPointY >= 0;
    }

    public void set(int hotPointX, int hotPointY)
    {
	if (hotPointX < 0 || hotPointY < 0)
	    throw new IllegalArgumentException("hotPointX and hotPointY must be greater or equal to zero");
	this.hotPointX = hotPointX;
	this.hotPointY = hotPointY;
    }

    @Override public int getHotPointX()
    {
	return hotPointX;
    }

    @Override public int getHotPointY()
    {
	return hotPointY;
    }

    public void reset()
    {
	hotPointX = -1;
	hotPointY = -1;
    }
}
