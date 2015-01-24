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

//FIXME:Automatic updating;

package org.luwrain.mainmenu;

import org.luwrain.core.*;
import org.luwrain.mainmenu.StringConstructor;

class DateTimeItem implements Item
{
    private StringConstructor stringConstructor;
    //    private Luwrain luwrain;
    private String value;

    public DateTimeItem(StringConstructor stringConstructor)
    {
	this.stringConstructor = stringConstructor;
	value = stringConstructor.currentDateTime();
    }

    @Override public String getText()
    {
	return value;
    }

    @Override public void introduce()
    {
	Speech.say(value);
    }

    @Override public boolean isAction()
    {
	return false;
    }

    @Override public void doAction()
    {
    }
}
