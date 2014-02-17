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

//FIXME:No longer static;

import java.sql.*;
import org.luwrain.core.Log;
import org.luwrain.core.registry.Registry;

public class PimManager
{
    public static final String MAIL_GROUP_URI_SCHEME = "mailgrp";
    private static final String NEWS_TYPE_PATH = "/org/luwrain/pim/news/storing/type";
    private static final String NEWS_URL_PATH = "/org/luwrain/pim/news/storing/url";
    private static final String NEWS_DRIVER_PATH = "/org/luwrain/pim/news/storing/driver";
    private static final String NEWS_LOGIN_PATH = "/org/luwrain/pim/news/storing/login";
    private static final String NEWS_PASSWD_PATH = "/org/luwrain/pim/news/storing/passwd";
    private static final String MAIL_TYPE_PATH = "/org/luwrain/pim/mail/storing/type";
    private static final String MAIL_URL_PATH = "/org/luwrain/pim/mail/storing/url";
    private static final String MAIL_DRIVER_PATH = "/org/luwrain/pim/mail/storing/driver";
    private static final String MAIL_LOGIN_PATH = "/org/luwrain/pim/mail/storing/login";
    private static final String MAIL_PASSWD_PATH = "/org/luwrain/pim/mail/storing/passwd";

    private Registry registry;
    private Connection newsJdbcCon, mailJdbcCon;

    public PimManager(Registry registry)
    {
	this.registry = registry;
    }

    public boolean initDefaultConnections()
    {
	boolean res = true;
	if (!initDefaultNewsCon())
	    res = false;
	if (!initDefaultMailCon())
	    res = false;
	return res;
    }

    private boolean initDefaultNewsCon()
    {
	if (registry.getTypeOf(NEWS_TYPE_PATH) != Registry.STRING)
	{
	    Log.warning("pim", "No value " + NEWS_TYPE_PATH + " needed for news storing, news service will be inaccessible");
	    return false;
	}
	if (registry.getTypeOf(NEWS_URL_PATH) != Registry.STRING)
	{
	    Log.warning("pim", "No value " + NEWS_URL_PATH + " needed for news storing, news service will be inaccessible");
	    return false;
	}
	if (registry.getTypeOf(NEWS_DRIVER_PATH) != Registry.STRING)
	{
	    Log.warning("pim", "No value " + NEWS_DRIVER_PATH + " needed for news storing, news service will be inaccessible");
	    return false;
	}
	if (registry.getTypeOf(NEWS_LOGIN_PATH) != Registry.STRING)
	{
	    Log.warning("pim", "No value " + NEWS_LOGIN_PATH + " needed for news storing, news service will be inaccessible");
	    return false;
	}
	if (registry.getTypeOf(NEWS_PASSWD_PATH) != Registry.STRING)
	{
	    Log.warning("pim", "No value " + NEWS_PASSWD_PATH + " needed for news storing, news service will be inaccessible");
	    return false;
	}
	final String type = registry.getString(NEWS_TYPE_PATH);
	final String url = registry.getString(NEWS_URL_PATH);
	final String driver = registry.getString(NEWS_DRIVER_PATH);
	final String login = registry.getString(NEWS_LOGIN_PATH);
	final String passwd = registry.getString(NEWS_PASSWD_PATH);
	if (!type.equals("jdbc"))
	{
	    Log.warning("pim", "only jdbc pim type for news is supported, news service will be inaccessible (\'" + type + "\' found)");
	    return false;
	}
	Log.debug("pim", "trying to get JDBC connection for news with following parameters:");
	Log.debug("pim", "type: " + type);
	Log.debug("pim", "url: " + url);
	Log.debug("pim", "driver: " + driver);
	Log.debug("pim", "login: " + login);
	Log.debug("pim", "passwd: " + passwd.length() + " characters");
	try {
	    Class.forName (driver).newInstance ();
	    newsJdbcCon = DriverManager.getConnection (url, login, passwd);
	}
	catch(Exception e)
	{
	    Log.error("pim", "news jdbc connection problem:" + e.getMessage());
	    return false;
	}
	Log.debug("pim", "jdbc connection for news is obtained");
	return true;
    }

    private boolean initDefaultMailCon()
    {
	if (registry.getTypeOf(MAIL_TYPE_PATH) != Registry.STRING)
	{
	    Log.warning("pim", "No value " + MAIL_TYPE_PATH + " needed for mail storing, mail service will be inaccessible");
	    return false;
	}
	if (registry.getTypeOf(MAIL_URL_PATH) != Registry.STRING)
	{
	    Log.warning("pim", "No value " + MAIL_URL_PATH + " needed for mail storing, mail service will be inaccessible");
	    return false;
	}
	if (registry.getTypeOf(MAIL_DRIVER_PATH) != Registry.STRING)
	{
	    Log.warning("pim", "No value " + MAIL_DRIVER_PATH + " needed for mail storing, mail service will be inaccessible");
	    return false;
	}
	if (registry.getTypeOf(MAIL_LOGIN_PATH) != Registry.STRING)
	{
	    Log.warning("pim", "No value " + MAIL_LOGIN_PATH + " needed for mail storing, mail service will be inaccessible");
	    return false;
	}
	if (registry.getTypeOf(MAIL_PASSWD_PATH) != Registry.STRING)
	{
	    Log.warning("pim", "No value " + MAIL_PASSWD_PATH + " needed for mail storing, mail service will be inaccessible");
	    return false;
	}
	final String type = registry.getString(MAIL_TYPE_PATH);
	final String url = registry.getString(MAIL_URL_PATH);
	final String driver = registry.getString(MAIL_DRIVER_PATH);
	final String login = registry.getString(MAIL_LOGIN_PATH);
	final String passwd = registry.getString(MAIL_PASSWD_PATH);
	if (!type.equals("jdbc"))
	{
	    Log.warning("pim", "only jdbc pim type for mail is supported, mail service will be inaccessible (\'" + type + "\' found)");
	    return false;
	}
	Log.debug("pim", "trying to get JDBC connection for mail with following parameters:");
	Log.debug("pim", "type: " + type);
	Log.debug("pim", "url: " + url);
	Log.debug("pim", "driver: " + driver);
	Log.debug("pim", "login: " + login);
	Log.debug("pim", "passwd: " + passwd.length() + " characters");
	try {
	    Class.forName (driver).newInstance ();
	    mailJdbcCon = DriverManager.getConnection (url, login, passwd);
	}
	catch(Exception e)
	{
	    Log.error("pim", "mail jdbc connection problem:" + e.getMessage());
	    return false;
	}
	Log.debug("pim", "jdbc connection for mail is obtained");
	return true;
    }

    public NewsStoring getNewsStoring()
    {
	return newsJdbcCon != null?new NewsStoringSql(registry, newsJdbcCon):null;
    }

    public MailStoring getMailStoring()
    {
	return mailJdbcCon != null?new MailStoringSql(registry, mailJdbcCon):null;
    }
}
