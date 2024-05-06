/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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
import org.apache.logging.log4j.*;

import org.luwrain.core.*;
import org.luwrain.script.*;

import static org.luwrain.core.NullCheck.*;

public class ProviderHook
{
    static protected final Logger log = LogManager.getLogger();
    protected final HookContainer hookContainer;

    public ProviderHook(HookContainer hookContainer)
    {
	notNull(hookContainer, "hookContainer");
	this.hookContainer = hookContainer;
    }

    public Object run(String hookName, Object[] args)
    {
	notEmpty(hookName, "hookName");
	notNullItems(args, "args");
	final AtomicReference<Object> res = new AtomicReference<>();
	hookContainer.runHooks(hookName, (hook)->{
		try {
		    final Object obj = hook.run(args);
		    if (obj == null)
			return Luwrain.HookResult.CONTINUE;
		    res.set(obj);
		    return Luwrain.HookResult.BREAK;
		}
		catch(Throwable ex)
		{
		    log.catching(ex);
		    res.set(ex);
		    return Luwrain.HookResult.BREAK;
		}
	    });
	if (res.get() == null)
	    return null;
	if (res.get() instanceof Throwable t)
	{
	    if (res.get() instanceof RuntimeException r)
		throw r;
	    throw new RuntimeException(t);
	}
	return res.get();
    }
}
