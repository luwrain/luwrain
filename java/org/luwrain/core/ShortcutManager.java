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

class ShortcutManager
{
    private Environment environment;
    private Vector<Shortcut> shortcuts = new Vector<Shortcut>();

    public ShortcutManager(Environment environment)
    {
	this.environment = environment;
    }

    public void add(Shortcut shortcut)
    {
	if (shortcut != null)
	    shortcuts.add(shortcut);
    }

    public boolean launch(String name, String[] args)
    {
	if (name == null || name.trim().isEmpty())
	    return false;
	Iterator<Shortcut> it = shortcuts.iterator();
	while(it.hasNext())
	{
	    Shortcut s = it.next();
	    if (!s.getName().equals(name))
		continue;
	    s.launch(args);
	    return true;
	}
	return false;
    }

    public void fillWithStandardShortcuts()
    {
	//FIXME:
    }
}
