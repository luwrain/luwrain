/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import org.luwrain.base.*;
import org.luwrain.core.extensions.*;

final class ObjRegistry
{
    static private final String LOG_COMPONENT = Base.LOG_COMPONENT;

    static private final class Entry<E> 
    {
	final Extension ext;
	final String name;
	final E obj;

	Entry(Extension ext, String name, E obj)
	{
	    NullCheck.notEmpty(name, "name");
	    NullCheck.notNull(obj, "obj");
	    this.ext = ext;
	    this.name = name;
	    this.obj = obj;
	}
    }

    private Map<String, Entry<Shortcut>> shortcuts = new HashMap();
    private Map<String, Entry<CommandLineTool>> cmdLineTools = new HashMap();

    boolean add(Extension ext, ExtensionObject obj)
    {
	NullCheck.notNull(obj, "obj");
	final String name = obj.getExtObjName();
	if (name == null || name.trim().isEmpty())
	    return false;
	boolean res = false;
	if (obj instanceof CommandLineTool)
	{
	    final CommandLineTool tool = (CommandLineTool)obj;
	    if (!cmdLineTools.containsKey(name))
	    {
		cmdLineTools.put(name, new Entry(ext, name, tool));
		res = true;
	    }
	}
	if (obj instanceof Shortcut)
	{
	    final Shortcut shortcut = (Shortcut)obj;
	    if (!shortcuts.containsKey(name))
	    {
		shortcuts.put(name, new Entry(ext, name, shortcut));
		res = true;
	    }
	}
	if (!res)
	    Log.warning(LOG_COMPONENT, "failed to add an extension object of class " + obj.getClass().getName() + " with name \'" + name + "\'");
	return res;
    }

    CommandLineTool getCommandLineTool(String name)
    {
	NullCheck.notEmpty(name, "name");
	if (!cmdLineTools.containsKey(name))
	    return null;
	return cmdLineTools.get(name).obj;
    }

    String[] getCmdLineToolNames()
    {
	final List<String> res = new LinkedList();
	for(Map.Entry<String, Entry<CommandLineTool>> e: cmdLineTools.entrySet())
	    res.add(e.getKey());
	final String[] str = res.toArray(new String[res.size()]);
	Arrays.sort(str);
	return str;
    }

    Shortcut getShortcut(String name)
    {
	NullCheck.notEmpty(name, "name");
	if (!shortcuts.containsKey(name))
	    return null;
	return shortcuts.get(name).obj;
    }

    String[] getShortcutNames()
    {
	final List<String> res = new LinkedList();
	for(Map.Entry<String, Entry<Shortcut>> e: shortcuts.entrySet())
	    res.add(e.getKey());
	final String[] str = res.toArray(new String[res.size()]);
	Arrays.sort(str);
	return str;
    }

    Application[] prepareApp(String name, String[] args)
    {
	NullCheck.notEmpty(name, "name");
	NullCheck.notNullItems(args, "args");
	final Shortcut shortcut = getShortcut(name);
	if (shortcut == null)
	    return null;
	return shortcut.prepareApp(args);
    }
}
