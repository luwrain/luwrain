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

package org.luwrain.app.console;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.controls.ConsoleArea.InputHandler;

class Utils
{
    static String firstWord(String text)
    {
	NullCheck.notNull(text, "text");
	final int pos = text.indexOf(" ");
	if (pos < 0)
	    return text.trim();
	return text.substring(0, pos).trim();
    }

    static void installListener()
    {
	Log.addListener((message)->{
		NullCheck.notNull(message, "message");
		Base.messages.add(message);
		//	    onNewMessage(message);
	    });
    }
}
