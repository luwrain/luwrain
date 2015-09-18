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

import java.util.*;

public class CachedTreeModel implements TreeModel
{
static private class CacheItem
{
    public Object parent;
    public Object[] objs = new Object[0];

    public CacheItem(Object parent)
    {
	this.parent = parent;
	if (parent == null)
	    throw new NullPointerException("parent may not be null");
    }
}

    private CachedTreeModelSource source;
    private  LinkedList<CacheItem> cache = new LinkedList<CacheItem>();

    public CachedTreeModel(CachedTreeModelSource source)
    {
	this.source = source;
	if (source == null)
	    throw new NullPointerException("source may not be null");
    }

    @Override public Object getRoot()
    {
	return source.getRoot();
    }

    @Override public boolean isLeaf(Object node)
    {
	final Object[] objs = source.getChildObjs(node);
	return objs == null || objs.length < 1;
    }

    @Override public void beginChildEnumeration(Object node)
    {
	if (node == null)
	    return;
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
	    return;
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
