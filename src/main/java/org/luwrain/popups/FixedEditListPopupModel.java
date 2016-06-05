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

import org.luwrain.popups.EditListPopup.Item;

public class FixedEditListPopupModel extends DynamicEditListPopupModel
{
    protected EditListPopup.Item[] fixedItems;

    public FixedEditListPopupModel(String[] items)
    {
	if (items == null)
	    throw new NullPointerException("items may not be null");
	final Vector<Item> v = new Vector<Item>();
	for(String s: items)
	    if (s != null && !s.isEmpty())
		v.add(new Item(s));
	fixedItems = new Item[v.size()];
	for(int i = 0;i < v.size();++i)
	    fixedItems[i] = new Item(items[i]);
	    Arrays.sort(fixedItems);
    }

    @Override protected Item[] getItems(String context)
    {
	//Returning every time the same items regardless the context;
	return fixedItems;
    }

    @Override protected Item getEmptyItem(String context)
    {
	return new Item();
    }
}
