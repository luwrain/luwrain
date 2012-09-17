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

package com.marigostra.luwrain.comm;

import java.sql.*;

public class PimStorage
{
    public static final int STORAGE_SQL = 1;
    public static final int STORAGE_LDAP = 2;

    public static int type = STORAGE_SQL;

    public static String login = new String();
    public static String passwd = new String();
    public static String driver = new String();
    public static String url = new String();

    private static UserDatabase userDatabase = null;

    public static void connect() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
	if (type == STORAGE_SQL)
	{
	    if (userDatabase != null)
		return;
	    userDatabase = new UserDatabase();
	    userDatabase.connect(driver, url, login, passwd);
	}
    }

    public static Connection sqlConnection()
    {
	if (userDatabase == null)
	    return null;
	return userDatabase.connection();
    }
}
