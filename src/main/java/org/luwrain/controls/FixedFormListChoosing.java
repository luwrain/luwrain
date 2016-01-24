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

import org.luwrain.core.*;
import org.luwrain.popups.Popups;

public class FixedFormListChoosing implements FormListChoosing
{
    private Luwrain luwrain;
    private String name;
    private Object[] items;
    private int popupFlags;

    public FixedFormListChoosing(Luwrain luwrain, String name,
			  Object[] items, int popupFlags)
    {
	this.luwrain = luwrain;
	this.name = name;
	this.items = items;
	this.popupFlags = popupFlags;
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(items, "items");
	for(int i = 0;i < items.length;++i)
	    NullCheck.notNull(items[i], "items[" + i + "]");
    }

    @Override public Object chooseItem(Area area, String formListItem, Object currentSelected)
    {
	return     Popups.fixedList(luwrain, name, items, popupFlags);
    }
}
