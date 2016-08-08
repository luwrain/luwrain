/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.app.console;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

public class ConsoleApp implements Application, MonoApp
{
    private Luwrain luwrain;
    private SimpleArea area;
    private Log.Listener listener;

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	this.luwrain = luwrain;
	createArea();
	addListener();
	return true;
    }

    private void createArea()
    {
	area = new SimpleArea(new DefaultControlEnvironment(luwrain), "LUWRAIN"){
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    switch(event.getCode())
		    {
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
	    };
    }

    private void addListener()
    {
	listener = (message)->{
	    NullCheck.notNull(message, "message");
	    area.addLine(message.component() + ":" + message.level() + ":" + message.message());
	};
	Log.addListener(listener);
    }

    @Override public AreaLayout getAreasToShow()
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

private void closeApp()
    {
	luwrain.closeApp();
    }
}
