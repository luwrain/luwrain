/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

class DummyArea implements Area
{
    @Override public int getLineCount()
    {
	return 2;
    }

    @Override public String getLine(int index)
    {
	switch(index)
	{
	case 0:
	    return "abc";
	case 1:
	    return "123";
	default:
	    return "";
	}
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event) 
    {
	return false;
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	return false;
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	return false;
    }

    @Override public int getHotPointY()
    {
	return 0;
    }

    @Override public int getHotPointX()
    {
	return 0;
    }

    @Override public String getAreaName()
    {
	return "#name#";
    }

    @Override public Action[] getAreaActions()
    {
	return null;
    }
}
