/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
    class Entry 
    {
	public Extension extension;
	public String name = "";
	public SharedObject sharedObject;

	public Entry(Extension extension,
		     String name,
		     SharedObject sharedObject)
	{
	    this.extension = extension;
	    this.name = name;
	    this.sharedObject = sharedObject;
	    if (extension == null)
		throw new NullPointerException("extension may not be null");
	    if (name == null)
		throw new NullPointerException("name may not be null");
	    if (name.trim().isEmpty())
		throw new IllegalArgumentException("name may not be empty");
	    if (sharedObject == null)
		throw new NullPointerException("sharedObject may not be null");
	}
    }

    private TreeMap<String, Entry> sharedObjects = new TreeMap<String, Entry>();

    public boolean add(Extension extension, SharedObject sharedObject)
    {
	if (extension == null)
	    throw new NullPointerException("extension may not be null");
	if (sharedObject == null)
	    throw new NullPointerException("sharedObject may not be null");
	final String name = sharedObject.getName();
	if (name == null || name.trim().isEmpty())
	    return false;
	if (sharedObjects.containsKey(name))
	    return false;
	sharedObjects.put(name, new Entry(extension, name, sharedObject));
	return true;
    }

    public Object getSharedObject(String name)
    {
	if (name == null)
	    throw new NullPointerException("name may not be null");
	if (name.trim().isEmpty())
	    throw new IllegalArgumentException("name may not be empty");
	if (!sharedObjects.containsKey(name))
	    return null;
	return sharedObjects.get(name).sharedObject.getSharedObject();
    }

    public String[] getSharedObjectsNames()
    {
	Vector<String> res = new Vector<String>();
	for(Map.Entry<String, Entry> e: sharedObjects.entrySet())
	    res.add(e.getKey());
	String[] str = res.toArray(new String[res.size()]);
	Arrays.sort(str);
	return str;
    }
}
