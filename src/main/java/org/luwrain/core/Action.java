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

//LWR_API 1.0

package org.luwrain.core;

import org.luwrain.core.events.InputEvent;

public final class Action
{
    private final String name;
    private final String title;
    private final InputEvent event;

    public Action(String name, String title)
    {
		NullCheck.notEmpty(name, "name");
	NullCheck.notEmpty(title, "title");
	this.name = name;
	this.title = title;
	this.event = null;
    }

    public Action(String name, String title, InputEvent event)
    {
		NullCheck.notEmpty(name, "name");
	NullCheck.notEmpty(title, "title");
	NullCheck.notEmpty(event, "event");
	this.name = name;
	this.title = title;
	this.event = event;
    }

    public String name() {return name;}
    public String title() { return title; }
    public InputEvent inputEvent() { return event; }
}
