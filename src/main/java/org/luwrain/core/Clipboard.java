/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.io.*;

public final class Clipboard
{
    static private final String LOG_COMPONENT = "core";

    private byte[][] objs = null;
    private String[] strings = null;

    public boolean set(Serializable[] objs)
    {
	NullCheck.notNullItems(objs, "objs");
	final byte[][] newObjs = new byte[objs.length][];
	final String[] newStrings = new String[objs.length];
	for(int i = 0;i < objs.length;++i)
	{
	    newObjs[i] = serialize(objs[i]);
	    if (newObjs[i] == null)
		return false;
	    newStrings[i] = objs[i].toString();
	}
	this.objs = newObjs;
	this.strings = newStrings;
	return true;
    }

    public boolean set(Serializable obj)
    {
	NullCheck.notNull(obj, "obj");
	return set(new Serializable[]{obj});
    }

    public Object[] get()
    {
	if (objs == null || objs.length == 0)
	    return new Object[0];
	final Object[] res = new Object[objs.length];
	for(int i = 0;i < objs.length;++i)
	{
	    res[i] = deserialize(objs[i]);
	    if (res[i] == null)
		return new Object[0];
	}
	return res;
    }

    private byte[] serialize(Serializable obj)
    {
	NullCheck.notNull(obj, "obj");
	final ByteArrayOutputStream s = new ByteArrayOutputStream();
	try {
	    final ObjectOutputStream os = new ObjectOutputStream(s);
	    os.writeObject(obj);
	    os.flush();
	    s.flush();
	    return s.toByteArray();
	}
	catch(IOException e)
	{
	    Log.error(LOG_COMPONENT, "unable to save the object to clipboard:" + e.getClass().getName() + ":" + e.getMessage());
	    return null;
	}
    }

    private Object deserialize(byte[] bytes)
    {
	NullCheck.notNull(bytes, "bytes");
	final ByteArrayInputStream s = new ByteArrayInputStream(bytes);
	try {
	    final ObjectInputStream is = new ObjectInputStream(s);
	    return is.readObject();
	}
	catch(IOException | ClassNotFoundException e)
	{
	    Log.error(LOG_COMPONENT, "unable to read an object from clipboard data:" + e.getClass().getName() + ":" + e.getMessage());
	    return null;
	}
    }
}
