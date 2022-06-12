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

package org.luwrain.script.hooks;

import java.util.*;
import java.util.concurrent.atomic.*;
import org.graalvm.polyglot.*;

import org.luwrain.core.*;
import org.luwrain.script.*;

public class PermissionHook
{
    protected final HookContainer hookContainer;

    public PermissionHook(HookContainer hookContainer)
    {
	NullCheck.notNull(hookContainer, "hookContainer");
	this.hookContainer = hookContainer;
    }

    public boolean  run(String hookName, Object[] args)
    {
	NullCheck.notEmpty(hookName, "hookName");
	NullCheck.notNullItems(args, "args");
	final AtomicBoolean execRes = new AtomicBoolean(true);
	final AtomicReference<RuntimeException> error = new AtomicReference<>();
	hookContainer.runHooks(hookName, (hook)->{
		try {
		    final Object res = hook.run(args);
		    if (res == null || !(res instanceof Value))
		    {
			execRes.set(false);
						return Luwrain.HookResult.BREAK;
		    }
			final Value value = (Value)res;
		    if (value.isNull() || !value.isBoolean() || !value.asBoolean())
		    {
			execRes.set(false);
			return Luwrain.HookResult.BREAK;
		    }
			return Luwrain.HookResult.CONTINUE;
		}
		catch(Throwable e)
		{
		    final RuntimeException runtimeEx;
		    if (!(e instanceof RuntimeException))
			runtimeEx = new RuntimeException(e); else
			runtimeEx = (RuntimeException)e;
		    error.set(runtimeEx);
		    return Luwrain.HookResult.BREAK;
		}
	    });
	if (error.get() != null)
	    throw error.get();
	return execRes.get();
    }
}
