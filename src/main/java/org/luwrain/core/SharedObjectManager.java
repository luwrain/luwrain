/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

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

import org.luwrain.core.extensions.*;

class SharedObjectManager
{
    private class Entry 
    {
Extension extension;
String name = "";
SharedObject sharedObject;

Entry(Extension extension, String name,
		     SharedObject sharedObject)
	{
	    NullCheck.notNull(name, "name");
	    NullCheck.notNull(sharedObject, "sharedObject");
	    this.extension = extension;
	    this.name = name;
	    this.sharedObject = sharedObject;
	    if (name.trim().isEmpty())
		throw new IllegalArgumentException("name may not be empty");
	}
    }

    private final TreeMap<String, Entry> sharedObjects = new TreeMap<String, Entry>();

    //Standard shared objects
    private PartitionsPopupControl partitionsPopupControl;

boolean add(Extension extension, SharedObject sharedObject)
    {
	NullCheck.notNull(sharedObject, "sharedObject");
	final String name = sharedObject.getName();
	if (name == null || name.trim().isEmpty())
	    return false;
	if (sharedObjects.containsKey(name))
	    return false;
	sharedObjects.put(name, new Entry(extension, name, sharedObject));
	return true;
    }

Object getSharedObject(String name)
    {
	NullCheck.notNull(name, "name");
	if (!sharedObjects.containsKey(name))
	    return null;
	return sharedObjects.get(name).sharedObject.getSharedObject();
    }

String[] getSharedObjectsNames()
    {
	final LinkedList<String> res = new LinkedList<String>();
	for(Map.Entry<String, Entry> e: sharedObjects.entrySet())
	    res.add(e.getKey());
	final String[] str = res.toArray(new String[res.size()]);
	Arrays.sort(str);
	return str;
    }

    void createStandardObjects(Environment env)
    {
	NullCheck.notNull(env, "env");
	if (partitionsPopupControl == null)
	    partitionsPopupControl = new PartitionsPopupControl(env.getObjForEnvironment(), env.getHardware());

	add(null, new SharedObject(){
		@Override public String getName()
		{
		    return "luwrain.partitionspopupcontrol";
		}
		@Override public Object getSharedObject()
		{
		    return partitionsPopupControl;
		}
	    });
    }
}
