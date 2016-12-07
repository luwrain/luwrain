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

package org.luwrain.settings;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

class FileTypes extends ListArea implements SectionArea
{
    private static class Item implements Comparable
    {
	String extension;
	String[] shortcuts;

	Item(String extension, String[] shortcuts)
	{
	    NullCheck.notNull(extension, "extension");
	    NullCheck.notNullItems(shortcuts, "shortcuts");
	    this.extension = extension;
	    this.shortcuts = shortcuts;
	}

	@Override public String toString()
	{
	    if (shortcuts.length == 0)
		return extension + ": ";
	    final StringBuilder b = new StringBuilder();
	    b.append(extension + ": ");
	    b.append(shortcuts[0]);
	    for(int i = 1;i < shortcuts.length;++i)
		b.append(", " + shortcuts[i]);
	    return new String(b);
	}

	@Override public int compareTo(Object o)
	{
	    if (o == null || !(o instanceof Item))
		return 0;
	    return extension.compareTo(((Item)o).extension);
	}
    }

    private ControlPanel controlPanel;

    FileTypes(ControlPanel controlPanel, ListArea.Params params)
    {
	super(params);
	NullCheck.notNull(controlPanel, "controlPanel");
	this.controlPanel = controlPanel;
	setClickHandler((area, index, obj)->editItem(obj));
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onKeyboardEvent(event))
	    return true;
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onEnvironmentEvent(event))
	    return true;
	return super.onEnvironmentEvent(event);
    }

    @Override public boolean saveSectionData()
    {
	return true;
    }

    private boolean editItem(Object obj)
    {
	return false;
    }

    static private Item[] loadItems(Registry registry)
    {
	NullCheck.notNull(registry, "registry");
	final org.luwrain.core.RegistryKeys registryKeys = new org.luwrain.core.RegistryKeys();
	final LinkedList<Item> res = new LinkedList<Item>();
	for(String s: registry.getValues(Settings.FILE_TYPES_PATH))
	{
	    final String path = Registry.join(Settings.FILE_TYPES_PATH, s);
	    if (s.trim().isEmpty() || registry.getTypeOf(path) != Registry.STRING)
		continue;
	    final String value = registry.getString(path);
	    final LinkedList<String> shortcuts = new LinkedList<String>();
	    for(String ss: value.split(":", -1))
		if (!ss.trim().isEmpty())
		    shortcuts.add(ss.trim());
	    final String[] toSort = shortcuts.toArray(new String[shortcuts.size()]);
	    Arrays.sort(toSort);
	    res.add(new Item(s.trim(), toSort));
	}
	final Item[] toSort = res.toArray(new Item[res.size()]);
	Arrays.sort(toSort);
	return toSort;
    }

    static FileTypes create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	final Luwrain luwrain = controlPanel.getCoreInterface();
	final ListArea.Params params = new ListArea.Params();
	params.environment = new DefaultControlEnvironment(luwrain);
	params.appearance = new DefaultListItemAppearance(params.environment);
	params.name = "Типы файлов";
	params.model = new FixedListModel(loadItems(luwrain.getRegistry()));
	return new FileTypes(controlPanel, params);
    }
}
