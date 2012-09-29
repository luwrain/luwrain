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

package org.luwrain.app.news;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class GroupArea extends SimpleArea
{
    private NewsReaderStringConstructor stringConstructor;
    private NewsReaderActions actions;

    public GroupArea(NewsReaderActions actions, NewsReaderStringConstructor stringConstructor)
    {
	super(stringConstructor.groupAreaName());
	this.actions = actions;
	this.stringConstructor = stringConstructor;
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (super.onKeyboardEvent(event))
	    return true;

	//Tab;
	if (event.isCommand() && event.getCommand() == KeyboardEvent.TAB && !event.isModified())
	{
	    actions.gotoArticles();
	    return true;
	}

	//Enter;
	if (event.isCommand() && event.getCommand() == KeyboardEvent.ENTER && !event.isModified())
	{
	    actions.openGroup(getHotPointY());
	    return true;
	}
	return false;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.CLOSE)
	{
	    actions.closeNewsReader();
	    return true;
	}
	return false;
    }
}
