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

import java.util.*;

public class NewsReaderStringConstructor implements org.luwrain.app.news.NewsReaderStringConstructor
{
    public String appName()
    {
	return "Чтение новостей";
    }

    public String groupAreaName()
    {
	return "Список групп новостей";
    }

    public String summaryAreaName()
    {
	return "Список новостей";
    }

    public String viewAreaName()
    {
	return "Просмотр новости";
    }

public     String errorReadingArticles()
    {
	return "Произошла ошибка доставки новостных статей.";
    }

    public String readPrefix()
    {
	return "прочитано";
    }

    public String markedPrefix()
    {
	return "помечено";
    }

    public String noSummaryItems()
    {
	return "Новостные статьи отсутствуют";
    }

    public String noSummaryItemsAbove()
    {
	return "Начало списка новостных статей";
    }

    public String noSummaryItemsBelow()
    {
	return "Конец списка новостных статей";
    }
}
