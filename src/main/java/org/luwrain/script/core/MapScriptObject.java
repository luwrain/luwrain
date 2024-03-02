/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.script.core;

import java.io.*;
import java.util.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;

import static org.luwrain.core.NullCheck.*;

public class MapScriptObject implements ProxyObject
{
    final Map<String, Object> members;
    ProxyArray membersCache = null;

    public MapScriptObject(Map<String, Object> members)
    {
	notNull(members, "members");
	this.members = members;
    }

    public MapScriptObject()
    {
	this(new HashMap<>());
    }

    @Override public Object getMember(String name)
    {
	notEmpty(name, "name");
	final Object obj = members.get(name);
	return obj;
	    }

    @Override public boolean hasMember(String name)
    {
	notEmpty(name, "name");
	return members.containsKey(name);
	    }

    @Override public Object getMemberKeys()
    {
	if (membersCache != null)
	    return membersCache;
	final List<String> m = new ArrayList<>();
	for(Map.Entry<String, Object> e: members.entrySet())
	    m.add(e.getKey());
	membersCache = ProxyArray.fromArray((Object[])m.toArray(new String[m.size()]));
	return membersCache;
}

@Override public void putMember(String name, Value value)
{
    notEmpty(name, "name");
    if (value != null)
	members.put(name, value); else
	members.remove(value);
    membersCache = null;
}

    /*
    public ProxyArray array(Object[] a)
    {
	if (a == null)
	    return null;
	return ProxyArray.fromArray(a);
    }
    */

    public MapScriptObject add(String name, Object value)
    {
	notEmpty(name, "name");
	notNull(value, "value");
	members.put(name, value);
	membersCache = null;
	return this;
    }

    public void updateMembersCache()
    {
	membersCache = null;
    }
}
