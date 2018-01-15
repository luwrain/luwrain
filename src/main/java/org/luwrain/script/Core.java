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

package org.luwrain.script;

import javax.script.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.core.*;

public class Core
{
    static private final String LOG_COMPONENT = "script";
    
    private final ScriptEngine engine;

    public Core()
    {
	final ScriptEngine res;
	try {
	    ScriptEngineManager manager = new ScriptEngineManager();
	    res = manager.getEngineByName("nashorn");
	    	    Log.debug(LOG_COMPONENT, "the script core initialized");
	}
	catch(Exception e)
	{
	    Log.error(LOG_COMPONENT, "unable to init the script core:" + e.getClass().getName() + ":" + e.getMessage());
	    engine = null;
	    return;
	}
	engine = res;
    }
}
