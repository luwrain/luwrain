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

public class InstanceManager
{
    private class Entry
    {
	public Object instance;
	public Application app;

	public Entry(Object instance, Application app)
	{
	    this.instance = instance;
	    this.app = app;
	}
    }

    private Vector<Entry> entries = new Vector<Entry>();

    public Object getInstanceByApp(Application app)
    {
	for(int i = 0;i < entries.size();i++)
	    if (entries.get(i).app == app)
		return entries.get(i).instance;
	return null;
    }

    public Application getAppByInstance(Object instance)
    {
	for(int i = 0;i < entries.size();i++)
	    if (entries.get(i).instance == instance)
		return entries.get(i).app;
	return null;
    }

    public Object registerApp(Application app)
    {
	if (app == null)
	    return null;
	Object instance = getInstanceByApp(app);
	if (instance != null)
	    return instance;
	instance = new Object();
	entries.add(new Entry(instance, app));
	return instance;
    }

    public void releaseInstance(Object instance)
    {
	for(int i = 0;i < entries.size();i++)
	    if (entries.get(i).instance == instance)
	    {
		entries.remove(i);
		return;
	    }
    }

    public void releaseInstanceByApp(Application app)
    {
	for(int i = 0;i < entries.size();i++)
	    if (entries.get(i).app == app)
	    {
		entries.remove(i);
		return;
	    }
    }
}
