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
import org.luwrain.pim.StoredMailGroup;
import org.luwrain.pim.MailStoring;

public class LocalGroup implements MailGroup
{
    private MailStoring mailStoring;
    private StoredMailGroup mailGroup;
    private MailGroup childGroups[];
    private MailGroup parent;
    private boolean opened = false;
    private boolean corrupted = false;

    public LocalGroup(MailGroup parent, MailStoring mailStoring, StoredMailGroup mailGroup)
    {
	this.parent = parent;
	this.mailStoring = mailStoring;
	this.mailGroup = mailGroup;
	this.childGroups = null;
	this.opened = false;
	this.corrupted = false;
    }

    public String getName()
    {
	if (mailGroup == null || mailGroup.getName() == null)
	    return new String();
	if (corrupted)
	    return mailGroup.getName() + " (corrupted)";//FIXME:
	return mailGroup.getName();
    }

    public boolean hasChildFolders()//FIXME:hasChildGroups()
    {
	if (!opened)
	    open();
	if (corrupted)
	    return false;
	if (childGroups == null || childGroups.length < 1)
	    return false;
	return true;
    }

    public MailGroup[] getChildGroups()
    {
	if (!opened)
	    open();
	if (corrupted)
	    return null;
	return childGroups;
    }

    public Message[] getMessages()
    {
	return new Message[0];
    }

    public MailGroup getParentGroup()
    {
	return parent;
    }

    private void open()
    {
	if (opened)
	    return;
	StoredMailGroup groups[];
	try {
	    groups = mailStoring.loadChildGroups(mailGroup);
	}
	catch(Exception e)
	{
	    //FIXME:Log report;
	    corrupted = true;
	    return;
	}
	if (groups == null)
	    return;
	childGroups = new MailGroup[groups.length];
	for(int i = 0;i < groups.length;i++)
	{
	    if (groups[i] == null)
	    {
		childGroups = null;
		return;
	    }
	    childGroups[i] = new LocalGroup(this, mailStoring, groups[i]);
	}
	opened = true;
    }

    public String toString()
    {
	return getName();
    }
}
