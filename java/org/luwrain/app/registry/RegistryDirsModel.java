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

package org.luwrain.app.registry;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.core.registry.Registry;

class RegistryDirsModel implements TreeModel
{
    private RegistryActions actions;
    private StringConstructor stringConstructor;
    private Registry registry = Luwrain.getRegistry();
    private RegistryDir root;

    public RegistryDirsModel(RegistryActions actions, StringConstructor stringConstructor)
    {
	this.actions = actions;
	this.stringConstructor = stringConstructor; 
	this.root = new RegistryDir(stringConstructor.rootItemTitle());
    }

    public Object getRoot()
    {
	return root;
    }

    public int getChildCount(Object parent)
    {
	if (parent == null)
	    return 0;
	RegistryDir dir ;
	try {
	    dir = (RegistryDir)parent;
	}
	catch (ClassCastException e)
	{
	    Log.warning("registry-app", "trying to get child count  of non registry item:" + e.getMessage());
	    e.printStackTrace();
	    return 0;
	}
	String[] children = registry.getDirectories(dir.getPath());
	return children != null?children.length:0;
    }

    public Object getChild(Object parent, int index)
    {
	//FIXME:extremely ineffective, must be rewritten;
	if (parent == null)
	    return null;
	RegistryDir dir ;
	try {
	    dir = (RegistryDir)parent;
	}
	catch (ClassCastException e)
	{
	    Log.warning("registry-app", "trying to get children count  of non registry item:" + e.getMessage());
	    e.printStackTrace();
	    return null;
	}
	String[] children = registry.getDirectories(dir.getPath());
	if (children == null || index >= children.length)
	    return null;
	return new RegistryDir(dir, children[index]);
    }

    public boolean isLeaf(Object node)
    {
	return getChildCount(node) < 1;
    }
}
