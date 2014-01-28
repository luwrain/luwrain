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

import java.sql.Connection;
import java.sql.SQLException;

public class StoredMailAccountSql implements StoredMailAccount
{
    public Connection con;

    public long id = 0;

    public String name = new String();
    public String protocol = new String();
    public String host = new String();
    public int port = 110;
    public String file = new String();
    public String login = new String();
    public String passwd = new String();
    public String extInfo = new String();

    public StoredMailAccountSql(Connection con)
    {
	this.con = con;
    }

    public String getName()
    {
	return name;
    }

    public void setName(String value) throws SQLException
    {
	//FIXME:
    }

    public String getProtocol()
    {
	return protocol;
    }

    public void setProtocol(String value) throws SQLException
    {
	//FIXME:
    }

    public String getHost()
    {
	return host;
    }

    public void setHost(String value) throws SQLException
    {
	//FIXME:
    }

    public int getPort()
    {
	return port;
    }

    public void setPort(int value) throws SQLException
    {
	//FIXME:
    }

    public String getFile()
    {
	return file;
    }

    public void setFile(String value) throws SQLException
    {
	//FIXME:
    }

    public String getLogin()
    {
	return login;
    }

    public void setLogin(String value) throws SQLException
    {
	//FIXME:
    }

    public String getPasswd()
    {
	return passwd;
    }

    public void setPasswd(String value) throws SQLException
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
