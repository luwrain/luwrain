/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

import java.util.*;

abstract public class Lang
{
    private TreeMap<String, String> commandTitles = new TreeMap<String, String>();
    private TreeMap<String, Object> stringObjs = new TreeMap<String, Object>();

    abstract public String staticStr(int code);
    abstract public String hasSpecialNameOfChar(char ch);

    public String commandTitle(String command)
    {
	if (command == null)
	    throw new NullPointerException("command may not be null");
	    if (command.isEmpty())
		throw new IllegalArgumentException("command may not be empty");
	    if (!commandTitles.containsKey(command))
		return command;
	    return commandTitles.get(command);
    }

	protected void addCommandTitle(String command, String title)
	{
	    if (command == null)
		throw new NullPointerException("command may not be null");
	    if (command.isEmpty())
		throw new IllegalArgumentException("command may not be empty");
	    if (title == null)
		throw new NullPointerException("title may not be null");
	    if (title.isEmpty())
		throw new IllegalArgumentException("title may not be empty");
	    commandTitles.put(command, title);
	}

    public Object strings(String component)
    {
	if (component == null)
	    throw new NullPointerException("component may not be null");
	    if (component.isEmpty())
		throw new IllegalArgumentException("component may not be empty");
	    if (!stringObjs.containsKey(component))
		return null;
	    return stringObjs.get(component);
    }

	protected void addStrings(String component, Object obj)
	{
	    if (component == null)
		throw new NullPointerException("component may not be null");
	    if (component.isEmpty())
		throw new IllegalArgumentException("component may not be empty");
	    if (obj == null)
		throw new NullPointerException("obj may not be null");
	    stringObjs.put(component, obj);
	}
}
