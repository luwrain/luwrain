/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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
import org.luwrain.core.Langs;
import org.luwrain.app.commander.DirItem;
import org.luwrain.app.commander.PanelArea;

public class CommanderStringConstructor implements org.luwrain.app.commander.CommanderStringConstructor
{
    public String appName()
    {
	return "Обзор файлов и папок";
    }

    public String leftPanelName(String path)
    {
	return "Левая панель " + path;
    }

    public String rightPanelName(String path)
    {
	return "Правая панель " + path;
    }

    public String tasksAreaName()
    {
	return "Действия";
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

    public String dirItemIntroduction(DirItem item, boolean brief)
    {
	if (item == null)
	    return "";
	String text = item.getFileName();
	if (text.isEmpty())
	    return Langs.staticValue(Langs.EMPTY_LINE);
	if (text.equals(PanelArea.PARENT_DIR))
	    return "На уровень вверх";
	if (!brief)
	{
	    if (item.getType() == DirItem.DIRECTORY)
	    {
		if (item.isSelected())
		    text = "Выделенный каталог " + text; else
		    text = "Каталог " + text;
	    } else
		if (item.selected)
		    text = "Выделенный файл " + text;
	}
	return text;
    }
}
