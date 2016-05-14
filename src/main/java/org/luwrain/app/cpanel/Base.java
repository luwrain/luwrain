
package org.luwrain.app.cpanel;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

class Base
{
    private Luwrain luwrain;
    private Factory[] factories;
    private HashMap<Element, TreeItem> treeItems = new HashMap<Element, TreeItem>();

    boolean init(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	return true;
    }

    void refreshTreeItems()
    {

	final HashMap<Element, TreeItem> newItems = new HashMap<Element, TreeItem>();
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
		    final TreeItem item = new TreeItem(parent);
		    item.children.add(e);
		newItems.put(parent, item);
		} else
		    newItems.get(parent).children.add(e);
		if (!newItems.containsKey(e))
		newItems.put(e, new TreeItem(e));
	    }
	}

	for(Map.Entry<Element, TreeItem> n: newItems.entrySet())
	{
	    final TreeItem item = treeItems.get(n.getKey());
	    if (item != null && item.sect != null)
		n.getValue().sect = item.sect;
	}
	treeItems = newItems;
    }

    TreeArea.Model getTreeModel()
    {
	return null;
    }
}
