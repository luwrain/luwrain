/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core.shell.desktop;

import java.util.*;

import org.luwrain.core.*;

final class Storing extends Vector<UniRefInfo>
{
    private final Luwrain luwrain;
    private final Registry registry;

    Storing(Luwrain luwrain)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    this.luwrain = luwrain;
	    this.registry = luwrain.getRegistry();
	}

    UniRefInfo[] getAll()
    {
	return toArray(new UniRefInfo[size()]);
    }

    void load()
    {
	registry.addDirectory(Settings.DESKTOP_UNIREFS_PATH);
	final String[] values = registry.getValues(Settings.DESKTOP_UNIREFS_PATH);
	for(String v: values)
	{
	    final String path = Registry.join(Settings.DESKTOP_UNIREFS_PATH, v);
	    if (registry.getTypeOf(path) != Registry.STRING)
		continue;
	    final String s = registry.getString(path);
	    if (s.isEmpty())
		continue;
	    final UniRefInfo uniRef = luwrain.getUniRefInfo(s);
	    if (uniRef != null)
		add(uniRef);
	}
    }

    void save()
    {
	for(String v: registry.getValues(Settings.DESKTOP_UNIREFS_PATH))
	    registry.deleteValue(Registry.join(Settings.DESKTOP_UNIREFS_PATH, v));
	final UniRefInfo[] uniRefs = getAll();
	for(int i = 0;i < uniRefs.length;++i)
	{
	    String name = "" + (i + 1);
	    while (name.length() < 6)
		name = "0" + name;
	    registry.setString(Registry.join(Settings.DESKTOP_UNIREFS_PATH, name), uniRefs[i].getValue());
	}
    }
}
