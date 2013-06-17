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

package org.luwrain.dbus;

import org.freedesktop.dbus.exceptions.*;
import org.freedesktop.*;
import java.util.*;

public class Connections
{
    static public String[] getDevices() throws DBusException
    {
	NetworkManager nm = (NetworkManager)org.luwrain.dbus.DBus.con.getRemoteObject("org.freedesktop.NetworkManager", "/org/freedesktop/NetworkManager", NetworkManager.class);
	List<org.freedesktop.dbus.DBusInterface> d = nm.GetDevices();
	String[] res = new String[d.size()];
	Iterator it = d.iterator();
	int k = 0;
	while (it.hasNext())
res[k++] = it.next().toString();
	return res;
    }
}
