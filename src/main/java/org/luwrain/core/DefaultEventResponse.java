/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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
	return new EventResponses.Text(null, text);
    }

    /**
     * Speak a text with simultaneous sound.
     *
     * @param sound A sound, may be null (no sound needed)
     * @param text A text to say
     */
    static public EventResponse text(Sounds sound, String text) 
    {
	NullCheck.notNull(text, "text");
	return new EventResponses.Text(sound, text);
    }

    static public EventResponse letter(char letter)
    {
	if (Character.isWhitespace(letter) || letter == 160)//with non-breaking space
	    return new EventResponses.Hint(Hint.SPACE);
	return new EventResponses.Letter(letter);
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

    static public EventResponse treeItem(EventResponses.TreeItem.Type type, String text, int level, Suggestions suggestion)
    {
	NullCheck.notNull(type, "type");
	NullCheck.notNull(text, "text");
	return new EventResponses.TreeItem(type, text, level, suggestion);
    }

        static public EventResponse treeItem(EventResponses.TreeItem.Type type, String text, int level)
    {
	NullCheck.notNull(type, "type");
	NullCheck.notNull(text, "text");
	return new EventResponses.TreeItem(type, text, level);
    }
}
