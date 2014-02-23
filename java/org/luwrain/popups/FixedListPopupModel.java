/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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
    private String[] fixedItems;

    public FixedListPopupModel(String[] fixedItems)
    {
	if (fixedItems != null)
	{
	    this.fixedItems = new String[fixedItems.length];
	    for(int i = 0;i < fixedItems.length;++i)
		this.fixedItems[i] = fixedItems[i];
	    Arrays.sort(this.fixedItems);
	} else
	    this.fixedItems = new String[0];
    }

    protected String[] getItems(String context)
    {
	//Returning every time the same items regardless the context;
	return fixedItems;
    }

    protected String getEmptyItem(String context)
    {
	return "";
    }
}
