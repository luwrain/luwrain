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

//FIXME:ControlEnvironment interface support;

package org.luwrain.popups;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class ListPopup extends SimpleLinePopup
{
    private final static int MAX_ALTERNATIVES_TO_SAY = 100;

    private Luwrain luwrain;//FIXME:
    private ListPopupModel model;

    public ListPopup(Luwrain luwrain,
		     ListPopupModel model,
		     String name,
		     String prefix,
		     String text)
    {
	super(luwrain, name, prefix, text);
	this.model = model;
    }

    public ListPopup(Luwrain luwrain,
		     ListPopupModel model,
		     String name,
		     String prefix,
		     String text,
		     boolean noMultipleCopies)
    {
	super(luwrain, name, prefix, text, noMultipleCopies);
	this.model = model;
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
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

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
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
	    luwrain.say(Langs.staticValue(Langs.BEGIN_OF_LIST));
	    return;
	}
	if (item.isEmpty())
	    luwrain.say(Langs.staticValue(Langs.EMPTY_LINE)); else
	    luwrain.say(item);
	setText(item, "");
    }

    private void onKeyDown()
    {
	final String item = model.getListPopupNextItem(getTextBeforeHotPoint());
	if (item == null)
	{
	    luwrain.say(Langs.staticValue(Langs.END_OF_LIST));
	    return;
	}
	if (item.isEmpty())
	    luwrain.say(Langs.staticValue(Langs.EMPTY_LINE)); else
	    luwrain.say(item);
	setText(item, "");
    }
}
