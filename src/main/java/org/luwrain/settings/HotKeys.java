/*
   Copyright 2012-2023 Michael Pozhidaev <msp@luwrain.org>

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
import org.luwrain.registry.*;

import static org.luwrain.core.NullCheck.*;
import static org.luwrain.core.Registry.*;

final class HotKeys extends ListArea<HotKeys.Item> implements SectionArea
{
    private final ControlPanel controlPanel;

    HotKeys(ControlPanel controlPanel, ListArea.Params<Item> params)
    {
	super(params);
	notNull(controlPanel, "controlPanel");
	this.controlPanel = controlPanel;
	setListClickHandler((area, index, item)->editItem(item));
    }

        @Override public boolean saveSectionData()
    {
	return true;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	if (controlPanel.onInputEvent(event))
	    return true;
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	if (controlPanel.onSystemEvent(event))
	    return true;
	return super.onSystemEvent(event);
    }

    private boolean editItem(Item item)
    {
	return false;
    }

    static private Item[] loadItems(Luwrain luwrain)
    {
	final ArrayList<Item> res = new ArrayList<>();
	for(String d: luwrain.getRegistry().getDirectories(Settings.GLOBAL_KEYS_PATH))
	{
	    res.add(new Item(luwrain, d));
	}
final Item[] toSort = res.toArray(new Item[res.size()]);
Arrays.sort(toSort);
return toSort;
    }

    static HotKeys create(ControlPanel controlPanel)
    {
	notNull(controlPanel, "controlPanel");
	final Luwrain luwrain = controlPanel.getCoreInterface();
	final ListArea.Params<Item> params = new ListArea.Params<>();
	params.context = new DefaultControlContext(luwrain);
	params.appearance = new ListUtils.DefaultAppearance<>(params.context, Suggestions.LIST_ITEM);
	params.name = "Общие горячие клавиши";//FIXME:
	params.model = new ListUtils.FixedModel<>(loadItems(luwrain));
	return new HotKeys(controlPanel, params);
    }

        static final class Item implements Comparable
    {
		final Settings.HotKey sett;
	final String path, command, title;
	final HotKeyEntry entry;
	final InputEvent[] events;
	Item(Luwrain luwrain, String command)
	{
	    this.path = join(Settings.GLOBAL_KEYS_PATH, command);
	    this.sett = Settings.createHotKey(luwrain.getRegistry(), path);
	    this.entry = new HotKeyEntry(luwrain.getRegistry(), path);
	    	    this.command = command;
	    this.events = entry.getKeys();
	    this.title = luwrain.i18n().getCommandTitle(command);
	}
	@Override public String toString()
	{
	    if (events.length == 0)
	    return title;
	    final StringBuilder b = new StringBuilder();
	    b.append(title).append(": ");
	    for(InputEvent e: events)
		b.append(hotKeyToString(e));
	    return new String(b);
	}
	@Override public int compareTo(Object o)
	{
	    if (o == null || !(o instanceof Item))
		return 0;
	    return command.compareTo(((Item)o).command);
	}
    static private String hotKeyToString(InputEvent event)
    {
	final StringBuilder b = new StringBuilder();
	if (event.withControl())
	    b.append("Ctrl+");
	if (event.withAlt())
	    b.append("Alt+");
	if (event.withShift())
	    b.append("Shift+");
	if (!event.isSpecial())
	    b.append(Character.toString(Character.toUpperCase(event.getChar()))); else
	b.append(event.getSpecial().toString());
	return new String(b);
    }
}
}
