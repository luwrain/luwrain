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

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.pim.*;

class SummaryTableModel implements TableModel
{
    private MailStoring mailStoring;
    private StoredMailGroup mailGroup;
    private StoredMailMessage[] messages;//null value with existing group means invalid state, empty content should be a valid array with zero length;

    public SummaryTableModel(MailStoring mailStoring)
    {
	this.mailStoring = mailStoring;
    }

    public void setCurrentMailGroup(StoredMailGroup mailGroup)
    {
	this.mailGroup = mailGroup;
    }

    public boolean isValidState()
    {
	return mailGroup  == null || messages != null;
    }

    public int getRowCount()
    {
	return messages != null?messages.length:0;
    }

    public int getColCount()
    {
	return 3;
    }

    public Object getCell(int col, int row)
    {
	if (messages == null || row >= messages.length)
	    return null;
	return messages[row].getSubject();//FIXME:
    }

    public Object getRow(int index)
    {
	if (messages == null || index >= messages.length)
	    return null;
	return messages[index];
    }

    public Object getCol(int index)
    {
	return "Column";//FIXME:
    }

    public void refresh()
    {
	if (mailStoring == null || mailGroup == null)
	{
	    messages = null;
	    return;
	}
	try {
	    messages = mailStoring.loadMessagesFromGroup(mailGroup);
	}
	catch(Exception e)
	{
	    Log.error("mail", "loading messages from group" + mailGroup.getName() + ":" + e.getMessage());
	    e.printStackTrace();
	    messages = null;
	}
    }
}
