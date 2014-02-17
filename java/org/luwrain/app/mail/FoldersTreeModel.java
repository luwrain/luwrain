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

import java.util.*;
import org.luwrain.core.Log;
import org.luwrain.controls.*;
import org.luwrain.pim.*;

class FoldersTreeModel implements TreeModel
{
    private MailStoring mailStoring;
    private StringConstructor stringConstructor;
    private String root = "";
    private AbstractMap<Object, StoredMailGroup[]> groupsCache = new HashMap<Object, StoredMailGroup[]>();

    public FoldersTreeModel(MailStoring mailStoring, StringConstructor stringConstructor)
    {
	this.mailStoring = mailStoring;
	this.stringConstructor = stringConstructor;
	this.root = stringConstructor.mailFoldersRoot();
    }

    public Object getRoot()
    {
	return root;
    }

    public boolean isLeaf(Object node)
    {
	if (node == null)
	    return true;
	if (node == root)
	{
	    try {
		StoredMailGroup rootGroup = mailStoring.loadRootGroup();
		if (rootGroup == null)
		    return true;
		StoredMailGroup[] children = mailStoring.loadChildGroups(rootGroup);
		return children == null || children.length < 1;
	    }
	    catch (Exception e)
	    {
		Log.error("mail", "problem getting children of root mail group:" + e.getMessage());
		e.printStackTrace();
	    }
	    return true;
	}
	try {
	    StoredMailGroup[] children = mailStoring.loadChildGroups((StoredMailGroup)node);
	    return children == null || children.length < 1;
	}
	catch (Exception e)
	{
	    Log.error("mail", "problem getting children of mail group:" + e.getMessage());
	    e.printStackTrace();
	}
	return false;
    }

    public void beginChildEnumeration(Object node)
    {
	if (node == null)
	    return;
	if (node == root)
	{
	    try {
		StoredMailGroup rootGroup = mailStoring.loadRootGroup();
		if (rootGroup == null)
		    return;
		StoredMailGroup[] children = mailStoring.loadChildGroups(rootGroup);
		if (children != null)
		    groupsCache.put(root, children);
	    }
	    catch (Exception e)
	    {
		Log.error("mail", "problem getting children of root mail group:" + e.getMessage());
		e.printStackTrace();
	    }
	    return;
	}
	try {
	    StoredMailGroup[] children = mailStoring.loadChildGroups((StoredMailGroup)node);
	    if (children != null)
		groupsCache.put(node, children);
	}
	catch (Exception e)
	{
	    Log.error("mail", "problem getting children of mail group:" + e.getMessage());
	    e.printStackTrace();
	}
    }

    public int getChildCount(Object parent)
    {
	if (parent == null)
	    return 0;
	StoredMailGroup[] children = groupsCache.get(parent);
	return children != null?children.length:0;
    }

    public Object getChild(Object parent, int index)
    {
	if (parent == null)
	    return null;
	StoredMailGroup[] children = groupsCache.get(parent);
	return (children != null && index < children.length)?children[index]:null;
    }

    public void endChildEnumeration(Object node)
    {
	groupsCache.remove(node);
    }
}
