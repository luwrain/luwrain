/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

public final class App implements Application, MonoApp
{
    private Luwrain luwrain = null;
    private Base base = null;
    private ConsoleArea area = null;

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	this.luwrain = luwrain;
	this.base = new Base(luwrain);
	createArea();
	return new InitResult();
    }

    private void createArea()
    {
	final ConsoleArea.ClickHandler clickHandler = (area,index,obj)->{
	    return false;
	};
	final ConsoleArea.InputHandler inputHandler = (area,text)->{
	    return base.onInput(text, ()->area.refresh());
	};
	this.area = new ConsoleArea(base.createConsoleParams(clickHandler, inputHandler)){
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    switch(event.getCode())
		    {
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onSystemEvent(event);
		    }
		}
	    };
    }

    @Override public AreaLayout getAreaLayout()
    {
	return new AreaLayout(area);
    }

    @Override public String getAppName()
    {
	return "LUWRAIN";
    }

    @Override public MonoApp.Result onMonoAppSecondInstance(Application app)
    {
	NullCheck.notNull(app, "app");
	return MonoApp.Result.BRING_FOREGROUND;
    }

    @Override public void closeApp()
    {
	base.removeListener();
	luwrain.closeApp();
    }

    static public void installListener()
    {
	Utils.installListener();
    }
}
