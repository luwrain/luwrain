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

package org.luwrain.core;

import java.util.*;
import org.luwrain.core.events.*;

class Actions
{
    private Vector<Action> actions = new Vector<Action>();

    public void add(Action action)
    {
	if (action != null && action.getName() != null && !action .getName().trim().isEmpty())
	    actions.add(action);
    }

    public boolean run(String actionName)
    {
	for(Action act: actions)
	{
	    if (!act.getName().trim().equals(actionName.trim()))
		continue;
	    act.onAction();
	    return true;
	}
	return false;
    }

    public String[] getActionsName()
    {
	if (actions == null)
	    return new String[0];
	Vector<String> res = new Vector<String>();
	for(Action a: actions)
	    res.add(a.getName());
	String[] str = res.toArray(new String[res.size()]);
	Arrays.sort(str);
	return str;
    }

    public void fillWithStandardActions(Environment env)
    {
	if (env == null)
	    return;
	final Environment environment = env;
	//Main menu;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "main-menu";
		}
		public void onAction()
		{
		    e.mainMenu();
		}
	    });

	//Quit;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "quit";
		}
		public void onAction()
		{
		    e.quit();
		}
	    });

	//OK;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "ok";
		}
		public void onAction()
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.OK));
		}
	    });

	//Cancel;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "cancel";
		}
		public void onAction()
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.CANCEL));
		}
	    });

	//Close;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "close";
		}
		public void onAction()
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.CLOSE));
		}
	    });

	//Save;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "save";
		}
		public void onAction()
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.SAVE));
		}
	    });

	//open;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "open";
		}
		public void onAction()
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.OPEN));
		}
	    });

	//Refresh;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "refresh";
		}
		public void onAction()
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.REFRESH));
		}
	    });

	//copy-cut-point;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "copy-cut-point";
		}
		public void onAction()
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.COPY_CUT_POINT));
		}
	    });

	//copy;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "copy";
		}
		public void onAction()
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.COPY));
		}
	    });

	//cut;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "cut";
		}
		public void onAction()
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.CUT));
		}
	    });

	//paste;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "paste";
		}
		public void onAction()
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.PASTE));
		}
	    });

	//Describe;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "describe";
		}
		public void onAction()
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.DESCRIBE));
		}
	    });

	//Help;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "help";
		}
		public void onAction()
		{
		    e.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.HELP));
		}
	    });

	//Switch to next App;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "switch-next-app";
		}
		public void onAction()
		{
		    e.switchNextApp();
		}
	    });

	//Switch to next area;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "switch-next-area";
		}
		public void onAction()
		{
		    e.switchNextArea();
		}
	    });

	//Increase font size;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "increase-font-size";
		}
		public void onAction()
		{
		    e.increaseFontSize();
		}
	    });

	//Decrease font size;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "decrease-font-size";
		}
		public void onAction()
		{
		    e.decreaseFontSize();
		}
	    });

	//Notepad;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "notepad";
		}
		public void onAction()
		{
		    Application app = new org.luwrain.app.notepad.NotepadApp();
		    e.launchApp(app);
		}
	    });

	//Commander;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "commander";
		}
		public void onAction()
		{
		    Application app = new org.luwrain.app.commander.CommanderApp();
		    e.launchApp(app);
		}
	    });

	//News;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "news";
		}
		public void onAction()
		{
		    Application app = new org.luwrain.app.news.NewsReaderApp();
		    e.launchApp(app);
		}
	    });

	//Mail;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "mail";
		}
		public void onAction()
		{
		    Application app = new org.luwrain.app.mail.MailApp();
		    e.launchApp(app);
		}
	    });

	//Fetch;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "fetch";
		}
		public void onAction()
		{
		    Application app = new org.luwrain.app.fetch.FetchApp();
		    e.launchApp(app);
		}
	    });

	//Message;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "message";
		}
		public void onAction()
		{
		    Application app = new org.luwrain.app.message.MessageApp();
		    e.launchApp(app);
		}
	    });

	//Preview;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "preview";
		}
		public void onAction()
		{
		    Application app = new org.luwrain.app.preview.PreviewApp();
		    e.launchApp(app);
		}
	    });

	//control;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "control";
		}
		public void onAction()
		{
		    Application app = new org.luwrain.app.control.ControlApp();
		    e.launchApp(app);
		}
	    });

	//registry;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "registry";
		}
		public void onAction()
		{
		    Application app = new org.luwrain.app.registry.RegistryApp();
		    e.launchApp(app);
		}
	    });

	//Calendar;
	add(new Action() {
		private Environment e = environment;
		public String getName()
		{
		    return "calendar";
		}
		public void onAction()
		{
		    Application app = new org.luwrain.app.calendar.CalendarApp();
		    e.launchApp(app);
		}
	    });

    }
}
