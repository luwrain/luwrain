/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

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

public class AreaLayout
{
    public static final int SINGLE = 0;
    public static final int LEFT_TOP_BOTTOM = 1;
    public static final int LEFT_RIGHT_BOTTOM = 2;

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
    }

    public AreaLayout(int type, Area area1, Area area2)
    {
	layoutType = type;
	this.area1 = area1;
	this.area2 = area2;
    }

    public AreaLayout(int type, Area area1, Area area2, Area area3)
    {
	layoutType = type;
	this.area1 = area1;
	this.area2 = area2;
	this.area3 = area3;
    }

    public int getType()
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
