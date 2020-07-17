/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.template;

import java.util.*;
import java.util.concurrent.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

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

    protected final class Actions
    {
	private final ActionInfo[] actions;
	public Actions(ActionInfo[] actions)
	{
	    NullCheck.notNullItems(actions, "actions");
	    this.actions = actions.clone();
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
}
