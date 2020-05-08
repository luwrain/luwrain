/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.app.crash;

import org.luwrain.core.*;

public class CustomMessageException extends Exception
{
    protected final String[] message;

    public CustomMessageException(String[] message)
    {
	super("CustomMessageException can't be used as a real exception");
	NullCheck.notNullItems(message, "message");
	this.message = message.clone();
    }

    public final String[] getCustomMessage()
    {
	return this.message.clone();
    }
}
