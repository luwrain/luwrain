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

public class MailReaderStringConstructor implements org.luwrain.app.mail.MailReaderStringConstructor
{
    public String groupAreaName()
    {
	return "Список почтовых групп";
    }

    public String summaryAreaName()
    {
	return "Список сообщений";
    }

    public String messageAreaName()
    {
	return "Просмотр сообщения";
    }

    public String mailGroupsRoot()
    {
	return "Почтовые группы";
    }

    public String readPrefix()
    {
	return "прочитано";
    }

    public String markedPrefix()
    {
	return "помечено";
    }

    public String emptySummaryArea()
    {
	return "Сообщения в списке отсутствуют";
    }

    public String firstSummaryLine()
    {
	return "Первая строка списка сообщений";
    }

    public String lastSummaryLine()
    {
	return "Последняя строка списка сообщений";
    }

}
