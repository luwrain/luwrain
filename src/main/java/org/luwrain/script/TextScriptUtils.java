/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

class   This file is part of LUWRAIN.

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

package org.luwrain.script;

import java.util.*;
import java.util.concurrent.atomic.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.script.hooks.*;

public final class TextScriptUtils
{
    static final String LOG_COMPONENT = ScriptUtils.LOG_COMPONENT;

    /*
        static public Object createTextEditHookObject(EditArea editArea)
    {
	NullCheck.notNull(editArea, "editArea");
	return createTextEditHookObject(editArea, editArea.getContent(), editArea, editArea.getRegionPoint());
    }
    */

    static public Object reateTextEditHookObject(Area area, MutableLines lines, HotPointControl hotPoint, AbstractRegionPoint regionPoint)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(lines, "lines");
	NullCheck.notNull(hotPoint, "hotPoint");
	NullCheck.notNull(regionPoint, "regionPoint");
	final HookObject regionObj = createRegionHookObject(hotPoint, regionPoint);
	return new EmptyHookObject(){
	    @Override public Object getMember(String name)
	    {
		NullCheck.notNull(name, "name");
		switch(name)
		{
		case "lines":
		    return new MutableLinesHookObject(lines);
		case "hotPoint":
		    return new HotPointControlHookObject(hotPoint);
		case "regionPoint":
		    return new RegionPointHookObject(regionPoint);
		case "region":
		    return regionObj;
		default:
		    return super.getMember(name);
		}
	    }
	};
    }

    static HookObject createRegionHookObject(HotPoint p1, HotPoint p2)
    {
	final int fromX;
	final int fromY;
	final int toX;
	final int toY;
	if (p1.getHotPointX() < 0 || p1.getHotPointY() < 0 ||
	    p2.getHotPointX() < 0 || p2.getHotPointY() < 0)
	{
	    fromX = -1;
	    fromY = -1;
	    toX = -1;
	    toY = -1;
	} else
	    if (p1.getHotPointY() < p2.getHotPointY())
	    {
		fromX = p1.getHotPointX();
		fromY = p1.getHotPointY();
		toX = p2.getHotPointX();
		toY = p2.getHotPointY();
	    } else
	    	if (p2.getHotPointY() < p1.getHotPointY())
		{
		    fromX = p2.getHotPointX();
		    fromY = p2.getHotPointY();
		    toX = p1.getHotPointX();
		    toY = p1.getHotPointY();
		} else
		{
		    //p1.y == p2.y
		    fromY = p1.getHotPointY();
		    toY = p1.getHotPointY();
		    fromX = Math.min(p1.getHotPointX(), p2.getHotPointX());
		    toX = Math.max(p1.getHotPointX(), p2.getHotPointX());
		}
	return new EmptyHookObject(){
	    @Override public Object getMember(String name)
	    {
		NullCheck.notNull(name, "name");
		switch(name)
		{
		case "fromX":
		    return new Integer(fromX);
		case "fromY":
		    return new Integer(fromY);
		case "toX":
		    return new Integer(toX);
		case "toY":
		    return new Integer(toY);
		default:
		    return super.getMember(name);
		}
	    }
	};
    }

}
