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

public class Language implements org.luwrain.core.Language
{
    private LanguageStaticStrings staticStrings = new LanguageStaticStrings();

    public LanguageStaticStrings getStaticStrings()
    {
	return staticStrings;
    }

    public Object requestStringConstructor(String id)
    {
	if (id.equals("system-application"))
	    return new SystemAppStringConstructor(this);
	if (id.equals("news-reader"))
	    return new NewsReaderStringConstructor();
	if (id.equals("mail-reader"))
	    return new MailReaderStringConstructor();
	if (id.equals("message"))
	    return new MessageStringConstructor();
	if (id.equals("commander"))
	    return new CommanderStringConstructor();
	if (id.equals("notepad"))
	    return new NotepadStringConstructor();
	if (id.equals("fetch"))
	    return new FetchStringConstructor();
	if (id.equals("preview"))
	    return new PreviewStringConstructor();
	if (id.equals("control"))
	    return new ControlStringConstructor();
	return null;
    }

    public String getActionTitle(String actionName)
    {
	if (actionName == null)
	    return null;
	if (actionName.trim().equals("main-menu"))
	    return "Главное меню";
	if (actionName.trim().equals("quit"))
	    return "Завершить работу";
	if (actionName.trim().equals("ok"))
	    return "OK";
	if (actionName.trim().equals("cancel"))
	    return "Отмена";
	if (actionName.trim().equals("close"))
	    return "Закрыть";
	if (actionName.trim().equals("save"))
	    return "Сохранить";
	if (actionName.trim().equals("refresh"))
	    return "Обновить";
	if (actionName.trim().equals("describe"))
	    return "Описать";
	if (actionName.trim().equals("help"))
	    return "Помощь";
	if (actionName.trim().equals("switch-next-app"))
	    return "Перейти к следующему приложению";
	if (actionName.trim().equals("switch-next-area"))
	    return "Перейти к следующему окну";
	if (actionName.trim().equals("notepad"))
	    return "Блокнот";
	if (actionName.trim().equals("commander"))
	    return "Обзор файлов и папок";
	if (actionName.trim().equals("news"))
	    return "Новости";
	if (actionName.trim().equals("mail"))
	    return "Почта";
	if (actionName.trim().equals("fetch"))
	    return "Доставка сообщений и новостей";
	if (actionName.trim().equals("message"))
	    return "Новое сообщение";
	if (actionName.trim().equals("preview"))
	    return "Просмотр документов";
	if (actionName.trim().equals("control"))
	    return "Панель управления";
	return "";
    }
}
