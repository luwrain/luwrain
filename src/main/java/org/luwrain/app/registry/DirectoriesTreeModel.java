/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.app.registry;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.controls.*;

class DirectoriesTreeModel implements TreeModel
{
    private Luwrain luwrain;
    private Actions actions;
    private Strings strings;
    private Registry registry;
    private Directory root;
    private AbstractMap<Directory, String[]> dirsCache = new HashMap<Directory, String[]>();

    public DirectoriesTreeModel(Luwrain luwrain,
			     Actions actions,
			     Strings strings)
    {
	this.luwrain = luwrain;
	this.actions = actions;
	this.strings = strings;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (actions == null)
	    throw new NullPointerException("actions may not be null");
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
	this.registry = luwrain.getRegistry();
	this.root = new Directory(strings.rootItemTitle());
    }

    public Object getRoot()
    {
	return root;
    }

    public boolean isLeaf(Object node)
    {
	if (node == null)
	    return true;
	Directory dir = (Directory)node;
	String[] children = registry.getDirectories(dir.getPath());
	return children == null || children .length < 1;
    }

    public void beginChildEnumeration(Object node)
    {
	if (node == null)
	    return;
	Directory dir = (Directory)node;
	String[] children = registry.getDirectories(dir.getPath());
	if (children != null)
	    dirsCache.put(dir, children);
    }

    public int getChildCount(Object parent)
    {
	if (parent == null)
	    return 0;
	String[] children = dirsCache.get((Directory)parent);
	return children != null?children.length:0;
    }

    public Object getChild(Object parent, int index)
    {
	if (parent == null)
	    return 0;
	String[] children = dirsCache.get((Directory)parent);
	if (children != null && index < children.length)
	    return new Directory((Directory)parent, children[index]);
	return null;
    }

    public void endChildEnumeration(Object node)
    {
	if (dirsCache.containsKey((Directory)node))
	    dirsCache.remove((Directory)node);
    }
}