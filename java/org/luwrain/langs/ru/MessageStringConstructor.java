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

public class MessageStringConstructor implements org.luwrain.app.message.StringConstructor
{
    @Override public String appName()
    {
	return "Новое сообщение";
    }

    @Override public String noMailStoring()
    {
	return "Нет подключения к хранилищу электронной почты";
    }

    @Override public String withoutSubject()
    {
	return "(Нет темы)";
    }

    @Override public String emptyRecipient()
    {
	return "Необходимо указать адрес получателя";
    }

    @Override public String errorSendingMessage()
    {
	return "Произошла ошибка во время отправления сообщения";
    }
}
