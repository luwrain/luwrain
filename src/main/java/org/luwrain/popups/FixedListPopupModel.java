/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.util.*;

public class FixedListPopupModel extends DynamicListPopupModel
{
    private EditListPopupItem[] fixedItems;

    public FixedListPopupModel(String[] items)
    {
	if (items == null)
	    throw new NullPointerException("items may not be null");
	Vector<EditListPopupItem> v = new Vector<EditListPopupItem>();
	for(String s: items)
	    if (s != null && !s.isEmpty())
		v.add(new EditListPopupItem(s));
	fixedItems = new EditListPopupItem[v.size()];
	for(int i = 0;i < v.size();++i)
	    fixedItems[i] = new EditListPopupItem(items[i]);
	    Arrays.sort(fixedItems);
    }

    @Override protected EditListPopupItem[] getItems(String context)
    {
	//Returning every time the same items regardless the context;
	return fixedItems;
    }

    @Override protected EditListPopupItem getEmptyItem(String context)
    {
	return new EditListPopupItem();
    }
}
