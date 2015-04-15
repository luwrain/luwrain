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
    class Entry 
    {
	public Extension extension;
	public String name = "";
	public Command command;

	public Entry(Extension extension,
		     String name,
		     Command command)
	{
	    this.extension = extension;
	    this.name = name;
	    this.command = command;
	    //extension may be null meaning it is a basic command;
	    if (name == null)
		throw new NullPointerException("name may not be null");
	    if (name.trim().isEmpty())
		throw new IllegalArgumentException("name may not be empty");
	    if (command == null)
		throw new NullPointerException("command may not be null");
	}
    }

    private TreeMap<String, Entry> commands = new TreeMap<String, Entry>();

    public boolean add(Extension extension, Command command)
    {
	if (command == null)
	    throw new NullPointerException("command may not be null");
	final String name = command.getName();
	if (name == null || name.trim().isEmpty())
	    return false;
	if (commands.containsKey(name))
	    return false;
	commands.put(name, new Entry(extension, name, command));
	return true;
    }

    public boolean run(String name, Luwrain luwrain)
    {
	if (name == null)
	    throw new NullPointerException("name may not be null");
	if (name.trim().isEmpty())
	    throw new IllegalArgumentException("name may not be empty");
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (!commands.containsKey(name))
	    return false;
	commands.get(name).command.onCommand(luwrain);
	return true;
    }

    public String[] getCommandsName()
    {
	Vector<String> res = new Vector<String>();
	for(Map.Entry<String, Entry> e: commands.entrySet())
	    res.add(e.getKey());
	String[] str = res.toArray(new String[res.size()]);
	Arrays.sort(str);
	return str;
    }

    public void addBasicCommands(Environment env)
    {
	if (env == null)
	    throw new NullPointerException("env may not be null");
	final Environment environment = env;
	//Main menu;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "main-menu";
		}
		public void onCommand(CommandEnvironment env)
		{
		    e.mainMenu();
		}
	    });

	//Quit;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "quit";
		}
		public void onCommand(CommandEnvironment env)
		{
		    e.quit();
		}
	    });

	//OK;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "ok";
		}
		public void onCommand(CommandEnvironment env)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.OK));
		}
	    });

	//Cancel;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "cancel";
		}
		public void onCommand(CommandEnvironment env)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.CANCEL));
		}
	    });

	//Close;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "close";
		}
		public void onCommand(CommandEnvironment env)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.CLOSE));
		}
	    });

	//Save;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "save";
		}
		public void onCommand(CommandEnvironment env)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.SAVE));
		}
	    });

	//open;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "open";
		}
		public void onCommand(CommandEnvironment env)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.OPEN));
		}
	    });

	//Introduce;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "introduce";
		}
		public void onCommand(CommandEnvironment env)
		{
		    e.introduceActiveArea();
		}
	    });

	//Refresh;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "refresh";
		}
		public void onCommand(CommandEnvironment env)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.REFRESH));
		}
	    });

	//copy-cut-point;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "copy-cut-point";
		}
		public void onCommand(CommandEnvironment env)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.COPY_CUT_POINT));
		}
	    });

	//copy;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "copy";
		}
		public void onCommand(CommandEnvironment env)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.COPY));
		}
	    });

	//cut;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "cut";
		}
		public void onCommand(CommandEnvironment env)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.CUT));
		}
	    });

	//paste;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "paste";
		}
		public void onCommand(CommandEnvironment env)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.PASTE));
		}
	    });

	//Describe;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "describe";
		}
		public void onCommand(CommandEnvironment env)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.DESCRIBE));
		}
	    });

	//Help;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "help";
		}
		public void onCommand(CommandEnvironment env)
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.HELP));
		}
	    });

	//Switch to next App;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "switch-next-app";
		}
		public void onCommand(CommandEnvironment env)
		{
		    e.switchNextApp();
		}
	    });

	//Switch to next area;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "switch-next-area";
		}
		public void onCommand(CommandEnvironment env)
		{
		    e.switchNextArea();
		}
	    });

	//Increase font size;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "increase-font-size";
		}
		public void onCommand(CommandEnvironment env)
		{
		    e.increaseFontSize();
		}
	    });

	//Decrease font size;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "decrease-font-size";
		}
		public void onCommand(CommandEnvironment env)
		{
		    e.decreaseFontSize();
		}
	    });

	//control;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "control";
		}
		public void onCommand(CommandEnvironment env)
		{
		    Application app = new org.luwrain.app.control.ControlApp();
		    e.launchApp(app);
		}
	    });

	//registry;
	add(null, new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "registry";
		}
		public void onCommand(CommandEnvironment env)
		{
		    Application app = new org.luwrain.app.registry.RegistryApp();
		    e.launchApp(app);
		}
	    });
    }

    public void addOsCommands(Registry registry)
    {
	if (registry == null)
	    throw new NullPointerException("registry may not be null");
	final String path = new RegistryKeys().commandsOs();
	final String[] subdirs = registry.getDirectories(path);
	if (subdirs == null)
	    return;
	for(String s: subdirs)
	{
	    if (s.trim().isEmpty())
	    {
		Log.warning("environment", "registry directory " + path + " contains a subdirectory with an empty name");
		continue;
	    }
	    final String commandValue = path + "/" + s + "/command";
	    if (registry.getTypeOf(commandValue) != Registry.STRING)
	    {
		Log.warning("environment", "registry value " + commandValue + " supposed to be a string but it isn\'t a string");
		continue;
	    }
	    add(null, new OsCommand(s, registry.getString(commandValue)));
	}
    }
}
