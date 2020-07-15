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

public class TestingHotPointControl implements HotPointControl
{
    int x = 0;
    int y = 0;

    @Override public void beginHotPointTrans()
    {
    }

    @Override public void endHotPointTrans()
    {
    }

    @Override public int getHotPointX()
    {
	return x;
    }

    @Override public int getHotPointY()
    {
	return y;
    }

    @Override public void setHotPointX(int value)
    {
	if (value < 0)
	    throw new IllegalArgumentException("value (" + value + ") may not be negative");
	this.x = value;
    }

    @Override public void setHotPointY(int value)
    {
	if (value < 0)
	    throw new IllegalArgumentException("value (" + value + ") may not be negative");
	this.y = value;
    }
}
