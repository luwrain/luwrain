/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.langs.ru;

import org.luwrain.core.*;

public class Extension implements org.luwrain.core.Extension
{
    @Override public String init(String[] cmdLine, org.luwrain.core.Registry registry)
    {
	return null;
    }

    @Override public Command[] getCommands(CommandEnvironment env)
    {
	return new Command[0];
    }

    @Override public Shortcut[] getShortcuts()
    {
	return new Shortcut[0];
    }

    @Override public Worker[] getWorkers()
    {
	return new Worker[0];
    }

    @Override public SharedObject[] getSharedObjects()
    {
	return new SharedObject[0];
    }

    @Override public void i18nExtension(I18nExtension i18nExt)
    {
	i18nExt.addLang("ru", new org.luwrain.langs.ru.Lang());
	i18nExt.addCommandTitle("ru", "quit", "Завершить работу в Luwrain");
	i18nExt.addStrings("ru", "main-menu", new MainMenu());
	i18nExt.addStrings("ru", "luwrain.environment", new Environment());
    }

    @Override public org.luwrain.mainmenu.Item[] getMainMenuItems(CommandEnvironment env)
    {
	return new org.luwrain.mainmenu.Item[0];
    }

    @Override public void close()
    {
	//Nothing here;
    }
}
