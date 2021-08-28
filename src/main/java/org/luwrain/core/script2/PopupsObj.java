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

package org.luwrain.core.script2;

import java.util.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;
import org.luwrain.popups.*;

import static org.luwrain.script2.ScriptUtils.*;

final class PopupsObj implements ProxyObject
{
    static private final String[] KEYS = new String[]{
	"confirmDefaultNo",
	"confirmDefaultYes",
	"text",
    };
        static private final Set<String> KEYS_SET = new HashSet(Arrays.asList(KEYS));
    static private final ProxyArray KEYS_ARRAY = ProxyArray.fromArray((Object[])KEYS);


    private final Luwrain luwrain;

    PopupsObj(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notEmpty(name, "name");
	switch(name)
	{
	case "confirmDefaultYes":
	    return (ProxyExecutable)this::confirmDefaultYes;
	case "confirmDefaultNo":
	    return (ProxyExecutable)this::confirmDefaultNo;
	case "simple":
	    return (ProxyExecutable)this::text;
	default:
	    return null;
	}
    }

        @Override public boolean hasMember(String name) { return KEYS_SET.contains(name); }
    @Override public Object getMemberKeys() { return KEYS_ARRAY; }
    @Override public void putMember(String name, Value value) { throw new RuntimeException("The popups object doesn't support updating of its variables"); }

    private Boolean confirmDefaultYes(Value[] args)
    {
		if (!notNullAndLen(args, 2))
	    return false;
	if (!args[0].isString() || !args[1].isString())
	    return false;
	return new Boolean(Popups.confirmDefaultYes(luwrain, args[0].asString(), args[1].asString()));
    }

        private Boolean confirmDefaultNo(Value[] args)
    {
			if (!notNullAndLen(args, 2))
	    return false;
	if (!args[0].isString() || !args[1].isString())
	    return false;
	return new Boolean(Popups.confirmDefaultNo(luwrain, args[0].asString(), args[1].asString()));
    }

    private String text(Value[] args)
    {
				if (!notNullAndLen(args, 3))
	    return null;
		for(int i = 0;i < args.length;i++)
		    if (!args[i].isString())
			return null;
		final String name = args[0].asString();
		final String text = args[1].asString();
		final String defaultValue = args[2].asString();
						return Popups.text(luwrain, name, text, defaultValue);
	    }
}
