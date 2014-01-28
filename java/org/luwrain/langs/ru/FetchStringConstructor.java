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

public class FetchStringConstructor implements org.luwrain.app.fetch.FetchStringConstructor
{
    public String appName()
    {
	return "Доставка почты и новостей";
    }

    public String noNewsGroupsData()
    {
	return "Отсутствует информация о новостных группах!";
    }

    public String newsFetchingCompleted()
    {
	return "Доставка новостей завершена!";
    }

    public String newsFetchingError()
    {
	return "Произошла ошибка доставки новостей!";
    }

    public String newsGroupFetched(String name, int fresh, int total)
    {
	return "Группа " + name + " содержит " + fresh + " новых статей из " + total;
    }

    public String pressEnterToStart()
{
    return "Нажмите ENTER для начала работы!";
	}

    public String processAlreadyRunning()
    {
	return "Доставка уже запущена";
    }

    public String processNotFinished()
    {
	return "Доставка ещё не завершена";
    }

    public String readingMailFromAccount(String accountName)
    {
	return "Чтение электронной почты с учётной записи \"" + accountName + "\"";
    }

    public String connecting(String host)
    {
	return "Подключение к серверу " + host;
    }

    public String readingMailInFolder(String folder)
    {
	return "Открытие каталога \"" + folder + "\"";
    }

    public String readingMessage(int msgNum, int totalCount)
    {
	return "Получение сообщения " + msgNum + " из " + totalCount;
    }

    public String noMail()
    {
	return "Нет новых сообщений";
    }
}
