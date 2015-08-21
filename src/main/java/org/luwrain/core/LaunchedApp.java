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

import org.luwrain.util.*;

class LaunchedApp extends LaunchedAppBase
{
    public Application app;
    public int layoutType;
    public Area[] areas;
    public AreaWrapping[] areaWrappings;
    public int activeAreaIndex;
    public Application activeAppBeforeLaunch;

    public boolean init(Application newApp)
    {
	NullCheck.notNull(newApp, "newApp");
	app = newApp;
	final AreaLayout layout = app.getAreasToShow();
	if (layout == null)
	{
	    Log.warning("core", "application " + app.getClass().getName() + " has returned an empty area layout");
	    return false;
	}
	if (!layout.isValid())
	{
	    Log.warning("core", "application " + app.getClass().getName() + " has returned an invalid area layout");
	    return false;
	}
	layoutType = layout.getLayoutType();
	areas = layout.getAreas();
	if (areas == null)
	{
	    Log.warning("core", "application " + app.getClass().getName() + " has area layout without areas");
	    return false;
	}
	areaWrappings = new AreaWrapping[areas.length];
	for(int i = 0;i < areas.length
;++i)
	{
	    if (areas[i] == null)
	    {
		Log.warning("core", "application " + app.getClass().getName() + " has a null area");
		return false;
	    }
	    final AreaWrapping wrapping = new AreaWrapping();
	    wrapping.origArea = areas[i];
	    wrapping.securityWrapper = new SecurityAreaWrapper(areas[i]);
	    areaWrappings[i] = wrapping;
	}
	return true;
    }

    public void removeReviewWrappers()
    {
	if (areaWrappings != null)
	    for(AreaWrapping w: areaWrappings)
		w.reviewWrapper = null;
    }

    //Takes the reference of any kind, either to original area  or to a wrapper
    public boolean setActiveArea(Area area)
    {
	NullCheck.notNull(area, "area");
	if (areaWrappings == null)
	    return false;
	int index = 0;
	while(index < areaWrappings.length && !areaWrappings[index].containsArea(area))
	    ++index;
	if (index >= areaWrappings.length)
	    return false;
	activeAreaIndex = index;
	return true;
    }

	public Area getEffectiveActiveArea()
    {
	if (activeAreaIndex < 0 || areaWrappings == null)
	    return null;
	return areaWrappings[activeAreaIndex].getEffectiveArea();
    }
}
