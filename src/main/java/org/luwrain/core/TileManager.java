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
    }

    static final class CompositeNode extends Node
    {
	final Orientation orientation;
	Node node1 , node2;
int leafCount = 0;
CompositeNode(Orientation orientation, Node node1, Node node2)
	{
	    this.orientation = orientation;
	    this.node1 = node1;
	    this.node2 = node2;
	}
    }

    static final class LeafNode extends Node
    {
	final Tile tile;
	LeafNode(Tile tile)
	{
	    this.tile = tile;
	}
    }

    private Node nodes;

    void createSingle(Tile tile)
    {
	this.nodes = new LeafNode(tile);
    }

    void createLeftRight(Tile tile1, Tile tile2)
    {
	final LeafNode
	node1 = new LeafNode(tile1),
	node2 = new LeafNode(tile2);
	this.nodes = new CompositeNode(Orientation.HORIZ, node1, node2);
    }

    void createTopBottom(Tile tile1, Tile tile2)
    {
	final LeafNode
	node1 = new LeafNode(tile1),
node2 = new LeafNode(tile2);
	this.nodes = new CompositeNode(Orientation.VERT, node1, node2);
    }

    void createLeftTopBottom(Tile tile1, Tile tile2, Tile tile3)
    {
	final LeafNode
	node1 = new LeafNode(tile1),
	node2 = new LeafNode(tile2),
node3 = new LeafNode(tile3);
	final CompositeNode rightSide = new CompositeNode(Orientation.VERT, node2, node3);
	this.nodes = new CompositeNode(Orientation.HORIZ, node1, rightSide);
    }

    void createLeftRightBottom(Tile tile1, Tile tile2, Tile tile3)
    {
	final LeafNode
	node1 = new LeafNode(tile1),
	node2 = new LeafNode(tile2),
node3 = new LeafNode(tile3);
	final CompositeNode top = new CompositeNode(Orientation.HORIZ, node1, node2);
	this.nodes = new CompositeNode(Orientation.VERT, top, node3);
    }

    void createHorizontally(Tile[] tiles)
    {
	if (tiles == null || tiles.length == 0)
	{
	    this.nodes = null;
	    return;
	}
	if (tiles.length == 1)
	{
	    this.nodes = new LeafNode(tiles[0]);
	    return;
	}
	final LeafNode
	node1 = new LeafNode(tiles[0]),
node2 = new LeafNode(tiles[1]);
	CompositeNode node = new CompositeNode(Orientation.HORIZ, node1, node2);
	for(int i = 2;i < tiles.length;i++)
	    node = new CompositeNode(Orientation.HORIZ, node, new LeafNode(tiles[i]));
	this.nodes = node;
    }

        void createVertically(Tile tiles[])
    {
	if (tiles == null || tiles.length == 0)
	{
	    this.nodes = null;
	    return;
	}
	if (tiles.length == 1)
	{
	    this.nodes = new LeafNode(tiles[0]);
	    return;
	}
	final LeafNode
	node1 = new LeafNode(tiles[0]),
node2 = new LeafNode(tiles[1]);
	CompositeNode node = new CompositeNode(Orientation.VERT, node1, node2);
	for(int i = 2;i < tiles.length;i++)
	    node = new CompositeNode(Orientation.VERT, node, new LeafNode(tiles[i]));
	nodes = node;
    }

    void addTop(Tile tile)
    {
	if (tile == null)
	    return;
	if (nodes == null)
	{
	    this.nodes = new LeafNode(tile);
	    return;
	}
	this.nodes = new CompositeNode(Orientation.VERT, new LeafNode(tile), nodes);
    }

    void addBottom(Tile tile)
    {
	if (tile == null)
	    return;
	if (nodes == null)
	{
	    this.nodes = new LeafNode(tile);
	    return;
	}
	this.nodes = new CompositeNode(Orientation.VERT, nodes, new LeafNode(tile));
    }

    void addLeftSide(Tile tile)
    {
	if (tile == null)
	    return;
	if (nodes == null)
	{
	    this.nodes = new LeafNode(tile);
	    return;
	}
	this.nodes = new CompositeNode(Orientation.HORIZ, nodes, new LeafNode(tile));
    }

    void addRightSide(Tile tile)
    {
	if (tile == null)
	    return;
	if (nodes == null)
	{
	    this.nodes = new LeafNode(tile);
	    return;
	}
	this.nodes = new CompositeNode(Orientation.HORIZ, new LeafNode(tile), nodes);
    }

    void replace(Tile tile, TileManager replaceWith)
    {
	if (tile == null || replaceWith == null || replaceWith.nodes == null)
	    return;
	this.nodes = replaceImpl(nodes, tile, replaceWith.nodes);
    }

    private Node replaceImpl(Node node, Tile tile, Node replaceWith)
    {
	if (node == null)
	    return null;
	if (node instanceof LeafNode leaf)
	    return leaf.tile == tile?replaceWith:leaf;
	final CompositeNode composite = (CompositeNode)node;
	composite.node1 = replaceImpl(composite.node1, tile, replaceWith);
	composite.node2 = replaceImpl(composite.node2, tile, replaceWith);
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
	    Tile[] o = new Tile[1];
	    o[0] = leaf.tile;
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
	    it.onTile(leafNode.tile);
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
	return n.tile;
    }
}
