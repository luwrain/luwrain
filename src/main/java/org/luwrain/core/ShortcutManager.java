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

import java.util.*;

class ShortcutManager
{
    class Entry 
    {
	String name = "";
	Shortcut shortcut;

	Entry(String name, Shortcut shortcut)
	{
	    NullCheck.notNull(name, "name");
	    NullCheck.notNull(shortcut, "shortcut");
	    if (name.trim().isEmpty())
		throw new IllegalArgumentException("name may not be empty");
	    this.name = name;
	    this.shortcut = shortcut;
	}
    }

    private final TreeMap<String, Entry> shortcuts = new TreeMap<String, Entry>();

    boolean add(Shortcut shortcut)
    {
	NullCheck.notNull(shortcut, "shortcut");
	final String name = shortcut.getName();
	if (name == null || name.trim().isEmpty())
	    return false;
	if (shortcuts.containsKey(name))
	    return false;
	shortcuts.put(name, new Entry(name, shortcut));
	return true;
    }

    Application[] prepareApp(String name, String[] args)
    {
	NullCheck.notNull(name, "name");
	NullCheck.notNullItems(args, "args");
	if (name.trim().isEmpty())
	    throw new IllegalArgumentException("name may not be empty");
	if (!shortcuts.containsKey(name))
	    return null;
	return shortcuts.get(name).shortcut.prepareApp(args);
    }

    String[] getShortcutNames()
    {
	final Vector<String> res = new Vector<String>();
	for(Map.Entry<String, Entry> e: shortcuts.entrySet())
	    res.add(e.getKey());
	String[] str = res.toArray(new String[res.size()]);
	Arrays.sort(str);
	return str;
    }

    void addBasicShortcuts()
    {
	add(new Shortcut(){
		@Override public String getName()
		{
		    return "registry";
		}
		@Override public Application[] prepareApp(String[] args)
		{
		    Application[] res = new Application[1];
		    res[0] = new org.luwrain.app.registry.RegistryApp();
		    return res;
		}
	    });
    }

    void addOsShortcuts(Luwrain luwrain, Registry registry)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(registry, "registry");
	registry.addDirectory(Settings.OS_SHORTCUTS_PATH);
	for(String s: registry.getDirectories(Settings.OS_SHORTCUTS_PATH))
	{
	    if (s.trim().isEmpty())
		continue;
	    final OsCommands.OsShortcut shortcut = new OsCommands.OsShortcut(luwrain);
	    if (shortcut.init(Settings.createOsShortcut(registry, Registry.join(Settings.OS_SHORTCUTS_PATH, s))))
	    add(shortcut);
	}
    }

}
