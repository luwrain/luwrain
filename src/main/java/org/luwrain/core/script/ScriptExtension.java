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

package org.luwrain.core.script;

import java.util.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

class ScriptExtension implements org.luwrain.core.extensions.DynamicExtension, org.luwrain.core.HookContainer
{
    static private final String LOG_COMPONENT = Core.LOG_COMPONENT;

    final String name;
    private Instance instance = null;
    private Luwrain luwrain = null;

    ScriptExtension(String name)
    {
	NullCheck.notEmpty(name, "name");
	this.name = name;
    }

        void setInstance(Instance instance)
    {
	NullCheck.notNull(instance, "instance");
	this.instance = instance;
    }

    @Override public String init(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	return null;
    }

            @Override public void close()
    {
    }

    @Override public boolean runHooks(String hookName, Luwrain.HookRunner runner)
    {
	NullCheck.notEmpty(hookName, "hookName");
	if (!instance.luwrainObj.hooks.containsKey(hookName))
	    return true;
	for(org.luwrain.core.script.api.Hook h: instance.luwrainObj.hooks.get(hookName))
	{
	    try {
		final Luwrain.HookResult res = runner.runHook(h);
		if (res == null)
		    return false;
		switch(res)
		{
		case BREAK:
		    return false;
		case CONTINUE:
		default:
		    continue;
		}
	    }
	    catch(Throwable e)
	    {
		Log.error(LOG_COMPONENT, "running of the hook \'" + hookName + "\' failed in the extension \'" + name + "\':" + e.getClass().getName() + ":" + e.getMessage());
		return false;
	    }
	}
	return true;
    }

    @Override public ExtensionObject[] getExtObjects(Luwrain luwrain)
    {
	final List<ExtensionObject> res = new LinkedList();
	for(Shortcut s: instance.luwrainObj.shortcuts )
	    res.add(s);
	for(Worker w: instance.luwrainObj.workers)
	    res.add(w);
	return res.toArray(new ExtensionObject[res.size()]);
    }

    @Override public Command[] getCommands(Luwrain luwrain)
    {
	return instance.luwrainObj.commands.toArray(new Command[instance.luwrainObj.commands.size()]);
    }

    @Override public Shortcut[] getShortcuts(Luwrain luwrain)
    {
	return new Shortcut[0];
    }

    @Override public void i18nExtension(Luwrain luwrain, org.luwrain.i18n.I18nExtension i18nExt)
    {
    }

    @Override public org.luwrain.cpanel.Factory[] getControlPanelFactories(Luwrain luwrain)
    {
	return new org.luwrain.cpanel.Factory[0];
    } 

    @Override public UniRefProc[] getUniRefProcs(Luwrain luwrain)
    {
	return new UniRefProc[0];
    }
}
