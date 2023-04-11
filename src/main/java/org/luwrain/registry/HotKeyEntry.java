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

package org.luwrain.registry;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.io.json.*;

import static org.luwrain.core.Registry.*;

import static org.luwrain.core.NullCheck.*;

public final class HotKeyEntry
{
    private final Registry registry;
    private final String path;
    private final org.luwrain.core.Settings.HotKey entry;

    public HotKeyEntry(Registry registry, String path)
    {
	notNull(registry, "registry");
	notEmpty(path, "path");
	this.registry = registry;
	this.path = path;
	this.entry = Settings.createHotKey(registry, path);
    }

    public InputEvent[] getKeys()
    {
	final List<InputEvent> res = new ArrayList<>();
	if (!entry.getSpecial("").isEmpty() || !entry.getCharacter("").isEmpty())
	{
	    final EnumSet<InputEvent.Modifiers> m = EnumSet.noneOf(InputEvent.Modifiers.class);
	    if (entry.getWithAlt(false))
		m.add(InputEvent.Modifiers.ALT);
	    if (entry.getWithControl(false))
		m.add(InputEvent.Modifiers.CONTROL);
	    if (entry.getWithShift(false))
		m.add(InputEvent.Modifiers.SHIFT);
	    if (!entry.getCharacter("").isEmpty())
		res.add(new InputEvent(entry.getCharacter("").charAt(0), m)); else
		if (!entry.getSpecial("").isEmpty())
		{
		    final InputEvent.Special sp = InputEvent.translateSpecial(entry.getSpecial(""));
		    if (sp != null)
			res.add(new InputEvent(sp, m));
		}
	}
	return res.toArray(new InputEvent[res.size()]);
    }

    public void setKeys()
    {
    }
}
