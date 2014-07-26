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


import java.io.*;
import java.util.*;
import org.luwrain.core.Langs;
import org.luwrain.app.commander.DirItem;
import org.luwrain.app.commander.PanelArea;

class CommanderStringConstructor implements org.luwrain.app.commander.StringConstructor
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

    public String done()
    {
	return "Завершено";
    }

    public String failed()
    {
	return "Ошибка";
    }

    public String copying(File[] files)
    {
	if (files == null)
	    return "";
	if (files.length == 1)
	    return "Копирование " + files[0].getName();
	return "Копирование " + files + " элемента(ов)";
    }

    public String copyPopupName()
    {
	return "Копирование";
    }

    public String copyPopupPrefix(File[] files)
    {
	if (files == null || files.length < 1)
	    return "";
	if (files.length == 1)
	    return "Копировать \"" + files[0].getName() + "\" в:";
	return "Копировать " + files.length + " элемента(ов) в:";
    }

    public String movePopupName()
    {
	return "Переместить/переименовать";
    }

    public String movePopupPrefix(File[] files)
    {
	if (files == null || files.length < 1)
	    return "";
	if (files.length == 1)
	    return "Переместить/переименовать \"" + files[0].getName() + "\" в:";
	return "Переместить " + files.length + " элемента(ов) в:";
    }

    public String mkdirPopupName()
    {
	return "Создание каталога";
    }

    public String mkdirPopupPrefix()
    {
	return "Имя нового каталога:";
    }

    public String delPopupName()
    {
	return "Удаление";
    }

    public String delPopupPrefix(File[] files)
    {
	if (files == null || files.length < 1)
	    return "";
	if (files.length == 1)
	{
	    if (files[0].isDirectory())
	    return "Вы действительно хотите удалить каталог \"" + files[0].getName() + "\"?";
	    return "Вы действительно хотите удалить файл \"" + files[0].getName() + "\"?";
	}
	return "Вы действительно хотите удалить " + files.length + " элемента(ов)?";
    }
}
