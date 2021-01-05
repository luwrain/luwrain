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

//LWR_API 1.0

package org.luwrain.core;

import java.io.*;

import com.google.gson.*;

public final class Clipboard implements java.util.function.Supplier
{
    static private final String LOG_COMPONENT = Base.LOG_COMPONENT;

    private final Gson gson = new Gson();
    private Obj[] objs = null;

    public boolean set(Object[] o)
    {
	if (o == null)
	    return false;
	for(int i = 0;i < o.length;i++)
	    if (o[i] == null || o[i].getClass().isArray())
		return false;
		this.objs = new Obj[o.length];
	for(int i = 0;i < o.length;i++)
	    this.objs[i] = saveObj(o[i], o[i].toString());
	return true;
    }

    public boolean set(Object[] o, String[] s)
    {
	if (o == null || s == null)
	    return false;
	if (o.length != s.length)
	    return false;
	for(int i = 0;i < o.length;i++)
	{
	    if (o[i] == null || s[i] == null)
		return false;
	    if (o[i].getClass().isArray())
		return false;
	}
	this.objs = new Obj[o.length];
	for(int i = 0;i < o.length;i++)
	    this.objs[i] = saveObj(o[i], s[i]);
	return true;
    }

    public boolean set(Object o)
    {
	if (o == null || o.getClass().isArray())
	    return false;
	return set(new Object[]{o});
    }

    @Override public Object[] get()
    {
	if (this.objs == null)
	    return new Object[0];
	final Object[] res = new Object[this.objs.length];
	for(int i = 0;i < this.objs.length;i++)
	    res[i] = restore(this.objs[i]);
	return res;
    }

    public String[] getStrings()
    {
	if (this.objs == null)
	    return new String[0];
	final String[] res = new String[this.objs.length];
	for(int i = 0;i < this.objs.length;i++)
	    res[i] = this.objs[i].str;
	return res;
	    }

    public String getString(String lineSep)
    {
	final String[] str = getStrings();
	if (str.length == 0)
	    return "";
		final StringBuilder b = new StringBuilder();
		for(int i = 0;i < str.length;i++)
		    b.append(str[i]).append(lineSep);
		return new String(b);
    }

    public boolean isEmpty()
    {
	return objs == null || objs.length == 0;
    }

    public void clear()
    {
	this.objs = null;
	    }

    private Obj saveObj(Object o, String s)
    {
	NullCheck.notNull(o, "o");
	NullCheck.notNull(s, "s");
	if (o instanceof String)
	    return new Obj(o.getClass(), null, o.toString(), o);
	if (o instanceof java.net.URL || o instanceof java.io.File ||
	    o instanceof java.net.URI)
	    return new Obj(o.getClass(), null, s, o);
	final StringWriter w = new StringWriter();
	gson.toJson(o, w);
	w.flush();
	return new Obj(o.getClass(), w.toString(), s, null);
    }

    private Object restore(Obj obj)
    {
	NullCheck.notNull(obj, "obj");
	if (obj.obj != null)
	    return obj.obj;
	NullCheck.notNull(obj.content, "obj.content");
	return gson.fromJson(obj.content, obj.cl);
    }

static private final class Obj
{
    final Class cl;
    final String content;
        final String str;
    final Object obj;
    Obj(Class cl, String content, String str, Object obj)
    {
	NullCheck.notNull(cl, "cl");
	NullCheck.notNull(str, "str");
	this.cl = cl;
	this.content = content;
		this.str = str;
	this.obj = obj;
    }
}
}
