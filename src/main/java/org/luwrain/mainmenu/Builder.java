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
    private CommandEnvironment commandEnv;
    private Registry registry;

    public Builder(Luwrain luwrain, CommandEnvironment commandEnv)
    {
	this.luwrain = luwrain;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	this.commandEnv = commandEnv;
	if (commandEnv == null)
	    throw new NullPointerException("commandEnv may not be null");
	this.strings = (Strings)luwrain.i18n().getStrings("main-menu");
	this.registry = luwrain.getRegistry();
    }

    public MainMenu build()
    {
	return new MainMenu(luwrain, commandEnv, strings, constructItems());
    }

    private Item[] constructItems()
    {
	Vector<Item> items = new Vector<Item>();
	items.add(new EmptyItem());
	items.add(new DateTimeItem(strings));
	items.add(new Separator(true, "Main commands"));//FIXME:
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
	Vector<Item> items = new Vector<Item>();
	for(String s: commandsList)
	    if (s == null || s.trim().isEmpty())
		items.add(new EmptyItem()); else
		items.add(new CommandItem(strings, s, luwrain.i18n().commandTitle(s)));
	//	items.add(new EmptyItem());
	//	items.add(new CommandItem(strings, "quit", luwrain.i18n().commandTitle("quit")));
	return items.toArray(new Item[items.size()]);
    }

    private String[] getCommandsList()
    {
	if (registry.getTypeOf("/org/luwrain/main-menu/content") != Registry.STRING)
	{
	    Log.warning("main-menu", "no registry value /org/luwrain/main-menu/content or its type is not a string");
	    return new String [0];
	}
	final String value = registry.getString("/org/luwrain/main-menu/content");
	if (value.trim().isEmpty())
	    return new String[0];
	return value.split(":");
    }
}
