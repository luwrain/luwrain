/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.mainmenu;

import org.luwrain.core.*;
import org.luwrain.util.*;

class CommandItem implements Item
{
    private Strings strings;
    private String command;
    private String title;

    public CommandItem(Strings strings,
		       String command, 
		      String title)
    {
	this.strings = strings;
	this.command = command;
	this.title = title;
	NullCheck.notNull(strings, "strings");
	NullCheck.notNull(command, "command");
	if (command.isEmpty())
	    throw new IllegalArgumentException("command may not be empty");
	NullCheck.notNull(title, "title");
    }

    @Override public String getMMItemText()
    {
	return title;
    }

    @Override public void introduceMMItem(Luwrain env)
    {
	env.playSound(Sounds.NEW_LIST_ITEM);
	env.say(title);
    }

    @Override public boolean isMMAction()
    {
	return true;
    }

    @Override public void doMMAction(Luwrain env)
    {
	if (!env.runCommand(command))
	    env.message(strings.noCommand(), Luwrain.MESSAGE_ERROR);
    }

    @Override public boolean isMMItemEnabled()
    {
	return true;
    }
}
