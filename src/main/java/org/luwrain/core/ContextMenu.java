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
    /*
    static private class Model implements ListArea.Model
    {
	private Action[] actions;

	Model(Action[] actions)
	{
	    this.actions = actions;
	    NullCheck.notNull(actions, "actions");
	}

	@Override public int getItemCount()
	{
	    return actions.length;
	}

	@Override public Object getItem(int index)
	{
	    return actions[index];
	}

	@Override public boolean toggleMark(int index)
    {
	return false;
    }

	@Override public void refresh()
	{
	}
    }
    */

    static private class Appearance implements ListItemAppearance
    {
	private Luwrain luwrain;

	Appearance(Luwrain luwrain)
	{
	    this.luwrain = luwrain;
	    NullCheck.notNull(luwrain, "luwrain");
	}

	@Override public void introduceItem(Object item, int flags)
	{
	    if (item == null || !(item instanceof Action))
		return;
	    final Action act = (Action)item;
	    luwrain.silence();
	    luwrain.playSound(Sounds.NEW_LIST_ITEM);
	    if (act.keyboardEvent() != null)
		luwrain.say(act.title() + " " + act.keyboardEvent().toString()); else
	    luwrain.say(act.title());
    }

	@Override public String getScreenAppearance(Object item, int flags)
	{
	    if (item == null || !(item instanceof Action))
		return "";
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
	    return getScreenAppearance(item, 0).length();
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
	return params;
    }
}
