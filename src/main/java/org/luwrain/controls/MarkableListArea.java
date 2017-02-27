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
import org.luwrain.core.events.*;

public class MarkableListArea extends ListArea
{
    public MarkableListArea(Params params)
    {
	super(params);
    }

    static public class MarksInfo
    {
	protected final HashSet items = new HashSet();

	public boolean marked(Object o)
	{
	    NullCheck.notNull(o, "o");
	    return items.contains(o);
	}

	void mark(Object o)
    {
	NullCheck.notNull(o, "o");
	items.add(o);
    }

	public void unmark(Object o)
	{
	    NullCheck.notNull(o, "o");
	    items.remove(o);
	}

	public boolean toggleMark(Object o)
	{
	    NullCheck.notNull(o, "o");
	    if (marked(o))
	    {
		unmark(o);
		return false;
	    }
	    mark(o);
	    return true;
	}

	public void markTheseOnly(Object[] o)
	{
	    NullCheck.notNullItems(o, "o");
	    items.clear();
	    for(Object oo: o)
		items.add(oo);
	}

	Object[] getAllMarked()
	{
	    final LinkedList res = new LinkedList();
	    for(Object o: items)
		res.add(o);
	    return res.toArray(new Object[res.size()]);
	}
    }
}
