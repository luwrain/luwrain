/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.controls;

//TODO:Refresh operations;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import java.util.*;

class TreeAreaNode
{
    public Object obj = null;
    public TreeAreaNode children[] = null;//If children is null but node is not leaf it means closed node without any info about content;
    public TreeAreaNode parent = null;
    boolean leaf = true;

    public String title()
    {
	if (obj == null)
	    return "";
	return obj.toString();
    }
}

class TreeAreaItem
{
    public static final int LEAF = 0;
    public static final int CLOSED = 1;
    public static final int OPENED = 2;

    public int type = LEAF;
    public String title;
    public int level = 0;
    public TreeAreaNode node;
}

public class TreeArea implements Area
{
    private String name;
    private TreeModel model = null;
    private TreeAreaNode root = null;
    private TreeAreaItem items[] = null;
    private int hotPointX = 0;
    private int hotPointY = 0;

    public TreeArea(String name, TreeModel model)
    {
	this.name = name;
	this.model = model;
	root = constructNode(model.getRoot());
	items = expandNode(root, 0);
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
	    return new String();
	return constructLineForScreen(items[index]);
    }

    public int getHotPointX()
    {
	if (hotPointX < 0)
	    return 0;
	return hotPointX;
    }

    public int getHotPointY()
    {
	if (hotPointY < 0)
	    return 0;
	return hotPointY;
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	//Space;
	if (!event.isCommand() && event.getCharacter() == ' ' == !event.isModified())
	{
	    if (hotPointY >= items.length)
		return false;
	    TreeAreaItem item = items[hotPointY];
	    onClick(item.node.obj);
	    return true;
	}
	if (!event.isCommand() || event.withAlt() || event.withShift())
	    return false;
	if (items == null || items.length < 1)
	{
	    Speech.say("FIXME", Speech.PITCH_HIGH);
	    return true;
	}
	final int cmd = event.getCommand();

	//Enter;
	if (cmd == KeyboardEvent.ENTER && !event.isModified())
	{
	    if (hotPointY >= items.length)
		return false;
	    TreeAreaItem item = items[hotPointY];
	    if (item.type == TreeAreaItem.LEAF)
	    {
		onClick(item.node.obj);
		return true;
	    }
	    if (item.type == TreeAreaItem.CLOSED)
	    {
		fillChildren(item.node);
		items = expandNode(root, 0);
		Speech.say("Раскрыто", Speech.PITCH_HIGH);
		Dispatcher.onAreaNewContent(this);
		return true;
	    }
	    if (item.type == TreeAreaItem.OPENED)
	    {
		item.node.children = null;
		items = expandNode(root, 0);
		Speech.say("Свёрнуто", Speech.PITCH_HIGH);
		Dispatcher.onAreaNewContent(this);
		return true;
	    }
	    return false;
	}

	//Down;
	if (cmd == KeyboardEvent.ARROW_DOWN && !event.isModified())
	{
	    if (hotPointY  >= items.length)
	    {
		Speech.say(Langs.staticValue(Langs.TREE_AREA_END), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointY++;
	    if (hotPointY >= items.length)
	    {
		hotPointX = 0;
		Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
	    } else
	    {
		TreeAreaItem item = items[hotPointY];
		hotPointX = (item.level + 1) * 2;
		Speech.say(constructLineForSpeech(item));
	    }
	    Dispatcher.onAreaNewHotPoint(this );
	    return true;
	}

	//Up;
	if (cmd == KeyboardEvent.ARROW_UP && !event.isModified())
	{
	    if (hotPointY  <= 0)
	    {
		Speech.say(Langs.staticValue(Langs.TREE_AREA_BEGIN), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointY--;
	    TreeAreaItem item = items[hotPointY];
	    hotPointX = (item.level + 1) * 2;
	    Speech.say(constructLineForSpeech(item));
	    Dispatcher.onAreaNewHotPoint(this );
	    return true;
	}

	//Control + down;
	if (cmd == KeyboardEvent.ARROW_DOWN && event.withControl())//FIXME:only;
	{
	    if (hotPointY  >= items.length)
	    {
		Speech.say(Langs.staticValue(Langs.TREE_AREA_END), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointY++;
	    if (hotPointY >= items.length)
	    {
		hotPointX = 0;
		Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
	    } else
	    {
		TreeAreaItem item = items[hotPointY];
		hotPointX = (item.level + 1) * 2;
		Speech.say(item.title);
	    }
	    Dispatcher.onAreaNewHotPoint(this );
	    return true;
	}

	//Control + Up;
	if (cmd == KeyboardEvent.ARROW_UP && event.withControl())
	{
	    if (hotPointY  <= 0)
	    {
		Speech.say(Langs.staticValue(Langs.TREE_AREA_BEGIN), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointY--;
	    TreeAreaItem item = items[hotPointY];
	    hotPointX = (item.level + 1) * 2;
	    Speech.say(item.title);
	    Dispatcher.onAreaNewHotPoint(this );
	    return true;
	}

	//Right;
	if (cmd == KeyboardEvent.ARROW_RIGHT && !event.isModified())
	{
	    if (hotPointY >= items.length)
		return false;
	    TreeAreaItem item = items[hotPointY];
	    final int bound = (item.level + 1) * 2;
	    if (hotPointX >= item.title.length() + bound)
	    {
		Speech.say(Langs.staticValue(Langs.END_OF_LINE), Speech.PITCH_HIGH);
		return true;
	    }
	    if (hotPointX < bound)
		hotPointX = bound; else
		hotPointX++;
	    if (hotPointX >= item.title.length() + bound)
		Speech.say(Langs.staticValue(Langs.END_OF_LINE), Speech.PITCH_HIGH); else
		Speech.sayLetter(item.title.charAt(hotPointX - bound));
	    Dispatcher.onAreaNewHotPoint(this);
	    return true;
	}

	//Left;
	if (cmd == KeyboardEvent.ARROW_LEFT && !event.isModified())
	{
	    if (hotPointY >= items.length)
		return false;
	    TreeAreaItem item = items[hotPointY];
	    final int bound = (item.level + 1) * 2;
	    if (hotPointX <= bound)
	    {
		Speech.say(Langs.staticValue(Langs.BEGIN_OF_LINE), Speech.PITCH_HIGH);
		return true;
	    }
	    if (hotPointX >= item.title.length() + bound)
		hotPointX = item.title.length() + bound - 1; else
		hotPointX--;
	    Speech.sayLetter(item.title.charAt(hotPointX - bound));
	    Dispatcher.onAreaNewHotPoint(this);
	    return true;
	}

	return false;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	//FIXME:refresh;
	return false;
    }

    public String getName()
    {
	if (name == null)
	    return new String();
	return name;
    }

    //Changes only leaf and children fields;
    private void fillChildren(TreeAreaNode node)
    {
	if (node == null)
	    return;
	if (node.obj == null ||
	    node.leaf ||
	    model.isLeaf(node.obj))
	{
	    node.children = null;
	    node.leaf = true;
	    return;
	}
	int count = model.getChildCount(node.obj);
	if (count < 1)
	{
	    node.children = null;
	    node.leaf = true;
	    return;
	}
	node.leaf = false;
	node.children = new TreeAreaNode[count];
	for(int i = 0;i < count;i++)
	{
	    node.children[i] = new TreeAreaNode();
	    node.children[i].obj = model.getChild(node.obj, i);
	    if (node.children[i].obj == null)
	    {
		node.children = null;
		node.leaf = true;
		return;
	    }
	    node.children[i].leaf = model.isLeaf(node.children[i].obj);
	    node.children[i].parent = node;
	    node.children[i].children = null;
	    System.out.println("Got " + node.children[i].title());
	}
    }

    private TreeAreaNode constructNode(Object obj)
    {
	if (obj == null)
	    return null;
	TreeAreaNode node = new TreeAreaNode();
	node.obj = obj;
	node.parent = null;
	node.leaf = model.isLeaf(obj);
	if (!node.leaf)
	fillChildren(node);
	return node;
    }

    TreeAreaItem[] expandNode(TreeAreaNode node, int level)
    {
	if (node == null)
	    return null;
	TreeAreaItem itself = new TreeAreaItem();
	itself.node = node;
	itself.title = node.title();
	itself.level = level;
	if (node.leaf || node.children == null)
	{
	    itself.type = node.leaf?TreeAreaItem.LEAF:TreeAreaItem.CLOSED;
	    TreeAreaItem res[] = new TreeAreaItem[1];
	    res[0] = itself;
	    return res;
	}
	itself.type = TreeAreaItem.OPENED;
	Vector<TreeAreaItem> items = new Vector<TreeAreaItem>();
	items.add(itself);
	for(int i = 0;i < node.children.length;i++)
	{
	    TreeAreaItem c[] = expandNode(node.children[i], level + 1);
	    if (c == null)
		continue;
	    for(int k = 0;k < c.length;k++)
		items.add(c[k]);
	}
	TreeAreaItem res[] = new TreeAreaItem[items.size()];
	Iterator<TreeAreaItem> it = items.iterator();
	int k = 0;
	while(it.hasNext())
	    res[k++] = it.next();
	return res;
    }

    private String constructLineForSpeech(TreeAreaItem item)
    {
	if (item == null || item.title == null)
	    return new String();
	String res = item.title;
	switch (item.type)
	{
	case TreeAreaItem.OPENED:
	    res = "Раскрыто " + res;//FIXME:
	    break;
	case TreeAreaItem.CLOSED:
	    res = "Свёрнуто " + res;
	    break;
	}
	return res + " уровень " + (item.level + 1);
    }

    private String constructLineForScreen(TreeAreaItem item)
    {
	if (item == null || item.title == null)
	    return new String();
	return item.title;
    }

    public void onClick(Object obj)
    {
	//Nothing here;
    }
}
