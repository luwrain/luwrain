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

import java.util.*;
import java.util.concurrent.atomic.*;

import org.luwrain.core.*;
import org.luwrain.script.*;

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
	final AtomicReference error = new AtomicReference();
	hookContainer.runHooks(hookName, (hook)->{
		try {
		    final Object res = hook.run(args);
		    if (res == null || !(res instanceof Boolean))
			return Luwrain.HookResult.CONTINUE;
		    if (((Boolean)res).booleanValue())
		    {
			execRes.set(true);
			return Luwrain.HookResult.BREAK;
		    }
		    return Luwrain.HookResult.CONTINUE;
		}
		catch(Throwable e)
		{
		    if (!(e instanceof RuntimeException))
			return Luwrain.HookResult.BREAK;
		    final RuntimeException runtimeEx = (RuntimeException)e;
		    error.set(runtimeEx);
		    return Luwrain.HookResult.BREAK;
		}
	    });
	if (error.get() != null)
	    throw (RuntimeException)error.get();
	return execRes.get();
    }

        public boolean  runNoExcept(String hookName, Object[] args)
    {
	NullCheck.notEmpty(hookName, "hookName");
	NullCheck.notNullItems(args, "args");
	try {
	    return run(hookName, args);
	}
	catch(Throwable e)
	{
	    return false;
	}
    }
}
