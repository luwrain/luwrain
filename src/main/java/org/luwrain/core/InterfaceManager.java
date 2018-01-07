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

import java.util.*;
import org.luwrain.util.*;

import org.luwrain.core.extensions.Extension;

public class InterfaceManager
{
    static private class Entry
    {
	static public final int APP = 1;
	static public final int EXTENSION = 2;

	int type;
	Object obj;
	Luwrain luwrain;

	public Entry(int type,
		     Object obj, Luwrain luwrain)
	{
	    this.type = type;
	    this.obj = obj;
	    this.luwrain = luwrain;
	    NullCheck.notNull(obj, "obj");
	    NullCheck.notNull(luwrain, "luwrain");
	}
    }

    private Luwrain objForEnvironment = null;
    private final Vector<Entry> entries = new Vector<Entry>();

    Luwrain requestNew(Application app, Environment environment)
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

    Luwrain requestNew(Extension ext, Environment environment)
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

    Luwrain findFor(Object obj)
    {
	if (obj == null)
	    throw new NullPointerException("obj may not be null");
	for(Entry e:entries)
	    if (e.obj == obj)
		return e.luwrain;
	return null;
    }

    Application findApp(Luwrain luwrain)
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

    Extension findExt(Luwrain luwrain)
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
	NullCheck.notNull(luwrain, "luwrain");
	for(int i = 0;i < entries.size();i++)
	    if (entries.get(i).luwrain == luwrain)
	    {
		entries.remove(i);
		return;
	    }
    }

    void createObjForEnvironment(Environment environment)
    {
	NullCheck.notNull(environment, "environment");
	if (objForEnvironment != null)
	    return;
	objForEnvironment = new Luwrain(environment);
    }

    Luwrain getObjForEnvironment()
    {
	return objForEnvironment;
    }

    //returns true if it is object for environemnt or an extension instance
    boolean isSuitsForEnvironmentPopup(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	return luwrain == objForEnvironment || findExt(luwrain) != null;
    }
}
