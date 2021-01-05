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

package org.luwrain.core.script.api;

import java.util.function.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.core.*;
import org.luwrain.popups.*;

final class PopupsObj extends AbstractJSObject
{
    private final Luwrain luwrain;

    PopupsObj(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	if (name.isEmpty())
	    return super.getMember(name);
	switch(name)
	{
	case "confirmDefaultYes":
	    return (BiPredicate)this::confirmDefaultYes;
	case "confirmDefaultNo":
	    return (BiPredicate)this::confirmDefaultNo;
	case "simple":
	    return simple();
	default:
	    return super.getMember(name);
	}
    }

    private boolean confirmDefaultYes(Object name, Object text)
    {
	final String nameStr = org.luwrain.script.ScriptUtils.getStringValue(name);
	final String textStr = org.luwrain.script.ScriptUtils.getStringValue(text);
	if (nameStr == null || textStr == null)
	    return false;
	return Popups.confirmDefaultYes(luwrain, nameStr, textStr);
    }

    private boolean confirmDefaultNo(Object name, Object text)
    {
	final String nameStr = org.luwrain.script.ScriptUtils.getStringValue(name);
	final String textStr = org.luwrain.script.ScriptUtils.getStringValue(text);
	if (nameStr == null || textStr == null)
	    return false;
	return Popups.confirmDefaultNo(luwrain, nameStr, textStr);
    }

    private org.luwrain.script.HookObject simple()
    {
	return new org.luwrain.script.EmptyHookObject(){
	    @Override public Object call(Object th, Object[] args)
	    {
		if (args == null || args.length != 3)
		    return null;
		final String name = org.luwrain.script.ScriptUtils.getStringValue(args[0]);
				final String text = org.luwrain.script.ScriptUtils.getStringValue(args[1]);
						final String defaultValue = org.luwrain.script.ScriptUtils.getStringValue(args[2]);
						if (name == null || text == null || defaultValue == null)
						    return null;
						return org.luwrain.popups.Popups.simple(luwrain, name, text, defaultValue);
	    }
	    @Override public boolean isFunction()
	    {
		return true;
	    }
	};
    }
}
