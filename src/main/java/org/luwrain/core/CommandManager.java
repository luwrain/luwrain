/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

import java.util.*;
import org.luwrain.core.events.*;

class CommandManager
{
    private Vector<Command> commands = new Vector<Command>();

    public void add(Command command)
    {
	if (command != null &&
	    command.getName() != null &&
	    !command.getName().trim().isEmpty())
	    commands.add(command);
    }

    public boolean run(String name, Luwrain luwrain)
    {
	for(Command cmd: commands)
	{
	    if (!cmd.getName().trim().equals(name.trim()))
		continue;
	    cmd.onCommand(luwrain);
	    return true;
	}
	return false;
    }

    public String[] getCommandsName()
    {
	if (commands == null)
	    return new String[0];
	Vector<String> res = new Vector<String>();
	for(Command c: commands)
	    res.add(c.getName());
	String[] str = res.toArray(new String[res.size()]);
	Arrays.sort(str);
	return str;
    }

    public void fillWithStandardCommands(Environment env)
    {
	if (env == null)
	    return;
	final Environment environment = env;
	//Main menu;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "main-menu";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.mainMenu();
		}
	    });

	//Quit;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "quit";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.quit();
		}
	    });

	//OK;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "ok";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.OK));
		}
	    });

	//Cancel;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "cancel";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.CANCEL));
		}
	    });

	//Close;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "close";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.CLOSE));
		}
	    });

	//Save;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "save";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.SAVE));
		}
	    });

	//open;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "open";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.OPEN));
		}
	    });

	//Refresh;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "refresh";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.REFRESH));
		}
	    });

	//copy-cut-point;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "copy-cut-point";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.COPY_CUT_POINT));
		}
	    });

	//copy;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "copy";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.COPY));
		}
	    });

	//cut;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "cut";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.CUT));
		}
	    });

	//paste;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "paste";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.PASTE));
		}
	    });

	//Describe;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "describe";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.DESCRIBE));
		}
	    });

	//Help;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "help";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.HELP));
		}
	    });

	//Switch to next App;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "switch-next-app";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.switchNextApp();
		}
	    });

	//Switch to next area;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "switch-next-area";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.switchNextArea();
		}
	    });

	//Increase font size;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "increase-font-size";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.increaseFontSize();
		}
	    });

	//Decrease font size;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "decrease-font-size";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.decreaseFontSize();
		}
	    });

	//control;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "control";
		}
		public void onCommand(Luwrain luwrain)
		{
		    Application app = new org.luwrain.app.control.ControlApp();
		    e.launchApp(app);
		}
	    });

	//registry;
	add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "registry";
		}
		public void onCommand(Luwrain luwrain)
		{
		    Application app = new org.luwrain.app.registry.RegistryApp();
		    e.launchApp(app);
		}
	    });

    }
}
