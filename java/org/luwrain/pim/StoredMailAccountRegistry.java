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

import org.luwrain.core.registry.Registry;

class StoredMailAccountRegistry implements StoredMailAccount
{
    public Registry registry;

    public long id = 0;
    public String name = "";
    public int type = MailAccount.INCOMING;
    public int protocol = MailAccount.POP3;
    public String host = "";
    public int port;
    public String login = "";
    public String passwd = "";

    public StoredMailAccountRegistry(Registry registry)
    {
	this.registry = registry;
    }

    public String getName()
    {
	return name;
    }

    public void setName(String value) throws Exception
    {
	//FIXME:
    }

    public int getType()
    {
	return type;
    }

    public void setType(int value) throws Exception
    {
	//FIXME:
    }

    public int getProtocol()
    {
	return protocol;
    }

    public void setProtocol(int value) throws Exception
    {
	//FIXME:
    }

    public String getHost()
    {
	return host;
    }

    public void setHost(String value) throws Exception
    {
	//FIXME:
    }

    public int getPort()
    {
	return port;
    }

    public void setPort(int value) throws Exception
    {
	//FIXME:
    }

    public String getLogin()
    {
	return login;
    }

    public void setLogin(String value) throws Exception
    {
	//FIXME:
    }

    public String getPasswd()
    {
	return passwd;
    }

    public void setPasswd(String value) throws Exception
    {
	//FIXME:
    }
}
