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

package org.luwrain.script.core;

import java.io.*;
import java.util.*;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;

final class CommandWrapper implements Command
{
    private final LuwrainObj luwrainObj;
    private final String name;
    private final Value func;

    CommandWrapper(LuwrainObj luwrainObj, String name, Value func)
    {
	NullCheck.notNull(luwrainObj, "luwrainObj");
	NullCheck.notEmpty(name, "name");
	NullCheck.notNull(func, "func");
	this.luwrainObj = luwrainObj;
	this.name = name;
	this.func = func;
    }

    @Override public void onCommand(Luwrain luwrain)
    {
	synchronized(luwrainObj) {
	    func.execute(null, new Object[0]);
	}
    }

    @Override public String getName()
    {
	return name;
    }
}
