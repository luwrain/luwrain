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

public class PimManager
{
    public static final int STORAGE_SQL = 1;
    public static final int STORAGE_LDAP = 2;

    public static int type = STORAGE_SQL;

    public static String login = new String();
    public static String passwd = new String();
    public static String driver = new String();
    public static String url = new String();

    private static UserDatabase database;

    public static NewsStoring createNewsStoring()
    {
	if (type == STORAGE_SQL)
	{
	    ensureDatabaseReady();
	    if (database == null)
		return null;
	    Connection con = database.getDefaultConnection();
	    if (con == null)
		return null;
	    return new NewsStoringSql(con);
	}
	//FIXME:LDAP;
	return null;
    }

    public static MailStoring createMailStoring()
    {
	if (type == STORAGE_SQL)
	{
	    ensureDatabaseReady();
	    if (database == null)
		return null;
	    Connection con = database.getDefaultConnection();
	    if (con == null)
		return null;
	    return new MailStoringSql(con);
	}
	//FIXME:LDAP;
	return null;
    }

    private static void ensureDatabaseReady()
    {
	if (database != null)
	    return;
	database = new UserDatabase(driver, url, login, passwd);
    }
}
