
package org.luwrain.script2;

import java.io.*;
import java.util.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;

final class Wizard implements ProxyObject
{
    private final WizardArea wizardArea;

    public Wizard(WizardArea wizardArea)
    {
	NullCheck.notNull(wizardArea, "wizardArea");
	this.wizardArea = wizardArea;
    }

    @Override public Object getMember(String name)
    {
	if (name == null)
	    return null;
	switch(name)
	{
	case "showFrame":
	    return(ProxyExecutable)this::showFrame;
	default:
	    return null;
	}
    }

    @Override public boolean hasMember(String name)
    {
	switch(name)
	{
	case "showFrame":
	    return true;
	default:
	    return false;
	}
    }

    @Override public Object getMemberKeys()
    {
	return new String[]{
	    "showFrame",
	};
    }

    @Override public void putMember(String name, Value value)
    {
    }

    private Object showFrame(Value[] args)
    {
	if (!ScriptUtils.notNullAndLen(args, 1))
	    return false;
	final List items = ScriptUtils.getArrayItems(args[0]);
	if (items == null || items.isEmpty())
	    return false;
	return false;
	    }
}
