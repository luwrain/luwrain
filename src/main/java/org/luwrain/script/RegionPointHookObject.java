/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.script;

import java.util.*;
import java.util.function.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;

public class RegionPointHookObject extends EmptyHookObject
{
    protected final AbstractRegionPoint regionPoint;
    protected final boolean readOnly;

    public RegionPointHookObject(AbstractRegionPoint regionPoint, boolean readOnly)
    {
	NullCheck.notNull(regionPoint, "regionPoint");
	this.regionPoint = regionPoint;
	this.readOnly = readOnly;
    }

        public RegionPointHookObject(AbstractRegionPoint regionPoint)
    {
	this(regionPoint, false);
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notEmpty(name, "name");
	switch(name)
	{
	case "x":
	    return new Integer(regionPoint.getHotPointX());
	case "y":
	    return new Integer(regionPoint.getHotPointY());
	case "initialized":
	    return new Boolean(regionPoint.isInitialized());
	default:
	    return super.getMember(name);
	}
    }

    /*
    @Override public void setMember(String name, Object value)
    {
	NullCheck.notEmpty(name, "name");
	NullCheck.notNull(value, "value");
	switch(name)
	{
	case "x":
	    {
			final Number numValue = ScriptUtils.getNumberValue(value);
	if (numValue == null || numValue.intValue() < 0)
	    return;
	    hotPoint.setHotPointX(numValue.intValue());
	    }
	    return;
	case "y":
	    {
			final Number numValue = ScriptUtils.getNumberValue(value);
	if (numValue == null || numValue.intValue() < 0)
	    return;
	    hotPoint.setHotPointY(numValue.intValue());
	    }
	    return;
	default:
	    super.setMember(name, value);
	}
    }
    */
    }
