/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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
import org.luwrain.core.events.*;

public class GlobalKeys
{
    class Item
    {
	public KeyboardEvent event;
	public String actionName;

	public Item(KeyboardEvent event, String actionName)
	{
	    this.event = event;
	    this.actionName = actionName;
	}
    }

    private Vector<Item> items = new Vector<Item>();

    public GlobalKeys()
    {
	addMapping(new KeyboardEvent(true, KeyboardEvent.TAB, ' ', false, false, true, false), "switch-next-app");
	addMapping(new KeyboardEvent(true, KeyboardEvent.TAB, ' ', true, false, false, false), "switch-next-area");
	addMapping(new KeyboardEvent(true, KeyboardEvent.WINDOWS, ' '), "main-menu");
	addMapping(new KeyboardEvent(true, KeyboardEvent.ESCAPE, ' '), "cancel");
	addMapping(new KeyboardEvent(false, 0, 'c', false, true, false, false), "ok");
	addMapping(new KeyboardEvent(false, 0, 'q', false, true, false, false), "quit");
	addMapping(new KeyboardEvent(true, KeyboardEvent.F2, ' '), "save");
	addMapping(new KeyboardEvent(true, KeyboardEvent.F3, ' '), "open");
	addMapping(new KeyboardEvent(true, KeyboardEvent.F4, ' '), "close");

	addMapping(new KeyboardEvent(false, 0, '=', false, false, true, false), "increase-font-size");
	addMapping(new KeyboardEvent(false, 0, '-', false, false, true, false), "decrease-font-size");
    }

    public String getActionName(KeyboardEvent event )
    {
	if (event == null)
	    return null;
	for(int i = 0;i < items.size();i++)
	{
	    Item item = items.get(i);
	    if (item.event.equals(event))
		return item.actionName;
	}
	return null;
    }

    public void addMapping(KeyboardEvent event, String actionName)
    {
	if (event == null || actionName == null)
	    return;
	if (getActionName(event) != null)
	    return;
	items.add(new Item(event, actionName));
    }
}
