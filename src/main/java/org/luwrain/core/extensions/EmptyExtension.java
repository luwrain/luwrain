/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core.extensions;

import org.luwrain.core.*;

public class EmptyExtension implements Extension
{
    @Override public String init(Luwrain luwrain)
    {
	return null;
    }

    @Override public Command[] getCommands(Luwrain luwrain)
    {
	return new Command[0];
    }

    @Override public Shortcut[] getShortcuts(Luwrain luwrain)
    {
	return new Shortcut[0];
    }

    @Override public Worker[] getWorkers(Luwrain luwrain)
    {
	return new Worker[0];
    }

    @Override public SharedObject[] getSharedObjects(Luwrain luwrain)
    {
	return new SharedObject[0];
    }

    @Override public void i18nExtension(Luwrain luwrain, I18nExtension i18nExt)
    {
    }

    @Override public org.luwrain.mainmenu.Item[] getMainMenuItems(Luwrain luwrain)
    {
	return new org.luwrain.mainmenu.Item[0];
    }

    @Override public org.luwrain.cpanel.Section[] getControlPanelSections(Luwrain luwrain)
    {
	return new org.luwrain.cpanel.Section[0];
    } 

    @Override public UniRefProc[] getUniRefProcs(Luwrain luwrain)
    {
	return new UniRefProc[0];
    }

    @Override public void close()
    {
    }
}
