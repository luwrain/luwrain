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

package org.luwrain.popups;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class EditListPopup extends SimpleEditPopup
{
    static private final int MAX_ALTERNATIVES_TO_SAY = 100;

    static public class Item implements Comparable
    {
	public final String value;
	public final String announcement;

	public Item()
	{
	    value = "";
	    announcement = "";
	}

	public Item(String value, String announcement)
	{
	    NullCheck.notNull(value, "value");
	    NullCheck.notNull(announcement, "announcement");
	    this.value = value;
	    this.announcement = announcement;
	}

	public Item(String value)
	{
	    NullCheck.notNull(value, "value");
	    this.value = value;
	    this.announcement = value;
	}

	/*
	public String value() { return value; }
	public String announcement() { return announcement; }
	*/
	@Override public String toString()
	{
	    return value;
	}

	@Override public int compareTo(Object o)
	{
	    return value.compareTo(o.toString());
	}
    }

    public interface Model
    {
	String getCompletion(String beginning);
	String[] getAlternatives(String beginning);
	//May return null, that means no item
	//Empty value is a usual valid value
	Item getListPopupPreviousItem(String text);
	//May return null, that means no item
	//Empty value is a usual valid value
	Item getListPopupNextItem(String text);
    }

    protected final Model model;

    public EditListPopup(Luwrain luwrain, EditListPopup.Model model,
			 String name, String prefix,
			 String text, Set<Popup.Flags> popupFlags)
    {
	super(luwrain, name, prefix, text, popupFlags);
	NullCheck.notNull(model, "model");
	this.model = model;
    }

    @Override public boolean onInputEvent(KeyboardEvent event)
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
		return super.onInputEvent(event);
	    }
	return super.onInputEvent(event);
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
		luwrain.speak(getSpokenText(completion));
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
	luwrain.speak(res);
    }

    private void onKeyUp(boolean briefIntroduction)
    {
	final Item item = model.getListPopupPreviousItem(getTextBeforeHotPoint());
	if (item == null)
	{
	    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.NO_ITEMS_ABOVE));
	    return;
	}
	final String value = briefIntroduction?item.announcement:item.value;
	if (value.isEmpty())
	    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE)); else
	    luwrain.speak(getSpokenText(value));
	setText(item.value, "");
    }

    private void onKeyDown(boolean briefIntroduction)
    {
	final Item item = model.getListPopupNextItem(getTextBeforeHotPoint());
	if (item == null)
	{
	    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.NO_ITEMS_BELOW));
	    return;
	}
	final String value = briefIntroduction?item.announcement:item.value;
	if (value.isEmpty())
	    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE)); else
	    luwrain.speak(getSpokenText(value));
	setText(item.value, "");
    }
}
