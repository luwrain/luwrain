
package org.luwrain.core.script2;

import java.io.*;
import java.util.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;
import org.luwrain.script2.*;

final class LuwrainObj implements ProxyObject
{
    final Luwrain luwrain;
    final Map<String, List<Value> > hooks = new HashMap();

    LuwrainObj(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    @Override public Object getMember(String name)
    {
	if (name == null)
	    return null;
	switch(name)
	{
	case "addHook":
	    return(ProxyExecutable)this::addHook;
	default:
	    return null;
	}
    }

    @Override public boolean hasMember(String name)
    {
	switch(name)
	{
	case "addHook":
	    return true;
	default:
	    return false;
	}
    }

    @Override public Object getMemberKeys()
    {
	return new String[]{
	    "addHook",
	};
    }

    @Override public void putMember(String name, Value value)
    {
	throw new RuntimeException("The Luwrain object doesn't support updating of its variables");
    }

    private Object addHook(Value[] args)
    {
	if (!ScriptUtils.notNullAndLen(args, 2))
	    return false;
	if (!args[0].isString() || !args[1].canExecute())
	    return false;
	final String name = args[0].asString();
	if (name.trim().isEmpty())
	    return false;
	List<Value> h = this.hooks.get(name);
	if (h == null)
	{
	    h = new ArrayList();
	    this.hooks.put(name, h);
	    }
	h.add(args[1]);
		return true;
    }
}
