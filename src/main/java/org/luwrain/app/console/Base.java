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
import org.luwrain.controls.*;
import org.luwrain.controls.ConsoleArea.InputHandler;

final class Base extends Utils
{
        static final List messages = new LinkedList();

final Luwrain luwrain;
    private final ConsoleCommand[] commands;

    Base(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	this.commands = new ConsoleCommand[]{
	    new Commands.Prop(luwrain),
	};
    }

    InputHandler.Result onInput(String text, Runnable updating)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(updating, "updating");
	if (text.trim().isEmpty())
	    return InputHandler.Result.REJECTED;
	for(ConsoleCommand c: commands)
	    if (c.onCommand(text, messages))
	    {
		updating.run();
		luwrain.playSound(Sounds.OK);
		return InputHandler.Result.CLEAR_INPUT;
	    }
	messages.add("Unknown command: " + firstWord(text));
	updating.run();
	luwrain.playSound(Sounds.ERROR);
	return InputHandler.Result.CLEAR_INPUT;
    }

    void removeListener()
    {
	//	Log.removeListener(listener);
    }

    ConsoleArea.Params createConsoleParams(ConsoleArea.ClickHandler clickHandler, ConsoleArea.InputHandler inputHandler)
    {
	NullCheck.notNull(clickHandler, "clickHandler");
	NullCheck.notNull(inputHandler, "inputHandler");
	final ConsoleArea.Params params = new ConsoleArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.name = "LUWRAIN";
	params.model = new Model();
	params.appearance = new Appearance();
	params.clickHandler = clickHandler;
	params.inputHandler = inputHandler;
	params.inputPos = ConsoleArea.InputPos.BOTTOM;
	params.inputPrefix = "LUWRAIN>";
	return params;
    }

    static private class Model implements ConsoleArea.Model
    {
	@Override public int getItemCount()
	{
	    return messages.size();
	}
	@Override public Object getItem(int index)
	{
	    return messages.get(index);
	}
    }

    private final class Appearance implements ConsoleArea.Appearance
    {
	@Override public void announceItem(Object item)
	{
	    NullCheck.notNull(item, "item");
	    if (!(item instanceof Log.Message))
	    {
		luwrain.setEventResponse(DefaultEventResponse.text(Sounds.LIST_ITEM, luwrain.getSpeakableText(item.toString(), Luwrain.SpeakableTextType.PROGRAMMING)));
		return;
	    }
	    final Log.Message message = (Log.Message)item;
	    luwrain.setEventResponse(DefaultEventResponse.text(Sounds.LIST_ITEM, luwrain.getSpeakableText(message.message, Luwrain.SpeakableTextType.PROGRAMMING)));
	}
	@Override public String getTextAppearance(Object item)
	{
	    NullCheck.notNull(item, "item");
	    return item.toString();
	}
    };
}
