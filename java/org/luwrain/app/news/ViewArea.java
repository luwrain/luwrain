/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

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

public class ViewArea extends SimpleArea
{
    private NewsReaderStringConstructor stringConstructor;
    private NewsReaderActions actions;

    public ViewArea(NewsReaderActions actions, NewsReaderStringConstructor stringConstructor)
    {
	super(stringConstructor.viewAreaName());
	this.actions =  actions;
	this.stringConstructor = stringConstructor;
	setContent(prepareText());
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (super.onKeyboardEvent(event))
	    return true;

	//Tab;
	if (event.isCommand() && event.getCommand() == KeyboardEvent.TAB && !event.isModified())
	{
	    actions.gotoGroups();
	    return true;
	}
	return false;
    }

    private String[] prepareText()
    {
	String res[] = new String[3];
	for(int i = 0;i < 3;i++)
	{
	    res[i] = new String("Text ");
	    res[i] += (i + 1);
	}
	return res;
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
