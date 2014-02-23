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

import org.luwrain.core.Langs;

public class LanguageStaticStrings implements org.luwrain.core.LanguageStaticStrings
{
    public String getString(int id)
    {
	switch (id)
	{
	case Langs.SPACE:
	    return "Пробел";
	case Langs.TAB:
	    return "Таб";
	case Langs.BEGIN_OF_LINE:
	    return "Начало строки";
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
	case Langs.TREE_AREA_BEGIN:
	    return "Начало просмотра дерева";
	case Langs.TREE_AREA_END:
	    return "Конец просмотра дерева";
	case Langs.BEGIN_OF_LIST:
	    return "Начало списка";
	case Langs.END_OF_LIST:
	    return "Конец списка";
	case Langs.EMPTY_TREE:
	    return "Пустое дерево";
	case Langs.EMPTY_TREE_ITEM:
	    return "Пустой элемент дерева";
	case Langs.TREE_EXPANDED:
	    return "Раскрыто";
	case Langs.TREE_COLLAPSED:
	    return "Свёрнуто";
	case Langs.TREE_LEVEL:
	    return "Уровень";

	case Langs.NO_REQUESTED_ACTION:
	    return "Выбранное Вами действие недоступно в системе";
	case Langs.NO_ACTIVE_AREA:
	    return "Нет активного объекта";
	case Langs.APPLICATION_INTERNAL_ERROR:
	    return "Выполнение операции было прервано из-за внутренней ошибки приложения";
	case Langs.APPLICATION_CLOSE_ERROR_HAS_POPUP:
	    return "Перед закрытием приложения необходимо закрыть его всплывающие окна";
	case Langs.INSUFFICIENT_MEMORY_FOR_APP_LAUNCH:
	    return "Недостаточно памяти для запуска приложения";
	case Langs.UNEXPECTED_ERROR_AT_APP_LAUNCH:
	    return "Запуск прерван из-за внутренней ошибки приложения";
	case Langs.START_WORK_FROM_MAIN_MENU:
	    return "Начните работу с главного меню";
	case Langs.NO_LAUNCHED_APPS:
	    return "Все приложения закрыты";
	case Langs.LIST_NO_ITEMS:
	    return "Элементы в списке отсутствуют";
	case Langs.FONT_SIZE:
	    return "Размер шрифта:";
	case Langs.QUIT_CONFIRM_NAME:
	    return "Завершение работы";
	case Langs.QUIT_CONFIRM:
	    return "Вы действительно хотите завершить работу в Luwrain?";
	case Langs.OPEN_POPUP_NAME:
	    return "Открытие файла";
	case Langs.OPEN_POPUP_PREFIX:
	    return "Введите имя файла для открытия:";
	default:
	    return "#Неизвестный идентификатор строки?#";
	}
    }
}
