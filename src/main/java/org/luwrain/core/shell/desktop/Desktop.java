/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

import  com.google.auto.service.*;

import org.luwrain.core.*;
import org.luwrain.core.shell.*;

@AutoService(org.luwrain.core.Desktop.class)
public final class Desktop implements org.luwrain.core.Desktop
{
    private Luwrain luwrain = null;
    private String name = "";
    private DesktopArea desktopArea = null;
    private Strings strings = null;
    private Conversations conv = null;

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, Strings.NAME);
	this.strings = (Strings)o;
	this.conv = new Conversations(luwrain, strings);
	final Settings.UserInterface sett = Settings.createUserInterface(luwrain.getRegistry());
	this.name = sett.getDesktopTitle("").trim();
	if (this.name.isEmpty())
	    this.name = luwrain.i18n().getStaticStr("Desktop");
	this.desktopArea = new DesktopArea(luwrain, name, conv);
	return new InitResult();
    }

    @Override public String getAppName()
    {
	return this.name;
    }

    @Override public AreaLayout getAreaLayout()
    {
	return new AreaLayout(desktopArea);
    }

    @Override public void onAppClose()
    {
    }
}
