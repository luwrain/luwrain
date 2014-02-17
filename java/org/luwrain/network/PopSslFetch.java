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

package org.luwrain.network;

import java.io.*;
import java.util.*;
import com.sun.mail.pop3.POP3SSLStore;
import javax.mail.*;
import javax.mail.internet.*;
import org.luwrain.pim.MailMessage;

public class PopSslFetch
{
    private Session session;
    private Store store;
    private Folder folder;
    private MailConsumer consumer;

    public PopSslFetch(MailConsumer consumer)
    {
	this.consumer = consumer;
    }

    public void fetch(String host,
		      int port,
		      String login,
			 String passwd) throws Exception
    {
	try {
	    connect(host, port, login, passwd);
	    openFolder("Inbox");
	    MailProcessor proc = new MailProcessor(session);
	    Message[] messages = folder.getMessages();
	    FetchProfile fp = new FetchProfile();
	    fp.add(FetchProfile.Item.ENVELOPE);
	    //	folder.fetch(messages, fp);
	for(Message m: messages)
	    if (m instanceof MimeMessage)
		consumer.onMessage(proc.constructMailMessage((MimeMessage)m));
	}
	finally
	{
	    closeFolder();
	    disconnect();
	}
    }

    private void connect(String host,
			int port,
			String login,
			String passwd) throws Exception
    {
	String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	Properties pop3Props = new Properties();
	pop3Props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
	pop3Props.setProperty("mail.pop3.socketFactory.fallback", "false");
	pop3Props.setProperty("mail.pop3.port", "" + port);
	pop3Props.setProperty("mail.pop3.socketFactory.port", "" + port);
	URLName url = new URLName("pop3", host, port, "", login, passwd);
	session = Session.getInstance(pop3Props, null);
	store = new POP3SSLStore(session, url);
	store.connect();
    }

    private void disconnect() throws Exception
    {
	if (store == null)
	    return;
	store.close();
	store = null;
    }

    private void openFolder(String folderName) throws Exception
    {
	folder = store.getDefaultFolder();
	folder = folder.getFolder(folderName);
	if (folder == null)
	    throw new Exception("Invalid folder");
	try {
	    folder.open(Folder.READ_WRITE);
	}
	catch (MessagingException e)
	{
	    folder.open(Folder.READ_ONLY);
	}
    }

    private void closeFolder() throws Exception
    {                                                                                                     
	if (folder == null)
	    return;
	folder.close(false);
	folder = null;
    }
}
