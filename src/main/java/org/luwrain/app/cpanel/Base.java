/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.app.cpanel;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;
import org.luwrain.settings.StandardFactory;

class Base
{
    private final Luwrain luwrain;
    private final Factory[] factories;
    private final StandardFactory standardFactory;
    private HashMap<Element, TreeItem> treeItems = new HashMap<Element, TreeItem>();
    private SectionsTreeModelSource treeModelSource;
    private CachedTreeModel treeModel;

    Base(Luwrain luwrain, Factory[] factories)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNullItems(factories, "factories");
	this.luwrain = luwrain;
	this.standardFactory = new StandardFactory(luwrain);
	this.factories = factories;
	treeModelSource = new SectionsTreeModelSource(this, 
treeItems);
	treeModel = new CachedTreeModel(treeModelSource);
	refreshTreeItems();
    }

    void refreshTreeItems()
    {
	final HashMap<Element, TreeItem> newItems = new HashMap<Element, TreeItem>();
	for(Element e: standardFactory.getElements())
	{
	    if (!newItems.containsKey(e))
		newItems.put(e, new TreeItem(e, standardFactory));
	    final Element parent = e.getParentElement();
	    if (parent == null)
		continue;
	    if (!newItems.containsKey(parent))
	    {
		final TreeItem item = new TreeItem(parent, standardFactory);
		item.children.add(e);
		newItems.put(parent, item);
	    } else
		newItems.get(parent).children.add(e);
	}
	for(Factory f: factories)
	{
	    final Element[] elements = f.getElements();
	    for(Element e: elements)
	    {
		if (e == null)
		{
		    Log.warning("cpanel", "control panel factory " + f.getClass().getName() + " provided a null element");
		    continue;
		}
		final Element parent = e.getParentElement();
		if (parent == null)
		{
		    Log.warning("cpanel", "control panel element " + e.getClass().getName() + " has null parent");
		    continue;
		}
		if (!newItems.containsKey(parent))
		{
		    final TreeItem item = new TreeItem(parent, f);
		    item.children.add(e);
		    newItems.put(parent, item);
		} else
		    newItems.get(parent).children.add(e);
		if (!newItems.containsKey(e))
		    newItems.put(e, new TreeItem(e, f));
	    }
	}
	for(Map.Entry<Element, TreeItem> n: newItems.entrySet())
	{
	    final TreeItem item = treeItems.get(n.getKey());
	    if (item != null && item.sect != null)
		n.getValue().sect = item.sect;
	}
	treeItems = newItems;
	treeModelSource.setTreeItems(treeItems);
    }

    void addOnDemandElements(TreeItem treeItem)
    {
	NullCheck.notNull(treeItem, "treeItem");
	if (treeItem.onDemandFilled)
	    return;
	final Element[] toAdd = treeItem.factory.getOnDemandElements(treeItem.el);
	for(Element e: toAdd)
	{
	    if (!e.getParentElement().equals(treeItem.el))
		continue;
		if (!treeItems.containsKey(e))
treeItems.put(e, new TreeItem(e, treeItem.factory));
		treeItem.children.add(e);
	}
	treeItem.onDemandFilled = true;
    }

    TreeArea.Model getTreeModel()
    {
	return treeModel;
    }
}
