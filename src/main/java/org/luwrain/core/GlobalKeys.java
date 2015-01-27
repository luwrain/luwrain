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
	if (getActionName(event) != null)
	    return;
	items.add(new Item(event, actionName));
    }

    private KeyboardEvent getKeyboardEventFromRegistry(String path)
    {
	int command = -1;
	char c = ' ';
	if (registry.getTypeOf(path + "/non-character") == Registry.STRING)
	{
	    final String value = registry.getString(path + "/non-character");
	    command = translateNonCharacter(value);
	    if (command < 0)
	    {
		Log.warning("environment", "registry path " + path + " references the unknown non-character action \'" + value + "\'");
		return null;
	    }
	} else
	{
	    if (registry.getTypeOf(path + "/character") != Registry.STRING)
	    {
		Log.warning("environment", "registry path " + path + " does not contain neither \'non-character\' nor \'character\' values");
		return null;
	    }
	    final String value = registry.getString(path + "/character");
	    if (value.isEmpty())
	    {
		Log.warning("environment", "registry value " + path + "/character is empty");
		return null;
	    }
	    c = value.charAt(0);
	}
	boolean withControl = false;
	boolean withShift = false;
	boolean withLeftAlt = false;
	boolean withRightAlt = false;
	if (registry.getTypeOf(path + "/with-control") == Registry.BOOLEAN)
	    withControl = registry.getBoolean(path + "/with-control");
	if (registry.getTypeOf(path + "/with-shift") == Registry.BOOLEAN)
	    withShift = registry.getBoolean(path + "/with-shift");
	if (registry.getTypeOf(path + "/with-left-alt") == Registry.BOOLEAN)
	    withLeftAlt = registry.getBoolean(path + "/with-left-alt");
	if (registry.getTypeOf(path + "/with-right-alt") == Registry.BOOLEAN)
	    withRightAlt = registry.getBoolean(path + "/with-right-alt");
	return new KeyboardEvent(command >= 0, command, c, withShift, withControl, withLeftAlt, withRightAlt); 
    }

    private int translateNonCharacter(String value)
    {
	if (value == null || value.isEmpty())
	    return -1;
	if (value.equals("enter"))
	    return KeyboardEvent.ENTER;
	if (value.equals("backspace"))
	    return KeyboardEvent.BACKSPACE;
	if (value.equals("escape"))
	    return KeyboardEvent.ESCAPE;
	if (value.equals("tab"))
	    return KeyboardEvent.TAB;
	if (value.equals("ARROW_DOWN"))
	    return KeyboardEvent.ARROW_DOWN;
	if (value.equals("arrow-up"))
	    return KeyboardEvent.ARROW_UP;
	if (value.equals("arrow-left"))
	    return KeyboardEvent.ARROW_LEFT;
	if (value.equals("arrow-right"))
	    return KeyboardEvent.ARROW_RIGHT;
	if (value.equals("insert"))
	    return KeyboardEvent.INSERT;
	if (value.equals("delete"))
	    return KeyboardEvent.DELETE;
	    if (value.equals("home"))
		return KeyboardEvent.HOME;
	    if (value.equals("end"))
		return KeyboardEvent.END;
	if (value.equals("page-up"))
	    return KeyboardEvent.PAGE_UP;
	if (value.equals("page-down"))
	    return KeyboardEvent.PAGE_DOWN;
	if (value.equals("f1"))
	    return KeyboardEvent.F1;
	if (value.equals("f2"))
	    return KeyboardEvent.F2;
	if (value.equals("f3"))
	    return KeyboardEvent.F3;
	if (value.equals("f4"))
	    return KeyboardEvent.F4;
	if (value.equals("f5"))
	    return KeyboardEvent.F5;
	if (value.equals("f6"))
	    return KeyboardEvent.F6;
	if (value.equals("f7"))
	    return KeyboardEvent.F7;
	if (value.equals("f8"))
	    return KeyboardEvent.F8;
	if (value.equals("f9"))
	    return KeyboardEvent.F9;
	if (value.equals("f10"))
	    return KeyboardEvent.F10;
	if (value.equals("f11"))
	    return KeyboardEvent.F11;
	if (value.equals("f12"))
	    return KeyboardEvent.F12;
	if (value.equals("windows"))
	    return KeyboardEvent.WINDOWS;
	if (value.equals("context-menu"))
	    return KeyboardEvent.CONTEXT_MENU;
	if (value.equals("shift"))
	    return KeyboardEvent.SHIFT;
	if (value.equals("control"))
	    return KeyboardEvent.CONTROL;
	if (value.equals("left-alt"))
	    return KeyboardEvent.LEFT_ALT;
	if (value.equals("right-alt"))
	    return KeyboardEvent.RIGHT_ALT;
	return -1;
    }
}
