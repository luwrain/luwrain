/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

public interface EventResponse
{
    public enum Type {
	REGULAR,
	LIST_ITEM,
	MESSAGE,
	MESSAGE_OK,
	MESSAGE_DONE,
	MESSAGE_ERROR,
    };

    public static final class Unit
    {
	public enum Type { CHAR, TEXT };

	private final Type type;
	private final char ch;
	private final String text;

	public Unit(String text)
	{
	    NullCheck.notNull(text, "text");
	    this.type = Type.TEXT;
	    this.text = text;
	    this.ch = '\0';
	}

	public Unit(char ch)
	{
	    this.type = Type.CHAR;
	    this.ch = ch;
	    this.text = null;
	}

	public Type getType()
	{
	    return type;
	}

	public char getChar()
	{
	    return ch;
	}

	public String getText()
	{
	    return text;
	}
    }

    public static final class Suggestion
    {
	public enum Type { PREDEFINED, TEXT };

	private final Type type;
	private final Suggestions predefined;
	private final String text;

	public Suggestion(Suggestions predefined)
	{
	    NullCheck.notNull(predefined, "predefined");
	    this.type = Type.PREDEFINED;
	    this.predefined = predefined;
	    this.text = null;
	}

	public Suggestion(String text)
	{
	    NullCheck.notNull(text, "text");
	    this.type = Type.TEXT;
	    this.text = text;
	    this .predefined = null;
	}

	public Type getType()
	{
	    return type;
	}

	public Suggestions getPredefined()
	{
	    return predefined;
	}

	public String getText()
	{
	    return text;
	}
    }

    Type getResponseType();
    Sounds getSound();
    Hints getHint();
    Unit getPrefix();
    Unit getResponseContent();
    Unit getPostfix();
    Suggestion getSuggestion();
}
