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

class LaunchedApp extends LaunchedAppBase
{
final Application app;
    int layoutType;
    Area[] areas;
    OpenedArea[] areaWrappings;
    int activeAreaIndex = 0;
    Application activeAppBeforeLaunch;

    LaunchedApp(Application app)
    {
	NullCheck.notNull(app, "app");
	this.app = app;
    }

    boolean init()
    {
	final AreaLayout layout = getValidAreaLayout();
	layoutType = layout.getLayoutType();
	areas = layout.getAreas();
	if (areas == null)
	{
	    Log.info("core", "application " + app.getClass().getName() + " has area layout without areas");
	    return false;
	}
	areaWrappings = new OpenedArea[areas.length];
	for(int i = 0;i < areas.length;++i)
	{
	    if (areas[i] == null)
	    {
		Log.info("core", "application " + app.getClass().getName() + " has a null area");
		return false;
	    }
	    areaWrappings[i] = new OpenedArea(areas[i]);
	}
	return true;
    }

    boolean refreshAreaLayout()
    {
	final Area previouslyActiveArea = areas[activeAreaIndex];
	final AreaLayout newLayout = getValidAreaLayout();
	final int newLayoutType = newLayout.getLayoutType();
	final Area[] newAreas = newLayout.getAreas();
	if (newAreas == null)
	{
	    Log.info("core", "application " + app.getClass().getName() + " has area layout without areas");
	    return false;
	}
	final OpenedArea[] newAreaWrappings = new OpenedArea[newAreas.length];
	for(int i = 0;i < newAreas.length;++i)
	{
	    if (newAreas[i] == null)
	    {
		Log.info("core", "application " + app.getClass().getName() + " has a null area");
		return false;
	    }
	    newAreaWrappings[i] = new OpenedArea(newAreas[i]);
	}
	layoutType = newLayoutType;
	areas = newAreas;
	areaWrappings = newAreaWrappings;
	activeAreaIndex = -1;
	for(int i = 0;i < areas.length;++i)
	    if (previouslyActiveArea == areas[i])
		activeAreaIndex = i;
	if (activeAreaIndex < 0 || activeAreaIndex > areas.length)
	    activeAreaIndex = 0;
	return true;
    }

    private AreaLayout getValidAreaLayout()
    {
	AreaLayout layout;
	try {
	    layout = app.getAreaLayout();
	}
	catch (Throwable e)
	{
	    Log.info("core", "application " + app.getClass().getName() + " has thrown an exception on getAreaLayout():" + e.getMessage());
	    e.printStackTrace();
	    return null;
	}
	if (layout == null)
	{
	    Log.info("core", "application " + app.getClass().getName() + " has returned an empty area layout");
	    return null;
	}
	if (!layout.isValid())
	{
	    Log.info("core", "application " + app.getClass().getName() + " has returned an invalid area layout");
	    return null;
	}
	return layout;
    }

    void removeReviewWrappers()
    {
	if (areaWrappings != null)
	    for(OpenedArea w: areaWrappings)
		w.wrapper = null;
    }

    //Takes the reference of any kind, either to original area  or to a wrapper
    boolean setActiveArea(Area area)
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

    Area getEffectiveActiveArea()
    {
	if (activeAreaIndex < 0 || areaWrappings == null)
	    return null;
	return areaWrappings[activeAreaIndex].getEffectiveArea();
    }

    @Override public Area getCorrespondingEffectiveArea(Area area)
    {
	NullCheck.notNull(area, "area");
	for(OpenedArea w: areaWrappings)
	    if (w.containsArea(area))
		return w.getEffectiveArea();
	return super.getCorrespondingEffectiveArea(area);
    }

    @Override public OpenedArea getAreaWrapping(Area area)
    {
	NullCheck.notNull(area, "area");
	for(OpenedArea w: areaWrappings)
	    if (w.containsArea(area))
		return w;
	return super.getAreaWrapping(area);
    }

    AreaLayout getEffectiveAreaLayout()
    {
	final Area[] a = new Area[areas.length];
	for(int i = 0;i < areaWrappings.length;++i)
	    a[i] = areaWrappings[i].getEffectiveArea();
	return new AreaLayout(layoutType, a);
    }

    void sendBroadcastEvent(org.luwrain.core.events.EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	//	super.sendBroadcastEvent(event);
	for(OpenedArea w: areaWrappings)
	    w.getEffectiveArea().onEnvironmentEvent(event);
    }
}
