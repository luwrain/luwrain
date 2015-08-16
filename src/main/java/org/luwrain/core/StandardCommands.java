		/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

class StandardCommands
{
public static Command[] createStandardCommands(Environment env)
    {
	if (env == null)
	    throw new NullPointerException("env may not be null");
	final Environment environment = env;
	LinkedList<Command> res = new LinkedList<Command>();

	//Main menu;
	res.add(new Command() {
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
	res.add(new Command() {
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
	res.add(new Command() {
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
	res.add(new Command() {
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
	res.add(new Command() {
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
	res.add(new Command() {
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
	res.add(new Command() {
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

	//Introduce;
	res.add(new Command() {
		private Environment e = environment;
		public String getName()
		{
		    return "introduce";
		}
		public void onCommand(Luwrain luwrain)
		{
		    e.introduceActiveArea();
		}
	    });

	//Refresh;
	res.add(new Command() {
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
	res.add(new Command() {
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
	res.add(new Command() {
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
	res.add(new Command() {
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
	res.add(new Command() {
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
	res.add(new Command() {
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
	res.add(new Command() {
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
	res.add(new Command() {
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
	res.add(new Command() {
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
	res.add(new Command() {
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
	res.add(new Command() {
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

	//control panel;
	res.add(new Command() {
		private Environment e = environment;
		@Override public String getName()
		{
		    return "control-panel";
}
		@Override public void onCommand(Luwrain luwrain)
		{
		    final Application app = new org.luwrain.app.cpanel.ControlPanelApp(e.getControlPanelSections());
		    e.launchApp(app);
		}
	    });

	//registry;
	res.add(new Command() {
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
	return res.toArray(new Command[res.size()]);
    }
}
