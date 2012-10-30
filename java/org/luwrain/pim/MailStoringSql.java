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

package org.luwrain.pim;

import java.sql.*;
import java.util.*;

public class MailStoringSql implements MailStoring
{
    private Connection con = null;

    public MailStoringSql(Connection con)
    {
	this.con = con;
    }

    public StoredMailGroup loadRootGroup() throws SQLException
    {
	Statement st = con.createStatement();
	ResultSet rs = st.executeQuery("SELECT id,name,group_type,parent_group_id,order_index,expire_after_days,ext_info FROM mail_group WHERE id = parent_group_id;");
	if (!rs.next())
	    throw new SQLException("No root group");//FIXME:Not SQLException;
	StoredMailGroupSql group = new StoredMailGroupSql(con);
	group.id = rs.getLong(1);
	group.name = rs.getString(2);
	group.groupType = rs.getString(3);
	group.parentGroupId = rs.getLong(4);
	group.orderIndex = rs.getInt(5);
	group.expireAfterDays = rs.getInt(6);
	group.extInfo = rs.getString(7);
	return group;
    }

    public StoredMailGroup[] loadChildGroups(StoredMailGroup parentGroup) throws SQLException
    {
	if (parentGroup == null)
	    return null;
	StoredMailGroupSql g = (StoredMailGroupSql)parentGroup;
	PreparedStatement st = con.prepareStatement("SELECT id,name,group_type,parent_group_id,order_index,expire_after_days,ext_info FROM mail_group WHERE parent_group_id = ? AND id <> parent_group_id ORDER BY order_index;");
	st.setLong(1, g.id);
	ResultSet rs = st.executeQuery();
	Vector<StoredMailGroupSql> groups = new Vector<StoredMailGroupSql>();
	while (rs.next())
	{
	    StoredMailGroupSql group = new StoredMailGroupSql(con);
	    group.id = rs.getLong(1);
	    group.name = rs.getString(2);
	    group.groupType = rs.getString(3);
	    group.parentGroupId = rs.getLong(4);
	    group.orderIndex = rs.getInt(5);
	    group.expireAfterDays = rs.getInt(6);
	    group.extInfo = rs.getString(7);
	    groups.add(group);
	}
	StoredMailGroup res[] = new StoredMailGroup[groups.size()];
	Iterator<StoredMailGroupSql> it = groups.iterator();
	int k = 0;
	while(it.hasNext())
	    res[k++] = it.next();
	return res;
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
	StoredMailGroupSql group = (StoredMailGroupSql)mailGroup;
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
	StoredMailGroupSql group = (StoredMailGroupSql)mailGroup;
	PreparedStatement st = con.prepareStatement("INSERT INTO mail_message (mail_group_id,state,from_addr,to_addr,subject,msg_date,raw_msg,content,ext_info) VALUES (?,?,?,?,?,?,?,?,?);");
	st.setLong(1, group.id);
	st.setInt(2, message.state);
	st.setString(3, message.fromAddr);
	st.setString(4, message.toAddr);
	st.setString(5, message.subject);
	st.setDate(6, 	    new java.sql.Date (message.date.getTime()));
	st.setString(7, 	    message.rawMsg);
	st.setString(8, message.content);
	st.setString(9, message.extInfo);
	//FIXME:	saveFromAddrs(messagemessage.fromAddrs = loadFromAddrs(message.id);
	//FIXME:	    message.toAddrs = loadToAddrs(message.id);
	st.executeUpdate();
    }

    public StoredMailAccount[] loadMailAccounts()throws SQLException
    {
	Statement st = con.createStatement();
	ResultSet rs = st.executeQuery("SELECT id,name,protocol,host,port,file,login,passwd,ext_info FROM mail_account;");
	Vector<StoredMailAccountSql> accounts = new Vector<StoredMailAccountSql>();
	while(rs.next())
	{
	    StoredMailAccountSql account = new StoredMailAccountSql(con);
	    account.id = rs.getLong(1);
	    account.name = rs.getString(2).trim();
	    account.protocol = rs.getString(3).trim();
	    account.host = rs.getString(4).trim();
	    account.port = rs.getInt(5);
	    account.file = rs.getString(6);
	    account.login = rs.getString(7);
	    account.passwd = rs.getString(8);
	    account.extInfo = rs.getString(9);
	    accounts.add(account);
	}
	StoredMailAccount res[] = new StoredMailAccount[accounts.size()];
	Iterator<StoredMailAccountSql> it = accounts.iterator();
	int k = 0;
	while (it.hasNext())
	    res[k++] = it.next();
	return res;
    }

    public String getStringIdentOfGroup(StoredMailGroup mailGroup)
    {
	if (mailGroup == null)
	    return null;
	StoredMailGroupSql group = (StoredMailGroupSql)mailGroup;
	return "" + group.id;
    }

    public StoredMailGroup loadGroupByStringIdent(String ident) throws SQLException
    {
	PreparedStatement st = con.prepareStatement("SELECT id,name,group_type,parent_group_id,order_index,expire_after_days,ext_info FROM mail_group WHERE id = ?;");
	st.setString(1, ident);
	ResultSet rs = st.executeQuery();
	if (!rs.next())
	    throw new SQLException("No root group");//FIXME:Not SQLException;
	StoredMailGroupSql group = new StoredMailGroupSql(con);
	group.id = rs.getLong(1);
	group.name = rs.getString(2);
	group.groupType = rs.getString(3);
	group.parentGroupId = rs.getLong(4);
	group.orderIndex = rs.getInt(5);
	group.expireAfterDays = rs.getInt(6);
	group.extInfo = rs.getString(7);
	return group;
    }

}
