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

//FIXME:No longer static;

import java.sql.*;
import org.luwrain.core.Log;

public class PimManager
{
    private static Link newsLink;

    public static boolean newsConnectJdbc(String url,
					  String driver,
					  String login,
					  String passwd)
    {
	Link l = new Link();
	l.type = Link.STORAGE_JDBC;
	l.url = url;
	l.driver = driver;
	l.login = login;
	l.passwd = passwd;
	try {
	    l.jdbcConnect();
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    Log.error("pim", "news jdbc link failed:" + e.getMessage());
	    return false;
	}
	newsLink = l;
	return true;
    }

    public static NewsStoring createNewsStoring()
    {
	if (newsLink == null)
	    return null;
	if (newsLink.type == Link.STORAGE_JDBC)
	    return newsLink.jdbcCon != null?new NewsStoringSql(newsLink.jdbcCon):null;
	return null;
    }

    public static MailStoring createMailStoring()
    {
	return null;
    }
}
