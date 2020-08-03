/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core.shell.desktop;

import org.luwrain.core.*;
import org.luwrain.core.shell.*;

public final class Desktop implements org.luwrain.core.Desktop
{
    private Luwrain luwrain = null;
    private String name = "";
    private DesktopArea desktopArea = null;
    private Conversations conversations = null;

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	this.desktopArea = new DesktopArea(luwrain, new Conversations(luwrain));
	return new InitResult();
    }

    @Override public void ready()
    {
	final Settings.UserInterface sett = Settings.createUserInterface(luwrain.getRegistry());
	this.name = sett.getDesktopTitle("").trim();
	if (this.name.isEmpty())
	    this.name = luwrain.i18n().getStaticStr("Desktop");
	this.desktopArea.setAreaName(this.name);
    }

    @Override public void setConversations(Conversations conversations)
    {
	this.conversations = conversations;
    }

    @Override public String getAppName()
    {
	return name;
    }

    @Override public AreaLayout getAreaLayout()
    {
	return new AreaLayout(desktopArea);
    }

    @Override public void closeApp()
    {
	//Never called
    }
}
