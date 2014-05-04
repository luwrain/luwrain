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

public class FetchStringConstructor implements org.luwrain.app.fetch.StringConstructor
{
    public String appName()
    {
	return "Доставка почты и новостей";
    }

    public String noNewsGroupsData()
    {
	return "Отсутствует информация о новостных группах!";
    }

    public String fetchingCompleted()
    {
	return "Доставка электронной почты и новостей завершена!";
    }

    public String newsGroupsError()
    {
	return "Произошла ошибка при доставке информации о новостных группах, доставка новостей пропущена";
    }

    public String noNewsGroups()
    {
	return "Нет групп новостей, доставка новостей пропущена";
    }

    public String newsFetchingError(String groupName)
    {
	return "Произошла ошибка доставки новостей для группы \"" + groupName + "\", группа пропущена";
    }

    public String newsGroupFetched(String name, int fresh, int total)
    {
	return"" + fresh + " " + NumberUtil.chooseNumberDependentForm(fresh, "новая статья", "новых статьи", "новых статей") +
	"в группе " + name;
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

    public String mailErrorWithAccount(String accountName)
    {
	return "Произошла ошибка при доставке почты с учётной записи \"" + accountName + "\", учётная запись пропущена";
    }

    public String connecting(String host)
    {
	return "Подключение к серверу " + host;
    }

    public String noMailAccounts()
    {
	return "Почтовых учётных записей нет, чтение электронной почты пропущено";
    }

    public String noMailStoring()
    {
	return "Нет соединения для хранения электронной почты, чтение почты пропущено";
    }

    public String mailAccountsProblem()
    {
	return "Произошла ошибка при получении списка учётных записей электронной почты, чтение почты пропущено";
    }

    /*
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
*/

    public String fetchedMailMessages(int count)
    {
	return "Получено сообщений: " + count;
    }
}
