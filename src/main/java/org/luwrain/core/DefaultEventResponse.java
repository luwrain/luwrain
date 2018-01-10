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

//LWR_API 1.0

package org.luwrain.core;

public class DefaultEventResponse
{
static public EventResponse text(String text) 
    {
	NullCheck.notNull(text, "text");
	return new EventResponses.Text(text);
    }

        static public EventResponse hint(Hint hint) 
    {
	NullCheck.notNull(hint, "hint");
	return new EventResponses.Hint(hint);
    }

    static public EventResponse hint(Hint hint, String text) 
    {
	NullCheck.notNull(hint, "hint");
	NullCheck.notNull(text, "text");
	return new EventResponses.Hint(hint, text);
    }

    static public EventResponse listItem(String text) 
    {
	NullCheck.notNull(text, "text");
	return new EventResponses.ListItem(null, text, null);
    }

    static public EventResponse listItem(String text, Suggestions suggestion) 
    {
	NullCheck.notNull(text, "text");
	return new EventResponses.ListItem(null, text, suggestion);
    }

    static public EventResponse listItem(Sounds sound, String text, Suggestions suggestion) 
    {
	NullCheck.notNull(text, "text");
	return new EventResponses.ListItem(sound, text, suggestion);
    }
}
