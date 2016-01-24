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

class TreeAreaNode
{
    Object obj;
    boolean leaf = true;
    TreeAreaNode children[];//If children is null but node is not leaf it means closed node without any info about content;
TreeAreaNode parent;

    //Actually, it is still unclear is it really good idea 
    // to request title dynamically each time;
    String title()
    {
	return obj != null?obj.toString():"";
    }

    void makeLeaf()
    {
	children = null;
	leaf = true;
    } 
}

class VisibleTreeItem
{
    static final int LEAF = 0;
    static final int CLOSED = 1;
    static final int OPENED = 2;

    int type = LEAF;
    String title = "";
    int level = 0;
    TreeAreaNode node;
}

public class TreeArea implements Area
{
    public interface ClickHandler
    {
	boolean onTreeClick(TreeArea area, Object obj);
    }

    static public class Params
    {
	public ControlEnvironment environment;
	public TreeModel model;
	public String name;
	public ClickHandler clickHandler;
    }

    protected ControlEnvironment environment;
    protected TreeModel model;
    protected String name = "";
    private TreeAreaNode root;
    private VisibleTreeItem[] items;
    private int hotPointX = 0;
    private int hotPointY = 0;
    private ClickHandler clickHandler;

    public TreeArea(ControlEnvironment environment, TreeModel model,
		    String name)
    {
	this.environment = environment;
	this.model = model;
	this.name = name;
	root = constructNode(model.getRoot(), null, true);//true means expand children;
	items = generateAllVisibleItems();
    }

    public TreeArea(Params params)    
{
    NullCheck.notNull(params, "params");
    environment = params.environment;
    model = params.model;
    name = params.name;
    clickHandler = params.clickHandler;
    NullCheck.notNull(environment, "environment");
    NullCheck.notNull(model, "model");
    NullCheck.notNull(name, "name");
	root = constructNode(model.getRoot(), null, true);//true means expand children;
	items = generateAllVisibleItems();
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
	if (!event.isCommand())
	{
	    if (event.getCharacter() == ' ')
		return onKeySpace(event);
	    return false;
	}
	if (items == null || items.length < 1)
	{
	    environment.hint(Hints.NO_CONTENT);
	    return true;
	}
	switch (event.getCommand())
	{
	case KeyboardEvent.ENTER:
	    return onKeyEnter(event);
	case KeyboardEvent.ARROW_DOWN:
	    return onKeyDown(event, false);
	case KeyboardEvent.ALTERNATIVE_ARROW_DOWN:
	    return onKeyDown(event, true);
	case KeyboardEvent.ARROW_UP:
	    return onKeyUp(event, false);
	case KeyboardEvent.ALTERNATIVE_ARROW_UP:
	    return onKeyUp(event, true);
	case KeyboardEvent.ARROW_RIGHT:
	    return onKeyRight(event);
	case KeyboardEvent.ARROW_LEFT:
	    return onKeyLeft(event);
	}
	return false;
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.REFRESH)
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

    public TreeModel getModel()
    {
	return model;
    }

