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

public class UserDatabase
{
    private Connection userDb = null;

    public void connect(String driver, String url, String login, String passwd) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
	if (userDb != null)
	    return;
	Class.forName (driver).newInstance ();
userDb = DriverManager.getConnection (url, login, passwd);
    }

    public Connection connection()
    {
	return userDb;
    }
}
