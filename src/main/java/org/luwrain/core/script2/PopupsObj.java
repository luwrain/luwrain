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

import java.util.function.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;
import org.luwrain.popups.*;

final class PopupsObj implements ProxyObject
{
    static private final String[] MEMBERS = new String[]{
	"confirmDefaultNo",
	"confirmDefaultYes",
	"text",
    };
    static private final ProxyArray MEMBERS_KEYS = ProxyArray.fromArray(MEMBERS);

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

    @Override public void putMember(String name, Value value)
    {
    }

    @Override public boolean hasMember(String name)
    {
	for(String s: MEMBERS)
	    if (s.equals(name))
		return true;
	return false;
    }

    @Override public ProxyArray getMemberKeys()
    {
	return MEMBERS_KEYS;
    }

    private Boolean confirmDefaultYes(Value[] args)
    {
	if (args == null || args.length != 2)
	    return false;
	if (args[0] == null || !args[0].isString())
	    return false;
	if (args[1] == null || !args[1].isString())
	    return false;
	final String name = args[0].asString();
	final String text = args[1].asString();
	return new Boolean(Popups.confirmDefaultYes(luwrain, name, text));
    }

        private Boolean confirmDefaultNo(Value[] args)
    {
	if (args == null || args.length != 2)
	    return false;
	if (args[0] == null || !args[0].isString())
	    return false;
	if (args[1] == null || !args[1].isString())
	    return false;
	final String name = args[0].asString();
	final String text = args[1].asString();
	return new Boolean(Popups.confirmDefaultNo(luwrain, name, text));
    }

    private String text(Value[] args)
    {
		if (args == null || args.length != 3)
		    return null;
		for(int i = 0;i < args.length;i++)
		    if (args[i] == null || args[i].isString())
			return null;
		final String name = args[0].asString();
		final String text = args[1].asString();
		final String defaultValue = args[2].asString();
						return Popups.text(luwrain, name, text, defaultValue);
	    }
}
