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

package org.luwrain.desktop;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;

class Appearance implements ListArea.Appearance
{
    private Luwrain luwrain;
    private Strings strings;

    public Appearance(Luwrain luwrain, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.strings = strings;
    }

    @Override public void announceItem(Object item, Set<Flags> flags)
    {
	NullCheck.notNull(item, "item");
	NullCheck.notNull(flags, "flags");
	if (item instanceof String)
	{
	    final String s = (String)item;
	    if (s.trim().isEmpty())
		luwrain.hint(Hints.EMPTY_LINE); else
		luwrain.say(s);
	    return;
	}
	if (item instanceof UniRefInfo)
	{
	    final UniRefInfo i = (UniRefInfo)item;
	    luwrain.playSound(Sounds.NEW_LIST_ITEM);
	    if (flags.contains(Flags.BRIEF))
		luwrain.say(i.title()); else
		luwrain.say(i.toString());
	    return;
	}
    }

    @Override public String getScreenAppearance(Object item, Set<Flags> flags)
    {
	if (item == null)
	    return "";
	if (item instanceof String)
return (String)item;
	if (item instanceof UniRefInfo)
	{
	    final UniRefInfo i = (UniRefInfo)item;
	    return i.toString();
	}
	return "";
    }

    @Override public int getObservableLeftBound(Object item)
    {
	return 0;
    }

    @Override public int getObservableRightBound(Object item)
    {
	return getScreenAppearance(item, EnumSet.noneOf(Flags.class)).length();
    }
}
