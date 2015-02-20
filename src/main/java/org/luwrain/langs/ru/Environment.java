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

class Environment implements org.luwrain.core.Strings
{
    @Override public String quitPopupName()
    {
	return "Завершение работы";
    }

    @Override public String quitPopupText()
    {
	return "Вы действительно хотите завершить работу в Luwrain?";
    }

    @Override public String appLaunchNoEnoughMemory()
    {
	return "Недостаточно памяти для запуска приложения";
    }

    @Override public String appLaunchUnexpectedError()
    {
	return "Запуск прерван из-за внутренней ошибки приложения";
    }

    @Override public String appCloseHasPopup()
    {
	return "Перед закрытием приложения необходимо закрыть его всплывающие окна";
    }

    @Override public String noCommand()
    {
	return "Выбранное Вами действие недоступно в системе";
    }

    @Override public String startWorkFromMainMenu()
    {
	return "Начните работу с главного меню";
    }

    @Override public String noLaunchedApps()
    {
	return "Все приложения закрыты";
    }

    @Override public String fontSize(int size)
    {
	return "Размер шрифта: " + size;
    }

    @Override public String openPopupName()
    {
	return "Открытие файла";
    }

    @Override public String openPopupPrefix()
    {
	return "Введите имя файла для открытия:";
    }

    @Override public String commandPopupName()
    {
	return "Выполнение команды";
    }

    @Override public String commandPopupPrefix()
    {
	return "Команда:";
    }

@Override public String appBlockedByPopup()
    {
	return "Приложение недоступно из-за открытой всплывающей области";
    }
}
