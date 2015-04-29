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

//FIXME:Automatic updating;

package org.luwrain.mainmenu;

import org.luwrain.core.*;
import org.luwrain.mainmenu.Strings;

class DateTimeItem implements Item
{
    private Strings strings;

    private String value;

    public DateTimeItem(Strings strings)
    {
	this.strings = strings;
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
	value = strings.currentDateTime();
    }

    @Override public String getText()
    {
	return value;
    }

    @Override public void introduce(CommandEnvironment env)
    {
	env.playSound(Sounds.GENERAL_TIME);
	env.say(value);
    }

    @Override public boolean isAction()
    {
	return false;
    }

    @Override public void doAction(CommandEnvironment env)
    {
    }
}
