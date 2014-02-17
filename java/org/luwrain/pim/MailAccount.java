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

public class MailAccount
{
    public final static int INCOMING = 1;
    public final static int RELAY = 2;
    public final static int RELAY_DEFAULT = 3;

    public final static int POP3 = 1;
    public final static int POP3_SSL = 2;
    public final static int SMTP = 3;

    public String name = "";
    public int type;
    public int protocol;
    public String host = "";
    public int port;
    public String login = "";
    public String passwd = "";
}