    public void refresh()
    {
	final Object oldSelected = selected();
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
	if (oldSelected != null)
	{
	    if (!selectObject(oldSelected))
		selectFirstItem();
	} else
	    selectEmptyLastLine();
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
	    if (items[k].node.obj.equals(obj))//FIXME:equals;
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
    private void fillChildrenForNonLeaf(TreeAreaNode node)
    {
	if (node == null || node.obj == null)
	    return;
	if (node.leaf || model.isLeaf(node.obj))
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
	node.children = new TreeAreaNode[count];
	for(int i = 0;i < count;++i)
	{
	    TreeAreaNode n = new TreeAreaNode();
	    node.children[i] = n;
	    n.obj = model.getChild(node.obj, i);
	    if (n.obj == null)
	    {
		node.makeLeaf();
		model.endChildEnumeration(node.obj);
		return;
	    }
	    n.leaf = model.isLeaf(n.obj);
	    n.children = null;
	    n.parent = node;
	}
	model.endChildEnumeration(node.obj);
    }

    private TreeAreaNode constructNode(Object obj, TreeAreaNode parent, boolean fillChildren)
    {
	if (obj == null)
	    return null;
	TreeAreaNode node = new TreeAreaNode();
	node.obj = obj;
	node.parent = parent;
	node.leaf = model.isLeaf(obj);
	if (fillChildren && !node.leaf)
	fillChildrenForNonLeaf(node);
	return node;
    }

    private void refreshNode(TreeAreaNode node)
    {
	if (node == null || node.obj == null)
	    return;
	if (node.leaf)
	{
	    node.leaf = model.isLeaf(node.obj);
	    return;
	}
	//Was not a leaf;
	if (model.isLeaf(node.obj))
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
	TreeAreaNode[] newNodes = new TreeAreaNode[newCount];
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
	for(TreeAreaNode n: node.children)
	    refreshNode(n);
    }

    private boolean onKeySpace(KeyboardEvent event)
    {
	if (event.isModified() || items == null)
	    return false;
	if (hotPointY >= items.length)
	    return false;
	VisibleTreeItem item = items[hotPointY];
	if (item.node.obj != null)
	    onClick(item.node.obj);
	return true;
    }

    private boolean onKeyEnter(KeyboardEvent event)
    {
	if (event.isModified() || items == null || hotPointY >= items.length)
	    return false;
	VisibleTreeItem item = items[hotPointY];
	if (item.type == VisibleTreeItem.LEAF)
	{
	    onClick(item.node.obj);
	    return true;
	}
	if (item.type == VisibleTreeItem.CLOSED)
	{
	    fillChildrenForNonLeaf(item.node);
	    items = generateAllVisibleItems();
		environment.hint(Hints.TREE_BRANCH_EXPANDED);
		environment.onAreaNewContent(this);
		return true;
	}
	    if (item.type == VisibleTreeItem.OPENED)
	    {
		item.node.children = null;
		items = generateAllVisibleItems();
		environment.hint(Hints.TREE_BRANCH_COLLAPSED);
		environment.onAreaNewContent(this);
		return true;
	    }
	    return false;
    }

    private boolean onKeyDown(KeyboardEvent event, boolean briefIntroduction)
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
	    environment.say(constructLineForSpeech(items[hotPointY], briefIntroduction));
	}
	environment.onAreaNewHotPoint(this );
	return true;
    }

    private boolean onKeyUp(KeyboardEvent event, boolean briefIntroduction)
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
	environment.say(constructLineForSpeech(items[hotPointY], briefIntroduction));
	environment.onAreaNewHotPoint(this );
	return true;
    }

    private boolean onKeyRight(KeyboardEvent event)
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

    private boolean onKeyLeft(KeyboardEvent event)
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

    private VisibleTreeItem[] generateVisibleItems(TreeAreaNode node, int level)
    {
	if (node == null)
	    return null;
	VisibleTreeItem itself = new VisibleTreeItem();
	itself.node = node;
	itself.title = node.title();
	itself.level = level;
	if (node.leaf || node.children == null)
	{
	    itself.type = node.leaf?VisibleTreeItem.LEAF:VisibleTreeItem.CLOSED;
	    VisibleTreeItem res[] = new VisibleTreeItem[1];
	    res[0] = itself;
	    return res;
	}
	itself.type = VisibleTreeItem.OPENED;
	ArrayList<VisibleTreeItem> items = new ArrayList<VisibleTreeItem>();
	items.add(itself);
    	for(int i = 0;i < node.children.length;i++)
	{
	    VisibleTreeItem c[] = generateVisibleItems(node.children[i], level + 1);
	    if (c == null)
		continue;
	    for(VisibleTreeItem v: c)
		items.add(v);
	}
	VisibleTreeItem res[] = new VisibleTreeItem[items.size()];
	int k = 0;
	for(VisibleTreeItem i: items)
	    res[k++] = i;
	return res;
    }

    private VisibleTreeItem[] generateAllVisibleItems()
    {
	if (root == null)
	    return null;
	return generateVisibleItems(root, 0);
    }

    private String constructLineForSpeech(VisibleTreeItem item, boolean briefIntroduction)
    {
	if (item == null)
	    return environment.staticStr(LangStatic.EMPTY_LINE);//FIXME:
	String res = (item.title != null && !item.title.trim().isEmpty())?item.title.trim():environment.staticStr(LangStatic.EMPTY_LINE);
	if (briefIntroduction)
	    return res;
	switch (item.type)
	{
	case VisibleTreeItem.OPENED:
	    res = environment.staticStr(LangStatic.TREE_EXPANDED) + " " + res;
	    break;
	case VisibleTreeItem.CLOSED:
	    res = environment.staticStr(LangStatic.TREE_COLLAPSED) + " " + res;
	    break;
	}
	return res + " " + environment.staticStr(LangStatic.TREE_LEVEL) + " " + (item.level + 1);
    }

    private String constructLineForScreen(VisibleTreeItem item)
    {
	if (item == null)
	    return "";
	String res = "";
	for(int i = 0;i < item.level;++i)
	    res += "  ";
	switch(item.type)
	{
	case VisibleTreeItem.OPENED:
	    res += " -";
	    break;
	case VisibleTreeItem.CLOSED:
	    res += " +";
	    break;
	default:
	    res += "  ";
	}
	return res + (item.title != null?item.title:"");
    }

    private int getInitialHotPointX(int index)
    {
	if (items == null ||  index >= items.length)
	    return 0;
	return (items[index].level * 2) + 2;
    }
}
