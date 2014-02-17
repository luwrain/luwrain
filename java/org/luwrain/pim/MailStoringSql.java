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

package org.luwrain.pim;

import java.sql.*;
import java.util.*;
import org.luwrain.core.registry.Registry;

class MailStoringSql extends  MailStoringRegistry
{
    private Connection con;

    public MailStoringSql(Registry registry, Connection con)
    {
	super(registry);
	this.con = con;
    }

    private String[] loadFromAddrs(long id) throws SQLException
    {
	return new String[0];//FIXME:
    }

    private String[] loadToAddrs(long id) throws SQLException
    {
	return new String[0];//FIXME:
    }

    public StoredMailMessage[] loadMessagesFromGroup(StoredMailGroup mailGroup) throws SQLException
    {
	if (mailGroup == null)
	    return null;
	StoredMailGroupRegistry group = (StoredMailGroupRegistry)mailGroup;
	PreparedStatement st = con.prepareStatement("SELECT id,mail_group_id,state,from_addr,to_addr,subject,msg_date,raw_msg,content,ext_info FROM mail_message WHERE mail_group_id = ?;");
	st.setLong(1, group.id);
	ResultSet rs = st.executeQuery();
	Vector<StoredMailMessageSql> messages = new Vector<StoredMailMessageSql>();
	while(rs.next())
	{
	    StoredMailMessageSql message = new StoredMailMessageSql(con);
	    message.id = rs.getLong(1);
	    message.groupId = rs.getLong(2);
	    message.state = rs.getInt(3);
	    message.fromAddr = rs.getString(4).trim();
	    message.toAddr = rs.getString(5).trim();
	    message.subject = rs.getString(6).trim();
	    message.date = rs.getDate(7);
	    message.rawMsg = rs.getString(8);
	    message.content = rs.getString(9);
	    message.extInfo = rs.getString(10);
	    message.fromAddrs = loadFromAddrs(message.id);
	    message.toAddrs = loadToAddrs(message.id);
	    messages.add(message);
	}
	StoredMailMessage res[] = new StoredMailMessage[messages.size()];
	Iterator<StoredMailMessageSql> it = messages.iterator();
	int k = 0;
	while (it.hasNext())
	    res[k++] = it.next();
	return res;
    }

    public void addMessageToGroup(StoredMailGroup mailGroup, MailMessage message) throws SQLException
    {
	if (mailGroup == null || message == null)
	    return;
	StoredMailGroupRegistry group = (StoredMailGroupRegistry)mailGroup;
	PreparedStatement st = con.prepareStatement("INSERT INTO mail_message (mail_group_id,state,from_addr,to_addr,subject,msg_date,raw_msg,content,ext_info) VALUES (?,?,?,?,?,?,?,?,?);");
	st.setLong(1, group.id);
	st.setInt(2, message.state);
	st.setString(3, message.fromAddr);
	st.setString(4, message.toAddr);
	st.setString(5, message.subject);
	st.setDate(6, 	    new java.sql.Date (message.date.getTime()));
	st.setString(7, 	    message.rawMsg);
	st.setString(8, message.contentText);
	st.setString(9, message.extInfo);
	//FIXME:	saveFromAddrs(messagemessage.fromAddrs = loadFromAddrs(message.id);
	//FIXME:	    message.toAddrs = loadToAddrs(message.id);
	st.executeUpdate();
    }

    /*
    public String getStringIdentOfGroup(StoredMailGroup mailGroup)
    {
	if (mailGroup == null)
	    return null;
	StoredMailGroupRegistry group = (StoredMailGroupRegistry)mailGroup;
	return "" + group.id;
    }
    */
}
