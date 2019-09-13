/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

final class Instance
{
    static private final String LOG_COMPONENT = "script";

    private final ScriptEngine engine;
    private final Luwrain luwrain;
    final org.luwrain.core.script.api.LuwrainObj luwrainObj;

    Instance(Luwrain luwrain, File dataDir, Map<String, Object> objs)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(dataDir, "dataDir");
	NullCheck.notNull(objs, "objs");
	this.luwrain = luwrain;
	    final ScriptEngineManager manager = new ScriptEngineManager();
	    this.engine = manager.getEngineByName("nashorn");
	    this.luwrainObj = new org.luwrain.core.script.api.LuwrainObj(luwrain, dataDir);
	    this.engine.put("Luwrain", this.luwrainObj);
	    for(Map.Entry<String, Object> e: objs.entrySet())
		this.engine.put(e.getKey(), e.getValue());
    }

        Object exec(String text) throws ScriptException
    {
	NullCheck.notNull(text, "text");
	return engine.eval(text);
    }
}
