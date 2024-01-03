/*
   Copyright 2012-2023 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core.events;

import org.luwrain.core.*;
import static org.luwrain.core.NullCheck.*;

public class ActionEvent extends SystemEvent
{
    final Action action;

    public ActionEvent(Action action)
    {
	super(Code.ACTION);
	notNull(action, "action");
	this.action = action;
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
	notNull(actionName, "actionName");
	final ActionEvent actionEvent = (ActionEvent)event;
	return actionEvent.getActionName().equals(actionName);
    }
}
