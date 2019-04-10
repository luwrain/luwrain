/*
   Copyright 2012-2019 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core.script.api;

import java.util.*;
import java.util.function.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

final class MessageObj extends AbstractJSObject
{
    private final Luwrain luwrain;

    MessageObj(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	switch(name)
	{
	case "announcement":
	    return (Predicate)(textObj)->{
		final String text = org.luwrain.script.ScriptUtils.getStringValue(textObj);
		if (text == null || text.trim().isEmpty())
		    return new Boolean(false);
		luwrain.message(text, Luwrain.MessageType.ANNOUNCEMENT);
		return new Boolean(true);
	    };
	case "error":
	    return (Predicate)(textObj)->{
		final String text = org.luwrain.script.ScriptUtils.getStringValue(textObj);
		if (text == null || text.trim().isEmpty())
		    return new Boolean(false);
		luwrain.message(text, Luwrain.MessageType.ERROR);
		return new Boolean(true);
	    };
	case "chat":
	    return (Predicate)(textObj)->{
		final String text = org.luwrain.script.ScriptUtils.getStringValue(textObj);
		if (text == null || text.trim().isEmpty())
		    return new Boolean(false);
		luwrain.message(text, Sounds.CHAT_MESSAGE);
		return new Boolean(true);
	    };
	default:
	    return super.getMember(name);
	}
    }

    @Override public Object call(Object th, Object[] args)
    {
	if (args == null || args.length != 1)
	    return new Boolean(false);
	final String text = org.luwrain.script.ScriptUtils.getStringValue(args[0]);
	if (text == null || text.trim().isEmpty())
	    return new Boolean(false);
	luwrain.message(text);
	return new Boolean(true);
    }

    @Override public boolean isFunction()
    {
	return true;
    }
}
