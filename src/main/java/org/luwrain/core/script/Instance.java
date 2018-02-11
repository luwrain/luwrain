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

import java.io.*;

import javax.script.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.core.*;

class Instance
{
    static private final String LOG_COMPONENT = "script";

    private final ScriptEngine engine;
    private final Luwrain luwrain;

Instance(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	    final ScriptEngineManager manager = new ScriptEngineManager();
	    this.engine = manager.getEngineByName("nashorn");
	    this.engine.put("Luwrain", new Control(luwrain));
    }

    Invocable getInvocable()
    {
	return (Invocable) engine;
    }

        void exec(String text) throws ScriptException
    {
	NullCheck.notNull(text, "text");
	engine.eval(text);
    }


    void exec(InputStream is) throws ScriptException
    {
	NullCheck.notNull(is, "is");
	engine.eval(new BufferedReader(new InputStreamReader(is)));
    }
}
