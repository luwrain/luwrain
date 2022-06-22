/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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
import java.util.function.*;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;
import org.luwrain.script.core.*;
import static org.luwrain.script.ScriptUtils.*;

public final class App extends AppBase<Strings>
{
    static final String
	LOG_COMPONENT = "calc";

    private org.luwrain.script.core.Module module = null;
    private MainLayout mainLayout = null;

    public App()
    {
	super(Strings.NAME, Strings.class, "luwrain.calc");
    }

    @Override public AreaLayout onAppInit()
    {
	this.module = new org.luwrain.script.core.Module(getLuwrain(), (bindings, syncObj)->{
				bindings.putMember("pi", Math.PI);
				bindings.putMember("sin", (ProxyExecutable)(args)->singleArg(args, (x)->Math.sin(x.doubleValue())));
				bindings.putMember("cos", (ProxyExecutable)(args)->singleArg(args, (x)->Math.cos(x.doubleValue())));
	    });
	this.mainLayout = new MainLayout(this);
	setAppName(getStrings().appName());
	return mainLayout.getAreaLayout();
    }

    @Override public boolean onEscape()
    {
	closeApp();
	return true;
    }

    Number calculate(String[] expr) throws Exception
    {
	final StringBuilder text = new StringBuilder();
	for(String s: expr)
	{
	    final String str = s.replaceAll("//", "#");
	    final int pos = str.indexOf("#");
	    if (pos < 0)
		text.append(str + " "); else
		text.append(str.substring(0, pos) + " ");
	}
	return asNumber(module.eval(/*prescript +*/ new String(text)));
    }

    @Override public void closeApp()
    {
	this.module.close();
	super.closeApp();
    }

    static Double singleArg(Value[] args, Function<Double, Double> f)
    {
	if (!notNullAndLen(args, 1))
	    throw new IllegalArgumentException("Required one not null arg");
	if (!args[0].isNumber())
	    throw new IllegalArgumentException("The first argument must be a number");
	return f.apply(args[0].asDouble());
    }
}
