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
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;

import com.google.gson.*;

public final class Clipboard implements ClipboardOwner, java.util.function.Supplier
{
    static private final String LOG_COMPONENT = Base.LOG_COMPONENT;

    private final Gson gson = new Gson();
    private Obj<?>[] objs = null;
    private String systemClipboard = null;

    public <E> boolean set(E[] o)
    {
	if (o == null)
	    return false;
	for(int i = 0;i < o.length;i++)
	    if (o[i] == null || o[i].getClass().isArray())
		return false;
		this.objs = new Obj[o.length];
	for(int i = 0;i < o.length;i++)
	    this.objs[i] = saveObj(o[i], o[i].toString());
	setSystemClipboard();
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
	setSystemClipboard();
	return true;
    }

    public boolean set(Object o)
    {
	if (o == null || o.getClass().isArray())
	    return false;
	return set(new Object[]{o});
    }

    private void setSystemClipboard()
    {
	if (this.objs == null || this.objs.length == 0)
	    return;
	final StringBuilder b = new StringBuilder();
	final String lineSep = System.lineSeparator();
	for(Obj o: this.objs)
	    b.append(o.str).append(lineSep);
	try {
	    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(new String(b)), this);
	}
	catch(Throwable e)
	{
	    Log.error(LOG_COMPONENT, "unable to set the system clipboard: " + e.getClass().getName() + ": " + e.getMessage());
	    e.printStackTrace();
	}
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
	b.append(str[0]);
	for(int i = 1;i < str.length;i++)
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

    @SuppressWarnings("unchecked")
    private <E> Obj saveObj(E o, String s)
    {
	NullCheck.notNull(o, "o");
	NullCheck.notNull(s, "s");
	if (o instanceof String)
	    return new Obj<String>(String.class, null, o.toString(), (String)o);
	if (o instanceof java.net.URL)
	    return new Obj<java.net.URL>(java.net.URL.class, null, s, (java.net.URL)o);
	    if (o instanceof java.io.File)
			    return new Obj<java.io.File>(java.io.File.class, null, s, (java.io.File)o);
	       if (o instanceof java.net.URI)
	    return new Obj<java.net.URI>(java.net.URI.class, null, s, (java.net.URI)o);
	final StringWriter w = new StringWriter();
	gson.toJson(o, w);
	w.flush();
	return new Obj<E>((Class<E>)o.getClass(), w.toString(), s, null);
    }

    private <E> E restore(Obj<E> obj)
    {
	NullCheck.notNull(obj, "obj");
	if (obj.obj != null)
	    return obj.obj;
	NullCheck.notNull(obj.content, "obj.content");
	return gson.fromJson(obj.content, obj.cl);
    }

            @Override public void              lostOwnership(java.awt.datatransfer.Clipboard clipboard, Transferable contents)
    {
	Log.debug(LOG_COMPONENT, "the clipboard lost ownership");
	this.objs = null;
    }

static private final class Obj<E>
{
    final Class<E> cl;
    final String content;
        final String str;
    final E obj;
    Obj(Class<E> cl, String content, String str, E obj)
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
