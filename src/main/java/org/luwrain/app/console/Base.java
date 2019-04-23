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
import org.luwrain.controls.*;
import org.luwrain.controls.ConsoleArea.InputHandler;

final class Base
{
        static private final List messages = new LinkedList();

    private final Luwrain luwrain;
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

    Model createModel()
    {
	return new Model();
    }

    static void installListener()
    {
	Log.addListener((message)->{
	    NullCheck.notNull(message, "message");
	    messages.add(message);
	    //	    onNewMessage(message);
	    });
    }

    void removeListener()
    {
	//	Log.removeListener(listener);
    }

    static String firstWord(String text)
    {
	NullCheck.notNull(text, "text");
	final int pos = text.indexOf(" ");
	if (pos < 0)
	    return text.trim();
	return text.substring(0, pos).trim();
    }

interface ConsoleCommand
{
    boolean onCommand(String text, List messages);
}

    static private class Model implements ConsoleArea.Model
    {
	@Override public int getConsoleItemCount()
	{
	    return messages.size();
	}
	@Override public Object getConsoleItem(int index)
	{
	    return messages.get(index);
	}
    }
}
