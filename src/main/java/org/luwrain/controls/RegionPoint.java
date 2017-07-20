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

public class RegionPoint implements HotPoint
{
    protected int hotPointX = -1;
    protected int hotPointY = -1;

    public boolean onEnvironmentEvent(EnvironmentEvent event, int hotPointX, int hotPointY)
    {
	NullCheck.notNull(event, "event");
	if (hotPointX < 0 || hotPointY < 0)
	    throw new IllegalArgumentException("hotPointX and hotPointY must be greater or equal to zero");
	if (event.getType() == EnvironmentEvent.Type.REGULAR)
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
}
