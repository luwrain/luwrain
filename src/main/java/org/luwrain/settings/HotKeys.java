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

package org.luwrain.settings;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

class HotKeys extends ListArea implements SectionArea
{
    private static class Item implements Comparable
    {
	KeyboardEvent event;
	Settings.HotKey settings;
	String command;

	Item(Settings.HotKey settings, String command)
	{
	    NullCheck.notNull(settings, "settings");
	    NullCheck.notNull(command, "command");
	    this.settings = settings;
	    this.event = new KeyboardEvent(KeyboardEvent.Special.ENTER);
	    this.command = command;
	}

	@Override public String toString()
	{
	    return command;
	}

	@Override public int compareTo(Object o)
	{
	    if (o == null || !(o instanceof Item))
		return 0;
	    return command.compareTo(((Item)o).command);
	}
    }

    @Override public boolean saveSectionData()
    {
	return true;
    }

    private ControlPanel controlPanel;

    HotKeys(ControlPanel controlPanel, ListArea.Params params)
    {
	super(params);
	NullCheck.notNull(controlPanel, "controlPanel");
	this.controlPanel = controlPanel;
	setListClickHandler((area, index, obj)->editItem(obj));
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onKeyboardEvent(event))
	    return true;
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onSystemEvent(event))
	    return true;
	return super.onSystemEvent(event);
    }


    private boolean editItem(Object obj)
    {
	return false;
    }

    static private Item[] loadItems(Registry registry)
    {
	NullCheck.notNull(registry, "registry");
	final LinkedList<Item> res = new LinkedList<Item>();
	for(String d: registry.getDirectories(Settings.GLOBAL_KEYS_PATH))
	{
	    if (d.trim().isEmpty())
		continue;
	    final String path = Registry.join(Settings.GLOBAL_KEYS_PATH, d);
	    res.add(new Item(Settings.createHotKey(registry, path), d));
	}
final Item[] toSort = res.toArray(new Item[res.size()]);
Arrays.sort(toSort);
return toSort;
    }

    static HotKeys create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	final Luwrain luwrain = controlPanel.getCoreInterface();
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlEnvironment(luwrain);
	params.appearance = new ListUtils.DefaultAppearance(params.context, Suggestions.LIST_ITEM);
	params.name = "Общие горячие клавиши";
	params.model = new ListUtils.FixedModel(loadItems(luwrain.getRegistry()));
	return new HotKeys(controlPanel, params);
    }
}
