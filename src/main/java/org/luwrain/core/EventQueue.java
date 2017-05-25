/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

class EventQueue
{
    private final LinkedBlockingQueue<Event> events = new LinkedBlockingQueue<Event>(1024);
    private Event again = null;

    void putEvent(Event e)
    {
	try {
	    events.put(e);
	}
	catch (InterruptedException ex)
	{
	    Thread.currentThread().interrupt();
	}
    }

    boolean hasAgain()
    {
	return again != null;
    }

    void onceAgain(Event event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	if (again != null)
	{
	    Log.warning("queue", "adding the event to try it once again but there is already one");
	    return;
	}
	again = event;
    }

    Event takeEvent()
    {
	if (again != null)
	{
	    Event event = again;
	    again = null;
	    return event;
	}
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
