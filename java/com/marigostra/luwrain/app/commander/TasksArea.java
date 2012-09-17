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

package com.marigostra.luwrain.app.commander;

import com.marigostra.luwrain.core.*;
import com.marigostra.luwrain.core.events.*;

public class TasksArea implements Area
{
    private CommanderStringConstructor stringConstructor;
    private CommanderActions actions;
    private int hotPointX = 0;
    private int hotPointY = 0;

    public TasksArea(CommanderActions actions, CommanderStringConstructor stringConstructor)
    {
	this.stringConstructor = stringConstructor;
	this.actions = actions;
    }

    public int getLineCount()
    {
	return 1;
    }

    public String getLine(int index)
    {
	return new String();
    }

    public int getHotPointX()
    {
	if (hotPointX < 0)//Actually never happens;
	    return 0;
	return hotPointX;
    }

    public int getHotPointY()
    {
	if (hotPointY < 0)//Actually never happens;
	    return 0;
	return hotPointY;
    }

    public void setHotPoint(int x, int y)
    {
	//FIXME:
    }

    public void onKeyboardEvent(KeyboardEvent event)
    {
	if (event.isCommand() && event.getCommand() == KeyboardEvent.TAB && !event.isModified())
	{
	    actions.gotoLeftPanel();
	    return;
	}
	//FIXME:
    }

    public void onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.CLOSE)
	{
	    actions.closeCommander();
	    return;
	}
    }

    public String getName()
    {
	return "FIXME";
    }
}
