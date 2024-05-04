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

package org.luwrain.app.console;

import java.util.*;
import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;

public final class App extends AppBase<Strings> implements MonoApp
{
    static final ArrayList<Entry> events = new ArrayList<>();
    private ConsoleCommand[] commands = new ConsoleCommand[0];
    private MainLayout mainLayout = null;

    public App()
    {
	super(Strings.NAME, Strings.class, "luwrain.console");
    }

    @Override protected AreaLayout onAppInit()
    {
	this.mainLayout = new MainLayout(this);
	this.commands = new ConsoleCommand[]{
	    new Commands.Prop(getLuwrain()),
	};
	setAppName(getStrings().appName());
	return mainLayout.getAreaLayout();
    }

    ConsoleCommand[] getCommands()
    {
	return this.commands.clone();
    }

    @Override public boolean onEscape()
    {
	closeApp();
	return true;
    }

    @Override public MonoApp.Result onMonoAppSecondInstance(Application app)
    {
	NullCheck.notNull(app, "app");
	return MonoApp.Result.BRING_FOREGROUND;
    }
}
