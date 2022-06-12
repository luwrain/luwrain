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

package org.luwrain.script2.hooks;

import java.util.*;
import java.util.concurrent.atomic.*;
import org.graalvm.polyglot.*;

import org.luwrain.core.*;
import org.luwrain.script2.*;

public class ChainOfResponsibilityHook
{
    protected final HookContainer hookContainer;

    public ChainOfResponsibilityHook(HookContainer hookContainer)
    {
	NullCheck.notNull(hookContainer, "hookContainer");
	this.hookContainer = hookContainer;
    }

    public boolean  run(String hookName, Object[] args)
    {
	NullCheck.notEmpty(hookName, "hookName");
	NullCheck.notNullItems(args, "args");
	final AtomicBoolean execRes = new AtomicBoolean(false);
	final AtomicReference<RuntimeException> error = new AtomicReference<>();
	hookContainer.runHooks(hookName, (hook)->{
		try {
		    final Object res = hook.run(args);
		    if (res == null || !(res instanceof Value))
						return Luwrain.HookResult.CONTINUE;
			final Value value = (Value)res;
		    if (value.isNull() || !value.isBoolean() || !value.asBoolean())
			return Luwrain.HookResult.CONTINUE;
			execRes.set(true);
			return Luwrain.HookResult.BREAK;
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

    static public     boolean  run(HookContainer hookContainer, String hookName, Object[] args)
    {
	NullCheck.notNull(hookContainer, "hookContainer");
	NullCheck.notEmpty(hookName, "hookName");
	NullCheck.notNullItems(args, "args");
	return new ChainOfResponsibilityHook(hookContainer).run(hookName, args);
    }
}
