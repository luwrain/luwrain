/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.app.base;

import java.util.*;
import java.util.concurrent.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class LayoutBase
{
protected interface ActionHandler
{
    boolean onAction();
}

    protected final class ActionInfo
    {
	final String name;
	final String title;
	final InputEvent inputEvent;
	final ActionHandler handler;
	public ActionInfo(String name, String title, InputEvent inputEvent, ActionHandler handler)
	{
	    NullCheck.notEmpty(name, "name");
	    NullCheck.notEmpty(title, "title");
	    NullCheck.notNull(handler, "handler");
	    this.name = name;
	    this.title = title;
	    this.inputEvent = inputEvent;
	    this.handler = handler;
	}
	public ActionInfo(String name, String title, ActionHandler handler)
	{
	    this(name, title, null, handler);
	}
    }

    static public final class Actions
    {
	private final ActionInfo[] actions;
	public Actions(ActionInfo[] actions)
	{
	    NullCheck.notNullItems(actions, "actions");
	    this.actions = actions.clone();
	}
	public Actions()
	{
	    this(new ActionInfo[0]);
	}
	public org.luwrain.core.Action[] getAreaActions()
	{
	    final List<org.luwrain.core.Action> res = new LinkedList();
	    for(ActionInfo a: actions)
		if (a.inputEvent != null)
		    res.add(new org.luwrain.core.Action(a.name, a.title, a.inputEvent)); else
		    		    res.add(new org.luwrain.core.Action(a.name, a.title));
	    return res.toArray(new org.luwrain.core.Action[res.size()]);
	}
	public boolean handle(String actionName)
	{
	    NullCheck.notEmpty(actionName, "actionName");
	    for(ActionInfo a: actions)
	    if (a.name.equals(actionName))
		return a.handler.onAction();
	    return false;
	}
	boolean onActionEvent(SystemEvent event)
	{
	    NullCheck.notNull(event, "event");
	    	    for(ActionInfo a: actions)
			if (ActionEvent.isAction(event, a.name))
			    return a.handler.onAction();
		    return false;
	}
    }

    protected final AppBase app;
    protected LayoutControlContext controlContext = null;
    private final Map<Area, Area> areaWrappers = new HashMap();
    private AreaLayout areaLayout = null;

    protected LayoutBase(AppBase app)
    {
	this.app = app;
    }

    protected LayoutBase()
    {
	this(null);
    }

    protected Actions actions(ActionInfo ... a)
    {
	return new Actions(a);
	    }

    protected ActionInfo action(String name, String title, InputEvent inputEvent, ActionHandler handler)
    {
	NullCheck.notEmpty(name, "name");
	NullCheck.notEmpty(title, "title");
	NullCheck.notNull(handler, "handler");
	return new ActionInfo(name, title, inputEvent, handler);
    }

    protected ActionInfo action(String name, String title, ActionHandler handler)
    {
	NullCheck.notEmpty(name, "name");
	NullCheck.notEmpty(title, "title");
	NullCheck.notNull(handler, "handler");
	return new ActionInfo(name, title, handler);
    }

    protected Area getWrappingArea(Area area, Actions actions)
    {
	NullCheck.notNull(area, "area");
		if (app == null)
	    throw new IllegalStateException("No app instance, provide it with the corresponding constructor");
	final Area res = new Area(){
	    @Override public int getLineCount()
	    {
		return area.getLineCount();
	    }
	    @Override public String getLine(int index)
	    {
		return area.getLine(index);
	    }
	    @Override public int getHotPointX()
	    {
		return area.getHotPointX();
	    }
	    @Override public int getHotPointY()
	    {
		return area.getHotPointY();
	    }
	    @Override public String getAreaName()
	    {
		return area.getAreaName();
	    }
	    @Override public boolean onInputEvent(InputEvent event)
	    {
		if (app.onInputEvent(this, event))
		    return true;
		return area.onInputEvent(event);
	    }
	    @Override public boolean onSystemEvent(SystemEvent event)
	    {
		if (actions != null)
		{
		    if (app.onSystemEvent(this, event, actions))
		    return true;
		} else
		{
		if (app.onSystemEvent(this, event))
		    return true;
		}
		return area.onSystemEvent(event);
	    }
	    @Override public boolean onAreaQuery(AreaQuery query)
	    {
		if (app.onAreaQuery(this, query))
		    return true;
		return area.onAreaQuery(query);
	    }
	    @Override public Action[] getAreaActions()
	    {
		return actions != null?actions.getAreaActions():area.getAreaActions();
	    }
	};
	areaWrappers.put(area, res);
	return res;
    }

    protected void clearAreaWrappers()
    {
	areaWrappers.clear();
    }

    protected void setAreaLayout(Area area, Actions actions)
    {
	NullCheck.notNull(area, "area");
	this.areaLayout = new AreaLayout(getWrappingArea(area, actions));
    }

    protected void setAreaLayout(int type, Area area1, Actions actions1, Area area2, Actions actions2)
    {
	NullCheck.notNull(area1, "area1");
	NullCheck.notNull(area2, "area2");
	this.areaLayout = new AreaLayout(type, getWrappingArea(area1, actions1), getWrappingArea(area2, actions2));
    }

    protected void setAreaLayout(int type, Area area1, Actions actions1, Area area2, Actions actions2, Area area3, Actions actions3)
    {
	NullCheck.notNull(area1, "area1");
	NullCheck.notNull(area2, "area2");
		NullCheck.notNull(area3, "area3");
		this.areaLayout = new AreaLayout(type, getWrappingArea(area1, actions1), getWrappingArea(area2, actions2), getWrappingArea(area3, actions3));
    }

    public AreaLayout getAreaLayout()
    {
	if (this.areaLayout == null)
	    throw new IllegalStateException("No area layout, use setAreaLayout() to set it");
	return this.areaLayout;
    }

    protected ControlContext getControlContext()
    {
	if (app == null)
	    throw new IllegalStateException("No app instance, provide it with the corresponding constructor");
	if (this.controlContext == null)
	    this.controlContext = new LayoutControlContext(new DefaultControlContext(app.getLuwrain()));
	return this.controlContext;
    }

    protected final class LayoutControlContext extends WrappingControlContext
    {
	public LayoutControlContext(ControlContext context)
	{
	    super(context);
	}
	    @Override public void onAreaNewContent(Area area)
	{
	    super.onAreaNewContent(getArea(area));
	}
    @Override public void onAreaNewName(Area area)
	{
	    super.onAreaNewName(getArea(area));
	}
    @Override public void onAreaNewHotPoint(Area area)
	{
super.onAreaNewHotPoint(getArea(area));
	}
@Override public int getAreaVisibleHeight(Area area)
	{
	    return super.getAreaVisibleHeight(getArea(area));
	}
    @Override public int getAreaVisibleWidth(Area area)
	{
	    return getAreaVisibleWidth(getArea(area));
	}
	    @Override public void onAreaNewBackgroundSound(Area area)
	{
	    super.onAreaNewBackgroundSound(getArea(area));
	}
	private Area getArea(Area area)
	{
	    NullCheck.notNull(area, "area");
	    final Area res = areaWrappers.get(area);
	    return res != null?res:area;
	}

	

    }
}
