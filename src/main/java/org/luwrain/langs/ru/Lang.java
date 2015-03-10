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

import org.luwrain.core.LangStatic;

public class Lang extends org.luwrain.core.Lang
{
    @Override public String staticStr(int code)
    {
	switch (code)
	{
	case LangStatic.SPACE:
	    return "Пробел";
	case LangStatic.TAB:
	    return "Таб";
	case LangStatic.BEGIN_OF_LINE:
	    return "Начало строки";
	case LangStatic.END_OF_LINE:
	    return "Конец строки";
	case LangStatic.EMPTY_LINE:
	    return "Пустая строка";
	case LangStatic.NO_LINES_ABOVE:
	    return "Текст выше отсутствует";
	case LangStatic.NO_LINES_BELOW:
	    return "Текст нниже отсутствует";
	case LangStatic.BEGIN_OF_TEXT:
	    return "Начало текста";
	case LangStatic.END_OF_TEXT:
	    return "Конец текста";
	case LangStatic.TREE_AREA_BEGIN:
	    return "Начало просмотра дерева";
	case LangStatic.TREE_AREA_END:
	    return "Конец просмотра дерева";
	case LangStatic.NO_ITEMS_ABOVE:
	    return "Элементы выше отсутствуют";
	case LangStatic.NO_ITEMS_BELOW:
	    return "Элементы ниже отсутствуют";
	case LangStatic.EMPTY_TREE:
	    return "Пустое дерево";
	case LangStatic.EMPTY_TREE_ITEM:
	    return "Пустой элемент дерева";
	case LangStatic.TREE_EXPANDED:
	    return "Раскрыто";
	case LangStatic.TREE_COLLAPSED:
	    return "Свёрнуто";
	case LangStatic.TREE_LEVEL:
	    return "Уровень";

	    //	case LangStatic.NO_REQUESTED_ACTION:
	    //	    return "Выбранное Вами действие недоступно в системе";
	    //	case LangStatic.NO_ACTIVE_AREA:
	    //	    return "Нет активного объекта";
	    //	case LangStatic.APPLICATION_INTERNAL_ERROR:
	    //	    return "Выполнение операции было прервано из-за внутренней ошибки приложения";
	    //	case LangStatic.APPLICATION_CLOSE_ERROR_HAS_POPUP:
	    //	    return "Перед закрытием приложения необходимо закрыть его всплывающие окна";
	    //	case LangStatic.INSUFFICIENT_MEMORY_FOR_APP_LAUNCH:
	    //	    return "Недостаточно памяти для запуска приложения";
	    //	case LangStatic.UNEXPECTED_ERROR_AT_APP_LAUNCH:
	    //	    return "Запуск прерван из-за внутренней ошибки приложения";
	    //	case LangStatic.START_WORK_FROM_MAIN_MENU:
	    //	    return "Начните работу с главного меню";
	    //	case LangStatic.NO_LAUNCHED_APPS:
	    //	    return "Все приложения закрыты";
	case LangStatic.LIST_NO_CONTENT:
	    return "Элементы в списке отсутствуют";
	    //	case LangStatic.FONT_SIZE:
	    //	    return "Размер шрифта:";
	    //	case LangStatic.QUIT_CONFIRM_NAME:
	    //	    return "Завершение работы";
	    //	case LangStatic.QUIT_CONFIRM:
	    //	    return "Вы действительно хотите завершить работу в Luwrain?";
	    //	case LangStatic.OPEN_POPUP_NAME:
	    //	    return "Открытие файла";
	    //	case LangStatic.OPEN_POPUP_PREFIX:
	    //	    return "Введите имя файла для открытия:";
	case LangStatic.NO_TABLE_ROWS:
	    return "Строки в таблице отсутствуют"; 
	case LangStatic.NO_TABLE_ROWS_ABOVE:
	    return "Строки выше отсутствуют"; 
	case LangStatic.NO_TABLE_ROWS_BELOW:
	    return "Строки ниже отсутствуют";
	case LangStatic.END_OF_TABLE_COL:
	    return "Конец столбца";
	case LangStatic.MESSAGE:
	    return "Сообщение";
	case LangStatic.MESSAGE_TO:
	    return "Кому:";
	case LangStatic.MESSAGE_CC:
	    return "Копия:";
	case LangStatic.MESSAGE_SUBJECT:
	    return "Тема:";
	case LangStatic.MESSAGE_TEXT:
	    return "Введите ниже текст сообщения:";
	case LangStatic.MESSAGE_ATTACHMENT:
	    return "Прикрепление";
	case LangStatic.MESSAGE_ATTACHMENT_POPUP_TITLE:
	    return "Прикрепления файла";
	case LangStatic.MESSAGE_ATTACHMENT_POPUP_PREFIX:
	    return "Файл для прикрепления:";
	case LangStatic.COPIED_LINES:
	    return "Скопировано строк:";
	case LangStatic.CUT_LINES:
	    return "Вырезано строк:";
	case LangStatic.COMMANDER_NO_CONTENT:
	    return "Содержимое каталога недоступно";
	case LangStatic.COMMANDER_SELECTED_DIRECTORY:
	    return "выделенный каталог";
	case LangStatic.COMMANDER_SELECTED:
	    return "выделено";
	case LangStatic.COMMANDER_DIRECTORY:
	    return "Каталог";
	case LangStatic.COMMANDER_PARENT_DIRECTORY:
	    return "На уровень вверх";
	case LangStatic.COMMANDER_USER_HOME:
	    return "Домашний каталог";
	case LangStatic.POPUP_IMPORTANT_LOCATIONS_NAME:
	    return "Выберите местоположение";
	default:
	    return "#Неизвестный идентификатор строки?#";
	}
    }

    @Override public String hasSpecialNameOfChar(char ch)
    {
	if (Character.isDigit(ch) || Character.isLetter(ch))
	    return null;
	switch(ch)
	{
	case '—':
	    return "длинное тире";
	case '~':
	    return "тильда";
	case '`':
	    return "обратный апостроф";
	case '!':
	    return "восклицательный знак";
	case '@':
	    return "собачка";
	case '#':
	    return "диез";
	case '$':
	    return "доллар";
	case '%':
	    return "процет";
	case '^':
	    return "знак степени";
	case '&':
	    	    return "амперсант";
	case '*':
	    return "звезда";
	case '(':
	    return "левая круглая скобка";
	case ')':
	    return "правая круглая скобка";
	case '_':
	    return "знак подчёркивания";
	case '-':
	    return "дефис";
	case '+':
	    return "плюс";
	case '=':
	    return "равно";
	case '[':
	    return "левая квадратная скобка";
	case ']':
	    return "правая квадратная скобка";
	case '{':
	    return "левая фигурная скобка";
	case '}':
	    return "правая фигурная скобка";
	case ':':
    return "двоеточие";
	case ';':
	    return "точка с запятой";
	case '\\':
	    return "обратная наклонная черта";
	case '|':
	    return "вертикальная черта";
	case '\'':
	    return "апостроф";
	case '\"':
	    return "двойная кавычка";
	case '/':
	    return "прямая наклонная черта";
	case '?':
	    return "вопросительный знак";
	case '<':
	    return "меньше";
	case '>':
	    return "больше";
	case ',':
	    return "запятая";
	case '.':
	    return "точка";
	default:
	    return Character.getName(ch);
	}
    }
}
