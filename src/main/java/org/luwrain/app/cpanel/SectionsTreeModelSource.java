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

package org.luwrain.app.cpanel;

import java.util.*;

import org.luwrain.core.NullCheck;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

class SectionsTreeModelSource implements CachedTreeModelSource
{
    private Base base;
    private HashMap<Element, TreeItem> treeItems;

    SectionsTreeModelSource(Base base, HashMap<Element, TreeItem> treeItems)
    {
	NullCheck.notNull(base, "base");
	NullCheck.notNull(treeItems, "treeItems");
	this.base = base;
	this.treeItems = treeItems;
    }

void     setTreeItems(HashMap<Element, TreeItem> treeItems)
    {
	NullCheck.notNull(treeItems, "treeItems");
	this.treeItems = treeItems;
    }

    @Override public Object getRoot()
    {
	return findSect(StandardElements.ROOT);
    }

    @Override public Object[] getChildObjs(Object obj)
    {
	NullCheck.notNull(obj, "obj");
	final Element el = ((Section)obj).getElement();
	final TreeItem item = treeItems.get(el);
	base.addOnDemandElements(item);
	if (item == null || item.children.isEmpty())
	    return new Section[0];
	final LinkedList<Section> res = new LinkedList<Section>();
	for(Element c: item.children)
	{
	    final Section sect = findSect(c);
	    if (sect != null)
		res.add(sect);
	}
	return res.toArray(new Section[res.size()]);
    }

    private Section findSect(Element el)
    {
	final TreeItem item = treeItems.get(el);
	if (el == null)
	    return null;
	if (item.sect != null)
	    return item.sect;
	if (item.factory == null)
	    return null;
	item.sect = item.factory.createSection(el);
	return item.sect;
    }
}
