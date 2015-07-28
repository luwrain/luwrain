/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
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

    @Override public void introduce(CommandEnvironment env)
    {
	env.playSound(Sounds.NEW_LIST_ITEM);
	env.say(title);
    }

    @Override public boolean isAction()
    {
	return true;
    }

    @Override public void doAction(CommandEnvironment env)
    {
	if (!env.runCommand(command))
	    env.message(strings.noCommand(), Luwrain.MESSAGE_ERROR);
    }
}
