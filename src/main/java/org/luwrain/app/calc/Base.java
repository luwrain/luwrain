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

package org.luwrain.app.calc;

import java.util.*;
import javax.script.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

final class Base
{
    private final Luwrain luwrain;
    private final Strings strings;
    final ScriptEngine engine;

    Base(Luwrain luwrain, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.strings = strings;
	final ScriptEngineManager manager = new ScriptEngineManager();
	this.engine = manager.getEngineByName("nashorn");
    }

    Number calculate(String expr) throws Exception
    {
	NullCheck.notNull(expr, "expr");
	final Object res = engine.eval("" + expr + ";");
	if (res != null && res instanceof Number)
	    return (Number)res;
	return null;
    }
}
