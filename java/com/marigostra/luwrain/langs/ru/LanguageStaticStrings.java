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

package com.marigostra.luwrain.langs.ru;

import com.marigostra.luwrain.core.Langs;

public class LanguageStaticStrings implements com.marigostra.luwrain.core.LanguageStaticStrings
{
    public String getString(int id)
    {
	switch (id)
	{
	case Langs.SPACE:
	    return "Пробел";
	case Langs.TAB:
	    return "Таб";
	case Langs.END_OF_LINE:
	    return "Конец строки";
	case Langs.EMPTY_LINE:
	    return "Пустая строка";
	case Langs.THE_FIRST_LINE:
	    return "Текст выше отсутствует";
	case Langs.THE_LAST_LINE:
	    return "Текст нниже отсутствует";
	case Langs.AREA_BEGIN:
	    return "Начало текста";
	case Langs.AREA_END:
	    return "Конец текста";

	case Langs.NO_REQUESTED_ACTION:
	    return "Выбранное Вами действие недоступно в системе";
	case Langs.NO_ACTIVE_AREA:
	    return "Нет активного объекта";
	case Langs.APPLICATION_INTERNAL_ERROR:
	    return "Выполнение операции было прервано из-за внутренней ошибки приложения";
	case Langs.APPLICATION_CLOSE_ERROR_HAS_POPUP:
	    return "Перед закрытием приложения необходимо закрыть его всплывающие окна";
	default:
	    return "#Неизвестный идентификатор строки?#";
	}
    }
}
