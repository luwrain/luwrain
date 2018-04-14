/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

//LWR_API 1.0

package org.luwrain.core.script;

import org.luwrain.base.*;
import org.luwrain.core.*;

public class ScriptExtension implements org.luwrain.core.extensions.DynamicExtension
{
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

        @Override public ExtensionObject[] getExtObjects(Luwrain luwrain)
    {
	return new ExtensionObject[0];
    }

    @Override public Command[] getCommands(Luwrain luwrain)
    {
	return new Command[0];
    }

    @Override public Shortcut[] getShortcuts(Luwrain luwrain)
    {
	return new Shortcut[0];
    }

    @Override public void i18nExtension(Luwrain luwrain, I18nExtension i18nExt)
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
