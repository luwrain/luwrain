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

package org.luwrain.core;

import org.luwrain.pim.PimManager;
import org.luwrain.mmedia.EnvironmentSounds;

public class Launch
{
    public static String[] commandLine;
    private static Interaction interaction;

    public static void go(String[] args)
    {
	commandLine = args;
	interaction = new org.luwrain.interaction.AwtInteraction();
	init();
	Environment.run(interaction, args);
	exit();
    }

    private static void init()
    {
	SpeechBackEndVoiceMan backend = new SpeechBackEndVoiceMan();
	backend.connect("localhost", 5511);
	Speech.setBackEnd(backend);
	EnvironmentSounds.setSoundFile(EnvironmentSounds.MAIN_MENU, "/home/luwrain/media/sounds/main-menu.wav");
	EnvironmentSounds.setSoundFile(EnvironmentSounds.MAIN_MENU_ITEM, "/home/luwrain/media/sounds/main-menu-item.wav");
	EnvironmentSounds.setSoundFile(EnvironmentSounds.EVENT_NOT_PROCESSED, "/home/luwrain/media/sounds/beep1.wav");
	PimManager.type = PimManager.STORAGE_SQL;//FIXME:
	PimManager.driver = "com.mysql.jdbc.Driver";
	PimManager.url = "jdbc:mysql://localhost/luwrain?characterEncoding=utf-8";
	PimManager.login = "root";
	PimManager.passwd = "";
	try {
	    org.luwrain.dbus.DBus.connect();
	}
	catch(org.freedesktop.dbus.exceptions.DBusException e)
	{
	    e.printStackTrace();
	}
	interaction.init();
    }

    private static void shutdown()
    {
	interaction.close();
	org.luwrain.dbus.DBus.shutdown();

    }


    public static void exit()
    {
	shutdown();
	System.exit(0);
    }
}
