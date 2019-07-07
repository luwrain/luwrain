/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

final class CommandManager
{
    private final Map<String, Entry> commands = new TreeMap();

    boolean add(Luwrain luwrain, Command command)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(command, "command");
	final String name = command.getName();
	if (name == null || name.trim().isEmpty())
	    return false;
	if (commands.containsKey(name))
	    return false;
	commands.put(name, new Entry(luwrain, name, command));
	return true;
    }

    boolean run(String name)
    {
	NullCheck.notEmpty(name, "name");
	if (!commands.containsKey(name))
	    return false;
	final Entry entry = commands.get(name);
	entry.command.onCommand(entry.luwrain);
	return true;
    }

    String[] getCommandNames()
    {
	final List<String> res = new LinkedList();
	for(Map.Entry<String, Entry> e: commands.entrySet())
	    res.add(e.getKey());
	String[] str = res.toArray(new String[res.size()]);
	Arrays.sort(str);
	return str;
    }

    void deleteByInstance(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final List<String> deleting = new LinkedList();
		for(Map.Entry<String, Entry> e: commands.entrySet())
		    if (e.getValue().luwrain == luwrain)
			deleting.add(e.getKey());
		for(String s: deleting)
		    commands.remove(s);
    }

    static private class Entry 
    {
	final Luwrain luwrain;
	final String name;
	final Command command;

	Entry(Luwrain luwrain, String name, Command command)
	{
	    NullCheck.notNull(luwrain, "luwrain ");
	    NullCheck.notEmpty(name, "name");
	    NullCheck.notNull(command, "command");
	    this.luwrain = luwrain;
	    this.name = name;
	    this.command = command;
	}
    }
}
