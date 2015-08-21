/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

import org.luwrain.core.events.*;

class SecurityAreaWrapper implements Area
{
    private Area area;

    public SecurityAreaWrapper(Area area)
    {
	this.area = area;
	if (area == null)
	    throw new NullPointerException("area may not be null");
    }

    @Override public String getAreaName()
    {
	return area.getAreaName();
    }

    @Override public int getHotPointX()
    {
	return area.getHotPointX();
    }

    @Override public int getHotPointY()
    {
	return area.getHotPointY();
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
	return area.onKeyboardEvent(event);
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
}
