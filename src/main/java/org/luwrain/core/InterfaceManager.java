/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

import org.luwrain.extensions.Extension;

public class InterfaceManager
{
    private class Entry
    {
	public static final int APP = 1;
	public static final int EXTENSION = 2;

	public int type;
	public Object obj;
	public Luwrain luwrain;

	public Entry(int type,
		     Object obj,
Luwrain luwrain)
	{
	    this.type = type;
	    this.obj = obj;
	    this.luwrain = luwrain;
	    if (obj == null)
		throw new NullPointerException("obj may not be null");
	    if (luwrain == null)
		throw new NullPointerException("luwrain may not be null");
	}
    }





    private Environment environment;
    private Vector<Entry> entries = new Vector<Entry>();

    public InterfaceManager(Environment environment)
    {
	this.environment = environment;
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
    }

    public Luwrain requestNew(Application app)
    {
	if (app == null)
	    throw new NullPointerException("app may not be null");
	final Luwrain existing = findFor(app);
	if (existing != null)
	    return existing;
	Luwrain luwrain = new Luwrain(environment);
	entries.add(new Entry(Entry.APP, app, luwrain));
	return luwrain;
    }

    public Luwrain requestNew(Extension ext)
    {
	if (ext == null)
	    throw new NullPointerException("ext may not be null");
	final Luwrain existing = findFor(ext);
	if (existing != null)
	    return existing;
	Luwrain luwrain = new Luwrain(environment);
	entries.add(new Entry(Entry.EXTENSION, ext, luwrain));
	return luwrain;
    }

    public Luwrain findFor(Object obj)
    {
	if (obj == null)
	    throw new NullPointerException("obj may not be null");
	for(Entry e:entries)
	    if (e.obj == obj)
		return e.luwrain;
	return null;
    }

    public Application findApp(Luwrain luwrain)
    {
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	for(Entry e: entries)
	    if (e.luwrain == luwrain &&
e.type == Entry.APP &&
e.obj instanceof Application)
		return (Application)e.obj;
	return null;
    }

    public Extension findExt(Luwrain luwrain)
    {
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	for(Entry e: entries)
	    if (e.luwrain == luwrain &&
e.type == Entry.EXTENSION &&
e.obj instanceof Extension)
		return (Extension)e.obj;
	return null;
    }

    public void release(Luwrain luwrain)
    {
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	for(int i = 0;i < entries.size();i++)
	    if (entries.get(i).luwrain == luwrain)
	    {
		entries.remove(i);
		return;
	    }
    }
}
