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

package org.luwrain.app.fetch;

//TODO:Proper empty line below;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.pim.StoredMailAccount;
import org.luwrain.pim.PimManager;
import org.luwrain.pim.MailStoring;
import javax.mail.*;
import java.util.*;

class FetchArea extends SimpleArea
{
    private Actions actions;
    private StringConstructor stringConstructor;

    public FetchArea(Actions actions, StringConstructor stringConstructor)
    {
	super(stringConstructor.appName());
	this.actions = actions;
	this.stringConstructor = stringConstructor;
	addLine(stringConstructor.pressEnterToStart());
	addLine("");
	addLine("");
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event.isCommand() && !event.isModified() &&
	    event.getCommand() == KeyboardEvent.ENTER)
	{
	    actions.launchFetching();
	    return true;
	}
	return super.onKeyboardEvent(event);
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	switch(event.getCode())
	{
	case EnvironmentEvent.THREAD_SYNC:
	    MessageLineEvent messageLineEvent = (MessageLineEvent)event;
	    if (getLineCount() > 1)
		setLine(getLineCount() - 1, messageLineEvent.message); else
		addLine(messageLineEvent.message);
	    addLine("");
	    if (messageLineEvent.message.equals(stringConstructor.fetchingCompleted()))
		Luwrain.message(messageLineEvent.message);
	    return true;
	case EnvironmentEvent.CLOSE:
	    actions.close();
	    return true;
	default:
	    return false;
	}
    }
}
