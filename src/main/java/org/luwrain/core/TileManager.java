/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core;

import static org.luwrain.core.NullCheck.*;

final class TileManager 
{
    enum Orientation {HORIZ, VERT};

    static abstract class Node
    {
	enum Type {LEAF, COMPOSITE};
	final Type type;
    Node(Type type) { this.type = type; }
    }

    static final class CompositeNode extends Node
    {
	final Orientation orientation;
	Node node1 , node2;
int leafCount = 0;
CompositeNode(Orientation orientation, Node node1, Node node2)
	{
	    super(Type.COMPOSITE);
	    this.orientation = orientation;
	    this.node1 = node1;
	    this.node2 = node2;
	}
    }

    static final class LeafNode extends Node
    {
	final Object obj;
	LeafNode(Object obj)
	{
	    super(Type.LEAF);
	    this.obj = obj;
	}
    }

    private Node nodes;

    void createSingle(Object obj)
    {
	nodes = new LeafNode(obj);
    }

    void createLeftRight(Object obj1, Object obj2)
    {
	LeafNode node1 = new LeafNode(obj1);
	LeafNode node2 = new LeafNode(obj2);
	nodes = new CompositeNode(Orientation.HORIZ, node1, node2);
    }

    void createTopBottom(Object obj1, Object obj2)
    {
	LeafNode node1 = new LeafNode(obj1);
	LeafNode node2 = new LeafNode(obj2);
	nodes = new CompositeNode(Orientation.VERT, node1, node2);
    }

    void createLeftTopBottom(Object obj1,
				    Object obj2, 
				    Object obj3)
    {
	LeafNode node1 = new LeafNode(obj1);
	LeafNode node2 = new LeafNode(obj2);
	LeafNode node3 = new LeafNode(obj3);
	CompositeNode rightSide = new CompositeNode(Orientation.VERT, node2, node3);
	nodes = new CompositeNode(Orientation.HORIZ, node1, rightSide);
    }

    void createLeftRightBottom(Object obj1,
				    Object obj2, 
				    Object obj3)
    {
	LeafNode node1 = new LeafNode(obj1);
	LeafNode node2 = new LeafNode(obj2);
	LeafNode node3 = new LeafNode(obj3);
	CompositeNode top = new CompositeNode(Orientation.HORIZ, node1, node2);
	nodes = new CompositeNode(Orientation.VERT, top, node3);
    }

    void createHorizontally(Object[] objects)
    {
	if (objects == null || objects.length <= 0)
	{
	    nodes = null;
	    return;
	}
	if (objects.length == 1)
	{
	    nodes = new LeafNode(objects[0]);
	    return;
	}
	LeafNode node1 = new LeafNode(objects[0]);
	LeafNode node2 = new LeafNode(objects[1]);
	CompositeNode node = new CompositeNode(Orientation.HORIZ, node1, node2);
	for(int i = 2;i < objects.length;i++)
	    node = new CompositeNode(Orientation.HORIZ, node, new LeafNode(objects[i]));
	nodes = node;
    }

        void createVertically(Object[] objects)
    {
	if (objects == null || objects.length <= 0)
	{
	    nodes = null;
	    return;
	}
	if (objects.length == 1)
	{
	    nodes = new LeafNode(objects[0]);
	    return;
	}
	LeafNode node1 = new LeafNode(objects[0]);
	LeafNode node2 = new LeafNode(objects[1]);
	CompositeNode node = new CompositeNode(Orientation.VERT, node1, node2);
	for(int i = 2;i < objects.length;i++)
	    node = new CompositeNode(Orientation.VERT, node, new LeafNode(objects[i]));
	nodes = node;
    }

    void addTop(Object o)
    {
	if (o == null)
	    return;
	if (nodes == null)
	{
	    nodes = new LeafNode(o);
	    return;
	}
	nodes = new CompositeNode(Orientation.VERT, new LeafNode(o), nodes);
    }

    void addBottom(Object o)
    {
	if (o == null)
	    return;
	if (nodes == null)
	{
	    nodes = new LeafNode(o);
	    return;
	}
	nodes = new CompositeNode(Orientation.VERT, nodes, new LeafNode(o));
    }

