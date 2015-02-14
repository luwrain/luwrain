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

class Separator implements Item 
{
    private Luwrain luwrain;
    private boolean isDefault = false;
    private String text = "";

    public Separator(Luwrain luwrain,
		     boolean isDefault,
		     String text)
    {
	this.luwrain = luwrain;
	this.isDefault = isDefault;
	this.text = text;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (text == null)
	    throw new NullPointerException("text may not be null");
    }

    public boolean isDefaultSeparator()
    {
	return isDefault;
    }

    public String getSeparatorText()
    {
	return text;
    }

    @Override public String getText()
    {
	return "";
    }

    @Override public void introduce()
    {
	luwrain.silence();
	luwrain.playSound(Sounds.MAIN_MENU_EMPTY_LINE);
    }

    @Override public boolean isAction()
    {
	return false;
    }

    @Override public void doAction()
    {
    }
}
