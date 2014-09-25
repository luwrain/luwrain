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

public class MailRegistryValues
{
    private static final String TYPE_PATH = "/org/luwrain/pim/mail/storing/type";
    private static final String URL_PATH = "/org/luwrain/pim/mail/storing/url";
    private static final String DRIVER_PATH = "/org/luwrain/pim/mail/storing/driver";
    private static final String LOGIN_PATH = "/org/luwrain/pim/mail/storing/login";
    private static final String PASSWD_PATH = "/org/luwrain/pim/mail/storing/passwd";

    private static final String OUTGOING_GROUP_URI_PATH = "/org/luwrain/pim/mail/group-outgoing-uri";

    private Registry registry;

    public MailRegistryValues(Registry registry)
    {
	this.registry = registry;
    }

    public String getOutgoingGroupUri()
    {
	return registry.getTypeOf(OUTGOING_GROUP_URI_PATH) == Registry.STRING?registry.getString(OUTGOING_GROUP_URI_PATH):null;
    }
}
