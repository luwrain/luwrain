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

package org.luwrain.app.fetch;

import org.luwrain.core.Log;
import org.luwrain.pim.*;
import org.luwrain.network.*;


class IncomingMailConsumer implements MailConsumer
{
    private  int count = 0;
    private MailStoring mailStoring;
    private StoredMailGroup mailGroup;

    public IncomingMailConsumer(MailStoring mailStoring, StoredMailGroup mailGroup)
    {
	this.mailStoring = mailStoring;
	this.mailGroup = mailGroup;
    }

    public void onMessage(MailMessage mailMessage)
    {
	if (mailMessage == null)
	    return;
	count++;
	try {
	    mailStoring.addMessageToGroup(mailGroup, mailMessage);
	}
	catch(Exception e)
	{
	    Log.error("fetch", "the problem while adding new message to mail group:" + e.getMessage());
	    e.printStackTrace();
	}
    }

    public int getCount()
    {
	return count;
    }
}
