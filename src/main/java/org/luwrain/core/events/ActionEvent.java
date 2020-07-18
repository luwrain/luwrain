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

//LWR_API 1.0

package org.luwrain.core.events;

import org.luwrain.core.*;

public class ActionEvent extends SystemEvent
{
    private Action action;

    public ActionEvent(Action action)
    {
	super(Code.ACTION);
	this.action = action;
	NullCheck.notNull(action, "action");
    }

    public Action getAction()
    {
	return action;
    }

    public String getActionName()
    {
	return action.name();
    }

    static public boolean isAction(Event event, String actionName)
    {
	if (event == null || !(event instanceof ActionEvent))
	    return false;
	NullCheck.notNull(actionName, "actionName");
	final ActionEvent actionEvent = (ActionEvent)event;
	return actionEvent.getActionName().equals(actionName);
    }
}
