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

package org.luwrain.app.calc;

import java.util.*;
import java.io.*;
import java.net.*;
import javax.script.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;

public final class App extends AppBase<Strings>
{
    static private final String RESOURCE_PATH = "org/luwrain/app/calc/prescript.js";

    private ScriptEngine engine;
    private MainLayout mainLayout = null;

    public App()
    {
	super(Strings.NAME, Strings.class);
    }

    @Override public boolean onAppInit()
    {
	final ScriptEngineManager manager = new ScriptEngineManager();
	this.engine = manager.getEngineByName("nashorn");
	this.mainLayout = new MainLayout(this);
	setAppName(getStrings().appName());
	return true;
    }

    @Override public boolean onEscape(InputEvent event)
    {
	closeApp();
	return true;
    }

    @Override public AreaLayout getDefaultAreaLayout()
    {
	return mainLayout.getAreaLayout();
    }

    Number calculate(String[] expr) throws Exception
    {
	NullCheck.notNullItems(expr, "expr");
	final StringBuilder text = new StringBuilder();
	for(String s: expr)
	{
	    final String str = s.replaceAll("//", "#");
	    final int pos = str.indexOf("#");
	    if (pos < 0)
		text.append(str + " "); else
		text.append(str.substring(0, pos) + " ");
	}
	final String prescript = readPrescript();
	final Object res = engine.eval(prescript + new String(text) + ";");
	if (res != null && res instanceof Number)
	    return (Number)res;
	return null;
    }

    private String readPrescript()
    {
	final StringBuilder b = new StringBuilder();
	final URL url = this.getClass().getClassLoader().getResource(RESOURCE_PATH);
	try {
	    final InputStream is = url.openStream();
	    try {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
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
	    getLuwrain().crash(e);
	    return "";
	}
    }

}
