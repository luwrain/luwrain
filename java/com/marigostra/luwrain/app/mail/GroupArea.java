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

package com.marigostra.luwrain.app.mail;

import com.marigostra.luwrain.core.*;
import com.marigostra.luwrain.core.events.*;

public class GroupArea extends SimpleArea
{
    private MailReaderStringConstructor stringConstructor;
    private MailReaderActions actions;

    public GroupArea(MailReaderActions actions, MailReaderStringConstructor stringConstructor)
    {
	super(stringConstructor.groupAreaName());
	this.actions = actions;
	this.stringConstructor = stringConstructor;
    }

    public void onUserKeyboardEvent(KeyboardEvent event)
    {
	if (event.isCommand() && event.getCommand() == KeyboardEvent.TAB && !event.isModified())
	{
	    actions.gotoSummary();
	    return;
	}
	if (event.isCommand() && event.getCommand() == KeyboardEvent.ENTER && !event.isModified())
	{
	    //	    actions.openGroup(getHotPointY());
	    return;
	}
    }

    public void onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.CLOSE)
	{
	    actions.closeMailReader();
	    return;
	}
    }
}
