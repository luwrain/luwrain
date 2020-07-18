/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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
	setListClickHandler((area, index, obj)->editItem(obj));
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onInputEvent(event))
	    return true;
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onSystemEvent(event))
	    return true;
	return super.onSystemEvent(event);
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
	params.context = new DefaultControlContext(luwrain);
	params.appearance = new ListUtils.DefaultAppearance(params.context, Suggestions.LIST_ITEM);
	params.name = "Типы файлов";
	params.model = new ListUtils.FixedModel(loadItems(luwrain.getRegistry()));
	return new FileTypes(controlPanel, params);
    }
}
