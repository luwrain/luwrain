
package org.luwrain.app.cpanel;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;
import org.luwrain.settings.StandardFactory;

class Base
{
    private Luwrain luwrain;
    private Factory[] factories;
    private final StandardFactory standardFactory = new StandardFactory();
    private HashMap<Element, TreeItem> treeItems = new HashMap<Element, TreeItem>();
    private SectionsTreeModelSource treeModelSource;
    private CachedTreeModel treeModel;

    boolean init(Luwrain luwrain, Factory[] factories)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNullItems(factories, "factories");
	this.luwrain = luwrain;
	this.factories = factories;
	treeModelSource = new SectionsTreeModelSource(treeItems);
	treeModel = new CachedTreeModel(treeModelSource);
	refreshTreeItems();
	return true;
    }

    void refreshTreeItems()
    {
	final HashMap<Element, TreeItem> newItems = new HashMap<Element, TreeItem>();
	final TreeItem rootItem = new TreeItem(StandardElements.ROOT, standardFactory);
	newItems.put(StandardElements.ROOT, rootItem);
	rootItem.children.add(StandardElements.APPLICATIONS);
	rootItem.children.add(StandardElements.KEYBOARD);
	rootItem.children.add(StandardElements.SOUNDS);
	rootItem.children.add(StandardElements.SPEECH);
	rootItem.children.add(StandardElements.NETWORK);
	rootItem.children.add(StandardElements.HARDWARE);
	rootItem.children.add(StandardElements.UI);
	rootItem.children.add(StandardElements.EXTENSIONS);
	rootItem.children.add(StandardElements.WORKERS);
	for(Element e: rootItem.children)
	    newItems.put(e, new TreeItem(e, standardFactory));
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

    TreeArea.Model getTreeModel()
    {
	return treeModel;
    }
}
