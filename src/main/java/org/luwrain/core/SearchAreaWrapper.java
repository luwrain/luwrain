/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core;

import org.luwrain.core.events.*;
import org.luwrain.util.*;

class SearchAreaWrapper implements Area, AreaWrapper
{
    private Area area;
    private Environment environment;
    private AreaWrappingBase wrappingBase;
    private int hotPointX = 0;
    private int hotPointY = 0;

    public SearchAreaWrapper(Area area,
			     Environment environment,
			     AreaWrappingBase wrappingBase)
    {
	this.area = area;
	this.environment = environment;
	this.wrappingBase = wrappingBase;
	NullCheck.notNull(area, "area");
	NullCheck.notNull(environment, "environment");
	NullCheck.notNull(wrappingBase, "wrappingBase");
    }

    @Override public String getAreaName()
    {
	return "Поиск" + area.getAreaName();
    }

    @Override public int getHotPointX()
    {
	return hotPointX;
    }

    @Override public int getHotPointY()
    {
	return hotPointY;
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
	if (event.isCommand() && !event.isModified())
	    switch(event.getCommand())
	    {
	    case KeyboardEvent.ESCAPE:
		closeSearch();
		return true;
	    }
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

    private void closeSearch()
    {
	wrappingBase.resetReviewWrapper();
	environment.onNewScreenLayout();
    }
}
