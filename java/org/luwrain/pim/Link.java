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

import java.sql.*;

public class Link
{
    public static final int STORAGE_JDBC = 1;
    public static final int STORAGE_LDAP = 2;

    public int type = STORAGE_JDBC;
    public String url;
    public String login;
    public String passwd;
    public String driver;
    public Connection jdbcCon;

    public Connection jdbcConnect() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
	Class.forName (driver).newInstance ();
	jdbcCon = DriverManager.getConnection (url, login, passwd);
	return jdbcCon;
    }
}
