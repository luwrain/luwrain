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

package org.luwrain.core;

//TODO:Proper active area specification at taking new layout;

import java.util.concurrent.atomic.AtomicBoolean;
import org.luwrain.core.events.*;
import org.luwrain.mmedia.EnvironmentSounds;

interface HiddenArea
{
    boolean onHiddenAreaNewContent(Area area);
    Area getHiddenArea();
}

abstract class WindowTreeNode
{
    public static final int AREA = 0;
    public static final int COMPOSITE = 1;
    public static final int POPUP = 2;

    public Application app = null;

    abstract public int getType();
}

class CompositeWindowTreeNode extends WindowTreeNode
{
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    public int direction = HORIZONTAL;
    public WindowTreeNode node1 = null;
    public WindowTreeNode node2 = null;

    public int getType()
    {
	return COMPOSITE;
    }
}

class AreaWindowTreeNode extends WindowTreeNode
{
    public Area area;//Normally never can be null;
    public HiddenArea hiddenArea = null;

    public int getType()
    {
	return AREA;
    }
}

class PopupWindowTreeNode extends AreaWindowTreeNode
{
    public WindowTreeNode node = null;
    public int place = 0;

    public int getType()
    {
	return POPUP;
    }
}

class TextReviewMode extends NavigateArea implements HiddenArea
{
    private Area hiddenArea = null;

    public int getLineCount()
    {
	if (hiddenArea == null)
	    return 1;
	return hiddenArea.getLineCount();
    }

    public String getLine(int index)
    {
	if (hiddenArea == null)
	    return new String();
	return hiddenArea.getLine(index);
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	//FIXME:
	return false;
    }

    public String getName()
    {
	if (hiddenArea == null)
	    return "FIXME";
	return hiddenArea.getName();///FIXME:Additional info;
    }

    public boolean onHiddenAreaNewContent(Area area)
    {
	return false;
    }

    public Area getHiddenArea()
    {
	return null;
    }
}

public class WindowManager
{
    public static final int POPUP_LEFT = 0;
    public static final int POPUP_TOP = 1;
    public static final int POPUP_RIGHT = 2;
    public static final int POPUP_BOTTOM = 3;

    private WindowTreeNode windows = null;
    private AreaWindowTreeNode active = null;

    //methods changing set of areas;

