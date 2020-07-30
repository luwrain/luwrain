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

package org.luwrain.app.console;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.template.*;

public final class App extends AppBase<Strings> implements MonoApp
{
                static final List messages = new LinkedList();
        private ConsoleCommand[] commands = new ConsoleCommand[0];
    private MainLayout mainLayout = null;

    public App()
    {
	super(Strings.NAME, Strings.class);
    }

    @Override public boolean onAppInit()
    {
	this.mainLayout = new MainLayout(this);
		this.commands = new ConsoleCommand[]{
		    new Commands.Prop(getLuwrain()),
		};
	setAppName(getStrings().appName());
	return true;
    }

    ConsoleCommand[] getCommands()
    {
	return this.commands.clone();
    }

    @Override public AreaLayout getDefaultAreaLayout()
    {
	return this.mainLayout.getLayout();
    }

    @Override public MonoApp.Result onMonoAppSecondInstance(Application app)
    {
	NullCheck.notNull(app, "app");
	return MonoApp.Result.BRING_FOREGROUND;
    }

    static public void installListener()
    {
	Utils.installListener();
    }
}
