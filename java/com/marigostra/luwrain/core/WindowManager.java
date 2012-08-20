/*
   Copyright 2012 Michael Pozhidaev <msp@altlinux.org>

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

package com.marigostra.luwrain.core;

class WindowTreeNode
{
    public static final int AREA = 0;
    public static final int COMPOSITE = 1;

    public Application app = null;
    private int type;

    public WindowTreeNode(int type)
    {
	this.type = type;
    }

    public int getType()
    {
	return type;
    }
}

class CompositeWindowTreeNode extends WindowTreeNode
{
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    public int direction = HORIZONTAL;
    public WindowTreeNode node1 = null;
    public WindowTreeNode node2 = null;

    public CompositeWindowTreeNode()
    {
	super(COMPOSITE);
    }
}

class AreaWindowTreeNode extends WindowTreeNode
{
public Area area = null;

    public AreaWindowTreeNode()
    {
	super(AREA);
    }
}

public class WindowManager
{
    private WindowTreeNode windows = null;
    private AreaWindowTreeNode active = null;

    public void takeNewLayout(Application app, AreaLayout layout)
    {
	switch (layout.getType())
	{
	case AreaLayout.SINGLE:
	    if (layout.getArea1() == null)
		return;
	    AreaWindowTreeNode node = new AreaWindowTreeNode();
	    node.app = app;
	    node.area = layout.getArea1();
	    windows = node;
	    active = node;
	    onSetActiveArea(node);
	    //FIXME:redraw;
	    break;
	case AreaLayout.LEFT_TOP_BOTTOM:
	    if (layout.getArea1() == null || layout.getArea2() == null || layout.getArea3() == null)
		return;
	    AreaWindowTreeNode node1 = new AreaWindowTreeNode(), node2 = new AreaWindowTreeNode(), node3 = new AreaWindowTreeNode();
	    node1.app = app;
	    node1.area = layout.getArea1();
	    node2.app = app;
	    node2.area = layout.getArea2();
	    node3.app = app;
	    node3.area = layout.getArea3();
	    CompositeWindowTreeNode composite1 = new CompositeWindowTreeNode(), composite2 = new CompositeWindowTreeNode();
	    composite1.app = app;
	    composite1.direction = CompositeWindowTreeNode.VERTICAL;
	    composite1.node1 = node1;
	    composite1.node2 = composite2;
	    composite2.app = app;
	    composite2.direction = CompositeWindowTreeNode.HORIZONTAL;
	    composite2.node1 = node2;
	    composite2.node2 = node3;
	    windows = composite1;
	    active = node1;
	    onSetActiveArea(node1);
	    //FIXME:redraw;
	    break;
	    //FIXME:case LEFT_RIGHT_BOTTOM;
	}
    }

    public void setActiveArea(Application app, Area area)
    {
	setActiveAreaImpl(windows, app, area);
    }

    public void onKeyboardEvent(KeyboardEvent event)
    {
	if (active == null)
	    return;
	active.area.onKeyboardEvent(event);
    }

    private boolean setActiveAreaImpl(WindowTreeNode node, Application app, Area area)
    {
	if (node == null)
	    return false;
	if (node.getType() == WindowTreeNode.AREA)
	{
	    AreaWindowTreeNode areaNode = (AreaWindowTreeNode)node;
	    if (areaNode.app != app || areaNode.area != area)
		return false;
	    active = areaNode;
	    onSetActiveArea(areaNode);
	    return true;
	}
	if (node.getType() != WindowTreeNode.COMPOSITE)
	    return false;
	CompositeWindowTreeNode compositeNode = (CompositeWindowTreeNode)node;
	if (setActiveAreaImpl(compositeNode.node1, app, area))
	    return true;
	if (setActiveAreaImpl(compositeNode.node2, app, area))
	    return true;
	return false;
    }

    private void onSetActiveArea(AreaWindowTreeNode areaNode)
    {
	Speech.say(areaNode.area.getName());
    }
}
