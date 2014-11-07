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

public class SystemAppStringConstructor implements org.luwrain.core.sysapp.StringConstructor
{
    private Language lang;

    public SystemAppStringConstructor(Language lang)
    {
	this.lang = lang;
    }

    public String mainMenuTitle()
    {
	return "Главное меню";
    }

    public String runActionTitle()
    {
	return "Выполнить команду";
    }

    public String runAction()
    {
	return "Команда:";
    }


    public String actionTitle(String action)
    {
	return action != null?lang.getActionTitle(action):"";
    }

    public String currentDateTime()
    {
	Calendar c = new GregorianCalendar();
	String value = "";
	value += withZeroes(c.get(Calendar.HOUR_OF_DAY), 2);
	value += ":";
	value += withZeroes(c.get(Calendar.MINUTE), 2);
	value += ", ";

	value += dayOfWeek(c.get(Calendar.DAY_OF_WEEK));
	value += ",";
	value += c.get(Calendar.DAY_OF_MONTH);
	value += " ";
	value += month(c.get(Calendar.MONTH));
	return value;
    }

    public String mainMenuNoItemsAbove()
    {
	return "Начало главного меню";
    }

    public String mainMenuNoItemsBelow()
    {
	return "Конец главного меню";
    }




    private String dayOfWeek(int index)
    {
	switch(index)
	{
	case 1:
	    return "Воскресенье";
	case 2:
	    return "Понедельник";
	case 3:
	    return "Вторник";
	case 4:
	    return "Среда";
	case 5:
	    return "Четверг";
	case 6:
	    return "Пятница";
	case 7:
	    return "Суббота";
	}
	return null;
    }

    private String month(int index)
    {
	switch(index)
	{
	case 0:
	    return "января";
	case 1:
	    return "февраля";
	case 2:
	    return "марта";
	case 3:
	    return "апреля";
	case 4:
	    return "мая";
	case 5:
	    return "июня";
	case 6:
	    return "июля";
	case 7:
	    return "августа";
	case 8:
	    return "сентября";
	case 9:
	    return "октября";
	case 10:
	    return "ноября";
	case 11:
	    return "декабря";
	}
	return null;
    }

    private String withZeroes(int value, int len)
    {
	String s = "";
	s += value;
	while(s.length() < len)
	    s = "0" + s;
	return s;
    }
}
