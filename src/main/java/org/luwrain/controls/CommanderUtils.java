/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.controls;

import org.luwrain.core.*;

import org.luwrain.controls.CommanderArea.EntryType;

public class CommanderUtils
{
    static public class ByNameComparator implements java.util.Comparator
    {
	@Override public int compare(Object o1, Object o2)
	{
	    NullCheck.notNull(o1, "o1");
	    NullCheck.notNull(o2, "o2");
	    if (!(o1 instanceof CommanderArea.SortingItem) || !(o2 instanceof CommanderArea.SortingItem))
		return 0;
	    final CommanderArea.SortingItem w1 = (CommanderArea.SortingItem)o1;
	    final CommanderArea.SortingItem w2 = (CommanderArea.SortingItem)o2;
	    if (w1.getEntryType() == EntryType.PARENT)
		return w2.getEntryType() == EntryType.PARENT?0:-1;
	    if (w2.getEntryType() == EntryType.PARENT)
		return w1.getEntryType() == EntryType.PARENT?0:1;
	    final String name1 = w1.getBaseName().toLowerCase();
	    final String name2 = w2.getBaseName().toLowerCase();
	    if (w1.isDirectory() && w2.isDirectory())
		return name1.compareTo(name2);
	    if (w1.isDirectory())
		return -1;
	    if (w2.isDirectory())
		return 1;
	    return name1.compareTo(name2);
	}
    }

    static public class AllEntriesFilter implements CommanderArea.Filter
    {
	@Override public boolean commanderEntrySuits(Object entry)
	{
	    return true;
	}
    }
}
