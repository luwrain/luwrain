/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
	this.items = items;
	NullCheck.notNull(items, "items");
	NullCheck.notNullItems(items, "items");
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
