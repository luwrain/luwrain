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

package org.luwrain.core;

import java.util.*;

import static org.luwrain.core.Base.*;
import static org.luwrain.core.NullCheck.*;

final class EventQueue
{
    static private final int
	MAX_LEN_LIMIT = 1024;

    static private final String LOG_COMPONENT = Base.LOG_COMPONENT;
    private final LinkedList<Event> events = new LinkedList<>();

    synchronized void putEvent(Event e)
    {
	notNull(e, "e");
	if (events.size() >= MAX_LEN_LIMIT)
	{
	    warn("exceeding max number of unprocessed  events in the events queue (" + MAX_LEN_LIMIT + ")");
	    return;
	}
	    events.addLast(e);
	    notify();
    }

    synchronized Event pickEvent()
    {
	try {
	    while(events.isEmpty())
	    wait();
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	}
	final Event e = events.pollFirst();
	return e;
	    }
}
