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

package org.luwrain.script.core;

import org.graalvm.polyglot.*;

import org.luwrain.core.*;

public final class Module implements AutoCloseable
{
    private final Bindings bindings;
    private final Luwrain luwrain;
    private Context context = null;
    final LuwrainObj luwrainObj;

    public Module(Luwrain luwrain, Bindings bindings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	this.luwrainObj = new LuwrainObj(luwrain);
	this.bindings = bindings;
    }

    public Module(Luwrain luwrain )
    {
	this(luwrain, null);
    }

    public void run(String text)
    {
	close();
	synchronized(luwrainObj.syncObj) {
	    this.context = Context.newBuilder()
	    .allowExperimentalOptions(true)
	    //.option("js.nashorn-compat", "true"
	    .build();
	    context.getBindings("js").putMember("Luwrain", this.luwrainObj);
	    if (bindings != null)
		bindings.onBindings(context.getBindings("js"), luwrainObj.syncObj);
	    context.eval("js", text);
	}
    }

    public Object eval(String exp)
    {
	synchronized(luwrainObj.syncObj) {
	    if (this.context == null)
		this.context = Context.newBuilder()
		.allowExperimentalOptions(true)
		//.option("js.nashorn-compat", "true"
		.build();
	    context.getBindings("js").putMember("Luwrain", this.luwrainObj);
	    if (bindings != null)
		bindings.onBindings(context.getBindings("js"), luwrainObj.syncObj);
	    return context.eval("js", exp);
	}
    }

    @Override public void close()
    {
	synchronized(luwrainObj.syncObj) {
	    if (context == null)
		return;
	    context.close();
	    context = null;
	}
    }
}
