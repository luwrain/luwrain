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

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.popups.Popups;

public class FormUtils
{
    static public class FixedListChoosing implements FormArea.ListChoosing
    {
	protected Luwrain luwrain;
	protected String name;
	protected Object[] items;
	protected Set<Popup.Flags> popupFlags;

	public FixedListChoosing(Luwrain luwrain, String name,
				 Object[] items, Set<Popup.Flags> popupFlags)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(name, "name");
	    NullCheck.notNullItems(items, "items");
	    NullCheck.notNull(popupFlags, "popupFlags");
	    this.luwrain = luwrain;
	    this.name = name;
	    this.items = items;
	    this.popupFlags = popupFlags;
	}

	@Override public Object chooseFormListItem(Area area, String formListItem, Object currentSelected)
	{
	    return     Popups.fixedList(luwrain, name, items, popupFlags);
	}
    }
}
