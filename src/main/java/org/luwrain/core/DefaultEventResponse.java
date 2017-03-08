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

public class DefaultEventResponse implements EventResponse
{
    protected final Type type;
    protected final Sounds sound;
    protected final Hints hint;
    protected final Unit prefix;
    protected final Unit content;
    protected final Unit postfix;
    protected final Suggestion suggestion;

    public DefaultEventResponse(Type type, Sounds sound, Hints hint,
				Unit prefix, Unit content, Unit postfix,
Suggestion suggestion)
    {
	NullCheck.notNull(type, "type");
	this.type = type;
	this.sound = sound;
	this.hint = hint;
	this.prefix = prefix;
	this.content = content;
	this.postfix = postfix;
	this.suggestion = suggestion;
    }

    @Override public Type getResponseType()
    {
	return type;
    }

    @Override public Sounds getSound()
    {
	return sound;
    }

    @Override public Hints getHint()
    {
	return hint;
    }

    @Override public Unit getPrefix()
    {
	return prefix;
    }

    @Override public Unit getResponseContent()
    {
	return content;
    }

    @Override public Unit getPostfix()
    {
	return postfix;
    }

    @Override public Suggestion getSuggestion()
    {
	return suggestion;
    }

    static public DefaultEventResponse text(String text) 
    {
	NullCheck.notNull(text, "text");
	return new DefaultEventResponse(Type.REGULAR, null, null, null, new Unit(text), null, null);
    }

    static public DefaultEventResponse listItem(String text, Suggestions suggestion) 
    {
	NullCheck.notNull(text, "text");
	return new DefaultEventResponse(Type.LIST_ITEM, null, null, null, new Unit(text), null, suggestion != null?new Suggestion(suggestion):null);
    }
}
