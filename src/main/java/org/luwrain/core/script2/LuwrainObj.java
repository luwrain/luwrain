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

package org.luwrain.core.script2;

import java.io.*;
import java.util.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.base.*;
import org.luwrain.core.*;
import org.luwrain.script2.*;
import org.luwrain.util.*;

final class LuwrainObj implements ProxyObject
{
    final Luwrain luwrain;
    final Map<String, List<Value> > hooks = new HashMap();
    final List<ExtensionObject> extObjs = new ArrayList();
    private final LogObj logObj;

    LuwrainObj(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	this.logObj = new LogObj(luwrain);
    }

    @Override public Object getMember(String name)
    {
	if (name == null)
	    return null;
	switch(name)
	{
	case "addHook":
	    return(ProxyExecutable)this::addHook;
	case "log":
	    return logObj;
	case "readTextFile":
	    return (ProxyExecutable)this::readTextFile;
	case "speak":
	    return (ProxyExecutable)this::speak;
	default:
	    return null;
	}
    }

    @Override public boolean hasMember(String name)
    {
	switch(name)
	{
	case "addHook":
	    case "log":
	    case "readTextFile":
	case "speak":
	    return true;
	default:
	    return false;
	}
    }

    @Override public Object getMemberKeys()
    {
	return ProxyArray.fromArray(
	    "addHook",
	    "log",
	    "readTextFile",
	    "speak"
				    );
    }

    @Override public void putMember(String name, Value value)
    {
	throw new RuntimeException("The Luwrain object doesn't support updating of its variables");
    }

    private Object addHook(Value[] args)
    {
	if (!ScriptUtils.notNullAndLen(args, 2))
	    return false;
	if (!args[0].isString() || !args[1].canExecute())
	    return false;
	final String name = args[0].asString();
	if (name.trim().isEmpty())
	    return false;
	List<Value> h = this.hooks.get(name);
	if (h == null)
	{
	    h = new ArrayList();
	    this.hooks.put(name, h);
	    }
	h.add(args[1]);
		return true;
    }

    private Object readTextFile(Value[] args)
    {
	if (!ScriptUtils.notNullAndLen(args, 1))
	    return new ScriptException("readTextFile takes exactly one non-null argument");
	final String fileName = ScriptUtils.asString(args[0]);
	if (fileName == null || fileName.isEmpty())
	    throw new ScriptException("readTextFile() takes a non-empty string with the name of the file as the furst argument");
	try {
	    final String text = FileUtils.readTextFileSingleString(new File(fileName), "UTF-8");
	    return ProxyArray.fromArray((Object[])FileUtils.universalLineSplitting(text));
	}
	catch(IOException e)
	{
	    throw new ScriptException(e);
	}
    }

    private Object speak(Value[] values)
    {
	if (!ScriptUtils.notNullAndLen(values, 1))
	    return false;
	if (!values[0].isString())
	    return false;
	luwrain.speak(values[0].asString());
	return true;
	
	    
    }
}
