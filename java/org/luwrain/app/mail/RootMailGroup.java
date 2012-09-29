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

import javax.mail.Message;

public class RootMailGroup implements MailGroup
{
    private MailReaderStringConstructor stringConstructor;
    private MailGroup childGroups[];

    public RootMailGroup(MailReaderStringConstructor stringConstructor)
    {
	this.stringConstructor = stringConstructor;
    }

public RootMailGroup(MailReaderStringConstructor stringConstructor, MailGroup childGroups[])
    {
	this.stringConstructor = stringConstructor;
	this.childGroups = childGroups;
    }

public void setChildGroups(MailGroup childGroups[])
{
    this.childGroups = childGroups;
}

    public String getName()
    {
	return stringConstructor.mailGroupsRoot();
    }

    public String toString()
    {
	return getName();
    }

    public boolean hasChildFolders()
    {
	return childGroups != null && childGroups.length > 0;
    }

    public MailGroup[] getChildGroups()
    {
	return childGroups;
    }

    public Message[] getMessages()
    {
	return new Message[0];
    }

    public MailGroup getParentGroup()
    {
	return null;
    }
}
