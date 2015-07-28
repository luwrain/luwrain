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

class PopupManager
{
        public static final int INVALID = -1;
    public static final int TOP = 0;
    public static final int BOTTOM = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    class Wrapper
    {
	public Application app;
	public Area area;
	public int position;
	public PopupEventLoopStopCondition stopCondition;
	public boolean noMultipleCopies;
	public boolean isWeak; 

	public Wrapper(Application app,
		       Area area,
		       int position,
		       PopupEventLoopStopCondition stopCondition,
		       boolean noMultipleCopies,
		       boolean isWeak)
	{
	    this.app = app;
	    this.area = area;
	    this.position = position;
	    this.stopCondition = stopCondition;
	    this.noMultipleCopies = noMultipleCopies;
	    this.isWeak = isWeak;
	}
    }

    private Vector<Wrapper> wrappers = new Vector<Wrapper>();

    //null app means environment popup;
    public void addNew(Application app,
			    Area area,
			    int position,
			    PopupEventLoopStopCondition stopCondition,
		       boolean noMultipleCopies,
		       boolean isWeak)
    {
	if (area == null)
	    throw new NullPointerException("area may not be null");
	if (stopCondition == null)
	    throw new NullPointerException("stopCondition may not be null");
	wrappers.add(new Wrapper(app, area, position, stopCondition, noMultipleCopies, isWeak));
    }

    public void removeLast()
    {
	if (wrappers.isEmpty())
	{
	    Log.warning("popups", "trying to remove last popup without having any popups at all");
	    return;
	}
	wrappers.remove(wrappers.size() - 1);
    }

    public boolean isLastPopupDiscontinued()
    {
	if (wrappers.isEmpty())
	    return true;
	return !wrappers.lastElement().stopCondition.continueEventLoop();
    }

    public boolean hasAny()
    {
	return !wrappers.isEmpty();
    }

    //null is a valid argument;
    public boolean hasPopupOfApp(Application app)
    {
	for(int i = 0;i < wrappers.size();i++)
	    if (wrappers.get(i).app == app)
		return true;
	return false;
    }

    public Application getAppOfLastPopup()
    {
	if (wrappers.isEmpty())
	    return null;
	return wrappers.lastElement().app;
    }

    public Area getAreaOfLastPopup()
    {
	if (wrappers.isEmpty())
	    return null;
	return wrappers.lastElement().area;
    }

    public int getPositionOfLastPopup()
    {
	if (wrappers.isEmpty())
	    return INVALID;
	return wrappers.lastElement().position;
    }

    public void onNewInstanceLaunch(Application app, Class newCopyClass)
    {
	for(Wrapper w: wrappers)
	    if (w.noMultipleCopies && app == w.app && w.area.getClass().equals(newCopyClass))
		w.stopCondition.cancel();
    }

    public boolean isWeakLastPopup()
    {
	if (wrappers.isEmpty())
	    return false;
	return wrappers.lastElement().isWeak;
    }

    public void cancelLastPopup()
    {
	if (wrappers.isEmpty())
	    return;
	wrappers.lastElement().stopCondition.cancel();
}
}
