/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.app.mail;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class GroupArea extends TreeArea
{
    private MailReaderStringConstructor stringConstructor;
    private MailReaderActions actions;
    private MailGroupTreeModel treeModel;

    public GroupArea(MailReaderActions actions,
		     MailReaderStringConstructor stringConstructor,
		     MailGroupTreeModel treeModel)
    {
	super(treeModel, stringConstructor.groupAreaName());
	this.actions = actions;
	this.stringConstructor = stringConstructor;
	this.treeModel = treeModel;
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (super.onKeyboardEvent(event))
	    return true;

	//Tab;
	if (event.isCommand() && event.getCommand() == KeyboardEvent.TAB && !event.isModified())
	{
	    actions.gotoSummary();
	    return true;
	}

	return false;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.CLOSE)
	{
	    actions.closeMailReader();
	    return true;
	}
	return false;
    }

    public void onClick(Object obj)
    {
	MailGroup group = (MailGroup)obj;
	actions.openGroup(group);
    }
}
