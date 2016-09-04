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

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.NullCheck;

public class FixedListModel implements ListArea.Model
{
    private Object[] items;

    public FixedListModel()
    {
	items = new Object[0];
    }

    public FixedListModel(Object[] items)
    {
	NullCheck.notNullItems(items, "items");
	this.items = items;
    }

    public void setItems(Object[] items)
    {
			  NullCheck.notNullItems(items, "items");
			  this.items = items;
    }

    public void clear()
    {
	items = new Object[0];
    }

    public Object[] getItems()
    {
	return items;
    }

    public void add(Object o)
    {
	NullCheck.notNull(o, "o");
	items = Arrays.copyOf(items, items.length + 1);
	items[items.length - 1] = o;
    }

    public boolean delete(int index)
    {
	if (index < 0 || index >= items.length)
	    return false;
	if (index + 1 < items.length)
	    for(int i = index;i < items.length - 1;++i)
		items[index] = items[index + 1];
	items = Arrays.copyOf(items, items.length - 1);
	return true;
    }

    @Override public int getItemCount()
    {
	return items.length;
    }

    @Override public Object getItem(int index)
    {
	return index < items.length?items[index]:null;
    }

    @Override public void refresh()
    {
    }

    @Override public boolean toggleMark(int index)
    {
	return false;
    }
}
