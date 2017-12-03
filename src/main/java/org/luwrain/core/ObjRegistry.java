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

    private Map<String, Entry<CommandLineTool>> cmdLineTools = new HashMap();

boolean add(Extension ext, ExtensionObject obj)
    {
	NullCheck.notNull(obj, "obj");
	final String name = obj.getExtObjName();
	if (name == null || name.trim().isEmpty())
	    return false;
	if (obj instanceof CommandLineTool)
	{
	    final CommandLineTool tool = (CommandLineTool)obj;
	if (cmdLineTools.containsKey(name))
	    return false;
cmdLineTools.put(name, new Entry(ext, name, tool));
	return true;
    }
	Log.warning(LOG_COMPONENT, "trying to add an extension object of unknown type " + obj.getClass().getName() + " with name \'" + name + "\'");
	return false;
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
}
