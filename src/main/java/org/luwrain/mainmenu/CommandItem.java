/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.mainmenu;

import org.luwrain.core.*;

class CommandItem implements Item
{
    private Luwrain luwrain;
    private String command;
    private String title;

    public CommandItem(Luwrain luwrain,
		       String command, 
		      String title)
    {
	this.luwrain = luwrain;
	this.command = command;
	this.title = title;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (command == null)
	    throw new NullPointerException("command may not be null");
	if (command.isEmpty())
	    throw new IllegalArgumentException("command may not be empty");
	if (title == null)
	    throw new NullPointerException("title may not be null");
    }

    @Override public String getText()
    {
	return title;
    }

    @Override public void introduce()
    {
	luwrain.say(title);
    }

    @Override public boolean isAction()
    {
	return true;
    }

    @Override public void doAction()
    {
	//FIXME:
    }
}
