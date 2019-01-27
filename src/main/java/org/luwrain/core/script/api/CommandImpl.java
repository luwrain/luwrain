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

import java.io.*;
import java.util.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

public final class CommandImpl implements Command
{
    private final String name;
    private final JSObject func;

    public CommandImpl(String name, JSObject func)
    {
	NullCheck.notEmpty(name, "name");
	NullCheck.notNull(func, "func");
	this.name = name;
	this.func = func;
    }

    @Override public void onCommand(Luwrain luwrain)
    {
	func.call(null, new Object[0]);
    }

    @Override public String getName()
    {
	return name;
    }
}
