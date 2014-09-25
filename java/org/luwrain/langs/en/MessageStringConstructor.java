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

package org.luwrain.langs.en;

import java.util.*;

public class MessageStringConstructor implements org.luwrain.app.message.StringConstructor
{
    @Override public String appName()
    {
	return "New message";
    }

    @Override public String noMailStoring()
    {
	return "No mail storing connection";
    }

    @Override public String withoutSubject()
    {
	return "(No subject)";
    }

    @Override public String emptyRecipient()
    {
	return "You should write the recipient address";
    }

    @Override public String errorSendingMessage()
    {
	return "An error occurred while sending message";
    }
}
