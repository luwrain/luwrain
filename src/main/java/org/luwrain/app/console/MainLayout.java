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

final class MainLayout extends LayoutBase implements ConsoleArea.InputHandler
{
    private final App app;
    private final ConsoleArea consoleArea;

    MainLayout(App app)
    {
	NullCheck.notNull(app, "app");
	this.app = app;
	this.consoleArea = new ConsoleArea(createConsoleParams()){
		@Override public boolean onInputEvent(InputEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onInputEvent(this, event))
			return true;
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onSystemEvent(this, event))
			return true;
		    return super.onSystemEvent(event);
		}
		@Override public boolean onAreaQuery(AreaQuery query)
		{
		    NullCheck.notNull(query, "query");
		    if (app.onAreaQuery(this, query))
			return true;
		    return super.onAreaQuery(query);
		}
	    };
    }

    @Override public Result onConsoleInput(ConsoleArea area, String text)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(text, "text");
	if (text.trim().isEmpty())
	    return Result.REJECTED;
	for(ConsoleCommand c: app.getCommands())
	    if (c.onCommand(text, app.messages))
	    {
		consoleArea.refresh();
		app.getLuwrain().playSound(Sounds.OK);
		return Result.CLEAR_INPUT;
	    }
	app.messages.add("Unknown command: " + Utils.firstWord(text));
	consoleArea.refresh();
	app.getLuwrain().playSound(Sounds.ERROR);
	return Result.CLEAR_INPUT;
    }

    private ConsoleArea.Params createConsoleParams()
    {
	final ConsoleArea.Params params = new ConsoleArea.Params();
	params.context = new DefaultControlContext(app.getLuwrain());
	params.name = "LUWRAIN";
	params.model = new Model();
	params.appearance = new Appearance();
	//	params.clickHandler = clickHandler;
	params.inputHandler = this;
	params.inputPos = ConsoleArea.InputPos.BOTTOM;
	params.inputPrefix = "LUWRAIN>";
	return params;
    }

    AreaLayout getLayout()
    {
	return new AreaLayout(consoleArea);
    }

    private final class Model implements ConsoleArea.Model
    {
	@Override public int getItemCount()
	{
	    return app.messages.size();
	}
	@Override public Object getItem(int index)
	{
	    return app.messages.get(index);
	}
    }

    private final class Appearance implements ConsoleArea.Appearance
    {
	@Override public void announceItem(Object item)
	{
	    NullCheck.notNull(item, "item");
	    if (!(item instanceof Log.Message))
	    {
		app.getLuwrain().setEventResponse(DefaultEventResponse.text(Sounds.LIST_ITEM, app.getLuwrain().getSpeakableText(item.toString(), Luwrain.SpeakableTextType.PROGRAMMING)));
		return;
	    }
	    final Log.Message message = (Log.Message)item;
	    app.getLuwrain().setEventResponse(DefaultEventResponse.text(Sounds.LIST_ITEM, app.getLuwrain().getSpeakableText(message.message, Luwrain.SpeakableTextType.PROGRAMMING)));
	}
	@Override public String getTextAppearance(Object item)
	{
	    NullCheck.notNull(item, "item");

	    	    if (!(item instanceof Log.Message))
			return item.toString();
		    	    final Log.Message message = (Log.Message)item;
			    return message.message;
			    	}
    };
}
