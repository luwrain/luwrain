/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.app.cpanel;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;
import org.luwrain.app.cpanel.sects.Tree;

class SectionsTreeModel implements TreeModel
{
    private Tree tree;

    SectionsTreeModel(Environment environment,
		      Strings strings,
			     Section[] extensionsSections)
    {
	NullCheck.notNull(environment, "environment");
	NullCheck.notNull(strings, "strings");
	NullCheck.notNull(extensionsSections, "extensionsSections");
	tree = new Tree(environment, strings, extensionsSections);
	tree.init();
    }

    @Override public Object getRoot()
    {
	return tree.getRoot();
    }

    @Override public boolean isLeaf(Object node)
    {
	if (node == null || !(node instanceof Section))
	    return true;
	final Section sect = (Section)node;
	final Section[] subsections = sect.getChildSections();
	return subsections == null || subsections.length < 1;
    }

    @Override public void beginChildEnumeration(Object obj)
    {
    }

    @Override public int getChildCount(Object node)
    {
	if (node == null || !(node instanceof Section))
	    return 0;
	final Section sect = (Section)node;
	final Section[] subsections = sect.getChildSections();
	return subsections != null?subsections.length:0;
    }

    @Override public Object getChild(Object node, int index)
    {
	if (node == null || !(node instanceof Section))
	    return 0;
	final Section sect = (Section)node;
	final Section[] subsections = sect.getChildSections();
	if (subsections == null)
	    return null;
	return index < subsections.length?subsections[index]:null;
    }

    @Override public void endChildEnumeration(Object obj)
    {
    }

    void refresh()
    {
	tree.refresh();
    }
}
