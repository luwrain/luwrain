/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.script;

import java.io.*;
import java.util.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;

final class Wizard implements ProxyObject
{
    private final WizardArea wizardArea;

    public Wizard(WizardArea wizardArea)
    {
	NullCheck.notNull(wizardArea, "wizardArea");
	this.wizardArea = wizardArea;
    }

    @Override public Object getMember(String name)
    {
	if (name == null)
	    return null;
	switch(name)
	{
	case "showFrame":
	    return(ProxyExecutable)this::showFrame;
	default:
	    return null;
	}
    }

    @Override public boolean hasMember(String name)
    {
	switch(name)
	{
	case "showFrame":
	    return true;
	default:
	    return false;
	}
    }

    @Override public Object getMemberKeys()
    {
	return new String[]{
	    "showFrame",
	};
    }

    @Override public void putMember(String name, Value value)
    {
    }

    private Object showFrame(Value[] args)
    {
	if (!ScriptUtils.notNullAndLen(args, 1))
	    return false;
	final List items = ScriptUtils.getArrayItems(args[0]);
	if (items == null || items.isEmpty())
	    return false;
	return false;
	    }
}
