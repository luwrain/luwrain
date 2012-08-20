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

package com.marigostra.luwrain.core;

import jcurses.system.*;

public class Launch
{
    public static String[] commandLine;

    public static void go(String[] args)
    {
	commandLine = args;
	init();
	Environment.run(args);
	exit();
    }

    public static void exit()
    {
	shutdown();
	System.exit(0);
    }

    private static void init()
    {
	SpeechBackEndVoiceMan backend = new SpeechBackEndVoiceMan();
	backend.connect("localhost", 5511);
	Speech.setBackEnd(backend);
	Toolkit.init();
	try {
	    com.marigostra.luwrain.comm.DBus.connect();
	}
	catch(org.freedesktop.dbus.exceptions.DBusException e)
	{
	    e.printStackTrace();
	    //FIXME:
	}
    }

    private static void shutdown()
    {
	com.marigostra.luwrain.comm.DBus.shutdown();
	Toolkit.shutdown();
    }
}
