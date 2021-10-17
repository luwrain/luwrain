/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core.shell;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.popups.*;

public final class ContextMenu extends ListPopup<Action>
{
    public ContextMenu(Luwrain luwrain, Action[] actions)
    {
	super(luwrain, createParams(luwrain, actions), EnumSet.noneOf(Popup.Flags.class));
    }

    static private ListArea.Params<Action> createParams(Luwrain luwrain, Action[] actions)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNullItems(actions, "actions");
	final ListArea.Params<Action> params = new ListArea.Params<>();
		params.context = new DefaultControlContext(luwrain);
	params.name = luwrain.i18n().getStaticStr("ContextMenuName");
	params.model = new ListUtils.FixedModel<>(actions);
	params.appearance = new Appearance(luwrain);
	params.flags = EnumSet.of(ListArea.Flags.EMPTY_LINE_TOP);
	params.transition = new Transition();
	return params;
    }

    static private final class Appearance extends ListUtils.AbstractAppearance<Action>
    {
	private final Luwrain luwrain;
	Appearance(Luwrain luwrain)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    this.luwrain = luwrain;
	}
	@Override public void announceItem(Action action, Set<Flags> flags)
	{
	    NullCheck.notNull(action, "action");
	    NullCheck.notNull(flags, "flags");
	    luwrain.silence();
	    luwrain.playSound(Sounds.MAIN_MENU_ITEM);
	    if (action.inputEvent() != null)
		luwrain.speak(action.title() + " " + luwrain.getSpeakableText(action.inputEvent().toString(), Luwrain.SpeakableTextType.PROGRAMMING)); else
		luwrain.speak(action.title());
	}
	@Override public String getScreenAppearance(Action action, Set<Flags> flags)
	{
	    NullCheck.notNull(action, "action");
	    NullCheck.notNull(flags, "flags");
	    if (action.inputEvent() != null)
		return action.title() + " (" + action.inputEvent() + ")"; else
		return action.title();
	}
    }

    static private final class Transition extends ListUtils.DefaultTransition
    {
	@Override public State transition(Type type, State fromState, int itemCount,
					  boolean hasEmptyLineTop, boolean hasEmptyLineBottom)
	{
	    NullCheck.notNull(type, "type");
	    NullCheck.notNull(fromState, "fromState");
	    if (itemCount == 0)
		throw new IllegalArgumentException("itemCount must be greater than zero");
	    switch(type)
	    {
	    case SINGLE_DOWN:
		if (fromState.type != State.Type.ITEM_INDEX || fromState.itemIndex + 1 != itemCount)
		    return super.transition(type, fromState, itemCount, hasEmptyLineTop, hasEmptyLineBottom);
		return new State(0);
	    case SINGLE_UP:
		if (fromState.type != State.Type.ITEM_INDEX || fromState.itemIndex != 0)
		    return super.transition(type, fromState, itemCount, hasEmptyLineTop, hasEmptyLineBottom);
		return new State(itemCount - 1);
	    default:
		return super.transition(type, fromState, itemCount, hasEmptyLineTop, hasEmptyLineBottom);
	    }
	}
    }
}
