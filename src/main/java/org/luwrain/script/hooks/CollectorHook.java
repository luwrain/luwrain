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

import org.luwrain.core.*;
import org.luwrain.script.*;
import static org.luwrain.script.ScriptUtils.*;

public class CollectorHook
{
    protected final HookContainer hookContainer;

    public CollectorHook(HookContainer hookContainer)
    {
	NullCheck.notNull(hookContainer, "hookContainer");
	this.hookContainer = hookContainer;
    }

    public Object[] run(String hookName, Object[] args)
    {
	NullCheck.notEmpty(hookName, "hookName");
	NullCheck.notNullItems(args, "args");
	final List<Object> res = new ArrayList<>();
	final AtomicReference<RuntimeException> ex = new AtomicReference<>();
	hookContainer.runHooks(hookName, (hook)->{
		try {
		    final Object obj = hook.run(args);
		    if (obj == null)
			return Luwrain.HookResult.CONTINUE;
		    res.add(obj);
		    return Luwrain.HookResult.CONTINUE;
		}
		catch(RuntimeException e)
		{
		    ex.set(e);
		    return Luwrain.HookResult.BREAK;
		}
	    });
	if (ex.get() != null)
	    throw ex.get();
	return res.toArray(new Object[res.size()]);
    }

    public Object[] runForArrays(String hookName, Object[]args)
    {
	NullCheck.notNull(hookName, "hookName");
	NullCheck.notNullItems(args, "args");
	final List<Object> res = new ArrayList<>();
	final Object[] objs = run(hookName, args);
	if (objs == null)
	    return null;
	for(Object o: objs)
	{
	    if (o  == null)
		continue;
	    final List<Object> values = getArrayItems(o);
	    if (values == null)
		res.add(o); else
	    res.addAll(values);
	}
	return res.toArray(new Object[res.size()]);
    }
}
