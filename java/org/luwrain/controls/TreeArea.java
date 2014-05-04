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

//FIXME:Custom checker of equality for refresh() and selectObject();

package org.luwrain.controls;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import java.util.*;

class TreeAreaNode
{
    public Object obj;
    boolean leaf = true;
    public TreeAreaNode children[];//If children is null but node is not leaf it means closed node without any info about content;
    public TreeAreaNode parent;


    //Actually it is still unclear is it really good idea 
    // to request title dynamically each time;
    public String title()
    {
	return obj != null?obj.toString():"";
    }

    public void makeLeaf()
    {
	children = null;
	leaf = true;
    } 
}

class VisibleTreeItem
{
    public static final int LEAF = 0;
    public static final int CLOSED = 1;
    public static final int OPENED = 2;

    public int type = LEAF;
    public String title = "";
    public int level = 0;
    public TreeAreaNode node;
}

public class TreeArea implements Area
{
    private ControlEnvironment environment;
    private TreeModel model;
    private String name = "";
    private TreeAreaNode root;
    private VisibleTreeItem[] items;
    private int hotPointX = 0;
    private int hotPointY = 0;

    private String beginOfLine = "";
    private String endOfLine = "";
    private String treeAreaBegin = "";
    private String treeAreaEnd = "";
    private String emptyTree = "";
    private String emptyItem = "";
    private String emptyLine = "";
    private String expanded = "";
    private String collapsed = "";
    private String level = "";

    public TreeArea(TreeModel model, String name)
    {
	this.environment = new DefaultControlEnvironment();
	this.model = model;
	this.name = name;
	root = constructNode(model.getRoot(), null, true);//true means expand children;
	items = generateAllVisibleItems();
	initStringConstants();
    }

    public TreeArea(ControlEnvironment environment,
		    TreeModel model,
		    String name)
    {
	this.environment = environment;
	this.model = model;
	this.name = name;
	root = constructNode(model.getRoot(), null, true);//true means expand children;
	items = generateAllVisibleItems();
	initStringConstants();
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
	    environment.say(emptyTree, Speech.PITCH_HIGH);
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

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.REFRESH)
	{
	    refresh();
	    return true;
	}
	return false;
    }

    public String getName()
    {
	return name != null?name:"";
    }

    public TreeModel getModel()
    {
	return model;
    }

    public void refresh()
    {
	Object oldSelected = getObjectUnderHotPoint();
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

    public Object getObjectUnderHotPoint()
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

    public void onClick(Object obj)
    {
	//Nothing here;
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
	    return emptyItem;
	String res = (item.title != null && !item.title.trim().isEmpty())?item.title.trim():emptyItem;
	if (briefIntroduction)
	    return res;
	switch (item.type)
	{
	case VisibleTreeItem.OPENED:
	    res = expanded + " " + res;
	    break;
	case VisibleTreeItem.CLOSED:
	    res = collapsed + " " + res;
	    break;
	}
	return res + " " + level + " " + (item.level + 1);
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
		environment.say(expanded, Speech.PITCH_HIGH);
		environment.onAreaNewContent(this);
		return true;
	}
	    if (item.type == VisibleTreeItem.OPENED)
	    {
		item.node.children = null;
		items = generateAllVisibleItems();
		environment.say(collapsed, Speech.PITCH_HIGH);
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
	    environment.say(treeAreaEnd, Speech.PITCH_HIGH);
	    return true;
	}
	hotPointY++;
	if (hotPointY >= items.length)
	{
	    hotPointX = 0;
	    environment.say(emptyLine, Speech.PITCH_HIGH);
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
	    environment.say(treeAreaBegin, Speech.PITCH_HIGH);
	    return true;
	}
	hotPointY--;
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
	    environment.say(emptyItem, Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointX >= value.length() + offset)
	{
	    environment.say(endOfLine, Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointX < offset)
	    hotPointX = offset; else
	    hotPointX++;
	if (hotPointX >= value.length() + offset)
	    environment.say(endOfLine, Speech.PITCH_HIGH); else
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
	    environment.say(emptyItem, Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointX <= offset)
	{
	    environment.say(beginOfLine, Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointX >= value.length() + offset)
	    hotPointX = value.length() + offset - 1; else
	    hotPointX--;
	environment.sayLetter(value.charAt(hotPointX - offset));
	environment.onAreaNewHotPoint(this);
	return true;
}

    private int getInitialHotPointX(int index)
    {
	if (items == null ||  index >= items.length)
	    return 0;
	return (items[index].level * 2) + 2;
    }

    private void initStringConstants()
    {
	beginOfLine = Langs.staticValue(Langs.BEGIN_OF_LINE);
	endOfLine = Langs.staticValue(Langs.END_OF_LINE);
	treeAreaBegin = Langs.staticValue(Langs.TREE_AREA_BEGIN);
	treeAreaEnd = Langs.staticValue(Langs.TREE_AREA_END);
	emptyTree = Langs.staticValue(Langs.EMPTY_TREE);
	emptyItem = Langs.staticValue(Langs.EMPTY_TREE_ITEM);
	emptyLine = Langs.staticValue(Langs.EMPTY_LINE);
	expanded  = Langs.staticValue(Langs.TREE_EXPANDED);
	collapsed = Langs.staticValue(Langs.TREE_COLLAPSED);
	level = Langs.staticValue(Langs.TREE_LEVEL);
    }
}
