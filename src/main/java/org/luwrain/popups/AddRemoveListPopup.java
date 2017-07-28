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
import org.luwrain.controls.*;

public class AddRemoveListPopup extends ListPopupBase
{
    public interface RemoveConfirmation
    {
	boolean mayRemove(Object o);
    }

    public interface ItemsSource
    {
	Object getNewItemToAdd();
    }

    protected final RemoveConfirmation removeConfirmation;
    protected final ItemsSource itemsSource;

    public AddRemoveListPopup(Luwrain luwrain, String name,
			      Object[] items, ItemsSource itemsSource, RemoveConfirmation removeConfirmation,
			      Set<Popup.Flags> popupFlags)
    {
	super(luwrain, constructParams(luwrain, items, name), popupFlags);
	NullCheck.notNull(itemsSource, "itemsSource");
	NullCheck.notNull(removeConfirmation, "removeConfirmation");
	this.itemsSource = itemsSource;
	this.removeConfirmation = removeConfirmation;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case INSERT:
		tryToAdd();
		return true;
	    case DELETE:
		return tryToRemove();
	    }
	return super.onKeyboardEvent(event);
    }

    //Returns true if new item has been added
    public boolean tryToAdd()
    {
	final Object newObj = itemsSource.getNewItemToAdd();
	if (newObj == null)
	    return false;
	final ListUtils.FixedModel m = (ListUtils.FixedModel)listModel;
	m.add(newObj);
	refresh();
	return true;
    }

    //Returns true if there is an item which we can try to remove
    public boolean tryToRemove()
    {
	final Object item = selected();
	if (item == null)
	    return false;
	if (!removeConfirmation.mayRemove(item))
	    return true;
	final ListUtils.FixedModel m = (ListUtils.FixedModel)listModel;
	m.remove(item);
	refresh();
	return true;
    }

    public Object[] result()
    {
	final ListUtils.FixedModel m = (ListUtils.FixedModel)listModel;
	return m.getItems();
    }

    static private ListArea.Params constructParams(Luwrain luwrain, 
						   Object[] items, String name)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNullItems(items, "items");
	NullCheck.notNull(name, "name");
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlEnvironment(luwrain);
	params.name = name;
	params.model = new ListUtils.FixedModel(items);
	params.appearance = new ListUtils.DefaultAppearance(params.context, Suggestions.LIST_ITEM);
	return params;
    }
}
