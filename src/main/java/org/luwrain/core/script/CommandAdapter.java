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

package org.luwrain.core.script;

import javax.script.*;

import org.luwrain.core.*;

final class CommandAdapter implements Command 
{
    private final Instance instance;
    private final Object obj;

    CommandAdapter(Instance instance, Object obj)
    {
	NullCheck.notNull(instance, "instance");
	NullCheck.notNull(obj, "obj");
	this.instance = instance;
	this.obj = obj;
    }

    @Override public String getName()
    {
	return "";
    }

    @Override public void onCommand(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	try {
	    final Object res = instance.getInvocable().invokeMethod(obj, "onCommand");
	}
	catch(ScriptException | NoSuchMethodException e)
	{
	    luwrain.crash(e);
	}
    }
}
