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

package org.luwrain.app.control;

import org.luwrain.core.*;
import org.luwrain.controls.*;

class ControlGroupsModel implements TreeModel
{
    private StringConstructor stringConstructor;
    private String root;
    private String interaction;
    private String mail;
    private String news;
    private String[] rootItems;

    public ControlGroupsModel(StringConstructor stringConstructor)
    {
	this.stringConstructor = stringConstructor; 
	createItems();
    }

    public Object getRoot()
    {
	return root;
    }

    public int getChildCount(Object parent)
    {
	if (parent == root)
	    return rootItems.length;
	return 0;
    }

    public Object getChild(Object parent, int index)
    {
	if (parent == root && index < rootItems.length)
	    return rootItems[index];
	return null;
    }

    public boolean isLeaf(Object node)
    {
	return node != root;
    }

    private void createItems()
    {
	root = stringConstructor.rootItemName();
	interaction = stringConstructor.interactionItemName();
	mail = stringConstructor.mailItemName();
	news = stringConstructor.newsItemName();
	rootItems = new String[3];
	rootItems[0] =interaction;
	rootItems[1] = mail;
	rootItems[2] = news; 
    }
}
