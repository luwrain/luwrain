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

import java.util.concurrent.*;

import static org.luwrain.core.NullCheck.*;

final class EventQueue
{
    static private final String LOG_COMPONENT = Base.LOG_COMPONENT;
    private final LinkedBlockingQueue<Event> events = new LinkedBlockingQueue<>(1024);

    void putEvent(Event e)
    {
	notNull(e, "e");
	try {
	    events.put(e);
	}
	catch (InterruptedException ex)
	{
	    Thread.currentThread().interrupt();
	}
    }

    Event pickEvent()
    {
	try {
	    return events.take();
	}
	catch (InterruptedException ex)
	{
	    Thread.currentThread().interrupt();
	    return null;
	}
    }
}
