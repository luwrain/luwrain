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

package org.luwrain.controls;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import java.util.*;

public class TreeArea implements Area
{
    protected ControlEnvironment environment;
    protected Model model;
    protected String name = "";
    protected Node root;
    protected VisibleItem[] items;
    protected int hotPointX = 0;
    protected int hotPointY = 0;
    protected ClickHandler clickHandler;

    public TreeArea(Params params)    
    {
	NullCheck.notNull(params, "params");
	NullCheck.notNull(params.environment, "params.environment");
	NullCheck.notNull(params.model, "params.model");
	NullCheck.notNull(params.name, "params.name");
	environment = params.environment;
	model = params.model;
	name = params.name;
	clickHandler = params.clickHandler;
	root = constructNode(model.getRoot(), null, true);//true means children should be expanded
	items = generateAllVisibleItems();
    }

    public ClickHandler getClickHandler()
    {
	return clickHandler;
    }

    public void setClickHandler(ClickHandler clickHandler)
    {
	NullCheck.notNull(clickHandler, "clickHandler");
	this.clickHandler = clickHandler;
    }

    public Model getModel()
    {
	return model;
    }

    public void setModel(Model model)
    {
	NullCheck.notNull(model, "model");
	this.model = model;
    }


    public int getLineCount()
    {
	if (items == null || items.length < 1)
	    return 1;
	return items.length + 1;
    }

    public String getLine(int index)
    {
	if (items == null || items.length < 1 || index >= items.length)
	    return "";
	return constructLineForScreen(items[index]);
    }

    public int getHotPointX()
    {
	return hotPointX >= 0?hotPointX:0;
    }

