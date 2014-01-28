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

package org.luwrain.app.mail;

import org.luwrain.core.*;
import org.luwrain.controls.*;

public class MailGroupTreeModel implements TreeModel
{
    private MailGroup rootGroup;

    public MailGroupTreeModel(MailGroup rootGroup)
    {
	this.rootGroup = rootGroup;
    }

    public Object getRoot()
    {
	return rootGroup;
    }

    public int getChildCount(Object parent)
    {
	if (parent == null)
	    return 0;
	MailGroup group = (MailGroup)parent;
	if (!group.hasChildFolders())
	    return 0;
	MailGroup[] children = group.getChildGroups();
	return children == null?0:children.length;
    }

    public Object getChild(Object parent, int index)
    {
	if (parent == null)
	    return 0;
	MailGroup group = (MailGroup)parent;
	if (!group.hasChildFolders())
	    return null;
	MailGroup[] children = group.getChildGroups();
	if (children == null || index >= children.length)
	    return null;
	return children[index];
    }

    public boolean isLeaf(Object node)
    {
	if (node == null)
	    return true;
	MailGroup group = (MailGroup)node;
	return !group.hasChildFolders();
    }
}
