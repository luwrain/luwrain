/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

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
