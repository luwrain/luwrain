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

package org.luwrain.script.hooks;

import java.util.concurrent.atomic.*;

import org.luwrain.core.*;
import org.luwrain.script.*;

public class TransformerHook
{
    static private final String LOG_COMPONENT = "core";

    protected final HookContainer hookContainer;

    public TransformerHook(HookContainer hookContainer)
    {
	NullCheck.notNull(hookContainer, "hookContainer");
	this.hookContainer = hookContainer;
    }

    public Object run(String hookName, Object obj)
    {
	NullCheck.notEmpty(hookName, "hookName");
	NullCheck.notNull(obj, "obj");
	final AtomicReference<Object> o = new AtomicReference<>(obj);
	hookContainer.runHooks(hookName, (hook)->{
		try {
		    final Object res = hook.run(new Object[]{ o.get() });
		    if (res != null)
			o.set(res);
		    return Luwrain.HookResult.CONTINUE;
		}
		catch(RuntimeException e)
		{
		    Log.error(LOG_COMPONENT, "the " + hookName + " hook thrown an exception: " + e.getClass().getName() + ": " + e.getMessage());
		    return Luwrain.HookResult.CONTINUE;
		}
	    });
	return o.get();
    }
}