    public int getHotPointY()
    {
	return hotPointY >= 0?hotPointY:0;
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	//Space;
	if (!event.isSpecial())
	{
	    if (event.getChar() == ' ')
		return onKeySpace(event);
	    return false;
	}
	if (items == null || items.length < 1)
	{
	    environment.hint(Hints.NO_CONTENT);
	    return true;
	}
	switch (event.getSpecial())
	{
	case ENTER:
	    return onKeyEnter(event);
	case ARROW_DOWN:
	    return onKeyDown(event, false);
	case ALTERNATIVE_ARROW_DOWN:
	    return onKeyDown(event, true);
	case ARROW_UP:
	    return onKeyUp(event, false);
	case ALTERNATIVE_ARROW_UP:
	    return onKeyUp(event, true);
	case ARROW_RIGHT:
	    return onKeyRight(event);
	case ARROW_LEFT:
	    return onKeyLeft(event);
	}
	return false;
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.Code.REFRESH)
	{
	    refresh();
	    return true;
	}
	return false;
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	return false;
    }

    @Override public Action[] getAreaActions()
    {
	return new Action[0];
    }

    @Override public String getAreaName()
    {
	return name != null?name:"";
    }


    public void refresh()
    {
	final Object oldSelected = selected();
	final int oldHotPointY = hotPointY;
	Object newRoot = model.getRoot();
	if (newRoot == null)
	{
	    root = null;
	    items = null;
	    return;
	}
	if (root.obj.equals(newRoot))//FIXME:equals();
	{
	    root.obj = newRoot;
	    refreshNode(root);
	} else
	    root = constructNode(model.getRoot(), null, true); //true means expand children;
	items = generateAllVisibleItems();
	environment.onAreaNewContent(this);
	if (oldSelected == null)
		selectFirstItem(); else
	{
	    if (!selectObject(oldSelected))
	{
	    if (items != null && oldHotPointY < items.length)
		hotPointY = oldHotPointY; else 
	    selectEmptyLastLine();
	    environment.onAreaNewHotPoint(this);
	}
	}
    }

    public Object selected()
    {
	if (items == null || hotPointY < 0 || hotPointY >= items.length)
	    return null;
	return items[hotPointY].node.obj;
    }

    public boolean selectObject(Object obj)
    {
	if (items == null || items.length == 0)
	    return false;
	int k;
	for(k = 0;k < items.length;++k)
	    if (items[k].node.obj.equals(obj))
		break;
	if (k >= items.length)
	    return false;
	hotPointY = k;
	hotPointX = getInitialHotPointX(hotPointY);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    public void selectEmptyLastLine()
    {
	if (items == null || items.length < 1)
	    hotPointY = 0; else
	    hotPointY = items.length;
	hotPointX = 0;
	environment.onAreaNewHotPoint(this);
    }

    public void selectFirstItem()
    {
	hotPointY = 0;
	hotPointX = getInitialHotPointX(0);
	environment.onAreaNewHotPoint(this);
    }

    protected void onClick(Object obj)
    {
	if (clickHandler != null)
	    clickHandler.onTreeClick(this, obj);
    }

    //Changes only 'leaf' and 'children' fields;
    protected void fillChildrenForNonLeaf(Node node)
    {
	if (node == null || node.obj == null)
	    return;
	if (node.leaf || isLeaf(node.obj))
	{
	    node.makeLeaf();
	    return;
	}
	model.beginChildEnumeration(node.obj);
	final int count = model.getChildCount(node.obj);
	if (count < 1)
	{
	    node.makeLeaf();
	    model.endChildEnumeration(node.obj);
	    return;
	}
	node.leaf = false;
	node.children = new Node[count];
	for(int i = 0;i < count;++i)
	{
	    Node n = new Node();
	    node.children[i] = n;
	    n.obj = model.getChild(node.obj, i);
	    if (n.obj == null)
	    {
		node.makeLeaf();
		model.endChildEnumeration(node.obj);
		return;
	    }
	    n.leaf = isLeaf(n.obj);
	    n.children = null;
	    n.parent = node;
	}
	model.endChildEnumeration(node.obj);
    }

    protected Node constructNode(Object obj, Node parent, boolean fillChildren)
    {
	if (obj == null)
	    return null;
	Node node = new Node();
	node.obj = obj;
	node.parent = parent;
	node.leaf = isLeaf(obj);
	if (fillChildren && !node.leaf)
	fillChildrenForNonLeaf(node);
	return node;
    }

    protected void refreshNode(Node node)
    {
	if (node == null || node.obj == null)
	    return;
	if (node.leaf)
	{
	    node.leaf = isLeaf(node.obj);
	    return;
	}
	//Was not a leaf;
	if (isLeaf(node.obj))
	{
	    node.makeLeaf();
	    return;
	}
	//Was and remains a non-leaf;
	if (node.children == null)
	    return;
	model.beginChildEnumeration(node.obj);
	final int newCount = model.getChildCount(node.obj);
	if (newCount == 0)
	{
	    node.makeLeaf();
	    model.endChildEnumeration(node.obj);
	    return;
	}
	Node[] newNodes = new Node[newCount];
	for(int i = 0;i < newCount;++i)
	{
	    Object newObj = model.getChild(node.obj, i);
	    if (newObj == null)
	    {
		node.makeLeaf();
		model.endChildEnumeration(node.obj);
		return;
	    }
	    int k;
	    for(k = 0;k < node.children.length;++k)
		if (node.children[k].obj.equals(newObj))//FIXME:equals();
		    break;
	    if (k < node.children.length)
	    {
		newNodes[i] = node.children[k]; 
		newNodes[i].obj = newObj;
	    }else
		newNodes[i] = constructNode(newObj, node, false);
	}
	model.endChildEnumeration(node.obj);
	node.children = newNodes;
	for(Node n: node.children)
	    refreshNode(n);
    }

    protected boolean onKeySpace(KeyboardEvent event)
    {
	if (event.isModified() || items == null)
	    return false;
	if (hotPointY >= items.length)
	    return false;
	VisibleItem item = items[hotPointY];
	if (item.node.obj != null)
	    onClick(item.node.obj);
	return true;
    }

    protected boolean onKeyEnter(KeyboardEvent event)
    {
	if (event.isModified() || items == null || hotPointY >= items.length)
	    return false;
	VisibleItem item = items[hotPointY];
	if (item.type == VisibleItem.Type.LEAF)
	{
	    onClick(item.node.obj);
	    return true;
	}
	if (item.type == VisibleItem.Type.CLOSED)
	{
	    fillChildrenForNonLeaf(item.node);
	    items = generateAllVisibleItems();
		environment.hint(Hints.TREE_BRANCH_EXPANDED);
		environment.onAreaNewContent(this);
		return true;
	}
	    if (item.type == VisibleItem.Type.OPENED)
	    {
		item.node.children = null;
		items = generateAllVisibleItems();
		environment.hint(Hints.TREE_BRANCH_COLLAPSED);
		environment.onAreaNewContent(this);
		return true;
	    }
	    return false;
    }

    protected boolean onKeyDown(KeyboardEvent event, boolean briefAnnouncement)
    {
	if (event.isModified() || items == null)
	    return false;
	if (hotPointY  >= items.length)
	{
	    environment.hint(Hints.TREE_END);
	    return true;
	}
	++hotPointY;
	if (hotPointY >= items.length)
	{
	    hotPointX = 0;
	    environment.hint(Hints.EMPTY_LINE);
	} else
	{
	    hotPointX = getInitialHotPointX(hotPointY);
announce(items[hotPointY], briefAnnouncement);
	}
	environment.onAreaNewHotPoint(this );
	return true;
    }

    protected boolean onKeyUp(KeyboardEvent event, boolean briefAnnouncement)
    {
	if (event.isModified() || items == null)
	    return false;
	if (hotPointY  <= 0)
	{
	    environment.hint(Hints.TREE_BEGIN);
	    return true;
	}
	--hotPointY;
	hotPointX = getInitialHotPointX(hotPointY);
announce(items[hotPointY], briefAnnouncement);
	environment.onAreaNewHotPoint(this );
	return true;
    }

    protected boolean onKeyRight(KeyboardEvent event)
    {
	if (event.isModified() ||
	    items == null || hotPointY >= items.length)
	    return false;
	final String value = items[hotPointY].title;
	final int offset = getInitialHotPointX(hotPointY);
	if (value.isEmpty())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX >= value.length() + offset)
	{
	    environment.hint(Hints.END_OF_LINE);
	    return true;
	}
	if (hotPointX < offset)
	    hotPointX = offset; else
	    hotPointX++;
	if (hotPointX >= value.length() + offset)
	    environment.hint(Hints.END_OF_LINE); else
	    environment.sayLetter(value.charAt(hotPointX - offset));
	environment.onAreaNewHotPoint(this);
	return true;
    }

    protected boolean onKeyLeft(KeyboardEvent event)
    {
	if (event.isModified() ||
	    items == null || hotPointY >= items.length)
	    return false;
	final String value = items[hotPointY].title;
	final int offset = getInitialHotPointX(hotPointY);
	if (value.isEmpty())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX <= offset)
	{
	    environment.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	if (hotPointX >= value.length() + offset)
	    hotPointX = value.length() + offset - 1; else
	    --hotPointX;
	environment.sayLetter(value.charAt(hotPointX - offset));
	environment.onAreaNewHotPoint(this);
	return true;
    }

    protected VisibleItem[] generateVisibleItems(Node node, int level)
    {
	if (node == null)
	    return null;
	VisibleItem itself = new VisibleItem();
	itself.node = node;
	itself.title = node.title();
	itself.level = level;
	if (node.leaf || node.children == null)
	{
	    itself.type = node.leaf?VisibleItem.Type.LEAF:VisibleItem.Type.CLOSED;
	    VisibleItem res[] = new VisibleItem[1];
	    res[0] = itself;
	    return res;
	}
	itself.type = VisibleItem.Type.OPENED;
	ArrayList<VisibleItem> items = new ArrayList<VisibleItem>();
	items.add(itself);
    	for(int i = 0;i < node.children.length;i++)
	{
	    VisibleItem c[] = generateVisibleItems(node.children[i], level + 1);
	    if (c == null)
		continue;
	    for(VisibleItem v: c)
		items.add(v);
	}
	VisibleItem res[] = new VisibleItem[items.size()];
	int k = 0;
	for(VisibleItem i: items)
	    res[k++] = i;
	return res;
    }

    protected VisibleItem[] generateAllVisibleItems()
    {
	if (root == null)
	    return null;
	return generateVisibleItems(root, 0);
    }

    protected boolean isLeaf(Object o)
    {
	NullCheck.notNull(o, "o");
	model.beginChildEnumeration(o);
	final boolean res = model.getChildCount(o) <= 0;
	model.endChildEnumeration(o);
	return res;
    }

    protected void announce(VisibleItem item, boolean briefAnnouncement)
    {
	NullCheck.notNull(item, "item");
	if (item.title.isEmpty())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return;
	}
	if (briefAnnouncement)
	{
	    environment.say(item.title);
	    return;
	}
	String res = item.title;
	switch (item.type)
	{
	case OPENED:
	    res = environment.staticStr(LangStatic.TREE_EXPANDED) + " " + res;
	    break;
	case CLOSED:
	    res = environment.staticStr(LangStatic.TREE_COLLAPSED) + " " + res;
	    break;
	}
	environment.say(res + " " + environment.staticStr(LangStatic.TREE_LEVEL) + " " + (item.level + 1));
}

    protected String constructLineForScreen(VisibleItem item)
    {
	if (item == null)
	    return "";
	String res = "";
	for(int i = 0;i < item.level;++i)
	    res += "  ";
	switch(item.type)
	{
	case OPENED:
	    res += " -";
	    break;
	case CLOSED:
	    res += " +";
	    break;
	default:
	    res += "  ";
	}
	return res + (item.title != null?item.title:"");
    }

protected int getInitialHotPointX(int index)
    {
	if (items == null ||  index >= items.length)
	    return 0;
	return (items[index].level * 2) + 2;
    }

    public interface ClickHandler
    {
	boolean onTreeClick(TreeArea area, Object obj);
    }

    public interface Model
    {
	Object getRoot();
	void beginChildEnumeration(Object obj);
	int getChildCount(Object parent);
	Object getChild(Object parent, int index);
	void endChildEnumeration(Object obj);
    }

    static public class Params
    {
	public ControlEnvironment environment;
	public String name;
	public Model model;
	public ClickHandler clickHandler;
    }

    static protected class Node
    {
	Object obj = null;
	Node parent = null;
	Node children[] = null;//If children is null and node is not a leaf, it means this is a closed node without any info about its content
	boolean leaf = true;

	void makeLeaf()
	{
	    children = null;
	    leaf = true;
	} 

	String title() { return obj != null?obj.toString():""; }
    }

    static protected class VisibleItem
    {
	enum Type {LEAF, CLOSED, OPENED};

	Type type = Type.LEAF;
	String title = "";
	int level = 0;
	Node node;
    }
}
