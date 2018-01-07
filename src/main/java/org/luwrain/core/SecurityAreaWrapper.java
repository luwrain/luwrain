/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core;

import org.luwrain.core.events.*;

class SecurityAreaWrapper implements Area
{
    private Area area;

    public SecurityAreaWrapper(Area area)
    {
	this.area = area;
	NullCheck.notNull(area, "area");
    }

    @Override public String getAreaName()
    {
	try {
	    final String res = area.getAreaName();
	    return res != null?res:area.getClass().getName();
	}
	catch(Throwable e)
	{
	    exceptionMsg("getName()", e);
	    e.printStackTrace();
	    return area.getClass().getName();
	}
    }

    @Override public int getHotPointX()
    {
	try {
	    final int res = area.getHotPointX();
	    return res >= 0?res:0;
	}
	catch(Throwable e)
	{
	    exceptionMsg("getHotPointX()", e);
	    e.printStackTrace();
	    return 0;
	}
    }

    @Override public int getHotPointY()
    {
	try {
final int res = area.getHotPointY();
return res >= 0?res:0;
	}
	catch(Throwable e)
	{
	    exceptionMsg("getHotPointY()", e);
	    e.printStackTrace();
	    return 0;
	}
    }

    @Override public int getLineCount()
    {
	try {
	    final int res = area.getLineCount();
	    return res >= 1?res:1;
	}
	catch(Throwable e)
	{
	    exceptionMsg("getLineCount()", e);
	    e.printStackTrace();
	    return 1;
	}
    }

    @Override public String getLine(int index)
    {
	try {
	    final String res = area.getLine(index);
	    return res != null?res:"";
	}
	catch(Throwable e)
	{
	    exceptionMsg("getLine()", e);
	    e.printStackTrace();
	    return "";
	}
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	//FIXME:Exception, but unclear
	return area.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	//FIXME:Exception, but unclear
	return area.onEnvironmentEvent(event);
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	//FIXME:Exception, but unclear
	return area.onAreaQuery(query);
    }

    @Override public Action[] getAreaActions()
    {
	//FIXME:Exception, but unclear
	return area.getAreaActions();
    }

    private void exceptionMsg(String method, Throwable e)
    {
	Log.error("core", "an instance of " + area.getClass().getName() + " has thrown an exception on " + method + ":" + e.getMessage());
    }
}
