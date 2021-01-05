/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

//LWR_API 1.0

package org.luwrain.core;

public class SimpleShortcutCommand implements Command
{
    protected final String cmdName;
    protected final String shortcutName;

    public SimpleShortcutCommand(String cmdName, String shortcutName)
    {
	NullCheck.notEmpty(cmdName, "cmdName");
	NullCheck.notNull(shortcutName, "shortcutName");
	this.cmdName = cmdName;
	this.shortcutName = shortcutName;
    }

        public SimpleShortcutCommand(String name)
    {
	this(name, name);
    }


    @Override public String getName()
    {
	return cmdName;
    }

    @Override public void onCommand(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	luwrain.launchApp(shortcutName);
    }
}
