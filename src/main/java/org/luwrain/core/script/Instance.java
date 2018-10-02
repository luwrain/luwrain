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

import java.util.*;
import java.io.*;

import javax.script.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.core.*;

class Instance
{
    static private final String LOG_COMPONENT = "script";

    private final ScriptEngine engine;
    private final Luwrain luwrain;
    final Control control;

    Instance(Luwrain luwrain, File dataDir, Map<String, JSObject> objs)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(dataDir, "dataDir");
	NullCheck.notNull(objs, "objs");
	this.luwrain = luwrain;
	    final ScriptEngineManager manager = new ScriptEngineManager();
	    this.engine = manager.getEngineByName("nashorn");
	    this.control = new Control(luwrain, dataDir);
	    this.engine.put("Luwrain", this.control);
	    for(Map.Entry<String, JSObject> e: objs.entrySet())
		this.engine.put(e.getKey(), e.getValue());
    }

        void exec(String text) throws ScriptException
    {
	NullCheck.notNull(text, "text");
	engine.eval(text);
    }
}
