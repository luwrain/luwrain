/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

class Separator implements Item 
{
    private boolean isDefault = false;
    private String text = "";

    public Separator(boolean isDefault, String text)
    {
	this.isDefault = isDefault;
	this.text = text;
	NullCheck.notNull(text, "text");
    }

    public boolean isDefaultSeparator()
    {
	return isDefault;
    }

    public String getSeparatorText()
    {
	return text;
    }

    @Override public String getMMItemText()
    {
	return "";
    }

    @Override public void introduceMMItem(Luwrain env)
    {
	env.silence();
	env.playSound(Sounds.MAIN_MENU_EMPTY_LINE);
    }

    @Override public boolean isMMAction()
    {
	return false;
    }

    @Override public void doMMAction(Luwrain env)
    {
    }

    @Override public boolean isMMItemEnabled()
    {
	return true;
    }
}
