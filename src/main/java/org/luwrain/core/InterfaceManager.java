/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

public final class InterfaceManager
{
    private final Core core;
    private final List<Entry> entries = new ArrayList();
    final Luwrain objForEnvironment;

    InterfaceManager(Base base)
    {
	NullCheck.notNull(base, "base");
	this.core = (Core)base;
	this.objForEnvironment = new LuwrainImpl((Core)base);
    }

    Luwrain requestNew(Application app)
    {
	NullCheck.notNull(app, "app");
	final Luwrain existing = findFor(app);
	if (existing != null)
	    return existing;
	final Luwrain luwrain = new LuwrainImpl(core);
	entries.add(new Entry(Entry.APP, app, luwrain));
	return luwrain;
    }

    public Luwrain requestNew(Extension ext)
    {
	NullCheck.notNull(ext, "ext");
	final Luwrain existing = findFor(ext);
	if (existing != null)
	    return existing;
	final Luwrain luwrain = new LuwrainImpl(core);
	entries.add(new Entry(Entry.EXTENSION, ext, luwrain));
	return luwrain;
    }

    private Luwrain findFor(Object obj)
    {
	NullCheck.notNull(obj, "obj");
	for(Entry e:entries)
	    if (e.obj == obj)
		return e.luwrain;
	return null;
    }

    Application findApp(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	for(Entry e: entries)
	    if (e.luwrain == luwrain &&
		e.type == Entry.APP &&
		e.obj instanceof Application)
		return (Application)e.obj;
	return null;
    }

    Extension findExt(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
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

    //returns true if it is object for the core or an extension instance
    boolean isSuitsForEnvironmentPopup(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	return luwrain == objForEnvironment || findExt(luwrain) != null;
    }

    static private class Entry
    {
	static final int APP = 1;
	static final int EXTENSION = 2;

	final int type;
	final Object obj;
	final Luwrain luwrain;

	Entry(int type, Object obj, Luwrain luwrain)
	{
	    NullCheck.notNull(obj, "obj");
	    NullCheck.notNull(luwrain, "luwrain");
	    this.type = type;
	    this.obj = obj;
	    this.luwrain = luwrain;
	}
    }
}
