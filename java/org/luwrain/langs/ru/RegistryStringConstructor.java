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

public class RegistryStringConstructor implements org.luwrain.app.registry.StringConstructor
{
    public String dirsAreaName()
    {
	return "Дерево каталогов";
    }

    public String valuesAreaName()
    {
	return "Список параметров";
    }

    public String rootItemTitle()
    {
	return "Реестр Luwrain";
    }

    public String introduceStringValue(String name, String value)
    {
	return "Строковый параметр " + name + " равен " + value;
    }

    public String introduceIntegerValue(String name, String value)
    {
	return "Целочисленный параметр " + name + " равен " + value;
    }

    public String introduceBooleanValue(String name, boolean value)
    {
	return "Булевый параметр " + name + " равен " + (value?"да":"нет");
    }

    public String yes()
    {
	return "Да";
    }

    public String no()
    {
	return "Нет";
    }
}
