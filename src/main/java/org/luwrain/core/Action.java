/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

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

import org.luwrain.core.events.KeyboardEvent;

public class Action
{
    private String name;
    private String title;
    private KeyboardEvent event = null;

    public Action(String name, String title)
    {
	this.name = name;
	this.title = title;
	NullCheck.notNull(name, "name");
	NullCheck.notNull(title, "title");
    }

    public Action(String name, String title,
		  KeyboardEvent event)
    {
	this.name = name;
	this.title = title;
	this.event = event;
	NullCheck.notNull(name, "name");
	NullCheck.notNull(title, "title");
	NullCheck.notNull(event, "event");
    }

    public String name() {return name;}
    public String title() { return title; }
    public KeyboardEvent keyboardEvent() { return event; }
}
