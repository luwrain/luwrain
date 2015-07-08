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

package org.luwrain.app.cpanel;

import org.luwrain.controls.TreeModel;

class SectionsTreeModel implements TreeModel
{
    public String root = "root";

    @Override public Object getRoot()
    {
	return "root";
    }

    @Override public boolean isLeaf(Object node)
    {
	return true;
    }

    @Override public void beginChildEnumeration(Object obj)
    {
    }

    @Override public int getChildCount(Object parent)
    {
	return 0;
    }

    @Override public Object getChild(Object parent, int index)
    {
	return null;
    }

    @Override public void endChildEnumeration(Object obj)
    {
    }
}
