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

import java.util.*;

class AppManager
{
    static private class Wrapper
    {
	public Application app;
	public Area activeArea;
	public Application activeAppBeforeLaunch;

	public Wrapper(Application app,
		       Area activeArea,
		       Application activeAppBeforeLaunch)
	{
	    this.app = app;
	    this.activeArea = activeArea;
	    this.activeAppBeforeLaunch = activeAppBeforeLaunch;
	    if (app == null)
		throw new NullPointerException("app may not be null");
	    if (activeArea == null)
		throw new NullPointerException("activeArea may not be null");
	}
    }

    private Vector<Wrapper> wrappers = new Vector<Wrapper>();
    private int activeAppIndex = -1;
    private Application defaultApp;

    public AppManager(Application defaultApp)
    {
	this.defaultApp = defaultApp;
	if (defaultApp == null)
	    throw new NullPointerException("defaultApp may not be null");
    }

    public boolean setActiveApp(Application app)
    {
	if (app == null)
	    throw new NullPointerException("app may not be null");
	ensureConsistent();
	for(int i = 0;i < wrappers.size();++i)
	    if (wrappers.get(i).app == app)
	    {
		activeAppIndex = i;
		return true;
	    }
	return false;
    }

    public boolean isAppActive(Application app)
    {
	if (app == null)
	    throw new NullPointerException("app may not be null");
	ensureConsistent();
	if (app == defaultApp && activeAppIndex < 0)
	    return true;
	if (wrappers.get(activeAppIndex).app == app)
	    return true;
	return false;
    }

    public Application getActiveApp()
    {
	ensureConsistent();
	if (activeAppIndex < 0)
	    return defaultApp;
	return wrappers.get(activeAppIndex).app;
    }

    public void registerNewApp(Application app, Area activeArea)
    {
	if (app == null)
	    throw new NullPointerException("app may not be null");
	if (activeArea == null)
	    throw new NullPointerException("activeApp may not be null");
	ensureConsistent();
	final Application activeNow = activeAppIndex >= 0?wrappers.get(activeAppIndex).app:null;
	final int index = findAppInWrappers(app);
	if (index >= 0)
	{
	    activeAppIndex = index;
	    return;
	}
	wrappers.add(new Wrapper(app, activeArea, activeNow));
	activeAppIndex = wrappers.size() - 1;
    }

    public void releaseApp(Application app)
    {
	if (app == null)
	    throw new NullPointerException("app may not be null");
	ensureConsistent();
	final int index = findAppInWrappers(app);
	if (index == -1)
	    return;
	final Wrapper removedWrapper = wrappers.get(index);
	wrappers.remove(index);
	for(Wrapper w:wrappers)
	    if (w.activeAppBeforeLaunch == app)
		w.activeAppBeforeLaunch = null;
	if (wrappers.isEmpty())
	{
	    activeAppIndex = -1;
	    return;
	}
	if (removedWrapper.activeAppBeforeLaunch == null)
	{
	    activeAppIndex = wrappers.size() - 1;
	    return;
	}
	//Trying to activate the application which was active before launch of the one being removed;
	activeAppIndex = findAppInWrappers(removedWrapper.activeAppBeforeLaunch);
	if (activeAppIndex < 0)//We have a bug in this case
	    activeAppIndex = wrappers.size() - 1;
    }

    public void switchNextApp()
    {
	ensureConsistent();
	if (wrappers.isEmpty())
	    return;
	if (activeAppIndex < 0)//We was in desktop
	{
	    activeAppIndex = 0;
	    return;
	}
	++activeAppIndex;
	if (activeAppIndex >= wrappers.size())
	    activeAppIndex = 0;
    }

    public boolean setActiveAreaOfApp(Application app, Area area)
    {
	if (app == null)
	    throw new NullPointerException("app may not be null");
	if (area == null)
	    throw new NullPointerException("area may not be null");
	ensureConsistent();
	//FIXME:Check new active area is in known area set;
	final int index = findAppInWrappers(app);
	if (index < 0)
	    return false;
	wrappers.get(index).activeArea = area;
	return true;
    }

    public Area getActiveAreaOfApp(Application app)
    {
	if (app == null)
	    throw new NullPointerException("app may not be null");
	ensureConsistent();
	if (app == defaultApp)
	    return defaultApp.getAreasToShow().getArea1();
	final int index = findAppInWrappers(app);
	if (index == -1)
	    return null;
	return wrappers.get(index).activeArea;
    }

    public Area getActiveAreaOfActiveApp()
    {
	ensureConsistent();
	if (activeAppIndex < 0)
	    return defaultApp.getAreasToShow().getArea1();
	return wrappers.get(activeAppIndex).activeArea;
    }

    private void ensureConsistent()
    {
	if (wrappers == null)
	    wrappers = new Vector<Wrapper>();
	if (wrappers.isEmpty())
	{
	    activeAppIndex = -1;
	    return;
	}
	if (activeAppIndex >= wrappers.size())
	    activeAppIndex = 0;
    }

    private int findAppInWrappers(Application app)
    {
	if (app == null)
	    throw new NullPointerException("app may not be null");
	for(int i = 0;i < wrappers.size();i++)
	    if (wrappers.get(i).app == app)
		return i;
	return -1;
    }
}
