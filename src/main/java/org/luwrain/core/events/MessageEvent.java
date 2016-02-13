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

package org.luwrain.core.events;

import org.luwrain.core.*;

/**
 * Used for issuing a message to the user from the background
 * thread. Create an instance of this class, put it to the event queue
 * through {@code Luwrain.enqueueEvent(} and the core with show it to the
 * user.
 */
public class MessageEvent extends EnvironmentEvent
{
    private String text = "";
    private int semantic;

    public MessageEvent(String text)
    {
	super(Code.MESSAGE);
	this.text = text;
	NullCheck.notNull(text, "text");
	this.semantic = Luwrain.MESSAGE_REGULAR;
    }

    public MessageEvent(String text, int semantic)
    {
	super(Code.MESSAGE);
	this.text = text;
	NullCheck.notNull(text, "text");
	this.semantic = semantic;
    }

    public String text()
    {
	return text;
    }

    public int semantic()
    {
	return semantic;
    }
}
