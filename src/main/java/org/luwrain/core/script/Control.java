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
import jdk.nashorn.api.scripting.AbstractJSObject;                                                                      
import java.util.function.*;

import org.luwrain.core.*;

final class Control extends AbstractJSObject
{
    private final Luwrain luwrain;

    Control(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    
    @Override public Object newObject(Object... args)
    {
	return new Control(luwrain);
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	switch(name)
	{
	    	case "message":
	    return (Consumer)this::message;
	case "addCommand":
	    return (Predicate)this::addCommand;
	default:
	    return null;
	}
    }

    private void message(Object b)
    {
	if (b != null && !b.toString().trim().isEmpty())
	    luwrain.message(b.toString());
    }

    private boolean addCommand(Object obj)
    {
	return false;
    }
}
