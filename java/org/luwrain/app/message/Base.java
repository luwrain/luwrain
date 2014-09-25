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

package org.luwrain.app.message;

import org.luwrain.core.*;
import org.luwrain.pim.*;
import org.luwrain.core.registry.Registry;

class Base
{
    private Registry registry;
    private MailStoring mailStoring;
    private StringConstructor stringConstructor;

    public void init(StringConstructor stringConstructor)
    {
	registry = Luwrain.getRegistry();
	mailStoring = Luwrain.getPimManager().getMailStoring();
	this.stringConstructor = stringConstructor;
    }

    public boolean isValid()
    {
	return mailStoring != null;
    }

    public boolean send(String to,
			String cc,
			String subject,
			String text,
			String[] attachments)
    {
	if (to == null || to.trim().isEmpty())
	    return false;
	try {
	    MailRegistryValues mailRegistryValues = new MailRegistryValues(registry);
	    MailMessage message = new MailMessage();
	    message.toAddr = to.trim();
	    //FIXME:cc;
	    message.subject = subject != null?subject:"";
	    message.contentText = text != null?text:"";
	    if (message.subject.trim().isEmpty())
		message.subject = stringConstructor.withoutSubject(); 
	    final String outgoingGroupUri = mailRegistryValues.getOutgoingGroupUri();
	    if (outgoingGroupUri == null || outgoingGroupUri.trim().isEmpty())
	    {
		Log.error("message", "no outgoing group URI");
		return false;
	    }
	    StoredMailGroup outgoingGroup = mailStoring.loadGroupByUri(outgoingGroupUri);
	    if (outgoingGroup == null)
	    {
		Log.error("message", "outgoing mail group doesn\'t exist, its URI is " + outgoingGroupUri);
		return false;
	    }
	    mailStoring.saveMessageInGroup(outgoingGroup, message);
	}
	catch (Exception e)
	{
	    Log.error("message", "constructed and sending a message:" + e.getMessage());
	    e.printStackTrace();
	    return false; 
	}
	return true;
    }
}
