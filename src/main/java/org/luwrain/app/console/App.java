/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
	final ConsoleArea.Params params = new ConsoleArea.Params();

	params.context = new DefaultControlContext(luwrain);
params.areaName = "LUWRAIN";
params.model = base.createModel();
params.appearance = new ConsoleArea.Appearance()
    {
	@Override public void announceItem(Object item)
	{
	    NullCheck.notNull(item, "item");
	    if (!(item instanceof Log.Message))
	    {
		luwrain.setEventResponse(DefaultEventResponse.text(luwrain.getSpokenText(item.toString(), Luwrain.SpokenTextType.PROGRAMMING)));
		return;
	    }
	    final Log.Message message = (Log.Message)item;
	    luwrain.setEventResponse(DefaultEventResponse.text(luwrain.getSpokenText(message.message, Luwrain.SpokenTextType.PROGRAMMING)));
	}
	@Override public String getTextAppearance(Object item)
	{
	    NullCheck.notNull(item, "item");
	    return item.toString();
	}
    };
params.clickHandler = (area,index,obj)->{
	    return false;
	};
params.inputHandler = (area,text)->{
    return base.onInput(text, ()->area.refresh());
	};
params.inputPos = ConsoleArea.InputPos.BOTTOM;
params.inputPrefix = "LUWRAIN>";

	area = new ConsoleArea(params){
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
	Base.installListener();
    }
}
