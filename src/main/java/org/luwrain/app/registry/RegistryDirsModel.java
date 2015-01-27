/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

class RegistryDirsModel implements TreeModel
{
    private Luwrain luwrain;
    private RegistryActions actions;
    private StringConstructor stringConstructor;
    private Registry registry;
    private RegistryDir root;
    private AbstractMap<RegistryDir, String[]> dirsCache = new HashMap<RegistryDir, String[]>();

    public RegistryDirsModel(Luwrain luwrain,
			     RegistryActions actions,
			     StringConstructor stringConstructor)
    {
	this.luwrain = luwrain;
	this.actions = actions;
	this.stringConstructor = stringConstructor; 
	this.registry = luwrain.getRegistry();
	this.root = new RegistryDir(stringConstructor.rootItemTitle());
    }

    public Object getRoot()
    {
	return root;
    }

    public boolean isLeaf(Object node)
    {
	if (node == null)
	    return true;
	RegistryDir dir = (RegistryDir)node;
	String[] children = registry.getDirectories(dir.getPath());
	return children == null || children .length < 1;
    }

    public void beginChildEnumeration(Object node)
    {
	if (node == null)
	    return;
	RegistryDir dir = (RegistryDir)node;
	String[] children = registry.getDirectories(dir.getPath());
	if (children != null)
	    dirsCache.put(dir, children);
    }

    public int getChildCount(Object parent)
    {
	if (parent == null)
	    return 0;
	String[] children = dirsCache.get((RegistryDir)parent);
	return children != null?children.length:0;
    }

    public Object getChild(Object parent, int index)
    {
	if (parent == null)
	    return 0;
	String[] children = dirsCache.get((RegistryDir)parent);
	if (children != null && index < children.length)
	    return new RegistryDir((RegistryDir)parent, children[index]);
	return null;
    }

    public void endChildEnumeration(Object node)
    {
	if (dirsCache.containsKey((RegistryDir)node))
	    dirsCache.remove((RegistryDir)node);
    }
}
