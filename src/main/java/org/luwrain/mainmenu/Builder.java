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

package org.luwrain.mainmenu;

import java.util.*;

import org.luwrain.core.*;

public class Builder
{
    private Strings strings;
    private Luwrain luwrain;
    private Registry registry;

    public Builder(Luwrain luwrain)
    {
	this.luwrain = luwrain;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	this.strings = (Strings)luwrain.lang().strings("main-menu");
	this.registry = luwrain.getRegistry();
    }

    public MainMenu build()
    {
	return new MainMenu(luwrain, strings, constructItems());
    }

    private Item[] constructItems()
    {
	Vector<Item> items = new Vector<Item>();
	items.add(new EmptyItem(luwrain));
	items.add(new DateTimeItem(luwrain, strings));
	items.add(new Separator(luwrain, true, "Main commands"));//FIXME:
	Item[] commands = commandItems(getCommandsList());
	for(Item i: commands)
	    items.add(i);
	return items.toArray(new Item[items.size()]);
	//FIXME:
    }

    private Item[] commandItems(String[] commandsList)
    {
	if (commandsList == null)
	    throw new NullPointerException("commandsList may not be null");
	Item[] items = new Item[commandsList.length];
	for(int i = 0;i < commandsList.length;++i)
	{
	    if (commandsList[i] == null)
		throw new NullPointerException("commandsList[" + i + "] may not be null");
	    items[i] = new CommandItem(luwrain, commandsList[i], luwrain.lang().commandTitle(commandsList[i]));
	}
	return items;
    }

    private String[] getCommandsList()
    {
	if (registry.getTypeOf("/org/luwrain/main-menu/content") != Registry.STRING)
	{
	    Log.warning("main-menu", "no registry value /org/luwrain/main-menu/content or its type is not a string");
	    String res[] = new String[1];
	    res[0] = "quit";
	    return res;
	}
	final String value = registry.getString("/org/luwrain/main-menu/content");
	if (value.trim().isEmpty())
	{
	    String res[] = new String[1];
	    res[0] = "quit";
	    return res;
	}
	return value.split(":");
    }
}
