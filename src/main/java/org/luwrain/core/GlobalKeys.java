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
import org.luwrain.core.events.*;

final class GlobalKeys
{
    private final List<Item> items = new Vector();
    private final Registry registry;

    GlobalKeys(Registry registry)
    {
	NullCheck.notNull(registry, "registry");
	this.registry = registry;
    }

    String getCommandName(InputEvent event )
    {
	NullCheck.notNull(event, "event");
	for(int i = 0;i < items.size();i++)
	{
	    final Item item = items.get(i);
	    if (item.event.equals(event))
		return item.actionName;
	}
	return null;
    }

    void loadFromRegistry()
    {
	final String globalKeysDir = Settings.GLOBAL_KEYS_PATH;
	String[] dirs = registry.getDirectories(Settings.GLOBAL_KEYS_PATH);
	if (dirs != null)
	    for(String s: dirs)
	    {
		final InputEvent event = getInputEventFromRegistry(globalKeysDir + "/" + s);
		if (event != null)
		    addMapping(event, s.trim());
	    }
    }

    private void addMapping(InputEvent event, String actionName)
    {
	NullCheck.notNull(event, "event");
	NullCheck.notNull(actionName, "actionName");
	if (getCommandName(event) != null)
	    return;
	items.add(new Item(event, actionName));
    }

    private InputEvent getInputEventFromRegistry(String path)
    {
	NullCheck.notNull(path, "path");
	final Settings.HotKey proxy = Settings.createHotKey(registry, path);
	InputEvent.Special special = null;
	char c = ' ';
	final String specialStr = proxy.getSpecial("");
	if (!specialStr.trim().isEmpty())
	{
	    special = InputEvent.translateSpecial(specialStr);
	    if (special == null)
	    {
		Log.error("core", "registry path " + path + " tries to use an unknown special keyboard code \'" + specialStr + "\'");
		return null;
	    }
	} else
	{
	    final String charStr = proxy.getCharacter("");
	    if (charStr.isEmpty())
	    {
		Log.error("core", "registry path " + path + " does not contain neither \'special\' nor \'character\' values");
		return null;
	    }
	    c = charStr.charAt(0);
	}
	final boolean withControl = proxy.getWithControl(false);
	final boolean withShift = proxy.getWithShift(false);
	final boolean withAlt = proxy.getWithAlt(false);
	return new InputEvent(special != null, special, c, withShift, withControl, withAlt); 
    }

    static private class Item
    {
	final InputEvent event;
	final String actionName;

	Item(InputEvent event, String actionName)
	{
	    NullCheck.notNull(event, "event");
	    NullCheck.notNull(actionName, "actionName");
	    this.event = event;
	    this.actionName = actionName;
	}
    }
}
