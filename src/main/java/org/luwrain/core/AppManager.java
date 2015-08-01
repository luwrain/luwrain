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
    class Wrapper
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
    private int[] visible = new int[0];
    private int activeAppIndex = -1;
    private Application defaultApp;

    public AppManager(Application defaultApp)
    {
	this.defaultApp = defaultApp;
	if (defaultApp == null)
	    throw new NullPointerException("defaultApp may not be null");
    }

    public boolean isVisibleApp(Application app)
    {
	if (app == null)
	    throw new NullPointerException("app may not be null");
	ensureConsistent();
	if (app == defaultApp && activeAppIndex < 0)
	    return true;
	for(int i = 0;i < getVisibleWrapperCount();i++)
	    if (getVisibleWrapper(i).app == app)
		return true;
	return false;
    }

    public Application[] getVisibleApps()
    {
	ensureConsistent();
	if (activeAppIndex < 0)
	    return new Application[]{defaultApp};
	Application[] a = new Application[getVisibleWrapperCount()];
	for(int i = 0;i < getVisibleWrapperCount();i++)
	    a[i] = getVisibleWrapper(i).app;
	return a;
    }

    public boolean isActiveApp(Application app)
    {
	if (app == null)
	    throw new NullPointerException("app may not be null");
	ensureConsistent();
	if (activeAppIndex < 0)
	    return app == defaultApp?true:false;
	return getVisibleWrapper(activeAppIndex).app == app;
    }

    //app must be already visible, free switching among non-visible apps not permitted;
    public boolean setActiveApp(Application app)
    {
	if (app == null)
	    throw new NullPointerException("app may not be null");
	ensureConsistent();
	for(int i = 0;i < getVisibleWrapperCount();i++)
	    if (getVisibleWrapper(i).app == app)
	    {
		activeAppIndex = i;
		return true;
	    }
	return false;
    }

    public Application getActiveApp()
    {
	ensureConsistent();
	if (activeAppIndex < 0)
	    return defaultApp;
	return getVisibleWrapper(activeAppIndex).app;
    }

    public void registerAppSingleVisible(Application app, Area activeArea)
    {
	if (app == null)
	    throw new NullPointerException("app may not be null");
	if (activeArea == null)
	    throw new NullPointerException("activeApp may not be null");
	ensureConsistent();
	final Application activeNow = getActiveApp();
	int index = findAppInWrappers(app);
	if (index < 0)
	{
	    wrappers.add(new Wrapper(app, activeArea, activeNow));
	    index = wrappers.size() - 1;
	}
	visible = new int[]{index};
	activeAppIndex = 0;
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
	    visible = new int[0];
	    activeAppIndex = -1;
	    return;
	}
	boolean presentInVisible = false;
	for(int i = 0;i < visible.length;++i)
	{
	    if (visible[i] == index)
	    {
		visible[i] = -1;
		presentInVisible = true;
		continue;
	    }
	    if (visible[i] > index)
		--visible[i];
	}
	if (presentInVisible)
	{
	    int count = 0;
	    for(int i = 0;i < visible.length;++i)
		if (visible[i] < 0)
		    ++count;
	    //The removed application was the only visible, trying to find something to replace it for the user;
	    if (count == visible.length)
	    {
		visible = new int[1];
		if (removedWrapper.activeAppBeforeLaunch != null)
		{
		    //Trying to activate the application which was active before launch of the one being removed;
		    int previouslyActiveIndex = 0;
		    while (previouslyActiveIndex < wrappers.size() && wrappers.get(previouslyActiveIndex).app != removedWrapper.activeAppBeforeLaunch)
			++previouslyActiveIndex;
		    visible[0] = previouslyActiveIndex >= wrappers.size()?wrappers.size() - 1:previouslyActiveIndex;
		} else
		    visible[0] = wrappers.size() - 1;
		activeAppIndex = 0;
		return;
	    }
	    int[] v = new int[visible.length - count];
	    int k = 0;
	    for(int i = 0;i < visible.length;++i)
		if (visible[i] >= 0)
		{
		    if (activeAppIndex == i)
			activeAppIndex = k;
		    v[k++] = visible[i];
		} else
		    if (activeAppIndex == i)
			++activeAppIndex;
	    visible = v;
	    if (activeAppIndex >= visible.length)
		activeAppIndex = visible.length - 1;
	}
    }

    public void switchNextVisible()
    {
	ensureConsistent();
	if (visible.length == 0)
	    return;
	++activeAppIndex;
	if (activeAppIndex >= visible.length)
	    activeAppIndex = 0;
    }

    public boolean switchNextInvisible()
    {
	ensureConsistent();
	if (activeAppIndex == -1)
	    return false;
	final int current = visible[activeAppIndex];
	for(int i = current + 1;i < wrappers.size();++i)
	{
	    int j;
	    for(j = 0;j < visible.length;++j)
		if (visible[j] == i)
		    break;
	    if (j < visible.length)
		continue;
	    visible[activeAppIndex] = i;
	    return true;
	}
	for(int i = 0;i < current;i++)
	{
	    int j;
	    for(j = 0;j < visible.length;++j)
		if (visible[j] == i)
		    break;
	    if (j < visible.length)
		continue;
	    visible[activeAppIndex] = i;
	    return true;
	}
	return false;
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
	if (index == -1)
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
	return wrappers.get(visible[activeAppIndex]).activeArea;
    }

    private void ensureConsistent()
    {
	if (wrappers == null)
	    wrappers = new Vector<Wrapper>();
	if (wrappers.isEmpty())
	{
	    if (visible.length != 0)
		visible = new int[0];
	    activeAppIndex = -1;
	    return;
	}
	int offset = 0;
	for(int i = 0;i < wrappers.size();++i)
	{
	    if (wrappers.get(i) == null || wrappers.get(i).app == null || wrappers.get(i).activeArea == null)
	    {
		Log.warning("applications", "found an inconsistent application");
		++offset;
		continue;
	    }
	    int j;
	    for(j = 0;j < i;++j)
		if (wrappers.get(i).app == wrappers.get(j).app)
		    break;
	    if (j < i)
	    {
		Log.warning("applications", "found the doubled application enter");
		++offset;
		continue;
	    }
	    wrappers.set(i - offset, wrappers.get(i));
	}
	wrappers.setSize(wrappers.size() - offset);
	if (visible == null || visible.length < 1)
	{
	    visible = new int[1];
	    visible[0] = wrappers.size() - 1;
	    activeAppIndex = 0;
	}
	offset = 0;
	for(int i = 0;i < visible.length;++i)
	{
	    if (visible[i] >= wrappers.size() || visible[i] < 0)
	    {
		Log.warning("applications", "found the visible entry with index exceeding bounds of wrappers vector");
		++offset;
		continue;
	    }
	    int j;
	    for(j = 0;j < i;++j)
		if (visible[i] == visible[j])
		    break;
	    if (j < i)
	    {
		Log.warning("applications", "found the doubled visible entries");
		++offset;
		continue;
	    }
	    visible[i - offset] = visible[i];
	}
	if (offset > 0)
	{
	    int[] v = new int[visible.length - offset];
	    for(int i = 0;i < v.length;++i)
		v[i] = visible[i];
	    visible = v;
	}
	if (activeAppIndex < 0 || activeAppIndex >= visible.length)
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

    private Wrapper getVisibleWrapper(int index)
    {
	return wrappers.get(visible[index]);
    }

    private int getVisibleWrapperCount()
    {
	return visible.length;
    }
}
