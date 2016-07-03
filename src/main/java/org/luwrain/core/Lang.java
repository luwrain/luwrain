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

abstract public class Lang
{
    protected final TreeMap<String, String> commandTitles = new TreeMap<String, String>();
    protected final TreeMap<String, Object> stringObjs = new TreeMap<String, Object>();

    abstract public String staticStr(int code);
    abstract public String getStaticStr(String id);
    abstract public String hasSpecialNameOfChar(char ch);

    public String getCommandTitle(String command)
    {
	NullCheck.notNull(command, "command");
	    if (!commandTitles.containsKey(command))
		return command;
	    return commandTitles.get(command);
    }

    public Object strings(String component)
    {
	NullCheck.notNull(component, "component");
	    if (!stringObjs.containsKey(component))
		return null;
	    return stringObjs.get(component);
    }

	protected void addCommandTitle(String command, String title)
	{
	    NullCheck.notNull(command, "command");
	    NullCheck.notNull(title, "title");
	    if (command.trim().isEmpty())
		throw new IllegalArgumentException("command may not be empty");
	    if (title.isEmpty())
		throw new IllegalArgumentException("title may not be empty");
	    commandTitles.put(command, title);
	}

	protected void addStrings(String component, Object obj)
	{
	    NullCheck.notNull(component, "component");
	    NullCheck.notNull(obj, "obj");
	    if (component.isEmpty())
		throw new IllegalArgumentException("component may not be empty");
	    stringObjs.put(component, obj);
	}
}
