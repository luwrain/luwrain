/*
   Copyright 2012 Michael Pozhidaev <msp@altlinux.org>

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

package com.marigostra.luwrain.app.mail;

import javax.mail.*;

public class RemoteAccount
{
    private Session session;
    private Store store;
    private Folder rootFolder;

    RemoteAccount(Session session)
    {
	this .session = session;
    }

    public void open()
    {
	/*FIXME:
	URLName url = new URLName("pop3", "pop.gmail.com", 995, "", username, password);
	store = new POP3SSLStore(session, url);
	store.connect();
	rootFolder = store.getDefaultFolder();
	*/
    }

    /*FIXME:
    public Folder openFolder(Folder folder, String folderName) throws MessagingException
    {
	Folder f = folder.getFolder(folderName);
	if (folder == null)
	    return null;
	try {
	    f.open(Folder.READ_WRITE);
	}
	catch (MessagingException e)
	{
	    f.open(Folder.READ_ONLY);
	}
    }
    */
}
