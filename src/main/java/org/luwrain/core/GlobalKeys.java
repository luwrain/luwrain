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

package org.luwrain.core;

import java.util.*;
import org.luwrain.core.events.*;

class GlobalKeys
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
    private Registry registry;

    public GlobalKeys(Registry registry)
    {
	this.registry = registry;
    }

    public String getCommandName(KeyboardEvent event )
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

    public void loadFromRegistry()
    {
	RegistryKeys registryKeys = new RegistryKeys();
	final String globalKeysDir = registryKeys.globalKeysDir();
	String[] dirs = registry.getDirectories(registryKeys.globalKeysDir());
	if (dirs != null)
	    for(String s: dirs)
	    {
		KeyboardEvent event = getKeyboardEventFromRegistry(globalKeysDir + "/" + s);
		if (event != null)
		    addMapping(event, s.trim());
	    }
    }

    public void addMapping(KeyboardEvent event, String actionName)
    {
	if (event == null || actionName == null)
	    return;
	if (getCommandName(event) != null)
	    return;
	items.add(new Item(event, actionName));
    }

    private KeyboardEvent getKeyboardEventFromRegistry(String path)
    {
	NullCheck.notNull(path, "path");
	final Settings.HotKey proxy = Settings.createHotKey(registry, path);
	KeyboardEvent.Special special = null;
	char c = ' ';
	final String specialStr = proxy.getSpecial("");
	if (!specialStr.trim().isEmpty())
	{
	    special = KeyboardEvent.translateSpecial(specialStr);
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
	final boolean withLeftAlt = proxy.getWithAlt(false);
	//	final boolean withRightAlt = proxy.getWithAlt(false);
	final boolean withRightAlt = false;
	return new KeyboardEvent(special != null, special, c, withShift, withControl, withLeftAlt, withRightAlt); 
    }
}
