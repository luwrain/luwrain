/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

import static org.luwrain.core.NullCheck.*;

final class CommandImpl implements Command
{
    private final Module module;
    private final String name;
    private final Value func;

    CommandImpl(Module module, String name, Value func)
    {
	notNull(module, "module");
	notEmpty(name, "name");
	notNull(func, "func");
	this.module = module;
	this.name = name;
	this.func = func;
    }

    @Override public void onCommand(Luwrain luwrain)
    {
	module.execFuncValue(func);
    }

    @Override public String getName()
    {
	return name;
    }
}
