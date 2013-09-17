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

package org.luwrain.langs.en;

import java.util.*;

public class MailReaderStringConstructor implements org.luwrain.app.mail.MailReaderStringConstructor
{
    public String groupAreaName()
    {
	return "Mail group list";
    }

    public String summaryAreaName()
    {
	return "Message list";
    }

    public String messageAreaName()
    {
	return "Message preview";
    }

    public String mailGroupsRoot()
    {
	return "Mail groups";
    }

    public String readPrefix()
    {
	return "Read";
    }

    public String markedPrefix()
    {
	return "Marked";
    }

    public String emptySummaryArea()
    {
	return "No messages in list";
    }

    public String firstSummaryLine()
    {
	return "First summary line";
    }

    public String lastSummaryLine()
    {
	return "Last summary list";
    }
}
