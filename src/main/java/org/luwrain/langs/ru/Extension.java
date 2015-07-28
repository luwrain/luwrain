/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

public class Extension extends org.luwrain.core.extensions.EmptyExtension
{
    @Override public void i18nExtension(Luwrain luwrain, I18nExtension i18nExt)
    {
	i18nExt.addLang("ru", new org.luwrain.langs.ru.Lang());
	i18nExt.addCommandTitle("ru", "quit", "Завершить работу в Luwrain");
	i18nExt.addCommandTitle("ru", "shutdown", "Выключить компьютер");

	i18nExt.addCommandTitle("ru", "control-panel", "Панель управления");
	i18nExt.addCommandTitle("en", "control-panel", "Control panel");

	i18nExt.addStrings("ru", "main-menu", new MainMenu());
	i18nExt.addStrings("ru", "luwrain.environment", new Environment());
	i18nExt.addStrings("ru", org.luwrain.app.cpanel.ControlPanelApp.STRINGS_NAME, new Control());
    }
}
