/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.desktop;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;

class Model implements ListArea.Model
{
    private final Luwrain luwrain;
    private final UniRefList uniRefList;
    private Object[] items;
    private String[] introduction;
    private String clickHereLine = null;
    private int firstUniRefPos = 0;

    Model(Luwrain luwrain, UniRefList uniRefList)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(uniRefList, "uniRefList");
	this.luwrain = luwrain;
	this.uniRefList = uniRefList;
    }

    @Override public int getItemCount()
    {
	return items != null?items.length:0;
    }

    @Override public Object getItem(int index)
    {
	if (items == null)
	    return null;
	return index < items.length?items[index]:null;
    }

    @Override public void refresh()
    {
	final LinkedList res = new LinkedList();
	if (introduction != null && introduction.length > 0)
	{
	    for(String s: introduction)
		res.add(s);
	    if (clickHereLine != null)
	    {
		res.add("");
		res.add(clickHereLine);
		res.add("");
	    }
	}
	firstUniRefPos = res.size();
	final UniRefInfo[] uniRefs = uniRefList.get();
	for(UniRefInfo u: uniRefs)
	    res.add(u);
	items = res.toArray(new Object[res.size()]);
    }

    @Override public boolean toggleMark(int index)
    {
	return false;
    }

    int getFirstUniRefPos()
    {
	return firstUniRefPos;
    }

    void setIntroduction(String[] text)
    {
	introduction =text;
    }

    void setClickHereLine(String line)
    {
	clickHereLine = line;
    }
}
