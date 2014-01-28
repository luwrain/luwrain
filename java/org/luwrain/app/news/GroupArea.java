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

package org.luwrain.app.news;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class GroupArea extends ListArea
{
    private NewsReaderStringConstructor stringConstructor;
    private NewsReaderActions actions;

    public GroupArea(NewsReaderActions actions,
		     NewsReaderStringConstructor stringConstructor,
		     GroupModel model)
    {
	super(model);
	this.actions = actions;
	this.stringConstructor = stringConstructor;
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (super.onKeyboardEvent(event))
	    return true;

	//Tab;
	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.TAB &&
	    !event.isModified())
	{
	    actions.gotoArticles();
	    return true;
	}

	//Enter;
	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.ENTER &&
	    !event.isModified())
	{
	    Log.debug("news", "" + getSelectedIndex());
	    if (getSelectedIndex() >= 0)
		actions.openGroup(getSelectedIndex());
	    return true;
	}

	return false;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	switch(event.getCode())
	{
	case EnvironmentEvent.CLOSE:
	    actions.closeNewsReader();
	    return true;
	case EnvironmentEvent.INTRODUCE:
	    Speech.say(stringConstructor.appName() + " " + stringConstructor.groupAreaName());
	    return true;
	default:
	    return false;
	}
    }

    public String getName()
    {
	return stringConstructor.groupAreaName();
    }
}
