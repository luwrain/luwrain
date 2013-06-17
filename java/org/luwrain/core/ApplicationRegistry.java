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

import java.util.*;

class ApplicationRegistry
{
    class Wrapper
    {
	public Application app;
	public Area activeArea;

	public Wrapper(Application app)
	{
	    this.app = app;
	}

	public Wrapper(Application app, Area activeArea)
	{
	    this.app = app;
	    this.activeArea = activeArea;
	}
    }

    private Vector<Wrapper> wrappers = new Vector<Wrapper>();
    private int[] visible = new int[0];
    private int activeAppIndex = -1;//-1 only if there are no visible applications;

    public boolean isVisibleApp(Application app)
    {
	ensureConsistent();
	for(int i = 0;i < getVisibleWrapperCount();i++)
	    if (getVisibleWrapper(i).app == app)
		return true;
	return false;
    }

    public Application[] getVisibleApps()
    {
	ensureConsistent();
	Application[] a = new Application[getVisibleWrapperCount()];
	for(int i = 0;i < getVisibleWrapperCount();i++)
	    a[i] = getVisibleWrapper(i).app;
	return a;
    }

    public boolean isActiveApp(Application app)
    {
	ensureConsistent();
	if (app == null || activeAppIndex < 0)
	    return false;
	return getVisibleWrapper(activeAppIndex).app == app;
    }

    //Only among visible;
    public boolean setActiveApp(Application app)
    {
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
	    return null;
	return getVisibleWrapper(activeAppIndex).app;
    }

    public void registerAppSingleVisible(Application app)
    {
	ensureConsistent();
	if (app == null)
	    return;
	int index = findAppInWrappers(app);
	if (index == -1)
	{
	    wrappers.add(new Wrapper(app));
	    index = wrappers.size() - 1;
	}
	visible = new int[1];
	visible[0] = index;
	activeAppIndex = 0;
    }

    public void releaseApp(Application app)
    {
	ensureConsistent();
	final int index = findAppInWrappers(app);
	if (index == -1)
	    return;
	wrappers.remove(index);
	if (wrappers.isEmpty())
	{
	    visible = new int[0];
	    activeAppIndex = -1;
	    return;
	}
	boolean presentInVisible = false;
	for(int i = 0;i < visible.length;i++)
	{
	    if (visible[i] == index)
	    {
		//FIXME:warning:if (presentInVisible)
		visible[i] = -1;
		presentInVisible = true;
		continue;
	    }
	    if (visible[i] > index)
		visible[i]--;
	}
	if (presentInVisible)
	{
	    int count = 0;
	    for(int i = 0;i < visible.length;i++)
		if (visible[i] == -1)
		    count++;
	    if (count == visible.length)
	    {
		visible = new int[0];
		activeAppIndex = -1;
		return;
	    }
	    int[] v = new int[visible.length - count];
	    int k = 0;
	    for(int i = 0;i < visible.length;i++)
		if (visible[i] != -1)
		{
		    if (activeAppIndex == i)
			activeAppIndex = k;
		    v[k++] = visible[i];
		} else
		    if (activeAppIndex == i)
			activeAppIndex++;
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
	activeAppIndex++;
	if (activeAppIndex >= visible.length)
	    activeAppIndex = 0;
    }

    public boolean switchNextInvisible()
    {
	ensureConsistent();
	final int current = visible[activeAppIndex];
	for(int i = current + 1;i < wrappers.size();i++)
	{
	    int j;
	    for(j = 0;j < visible.length;j++)
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
	    for(j = 0;j < visible.length;j++)
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
	ensureConsistent();
	if (app == null || area == null)
	    return false;
	//FIXME:Check new active area is in known area set;
	final int index = findAppInWrappers(app);
	if (index == -1)
	    return false;
	wrappers.get(index).activeArea = area;
	return true;
    }

    public Area getActiveAreaOfApp(Application app)
    {
	ensureConsistent();
	if (app == null)
	    return null;
	final int index = findAppInWrappers(app);
	if (index == -1)
	    return null;
	return wrappers.get(index).activeArea;
    }

    public Area getActiveAreaOfActiveApp()
    {
	ensureConsistent();
	if (activeAppIndex == -1)
	    return null;
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
	for(int i = 0;i < wrappers.size();i++)
	{
	    if (wrappers.get(i) == null || wrappers.get(i).app == null || wrappers.get(i).activeArea == null)
	    {
		offset++;
		continue;
	    }
	    int j;
	    for(j = 0;j < i;j++)
		if (wrappers.get(i).app == wrappers.get(j).app)
		    break;
	    if (j < i)
	    {
		offset++;
		continue;
	    }
	    wrappers.set(i - offset, wrappers.get(i));
	}
	wrappers.setSize(wrappers.size() - offset);
	if (visible == null)
	{
	    visible = new int[1];
	    visible[0] = wrappers.size() - 1;
	    activeAppIndex = 0;
	}
	offset = 0;
	for(int i = 0;i < visible.length;i++)
	{
	    if (visible[i] >= wrappers.size() || visible[i] < 0)
	    {
		offset++;
		continue;
	    }
	    int j;
	    for(j = 0;j < i;j++)
		if (visible[i] == visible[j])
		    break;
	    if (j < i)
	    {
		offset++;
		continue;
	    }
	    visible[i - offset] = visible[i];
	}
	if (offset > 0)
	{
	    int[] v = new int[visible.length - offset];
	    for(int i = 0;i < v.length;i++)
		v[i] = visible[i];
	    visible = v;
	}
	if (activeAppIndex < 0 || activeAppIndex >= visible.length)
	    activeAppIndex = 0;
    }

    private int findAppInWrappers(Application app)
    {
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
