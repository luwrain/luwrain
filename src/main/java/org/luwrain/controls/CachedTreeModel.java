/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

import org.luwrain.core.NullCheck;

public class CachedTreeModel implements TreeArea.Model
{
    static protected class CacheItem
    {
	private final Object parent;
	private Object[] objs = new Object[0];

	CacheItem(Object parent)
	{
	    NullCheck.notNull(parent, "parent");
	    this.parent = parent;
	}
    }

    private final CachedTreeModelSource source;
    private  final List<CacheItem> cache = new LinkedList<CacheItem>();

    public CachedTreeModel(CachedTreeModelSource source)
    {
	NullCheck.notNull(source, "source");
	this.source = source;
    }

    @Override public Object getRoot()
    {
	return source.getRoot();
    }

    @Override public void beginChildEnumeration(Object node)
    {
	NullCheck.notNull(node, "node");
	CacheItem newItem = null;
	for(CacheItem c: cache)
	    if (c.parent.equals(node))
		newItem = c;
	if (newItem == null)
	{
	    newItem = new CacheItem(node);
	    cache.add(newItem);
	}
	final Object[] objs = source.getChildObjs(node);
	if (objs == null || objs.length < 1)
	{
	    newItem.objs = new Object[0];
	    return;
	}
	newItem.objs = objs;
    }

    @Override public int getChildCount(Object parent)
    {
	if (parent == null)
	    return 0;
	for(CacheItem c: cache)
	    if (c.parent.equals(parent))
		return c.objs.length;
	return 0;
    }

    @Override public Object getChild(Object parent, int index)
    {
	if (parent == null)
	    return null;
	for(CacheItem c: cache)
	    if (c.parent.equals(parent))
		return c.objs[index];
	return null;
    }

    @Override public void endChildEnumeration(Object node)
    {
	//FIXME:
    }
}
