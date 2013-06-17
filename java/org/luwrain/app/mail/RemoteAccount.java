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

package org.luwrain.app.mail;

import javax.mail.*;
import com.sun.mail.pop3.*;

public class RemoteAccount implements MailGroup
{
    private Session session;
    private Store store;
    private Folder rootFolder;
    private RemoteAccountFolder childFolders[];
    private RootMailGroup rootMailGroup;
    private String name;

    public RemoteAccount(Session session,
			 RootMailGroup rootMailGroup)
    {
	this .session = session;
	this.rootMailGroup = rootMailGroup;
    }

    public void open() throws MessagingException
    {
	name = "tomsknet";
	URLName url = new URLName("pop3", "mail.tomsknet.ru", 110, "", "login", "passwd");
	store = new POP3Store(session, url);
	store.connect();
	rootFolder = store.getDefaultFolder();
	Folder[] children = rootFolder.list();
	if (children == null || children.length < 1)
	{
	    childFolders = null;
	    return;
	}
	childFolders = new RemoteAccountFolder[children.length];
	for(int i = 0;i < children.length;i++)
	    childFolders[i] = createRemoteAccountFolder(children[i]);
    }

    private RemoteAccountFolder createRemoteAccountFolder(Folder folder) throws MessagingException
    {
	if (folder == null)
	    return null;
	try {
	    folder.open(Folder.READ_WRITE);
	}
	catch (MessagingException e)
	{
	    folder.open(Folder.READ_ONLY);
	}
	if ((folder.getType() & Folder.HOLDS_FOLDERS) == 0)
	    return new RemoteAccountFolder(this, folder, false);
	Folder[] children = folder.list();
	return new RemoteAccountFolder(this, folder, children != null && children.length > 0);
    }

    //MailGroup interface;

    public String getName()
    {
	if (name == null)
	    return new String();
	return name;
    }

    public boolean hasChildFolders()
    {
	return childFolders != null && childFolders.length > 0;
    }

    public MailGroup[] getChildGroups()
    {
	return childFolders;
    }

    public Message[] getMessages()
    {
	return new Message[0];
    }

    public MailGroup getParentGroup()
    {
	return rootMailGroup;
    }

    public String toString()
    {
	return getName();
    }
}
