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

import javax.mail.*;
import com.sun.mail.pop3.*;
import org.luwrain.pim.MailMessage;
import org.luwrain.pim.MailStoring;
import org.luwrain.pim.StoredMailGroup;

public class MailFetching
{
    private Session session;
    private FetchProgressListener listener;
    private FetchStringConstructor stringConstructor;
    private boolean notifyFolders;
    private MailStoring mailStoring;
    private StoredMailGroup mailGroup;

    public MailFetching(Session session,
			FetchProgressListener listener,
			FetchStringConstructor stringConstructor,
			boolean notifyFolders,
			MailStoring mailStoring)
    {
	this.session = session;
	this.listener = listener;
	this.stringConstructor = stringConstructor;
	this.notifyFolders = notifyFolders;
	this.mailStoring = mailStoring;
	//FIXME:	this.mailGroup = mailGroup;
    }

    public void fetchPop3(URLName urlName) throws MessagingException, IllegalStateException
    {
	Store store = new POP3Store(session, urlName);
	listener.onProgressLine(stringConstructor.connecting(urlName.getHost()));
	store.connect();
	Folder defaultFolder = store.getDefaultFolder();
	processFolder(defaultFolder, false);
    }

    private void processFolder(Folder folder, boolean shouldOpenClose) throws MessagingException, IllegalStateException
    {
	if (folder == null)
	    return;
	if (shouldOpenClose)
	{
	    try {
		folder.open(Folder.READ_WRITE);
	    }
	    catch (MessagingException e)
	    {
		folder.open(Folder.READ_ONLY);
	    }
	}
	if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0)
	{
	    if (notifyFolders)
		listener.onProgressLine(stringConstructor.readingMailInFolder(folder.getName()));
	    int msgCount = folder.getMessageCount();
	    if (msgCount > 0)
	    {
		for(int i = 0;i < msgCount;i++)
		{
		    listener.onProgressLine(stringConstructor.readingMessage(i + 1, msgCount));
		    Message msg = folder.getMessage(i + 1);
		    processMessage(msg);
		}
	    } else
		listener.onProgressLine(stringConstructor.noMail());
	}
	if ((folder.getType() & Folder.HOLDS_FOLDERS) != 0)
	{
	    Folder[] children = folder.list();
	    if (children != null)
		for(int i = 0;i < children.length;i++)
		    processFolder(children[i], true);
	}
	if (shouldOpenClose)
	    folder.close(false);
    }

    private void processMessage(Message msg) throws MessagingException
    {
	org.luwrain.network.MailUtils.constructMailMessage((javax.mail.internet.MimeMessage)msg);
    }
}
