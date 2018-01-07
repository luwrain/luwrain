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

class Base
{
    private final Luwrain luwrain;
    static private final List<Log.Message> messages = new LinkedList();

    Base(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
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

    static private class Model implements ConsoleArea2.Model
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
