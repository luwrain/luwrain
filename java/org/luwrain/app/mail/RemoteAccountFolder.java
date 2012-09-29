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

package org.luwrain.app.mail;

import javax.mail.*;

public class RemoteAccountFolder implements MailGroup
{
    private MailGroup parent;
    private Folder folder;
    private boolean hasChildren = false;
    private RemoteAccountFolder childFolders[];

    public RemoteAccountFolder(MailGroup parent, Folder folder, boolean hasChildren)
    {
	this.parent = parent;
	this.folder = folder;
	this.hasChildren = hasChildren;
    }

    public Folder getFolder()
    {
	return folder;
    }

    private void ensureChildFoldersPrepared()
    {
	if (!hasChildren)
	{
	    childFolders = null;
	    return;
	}
	//FIXME:
    }

    public String getName()
    {
	if (folder == null)
	    return new String("NO_NAME:FIXME");
	return folder.getFullName();
    }

    public boolean hasChildFolders()
    {
	return hasChildren;
    }

    public MailGroup[] getChildGroups()
    {
	ensureChildFoldersPrepared();
	return childFolders;
    }

    public Message[] getMessages()
    {
	try {
	    if ((folder.getType() & Folder.HOLDS_MESSAGES) == 0)
		return new Message[0];//FIXME:
	    Message[] m = folder.getMessages();
	    System.out.println("Have " + m.length + " message");
	    return m;
	}
	catch (MessagingException e)
	{
	    e.printStackTrace();//FIXME:
	    return new Message[0];//FIXME:Something to indicate an error;
	}
    }

    public MailGroup getParentGroup()
    {
	return parent;
    }

    public String toString()
    {
	return getName();
    }
}
