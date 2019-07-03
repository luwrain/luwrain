/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

public class CollectorHook
{
    protected final Luwrain luwrain;

    public CollectorHook(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    public Object[] run(String hookName, Object[] args)
    {
	NullCheck.notEmpty(hookName, "hookName");
	NullCheck.notNullItems(args, "args");
	final List res = new LinkedList();
	final AtomicReference ex = new AtomicReference();
	luwrain.xRunHooks(hookName, (hook)->{
		try {
		    final Object obj = hook.run(args);
		    if (obj == null)
			return Luwrain.HookResult.CONTINUE;
		    res.add(obj);
		    return Luwrain.HookResult.BREAK;
		}
		catch(RuntimeException e)
		{
		    ex.set(e);
		    return Luwrain.HookResult.BREAK;
		}
	    });
	if (ex.get() != null && ex.get() instanceof RuntimeException)
	    throw (RuntimeException)ex.get();
	return res.toArray(new Object[res.size()]);
    }

    public Object[] runForArrays(String hookName, Object[]args)
    {
	NullCheck.notNull(hookName, "hookName");
	NullCheck.notNullItems(args, "args");
	final List res = new LinkedList();
	final Object[] objs = run(hookName, args);
	for(Object o: objs)
	{
	    final List values = ScriptUtils.getArray(o);
	    if (values == null)
		throw new RuntimeException("The hook \'" + hookName + "\' has returned non-array value");
	    for(Object oo: values)
		res.add(oo);
	}
	return res.toArray(new Object[res.size()]);
    }
}
