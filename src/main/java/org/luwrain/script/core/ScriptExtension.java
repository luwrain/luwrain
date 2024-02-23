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

package org.luwrain.script.core;

//import java.util.*;

import org.luwrain.core.*;

import static org.luwrain.core.NullCheck.*;

public final class ScriptExtension implements Extension, org.luwrain.core.HookContainer
{
    public final String name;
    private ScriptCore scriptCore = null;
    private Luwrain luwrain = null;

    public ScriptExtension(String name)
    {
	notEmpty(name, "name");
	this.name = name;
    }

    @Override public String init(Luwrain luwrain)
    {
	notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	this.scriptCore = new ScriptCore(luwrain);
	return null;
    }

    public Luwrain getLuwrainObj()
    {
	return this.luwrain;
    }

    public ScriptCore getScriptCore()
    {
	return scriptCore;
    }

            @Override public void close()
    {
    }

    @Override public boolean runHooks(String hookName, Luwrain.HookRunner runner)
    {
	notEmpty(hookName, "hookName");
	notNull(runner, "runner");
	return scriptCore.runHooks(hookName, runner);
    }

    @Override public ExtensionObject[] getExtObjects(Luwrain luwrain)
    {
	return scriptCore.getExtObjects();
    }

    @Override public Command[] getCommands(Luwrain luwrain)
    {
	return scriptCore.getCommands();
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
