/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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

	public Wrapper(Application app,
		       Area area,
		       int position,
		       PopupEventLoopStopCondition stopCondition,
		       boolean noMultipleCopies)
	{
	    this.app = app;
	    this.area = area;
	    this.position = position;
	    this.stopCondition = stopCondition;
	    this.noMultipleCopies = noMultipleCopies;
	}
    }

    private Vector<Wrapper> wrappers = new Vector<Wrapper>();

    public void addNewPopup(Application app,
			    Area area,
			    int position,
			    PopupEventLoopStopCondition stopCondition,
			    boolean noMultipleCopies)
    {
	wrappers.add(new Wrapper(app, area, position, stopCondition, noMultipleCopies));
    }

    public void removeLastPopup()
    {
	if (wrappers.isEmpty())
	    return;
	wrappers.remove(wrappers.size() - 1);
    }

    public boolean isLastPopupDiscontinued()
    {
	//FIXME:
	return false;
    }

    public boolean hasPopups()
    {
	return !wrappers.isEmpty();
    }

    public boolean hasPopupOfApp(Application app)
    {
	for(int i = 0;i < wrappers.size();i++)
	    if (wrappers.get(i).app == app)
		return true;
	return false;
    }

    public Application getAppOfLastPopup()
    {
	if (!hasPopups())
	    return null;
	return wrappers.lastElement().app;
    }

    public Area getAreaOfLastPopup()
    {
	if (!hasPopups())
	    return null;
	return wrappers.lastElement().area;
    }

    public int getPositionOfLastPopup()
    {
	if (!hasPopups())
	    return INVALID;
	return wrappers.lastElement().position;
    }

    public void onNewInstanceLaunch(Application app, Class newCopyClass)
    {
	for(Wrapper w: wrappers)
	    if (w.noMultipleCopies && app == w.app && w.area.getClass().equals(newCopyClass))
		w.stopCondition.onNewCopyLaunch();
    }
}
