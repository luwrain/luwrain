/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.interaction;

import java.util.Vector;
import org.luwrain.core.Log;

class OnScreenLineTracker
{
    class Vertex
    {
	public int pos = 0;
	public boolean followed = false;

	public Vertex(int pos, boolean followed)
	{
	    this.pos = pos;
	    this.followed = followed;
	}
    }

    private Vector<Vertex> vertices = new Vector<Vertex>();

    public void cover(int pos1, int pos2)
    {
	if (pos1 < 0 || pos1 > pos2)
	    return;
	addVertex(pos1);
	addVertex(pos2 + 1);
	for(int i = 0;i < vertices.size();i++)
	{
	    Vertex v = vertices.get(i);
	    if (v.pos >= pos1 && v.pos <= pos2)
		v.followed = true;
	    if (v.pos > pos2)
		break;
	}
	removeNeedless();
    }

    public void uncover(int pos1, int pos2)
    {
	if (pos1 < 0 || pos1 > pos2)
	    return;
	addVertex(pos1);
	addVertex(pos2 + 1);
	for(int i = 0;i < vertices.size();i++)
	{
	    Vertex v = vertices.get(i);
	    if (v.pos >= pos1 && v.pos <= pos2)
		v.followed = false;
	    if (v.pos > pos2)
		break;
	}
	removeNeedless();
    }

    public void clear()
    {
	vertices.clear();
    }

    public OnScreenLine[] getLines()
    {
	if (vertices.size() < 2)
	    return new OnScreenLine[0];
	OnScreenLine[] lines = new OnScreenLine[vertices.size() / 2];
	for(int i = 0;i < vertices.size() / 2;i++)
	{
	    if (!vertices.get(2 * i).followed || vertices.get((2 * i) + 1).followed ||
		vertices.get(2 * i).pos >= vertices.get((2 * i) + 1).pos)
	    {
		Log.warning("interaction", "on screen lines tracking  is broken, some of them will be missed");
		return new OnScreenLine[0];
	    }
	    lines[i] = new OnScreenLine(vertices.get(2 * i).pos, vertices.get((2 * i) + 1).pos - 1);
	}
	return lines;
    }

    private void addVertex(int pos)
    {
	int i = 0;
	while(i < vertices.size() && vertices.get(i).pos < pos)
	    i++;
	    if (i < vertices.size() && vertices.get(i).pos == pos)
		return;
	boolean followed = i > 0?vertices.get(i - 1).followed:false;
	if (i < vertices.size())
	    vertices.add(i, new Vertex(pos, followed)); else
	    vertices.add(new Vertex(pos, followed));
    }

    private void removeNeedless()
    {
	//There should not be unfollowed vertices at the beginning;
	int offset = 0;
	while(offset < vertices.size() && !vertices.get(offset).followed)
	    offset++;
	if (offset >= vertices.size())
	{
	    vertices.clear();
	    return;
	}
	if (offset > 0)
	{
	    for(int i = offset;i < vertices.size();i++)
		vertices.set(i - offset, vertices.get(i));
		vertices.setSize(vertices.size() - offset);
	}
	if (vertices.size() < 2)
	    return;
	    //The doubling removing;
	    offset = 0;
	for(int i = 1;i < vertices.size();i++)
	{
	    if (vertices.get(i - 1 - offset).followed == vertices.get(i).followed)
		offset++; else
		vertices.set(i - offset, vertices.get(i));
	}
	vertices.setSize(vertices.size() - offset);
	if (!vertices.isEmpty() && (vertices.size() % 2) != 0)
	{
	    Log.warning("interaction", "on screen lines tracking has odd vertex count:" + vertices.size() + ", clearing content");
	    vertices.clear();
	}
    }
}
