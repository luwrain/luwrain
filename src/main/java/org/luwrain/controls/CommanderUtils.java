/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

//LWR_API 1.0

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
	    if (!(o1 instanceof CommanderArea.NativeItem) || !(o2 instanceof CommanderArea.NativeItem))
		return 0;
	    final CommanderArea.NativeItem w1 = (CommanderArea.NativeItem)o1;
	    final CommanderArea.NativeItem w2 = (CommanderArea.NativeItem)o2;
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

    static public void defaultEntryAnnouncement(ControlContext context, String name, CommanderArea.EntryType type, boolean marked)
    {
	NullCheck.notNull(context, "context");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(type, "type");
	if (name.trim().isEmpty() && type != EntryType.PARENT)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return;
	}
	final StringBuilder b = new StringBuilder();
	if (marked)
	    b.append(context.getStaticStr("CommanderSelected") + " ");
	b.append(name);
	switch(type)
	{
	case PARENT:
	    context.say(context.getStaticStr("CommanderParentDirectory"));//FIXME:
	    return;
	case DIR:
	    b.append(context.getStaticStr("CommanderDirectory"));
	    break;
	case SYMLINK:
	case SYMLINK_DIR:
	    b.append(context.getStaticStr("CommanderSymlink"));
	    break;
	case SPECIAL:
	    b.append(context.getStaticStr("CommanderSpecial"));
	    break;
	}
	context.playSound(marked?Sounds.ATTENTION:Sounds.LIST_ITEM);
	context.say(new String(b));
    }
}
