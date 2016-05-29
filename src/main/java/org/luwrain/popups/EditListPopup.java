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

package org.luwrain.popups;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class EditListPopup extends SimpleEditPopup
{
    static private final int MAX_ALTERNATIVES_TO_SAY = 100;

    protected EditListPopupModel model;

    public EditListPopup(Luwrain luwrain, EditListPopupModel model,
			 String name, String prefix,
			 String text, Set<Popup.Flags> popupFlags)
    {
	super(luwrain, name, prefix, text, popupFlags);
	NullCheck.notNull(model, "model");
	this.model = model;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case TAB:
		onTab();
		return true;
	    case ARROW_DOWN:
		onKeyDown(false);
		return true;
	    case ARROW_UP:
		onKeyUp(false);
		return true;
	    case ALTERNATIVE_ARROW_DOWN:
		onKeyDown(true);
		return true;
	    case ALTERNATIVE_ARROW_UP:
		onKeyUp(true);
		return true;
	    default:
		return super.onKeyboardEvent(event);
	    }
	return super.onKeyboardEvent(event);
    }

    private void onTab()
    {
	final String text = getTextBeforeHotPoint();
	final String after = getTextAfterHotPoint();
	if (!text.isEmpty())
	{
	    final String completion = model.getCompletion(text);
	    if (completion != null && !completion.isEmpty())
	    {
		luwrain.say(completion);
		setText(text + completion, after);
		return;
	    }
	}
	String[] alternatives = model.getAlternatives(text);
	if (alternatives == null || alternatives.length < 1)
	    return;
	final int count = alternatives.length <= MAX_ALTERNATIVES_TO_SAY?alternatives.length:MAX_ALTERNATIVES_TO_SAY;
	String res = "";
	for(int i = 0;i < count;++i)
	    res += alternatives[i] + " ";
	luwrain.say(res);
    }

    private void onKeyUp(boolean briefIntroduction)
    {
	final EditListPopupItem item = model.getListPopupPreviousItem(getTextBeforeHotPoint());
	if (item == null)
	{
	    luwrain.hint(Hints.NO_ITEMS_ABOVE);
	    return;
	}
	final String value = briefIntroduction?item.introduction():item.value();
	if (value.isEmpty())
	    luwrain.hint(Hints.EMPTY_LINE); else
	    luwrain.say(value);
	setText(item.value(), "");
    }

    private void onKeyDown(boolean briefIntroduction)
    {
	final EditListPopupItem item = model.getListPopupNextItem(getTextBeforeHotPoint());
	if (item == null)
	{
	    luwrain.hint(Hints.NO_ITEMS_BELOW);
	    return;
	}
	final String value = briefIntroduction?item.introduction():item.value();
	if (value.isEmpty())
	    luwrain.hint(Hints.EMPTY_LINE); else
	    luwrain.say(value);
	setText(item.value(), "");
    }
}
