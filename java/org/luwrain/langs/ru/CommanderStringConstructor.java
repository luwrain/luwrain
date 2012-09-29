/*
   Copyright 2012 Michael Pozhidaev <msp@altlinux.org>

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

import java.util.*;

public class CommanderStringConstructor implements org.luwrain.app.commander.CommanderStringConstructor
{
    public String leftPanelName(String path)
    {
	return "Левая панель " + path;
    }

    public String rightPanelName(String path)
    {
	return "Правая панель " + path;
    }

    public String noItemsAbove()
    {
	return "Элементы выше отсутствуют";
    }

    public String noItemsBelow()
    {
	return "Элементы ниже отсутствуют";
    }

    public String inaccessibleDirectoryContent()
    {
	return "Содержимое каталога недоступно";
    }

    public String rootDirectory()
    {
	return "Корневой каталог";
    }
}
