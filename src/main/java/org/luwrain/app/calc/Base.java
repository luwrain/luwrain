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
import java.io.*;
import java.net.*;
import javax.script.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

final class Base
{
    static private final String RESOURCE_PATH = "org/luwrain/app/calc/prescript.js";
    
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
	final String prescript = (readPrescript() + "\n");
	final Object res = engine.eval(prescript + expr + ";");
	if (res != null && res instanceof Number)
	    return (Number)res;
	return null;
    }

    private String readPrescript()
    {
	final StringBuilder b = new StringBuilder();

		final URL url = ClassLoader.getSystemResource(RESOURCE_PATH);
		try {
		    		final InputStream is = url.openStream();
		    try {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = reader.readLine();
			while (line != null)
			{
			    b.append(line + "\n");
			    line = reader.readLine();
			}
			return new String(b);
		    }
		    finally {
			is.close();
		    }
		}
		catch(IOException e)
		{
		    luwrain.crash(e);
		    return "";
		}
    }
}
