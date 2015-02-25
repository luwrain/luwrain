/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.popups;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class EditListPopup extends SimpleEditPopup
{
    private final static int MAX_ALTERNATIVES_TO_SAY = 100;

    protected Luwrain luwrain;
    private EditListPopupModel model;

    public EditListPopup(Luwrain luwrain,
		     EditListPopupModel model,
		     String name,
		     String prefix,
		     String text)
    {
	super(luwrain, name, prefix, text);
	this.luwrain = luwrain;
	this.model = model;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (model == null)
	    throw new NullPointerException("model may not be null");
    }

    public EditListPopup(Luwrain luwrain,
		     EditListPopupModel model,
		     String name,
		     String prefix,
		     String text,
		     boolean noMultipleCopies)
    {
	super(luwrain, name, prefix, text, noMultipleCopies);
	this.luwrain = luwrain;
	this.model = model;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (model == null)
	    throw new NullPointerException("model may not be null");
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	if (event.isCommand() && !event.isModified())
	    switch(event.getCommand())
	    {
	    case KeyboardEvent.TAB:
		onTab();
		return true;
	    case KeyboardEvent.ARROW_DOWN:
		onKeyDown();
		return true;
	    case KeyboardEvent.ARROW_UP:
		onKeyUp();
		return true;
	    default:
		return super.onKeyboardEvent(event);
	    }
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	return super.onEnvironmentEvent(event);
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

    private void onKeyUp()
    {
	final String item = model.getListPopupPreviousItem(getTextBeforeHotPoint());
	if (item == null)
	{
	    luwrain.hint(Hints.NO_ITEMS_ABOVE);
	    return;
	}
	if (item.isEmpty())
	    luwrain.hint(Hints.EMPTY_LINE); else
	    luwrain.say(item);
	setText(item, "");
    }

    private void onKeyDown()
    {
	final String item = model.getListPopupNextItem(getTextBeforeHotPoint());
	if (item == null)
	{
	    luwrain.hint(Hints.NO_ITEMS_BELOW);
	    return;
	}
	if (item.isEmpty())
	    luwrain.hint(Hints.EMPTY_LINE); else
	    luwrain.say(item);
	setText(item, "");
    }
}
