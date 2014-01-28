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
import org.luwrain.pim.StoredMailAccount;
import org.luwrain.pim.PimManager;
import org.luwrain.pim.MailStoring;
import javax.mail.*;
import java.util.*;

public class FetchArea extends SimpleArea
{
    private FetchActions actions;
    private FetchStringConstructor stringConstructor;

    public FetchArea(FetchActions actions, FetchStringConstructor stringConstructor)
    {
	super("Fetch area");//FIXME:
	this.actions = actions;
	this.stringConstructor = stringConstructor;
	addLine(stringConstructor.pressEnterToStart());
	addLine("");
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (super.onKeyboardEvent(event ))
	    return true;

	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.ENTER &&
	    !event.isModified())
	{
	    actions.launchFetching();
	    return true;
	}

	return false;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.THREAD_SYNC)
	{
	    MessageLineEvent messageLineEvent = (MessageLineEvent)event;
	    addLine(messageLineEvent.message);
	    return true;
	}
	switch (event.getCode())
	{
	case EnvironmentEvent.CLOSE:
	    actions.closeFetchApp();
	    return true;
	case EnvironmentEvent.INTRODUCE:
	    Speech.say(stringConstructor.appName());
	    return true;
	default:
	    return false;
	}
    }

    public String getName()
    {
	return stringConstructor.appName();
    }
}
