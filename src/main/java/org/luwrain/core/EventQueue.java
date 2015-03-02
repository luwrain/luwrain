/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

import java.util.concurrent.*;

public class EventQueue
{
    private LinkedBlockingQueue<Event> events = new LinkedBlockingQueue<Event>(1024);
    private Event again = null;

    void putEvent(Event e)
    {
	try {
	    events.put(e);
	}
	catch (InterruptedException ex)
	{
	    ex.printStackTrace();//FIXME:
	}
    }

    public boolean hasAgain()
    {
	return again != null;
    }

    public void onceAgain(Event event)
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
	    ex.printStackTrace();
	    return null;
	}
    }
}
