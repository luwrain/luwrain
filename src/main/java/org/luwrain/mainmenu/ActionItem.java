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

class ActionItem implements Item
{
    private String action;
    private String title;

    public ActionItem(String action, String title)
    {
	this.action = action;
	this.title = title;
    }

    @Override public String getText()
    {
	return title;
    }

    @Override public void introduce()
    {
	Speech.say(title);
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
