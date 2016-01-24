/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import org.luwrain.util.*;

public class AreaLayout
{
    public static final int SINGLE = 0;
    public static final int LEFT_RIGHT = 1;
    public static final int TOP_BOTTOM = 2;
    public static final int LEFT_TOP_BOTTOM = 3;
    public static final int LEFT_RIGHT_BOTTOM = 4;

    private int layoutType = SINGLE;
    private Area area1 = null;
    private Area area2 = null;
    private Area area3 = null;

    public AreaLayout()
    {
    }

    public AreaLayout(Area area)
    {
	layoutType = SINGLE;
	area1 = area;
	NullCheck.notNull(area, "area");
    }

    public AreaLayout(int layoutType,
		      Area area1, Area area2)
    {
	this.layoutType = layoutType;
	this.area1 = area1;
	this.area2 = area2;
	NullCheck.notNull(area1, "area1");
	NullCheck.notNull(area2, "area2");
	if (layoutType != LEFT_RIGHT && layoutType != TOP_BOTTOM)
	    throw new IllegalArgumentException("Illegal layoutType " + layoutType);
    }

    public AreaLayout(int layoutType, Area area1,
		      Area area2, Area area3)
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

    public AreaLayout(int layoutType, Area[] areas)
    {
	NullCheck.notNull(areas, "areas");
	this.layoutType = layoutType;
	switch(layoutType)
	{
	case SINGLE:
	    if (areas.length < 1)
		throw new IllegalArgumentException("areas array must have at least one element");
	    area1 = areas[0];
	    NullCheck.notNull(area1, "area[0]");
	    return;
	case LEFT_RIGHT:
	case TOP_BOTTOM:
	    if (areas.length < 2)
		throw new IllegalArgumentException("areas array must have at least two elements");
	    area1 = areas[0];
	    area2 = areas[1];
	    NullCheck.notNull(area1, "areas[0]");
	    NullCheck.notNull(area2, "areas[1]");
	    return;
	case LEFT_TOP_BOTTOM:
	case LEFT_RIGHT_BOTTOM:

	    if (areas.length < 3)
		throw new IllegalArgumentException("areas array must have at least three elements");
	    area1 = areas[0];
	    area2 = areas[1];
	    area3 = areas[2];
	    NullCheck.notNull(area1, "areas[0]");
	    NullCheck.notNull(area2, "areas[1]");
	    NullCheck.notNull(area3, "areas[2]");
	    return;
	default:
	    throw new IllegalArgumentException("Illegal layoutType " + layoutType);
	}
    }

    public int getLayoutType()
    {
	return layoutType;
    }

    public Area getArea1()
    {
	return area1;
    }

    public Area getArea2()
    {
	return area2;
    }

    public Area getArea3()
    {
	return area3;
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
}
