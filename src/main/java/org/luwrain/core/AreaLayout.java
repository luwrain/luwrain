/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

import static org.luwrain.core.NullCheck.*;

public final class AreaLayout
{
    public enum Type {SINGLE, LEFT_RIGHT, TOP_BOTTOM, LEFT_TOP_BOTTOM, LEFT_RIGHT_BOTTOM };

    static public final Type
	SINGLE = Type.SINGLE,
	LEFT_RIGHT = Type.LEFT_RIGHT,
	TOP_BOTTOM = Type.TOP_BOTTOM,
	LEFT_TOP_BOTTOM = Type.LEFT_TOP_BOTTOM,
	LEFT_RIGHT_BOTTOM = Type.LEFT_RIGHT_BOTTOM;

    final Type layoutType;
    final Area area1;
    final Area area2;
    final Area area3;

    public AreaLayout(Area area)
    {
	layoutType = SINGLE;
	notNull(area, "area");
	this.area1 = area;
	this.area2 = null;
	this.area3 = null;
    }

    public AreaLayout(Type layoutType, Area area1, Area area2)
    {
	notNull(layoutType, "layoutType");
	notNull(area1, "area1");
	notNull(area2, "area2");
	if (layoutType != LEFT_RIGHT && layoutType != TOP_BOTTOM)
	    throw new IllegalArgumentException("Illegal layoutType " + layoutType);
	this.layoutType = layoutType;
	this.area1 = area1;
	this.area2 = area2;
	this.area3 = null;
    }

    public AreaLayout(Type layoutType, Area area1, Area area2, Area area3)
    {
	this.layoutType = layoutType;
	this.area1 = area1;
	this.area2 = area2;
	this.area3 = area3;
	NullCheck.notNull(area1, "area1");
	NullCheck.notNull(area2, "area2");
	NullCheck.notNull(area3, "area3");
	if (layoutType != LEFT_TOP_BOTTOM && layoutType != LEFT_RIGHT_BOTTOM)
	    throw new IllegalArgumentException("Illegal layoutType " + layoutType);
    }

    public AreaLayout(Type layoutType, Area[] areas)
    {
	notNull(layoutType, "layoutType");
	notNullItems(areas, "areas");
	this.layoutType = layoutType;
	switch(layoutType)
	{
	case SINGLE:
	    if (areas.length < 1)
		throw new IllegalArgumentException("areas array must have at least one element");
	    notNull(areas[0], "areas[0]");
	    this.area1 = areas[0];
	    this.area2 = null;
	    this.area3 = null;
	    return;
	case LEFT_RIGHT:
	case TOP_BOTTOM:
	    if (areas.length < 2)
		throw new IllegalArgumentException("areas array must have at least two elements");
	    notNull(areas[0], "areas[0]");
	    notNull(areas[1], "areas[1]");
	    this.area1 = areas[0];
	    this.area2 = areas[1];
	    this.area3 = null;
	    return;
	case LEFT_TOP_BOTTOM:
	case LEFT_RIGHT_BOTTOM:
	    if (areas.length < 3)
		throw new IllegalArgumentException("areas array must have at least three elements");
	    notNull(areas[0], "areas[0]");
	    notNull(areas[1], "areas[1]");
	    notNull(areas[2], "areas[2]");
	    this.area1 = areas[0];
	    this.area2 = areas[1];
	    this.area3 = areas[2];
	    return;
	default:
	    throw new IllegalArgumentException("Illegal layoutType " + layoutType.toString());
	}
    }

    public boolean isValid()
    {
	switch(layoutType)
	{
	case SINGLE:
	    return area1 != null;
	case LEFT_RIGHT:
	case TOP_BOTTOM:
	    return area1 != null && area2 != null;
	case LEFT_TOP_BOTTOM:
	case LEFT_RIGHT_BOTTOM:
	    return area1 != null && area2 != null && area3 != null;
	default:
	    return false;
	}
    }

    public Area[] getAreas()
    {
	switch(layoutType)
	{
	case SINGLE:
	    return new Area[]{area1};
	case LEFT_RIGHT:
	case TOP_BOTTOM:
	    return new Area[]{area1, area2};
	case LEFT_TOP_BOTTOM:
	case LEFT_RIGHT_BOTTOM:
	    return new Area[]{area1, area2, area3};
	default:
	    return new Area[0];
	}
    }

    public Area getDefaultArea()
    {
	return area1;
    }

    public boolean hasArea(Area area)
    {
	switch(layoutType)
	{
	case SINGLE:
	    return area1 == area;
	case LEFT_TOP_BOTTOM:
	    return area1 == area || area2 == area || area3 == area;
	case LEFT_RIGHT_BOTTOM:
	    return area1 == area || area2 == area || area3 == area;
	default:
	    return false;
	}
    }

    public Area getNextArea(Area area)
    {
	NullCheck.notNull(area, "area");
	final Area[] areas = getAreas();
	if (areas.length == 0)
	    return null;
	if (areas.length == 1)
	    return areas[0] == area?area:null;
	if (areas[areas.length - 1] == area)
	    return areas[0];
	for(int i = 0;i < areas.length - 1;i++)
	    if (areas[i] == area)
		return areas[i + 1];
	return null;
    }

    public Area getPrevArea(Area area)
    {
	NullCheck.notNull(area, "area");
	final Area[] areas = getAreas();
	if (areas.length == 0)
	    return null;
	if (areas.length == 1)
	    return areas[0] == area?area:null;
	if (areas[0] == area)
	    return areas[areas.length - 1];
	for(int i = 0;i < areas.length - 1;i++)
	    if (areas[i + 1] == area)
		return areas[i];
	return null;
    }
}
