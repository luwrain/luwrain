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

package org.luwrain.app.mail;

import javax.mail.Message;

public class EmptyMailGroup implements MailGroup
{
    private MailGroup parent;
    private String name;

    public EmptyMailGroup(MailGroup parent, String name)
    {
	this.parent = parent;
	this.name = name;
    }

    public String getName()
    {
	return name;
    }

    public boolean hasChildFolders()
    {
	return false;
    }

    public MailGroup[] getChildGroups()
    {
	return null;
    }

    public Message[] getMessages()
    {
	return new Message[0];
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
