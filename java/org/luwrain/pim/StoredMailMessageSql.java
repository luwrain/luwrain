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

package org.luwrain.pim;

import java.sql.Connection;
import java.sql.SQLException;

public class StoredMailMessageSql implements StoredMailMessage
{
    private Connection con;

    public long id;
    public long groupId;
    public int state = MailMessage.READ;
    public String fromAddr = new String();
    public String[] fromAddrs = new String[0];
    public String toAddr = new String();
    public String[] toAddrs = new String[0];
    public String subject = new String();
    public java.util.Date date = new java.util.Date();
    public String rawMsg = new String();
    public String content = new String();
    public String extInfo = new String();

    public StoredMailMessageSql(Connection con)
    {
	this.con = con;
    }

    public int getState()
    {
	return state;
    }

    public void setState(int value) throws SQLException
    {
	//FIXME:
    }

    public String getFromAddr()
    {
	return fromAddr;
    }

    public void setFromAddr(String value)  throws SQLException
    {
	//FIXME:
    }

    public String[] getFromAddrs()
    {
	return fromAddrs;
    }

    public void setFromAddrs(String[] values)  throws SQLException
    {
	//FIXME:
    }

    public String getToAddr()
    {
	return toAddr;
    }

    public void setToAddr(String value)  throws SQLException
    {
	//FIXME:
    }

    public String[] getToAddrs()
    {
	return toAddrs;
    }

    public void setToAddrs(String[] values)  throws SQLException
    {
	//FIXME:
    }

    public String getSubject()
    {
	return subject;
    }

    public void setSubject(String value)  throws SQLException
    {
	//FIXME:
    }

    public java.util.Date getDate()
    {
	return date;
    }

    public void setDate(java.util.Date value)  throws SQLException
    {
	//FIXME:
    }

    public String getRawMsg()
    {
	return rawMsg;
    }

    public void setRawMsg(String value)  throws SQLException
    {
	//FIXME:
    }

    public String getContent()
    {
	return content;
    }

    public void setContent(String value)  throws SQLException
    {
	//FIXME:
    }

    public String getExtInfo()
    {
	return extInfo;
    }

    public void setExtInfo(String value) throws SQLException
    {
	//FIXME:
    }
}
