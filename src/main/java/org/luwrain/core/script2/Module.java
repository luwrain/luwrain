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

package org.luwrain.core.script2;

import org.graalvm.polyglot.*;

import org.luwrain.core.*;

public final class Module implements AutoCloseable
{
    private final Luwrain luwrain;
    private Context context = null;
    final LuwrainObj luwrainObj;

    Module(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	this.luwrainObj = new LuwrainObj(luwrain);
    }

    public void run(String text)
    {
	close();
	this.context = Context.newBuilder()
	.allowExperimentalOptions(true)
	//.option("js.nashorn-compat", "true"
	.build();
	context.getBindings("js").putMember("Luwrain", this.luwrainObj);
context.eval("js", text);
    }

    @Override public void close()
    {
	if (context == null)
	    return;
	context.close();
	context = null;
    }

}