    void addLeftSide(Object o)
    {
	if (o == null)
	    return;
	if (nodes == null)
	{
	    nodes = new LeafNode(o);
	    return;
	}
	nodes = new CompositeNode(Orientation.HORIZ, nodes, new LeafNode(o));
    }

    void addRightSide(Object o)
    {
	if (o == null)
	    return;
	if (nodes == null)
	{
	    nodes = new LeafNode(o);
	    return;
	}
	nodes = new CompositeNode(Orientation.HORIZ, new LeafNode(o), nodes);
    }

    void replace(Object obj, TileManager replaceWith)
    {
	if (obj == null || replaceWith == null || replaceWith.nodes == null)
	    return;
	nodes = replaceImpl(nodes, obj, replaceWith.nodes);
    }

    private Node replaceImpl(Node node, Object obj, Node replaceWith)
    {
	if (node == null)
	    return null;
	if (node instanceof LeafNode leaf)
	    return leaf.obj == obj?replaceWith:leaf;
	CompositeNode composite = (CompositeNode)node;
	composite.node1 = replaceImpl(composite.node1, obj, replaceWith);
	composite.node2 = replaceImpl(composite.node2, obj, replaceWith);
	return composite;
    }

    void clear()
    {
	nodes = null;
    }

    Object[] getObjects()
    {
	return getObjectsImpl(nodes);
    }

    private Object[] getObjectsImpl(Node node)
    {
	if (node == null)
	    return new Object[0];
	if (node instanceof LeafNode leaf)
	{
	    Object[] o = new Object[1];
	    o[0] = leaf.obj;
	    return o;
	}
	CompositeNode composite = (CompositeNode)node;
	Object[] o1 = getObjectsImpl(composite.node1);
	Object[] o2 = getObjectsImpl(composite.node2);
	Object[] o = new Object[o1.length + o2.length];
	for(int i = 0;i < o1.length;i++)
	    o[i] = o1[i];
	for(int i = 0;i < o2.length;i++)
	    o[o1.length + i] = o2[i];
	return o;
    }

    void enumerate(TileVisitor it)
    {
	enumerateImpl(nodes, it);
    }

    private void enumerateImpl(Node node, TileVisitor it)
    {
	if (node == null)
	    return;
	if (node instanceof CompositeNode compositeNode)
	{
	    enumerateImpl(compositeNode.node1, it);
	    enumerateImpl(compositeNode.node2, it);
	    return;
	}
	if (node instanceof LeafNode leaf)
	{
	    LeafNode leafNode = (LeafNode)node;
	    it.onTile(leafNode.obj);
	    return;
	}
	//Should never goes here;
    }

    int countLeaves()
    {
	return countLeavesImpl(nodes);
    }

    private int countLeavesImpl(Node node)
    {
	if (node == null)
	    return 0;
	if (node instanceof LeafNode)
	    return 1;
	final CompositeNode composite = (CompositeNode)node;
	composite.leafCount = countLeavesImpl(composite.node1) + countLeavesImpl(composite.node2);
	return composite.leafCount;
    }

    Object getRoot()
    {
	return nodes;
    }

    static boolean isLeaf(Object o)
    {
	if (o == null)
	    return false;
	Node n = (Node)o;
	return n instanceof LeafNode;
    }

    int getLeafCount(Object o)
    {
	if (o == null)
	    return 0;
	Node n = (Node)o;
	if (n instanceof LeafNode)
	    return 1;
	final CompositeNode c = (CompositeNode)n;
	return c.leafCount;
    }

    Object getBranch1(Object o)
    {
	if (o == null)
	    return null;
	CompositeNode n = (CompositeNode)o;
	return n.node1;
    }

    Object getBranch2(Object o)
    {
	if (o == null)
	    return null;
	CompositeNode n = (CompositeNode)o;
	return n.node2;
    }

    Orientation getOrientation(Object o)
    {
	if (o == null)
	    return Orientation.HORIZ;
	final CompositeNode n = (CompositeNode)o;
	return n.orientation;
    }

    Object getLeafObject(Object o)
    {
	if (o == null)
	    return null;
	LeafNode n = (LeafNode)o;
	return n.obj;
    }
}