    public void takeCompleteNewLayout(Application app,
				      AreaLayout layout,
				      boolean blockingMode,
				      Area popupArea,
				      int popupPlace)
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
	    break;
	    //FIXME:case LEFT_RIGHT_BOTTOM;
	}
	if (popupArea == null)
	{
	    onSetActiveArea();
	    //FIXME:redraw;
	    return;
	}
	PopupWindowTreeNode popupNode = new PopupWindowTreeNode();
	popupNode.area = popupArea;
	popupNode.node = windows;
	popupNode.place = popupPlace;
	windows = popupNode;
	active = popupNode;
	onSetActiveArea();
    }

    public void replaceAreasOfAppWithNewLayout(Application oldApp,
				   Application newApp,
				   AreaLayout layout,
				   boolean blockingMode,
				   Area desiredActiveArea)
    {
	if (oldApp == null || newApp == null || layout == null)
	    return;
	if (hasAnyNonPopupAreaOfApp(newApp))
	    return;
	WindowTreeNode res;
	AreaWindowTreeNode newActive;
	switch (layout.getType())
	{
	case AreaLayout.SINGLE:
	    if (layout.getArea1() == null)
		return;
	    AreaWindowTreeNode node = new AreaWindowTreeNode();
	    node.app = newApp;
	    node.area = layout.getArea1();
	    res = node;
	    //Ignoring desiredActiveArea, no choice;
	    newActive = node;
	    break;
	case AreaLayout.LEFT_TOP_BOTTOM:
	    if (layout.getArea1() == null ||
		layout.getArea2() == null ||
		layout.getArea3() == null)
		return;
	    AreaWindowTreeNode node1 = new AreaWindowTreeNode(), node2 = new AreaWindowTreeNode(), node3 = new AreaWindowTreeNode();
	    node1.app = newApp;
	    node1.area = layout.getArea1();
	    node2.app = newApp;
	    node2.area = layout.getArea2();
	    node3.app = newApp;
	    node3.area = layout.getArea3();
	    CompositeWindowTreeNode composite1 = new CompositeWindowTreeNode(), composite2 = new CompositeWindowTreeNode();
	    composite1.app = newApp;
	    composite1.direction = CompositeWindowTreeNode.VERTICAL;
	    composite1.node1 = node1;
	    composite1.node2 = composite2;
	    composite2.app = newApp;
	    composite2.direction = CompositeWindowTreeNode.HORIZONTAL;
	    composite2.node1 = node2;
	    composite2.node2 = node3;
	    res = composite1;
		newActive = node1;
	    if (desiredActiveArea != null)
	    {
		if (desiredActiveArea == node1.area)
		    newActive = node1; else
		    if (desiredActiveArea == node2.area)
			newActive = node2; else
			if (desiredActiveArea == node3.area)
			    newActive = node3;
	    }
	    break;
	    //FIXME:case LEFT_RIGHT_BOTTOM;
	default:
	    return;
	}
	windows = replaceNonPopupNodeByAppImpl(windows, oldApp, res, new AtomicBoolean(true));
	if (active != null && active.app == oldApp)
	{
	    active = newActive;
	    onSetActiveArea();
	}
    }

    //If one of the areas to remove is active, none of the remaining areas becomes active, otherwise active area is left unchanged; 
    public void closeNonPopupAreasByApp(Application app)
    {
	fixNodeApplicationAssociations(windows);
	windows = closeNonPopupAreasByAppImpl(windows, app);
	if (active.app == app)
	    active = null;
    }

    //Changing active area;

    //Including popup;
    public void gotoNextArea()
    {
	AreaWindowTreeNode areaNodes[] = allAreaNodes();
	if (areaNodes == null || areaNodes.length == 0)
	{
	    active = null;
	    return;
	}
	int index = 0;
	for(index = 0;index < areaNodes.length;index++)
	    if (active == areaNodes[index])
		break;
	if (index >= areaNodes.length)
	{
	    active = areaNodes[0];
	    onSetActiveArea();
	    return;
	}
	index++;
	if (index >= areaNodes.length)
	    index = 0;
	if (active != areaNodes[index])
	{
	    active = areaNodes[index];
	    onSetActiveArea();
	}
    }

    public void setNonPopupAreaActive(Application app, Area area)
    {
	setNonPopupAreaActiveImpl(windows, app, area);
    }

    public void setActiveFirstNonPopupArea()
    {
	AreaWindowTreeNode areaNodes[] = allAreaNodes();
	if (areaNodes == null || areaNodes.length < 1)
	{
	    active = null;
	    return;
	}
	int index = 0;
	while(index < areaNodes.length && areaNodes[index].getType() == WindowTreeNode.POPUP)
	    index++;
	if (index >= areaNodes.length)
	{
	    active = null;
	    return;
	}
	active = areaNodes[index];
	onSetActiveArea();
    }

    //Information methods;

    public boolean hasNonPopupArea(Area area)
    {
	return hasNonPopupAreaImpl(windows, area);
    }

    public boolean hasAnyNonPopupAreaOfApp(Application app)
    {
	return hasAnyNonPopupAreaOfAppImpl(windows, app);
    }

    public boolean hasAnyNonPopupArea()
    {
	if (windows == null)
	    return false;
	if (windows.getType() != WindowTreeNode.POPUP)
	    return true;
	PopupWindowTreeNode popupNode = (PopupWindowTreeNode)windows;
	return popupNode.node != null;
    }


    //Returns null if active is popup area;
    public Application getAppOfActiveNonPopupArea()
    {
	if (active == null || active.getType() != WindowTreeNode.AREA)
	    return null;
	return active.app;
    }

    //Returns null if active area is popup;
    public Area getActiveNonPopupArea()
    {
	if (active == null || active.getType() != WindowTreeNode.AREA)
	    return null;
	return active.area;
    }

    //Popup area;

    public void openPopupArea(Area popupArea, int popupPlace)
    {
	if (windows != null && windows.getType() == WindowTreeNode.POPUP)
	{
	    PopupWindowTreeNode node = (PopupWindowTreeNode)windows;
	    node.area = popupArea;
	    node.place = popupPlace;
	    active = node;
	    onSetActiveArea();
	    return;
	}
	PopupWindowTreeNode popupNode = new PopupWindowTreeNode();
	popupNode.area = popupArea;
	popupNode.node = windows;
	popupNode.place = popupPlace;
	windows = popupNode;
	active = popupNode;
	onSetActiveArea();
	//FIXME:redraw;
    }

    public boolean hasPopupArea()
    {
	if (windows == null)
	    return false;
	return windows.getType() == WindowTreeNode.POPUP;
    }

    public boolean isPopupAreaActive()
    {
	if (active == null)
	    return false;
	return active.getType() == WindowTreeNode.POPUP;
    }

    public Area getPopupArea()
    {
	if (windows == null)
	    return null;
	if (windows.getType() != WindowTreeNode.POPUP)
	    return null;
	PopupWindowTreeNode popupNode = (PopupWindowTreeNode)windows;
	return popupNode.area;
    }

    public void setPopupAreaActive()
    {
	if (windows == null || windows.getType() != WindowTreeNode.POPUP)
	    return;
	active = (PopupWindowTreeNode)windows;
	onSetActiveArea();
    }

    //If popup area was active there is no active area after operation;
    public void closePopupArea()
    {
	if (windows == null || windows.getType() != WindowTreeNode.POPUP)
	    return;
	PopupWindowTreeNode popupNode = (PopupWindowTreeNode)windows;
	windows = popupNode.node;
	if (active == popupNode)
	    active = null;
    }

    //Events processing;

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (active == null)
	    return false;
	try {
	    if (!active.area.onKeyboardEvent(event))
		EnvironmentSounds.play(EnvironmentSounds.EVENT_NOT_PROCESSED);
	} 
	catch(Exception e)//FIXME:
	{
	    Environment.message(Langs.staticValue(Langs.APPLICATION_INTERNAL_ERROR));
	}
	return true;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (active == null)
	    return false;
	try {
	    active.area.onEnvironmentEvent(event);
	} 
	catch(Exception e)//FIXME:
	{
	    Environment.message(Langs.staticValue(Langs.APPLICATION_INTERNAL_ERROR));
	}
	return true;
    }

    public void onAreaNewHotPoint(Area area)
    {
	//No matter is popup area or not;
	//FIXME:
    }

    public void onAreaNewContent(Area area)
    {
	//No matter is popup area or not;
	//FIXME:
    }

    //Auxiliary method;

    private boolean setNonPopupAreaActiveImpl(WindowTreeNode node, Application app, Area area)
    {
	if (node == null)
	    return false;
	if (node.getType() == WindowTreeNode.AREA)
	{
	    AreaWindowTreeNode areaNode = (AreaWindowTreeNode)node;
	    if (areaNode.app != app)
		return false;
	    if (areaNode.hiddenArea != null && areaNode.hiddenArea.getHiddenArea() == area)
	    {
		active = areaNode;
		onSetActiveArea();
		return true;
	    }
	    if (areaNode.area == area)
	    {
		active = areaNode;
		onSetActiveArea();
		return true;
	    }
	    return false;
	}
	if (node.getType() == WindowTreeNode.POPUP)
	{
	    PopupWindowTreeNode popupNode = (PopupWindowTreeNode)node;
	    return setNonPopupAreaActiveImpl(popupNode.node, app, area);
	}
	if (node.getType() != WindowTreeNode.COMPOSITE)
	    return false;
	CompositeWindowTreeNode compositeNode = (CompositeWindowTreeNode)node;
	if (setNonPopupAreaActiveImpl(compositeNode.node1, app, area))
	    return true;
	if (setNonPopupAreaActiveImpl(compositeNode.node2, app, area))
	    return true;
	return false;
    }

    private void fixNodeApplicationAssociations(WindowTreeNode node)
    {
	if (node == null)
	    return;
	if (node.getType() == WindowTreeNode.COMPOSITE)
	{
	    CompositeWindowTreeNode composite = (CompositeWindowTreeNode)node;
	    if (composite.node1 == null && composite.node2 == null)//Normally never happens;
		return;
	    fixNodeApplicationAssociations(composite.node1);
	    fixNodeApplicationAssociations(composite.node2);
	    /*
	     * It could be a good idea in case when one of the nodes is null remove
	     * intermediate node and link parent node to the second not null node
	     * directly but actually this method should be called in conjunction with
	     * closing nodes by application and required cleaning gets done
	     * automatically.
	     */
	    if (composite.node1 == null)
	    {
		composite.app = composite.node2.app;
		return;
	    }
	    if (composite.node2 == null)
	    {
		composite.app = composite.node1.app;
		return;
	    }
	    if (composite.node1.app == composite.node2.app)//Including the case they are both have null value;
		composite.app = composite.node1.app; else
		composite.app = null;
	    return;
	}
	if (node.getType() == WindowTreeNode.POPUP)
	{
	    PopupWindowTreeNode popup = (PopupWindowTreeNode)node;
	    fixNodeApplicationAssociations(popup.node);
	    return;
	}
	//No actions for area node;
    }

    private WindowTreeNode closeNonPopupAreasByAppImpl(WindowTreeNode node , Application app)
    {
	if (node == null)
	    return null;
	if (node.getType() == WindowTreeNode.AREA)
	{
	    AreaWindowTreeNode areaNode = (AreaWindowTreeNode)node;
	    if (areaNode.app == app)
		return null;
	    return node;
	}
	if (node.getType() == WindowTreeNode.COMPOSITE)
	{
	    CompositeWindowTreeNode composite = (CompositeWindowTreeNode)node;
	    if (composite.app != null)
	    {
		if (composite.app == app)
		    return null;
		return node;
	    }
	    composite.node1 = closeNonPopupAreasByAppImpl(composite.node1, app);
	    composite.node2 = closeNonPopupAreasByAppImpl(composite.node2, app);
	    if (composite.node1 == null && composite.node2 == null)//Should not happen;
		return null;
	    if (composite.node1 == null)
		return composite.node2;
	    if (composite.node2 == null)
		return composite.node1;
	    return composite;
	}
	if (node.getType() == WindowTreeNode.POPUP)
	{
	    PopupWindowTreeNode popup = (PopupWindowTreeNode)node;
	    popup.node = closeNonPopupAreasByAppImpl(popup.node, app);
	    return popup;
	}
	return null;
    }

    private AreaWindowTreeNode[] allAreaNodes()
    {
	return allAreaNodesImpl(windows);
    }

    private AreaWindowTreeNode[] allAreaNodesImpl(WindowTreeNode node)
    {
	if (node == null)
	    return null;
	if (node.getType() == WindowTreeNode.AREA)
	{
	    AreaWindowTreeNode nodes[] = new AreaWindowTreeNode[1];
	    nodes[0] = (AreaWindowTreeNode)node;
	    return nodes;
	}
	if (node.getType() == WindowTreeNode.COMPOSITE)
	{
	    CompositeWindowTreeNode composite = (CompositeWindowTreeNode)node;
	    AreaWindowTreeNode nodes1[] = allAreaNodesImpl(composite.node1);
	    AreaWindowTreeNode nodes2[] = allAreaNodesImpl(composite.node2);
	    final int len1 = nodes1 != null?nodes1.length:0;
	    final int len2 = nodes2 != null?nodes2.length:0;
	    int pos = 0;
	    AreaWindowTreeNode res[] = new AreaWindowTreeNode[len1 + len2];
	    if (nodes1 != null)
		for(int i = 0;i < nodes1.length;i++)
		    res[pos++] = nodes1[i];
	    if (nodes2 != null)
		for(int i = 0;i < nodes2.length;i++)
		    res[pos++] = nodes2[i];
	    return res;
	}
	if (node.getType() == WindowTreeNode.POPUP)
	{
	    PopupWindowTreeNode popup = (PopupWindowTreeNode)node;
	    AreaWindowTreeNode nodes[] = allAreaNodesImpl(popup.node);
	    if (nodes == null)
	    {
		AreaWindowTreeNode res[] = new AreaWindowTreeNode[1];
		res[0] = popup;
		return res;
	    }
	    AreaWindowTreeNode res[] = new AreaWindowTreeNode[nodes.length + 1]; 
	    for(int i = 0;i < nodes.length;i++)
		res[i] = nodes[i];
	    res[res.length - 1] = popup;
	    return res;
	}
	//Normally never happens;
	return null;
    }

    private WindowTreeNode replaceNonPopupNodeByAppImpl(WindowTreeNode node, Application oldApp, WindowTreeNode replaceByNode, AtomicBoolean shouldContinue)
    {
	if (node == null)
	    return null;
	if (node.getType() == WindowTreeNode.AREA)
	{
	    if (node.app == oldApp)
	    {
		shouldContinue.set(false);
		return replaceByNode;
	    }
	    return node;
	}
	if (node.getType() == WindowTreeNode.COMPOSITE)
	{
	    if (node.app == oldApp)
	    {
		shouldContinue.set(false);
		return replaceByNode;
	    }
	    CompositeWindowTreeNode compositeNode = (CompositeWindowTreeNode)node;
	    compositeNode.node1 = replaceNonPopupNodeByAppImpl(compositeNode.node1, oldApp, replaceByNode, shouldContinue);
	    if (!shouldContinue.get())
		return compositeNode;
	    compositeNode.node2 = replaceNonPopupNodeByAppImpl(compositeNode.node2, oldApp, replaceByNode, shouldContinue);
	    return compositeNode;
	}
	if (node.getType() == WindowTreeNode.POPUP)
	{
	    PopupWindowTreeNode popupNode = (PopupWindowTreeNode)node;
	    popupNode.node = replaceNonPopupNodeByAppImpl(popupNode.node, oldApp, replaceByNode, shouldContinue);
	    return popupNode;
	}
	return node;//Normally never happens;
    }

    private boolean hasAnyNonPopupAreaOfAppImpl(WindowTreeNode node, Application app)
    {
	if (node == null)
	    return false;
	if (node.getType() == WindowTreeNode.AREA)
	    return node.app == app;
	if (node.getType() == WindowTreeNode.COMPOSITE)
	{
	    CompositeWindowTreeNode compositeNode = (CompositeWindowTreeNode)node;
	    if (hasAnyNonPopupAreaOfAppImpl(compositeNode.node1, app))
	    return true;
	    return 	    hasAnyNonPopupAreaOfAppImpl(compositeNode.node2, app);
	}
	if (node.getType() == WindowTreeNode.POPUP)
	{
	    PopupWindowTreeNode popupNode = (PopupWindowTreeNode)node;
	    return hasAnyNonPopupAreaOfAppImpl(popupNode.node, app);
	}
	return false;
    }

    private boolean hasNonPopupAreaImpl(WindowTreeNode node, Area area)
    {
	if (node == null)
	    return false;
	if (node.getType() == WindowTreeNode.AREA)
	{
	    AreaWindowTreeNode areaNode = (AreaWindowTreeNode)node;
	    return areaNode.area == area;//FIXME:Hidden area processing;
	}
	if (node.getType() == WindowTreeNode.COMPOSITE)
	{
	    CompositeWindowTreeNode compositeNode = (CompositeWindowTreeNode)node;
	    if (hasNonPopupAreaImpl(compositeNode.node1, area))
	    return true;
	    return 	    hasNonPopupAreaImpl(compositeNode.node2, area);
	}
	if (node.getType() == WindowTreeNode.POPUP)
	{
	    PopupWindowTreeNode popupNode = (PopupWindowTreeNode)node;
	    return hasNonPopupAreaImpl(popupNode.node, area);
	}
	return false;
    }

    private void onSetActiveArea()
    {
	if (active == null || active.area == null)
	    return;
	Speech.say(active.area.getName());
    }

    //Drawing methods;
}
