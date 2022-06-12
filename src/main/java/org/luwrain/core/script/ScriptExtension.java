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

package org.luwrain.core.script;

import java.util.*;

import org.luwrain.core.*;

class ScriptExtension implements Extension, org.luwrain.core.HookContainer
{
    static private final String LOG_COMPONENT = "Core.LOG_COMPONENT";

    final String name;
    private Luwrain luwrain = null;

    ScriptExtension(String name)
    {
	NullCheck.notEmpty(name, "name");
	this.name = name;
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
	return false;
    }

    @Override public ExtensionObject[] getExtObjects(Luwrain luwrain)
    {
	final List<ExtensionObject> res = new ArrayList<>();
	return res.toArray(new ExtensionObject[res.size()]);
    }

    @Override public Command[] getCommands(Luwrain luwrain)
    {
	return new Command[0];
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
