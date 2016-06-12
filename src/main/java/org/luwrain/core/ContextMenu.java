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

import org.luwrain.controls.*;
import org.luwrain.popups.*;

class ContextMenu extends ListPopup
{
    static private class Appearance implements ListArea.Appearance
    {
	private Luwrain luwrain;

	Appearance(Luwrain luwrain)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    this.luwrain = luwrain;
	}

	@Override public void announceItem(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    final Action act = (Action)item;
	    luwrain.silence();
	    luwrain.playSound(Sounds.LIST_ITEM);
	    if (act.keyboardEvent() != null)
		luwrain.say(act.title() + " " + act.keyboardEvent().toString()); else
	    luwrain.say(act.title());
    }

	@Override public String getScreenAppearance(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    final Action act = (Action)item;
	    if (act.keyboardEvent() != null)
		return act.title() + " (" + act.keyboardEvent() + ")"; else
		return act.title();
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

    ContextMenu(Luwrain luwrain, Action[] actions, Strings strings)
    {
	super(luwrain, constructParams(luwrain, actions, strings), EnumSet.noneOf(Popup.Flags.class));
    }

    static private ListArea.Params constructParams(Luwrain luwrain, Action[] actions,
						   Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNullItems(actions, "actions");
	NullCheck.notNull(strings, "strings");
	final ListArea.Params params = new ListArea.Params();
	params.name = strings.contextMenuName();
	params.model = new FixedListModel(actions);
	params.appearance = new Appearance(luwrain);
	params.environment = new DefaultControlEnvironment(luwrain);
	params.flags = ListArea.Params.loadPopupFlags(luwrain.getRegistry());
	return params;
    }
}
