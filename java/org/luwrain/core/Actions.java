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

package org.luwrain.core;

import java.util.*;
import org.luwrain.core.events.*;

public class Actions
{
    private Vector<Action> actions = new Vector<Action>();

    public void add(Action action)
    {
	actions.add(action);
    }

    public boolean run(String actionName)
    {
	Iterator<Action> it = actions.iterator();
	while(it.hasNext())
	{
	    Action act = it.next();
	    if (!act.getName().equals(actionName))
		continue;
	    act.onAction();
	    return true;
	}
	return false;
    }

    public void fillWithStandartActions()
    {
	//Main menu;
	add(new Action() {
		public String getName()
		{
		    return "main-menu";
		}
		public void onAction()
		{
		    Environment.mainMenu();
		}
	    });

	//Quit;
	add(new Action() {
		public String getName()
		{
		    return "quit";
		}
		public void onAction()
		{
		    Environment.quit();
		}
	    });

	//OK;
	add(new Action() {
		public String getName()
		{
		    return "ok";
		}
		public void onAction()
		{
		    Environment.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.OK));
		}
	    });

	//Cancel;
	add(new Action() {
		public String getName()
		{
		    return "cancel";
		}
		public void onAction()
		{
		    Environment.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.CANCEL));
		}
	    });

	//Close;
	add(new Action() {
		public String getName()
		{
		    return "close";
		}
		public void onAction()
		{
		    Environment.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.CLOSE));
		}
	    });

	//Save;
	add(new Action() {
		public String getName()
		{
		    return "save";
		}
		public void onAction()
		{
		    Environment.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.SAVE));
		}
	    });

	//Refresh;
	add(new Action() {
		public String getName()
		{
		    return "refresh";
		}
		public void onAction()
		{
		    Environment.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.REFRESH));
		}
	    });

	//Describe;
	add(new Action() {
		public String getName()
		{
		    return "describe";
		}
		public void onAction()
		{
		    Environment.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.DESCRIBE));
		}
	    });

	//Help;
	add(new Action() {
		public String getName()
		{
		    return "help";
		}
		public void onAction()
		{
		    Environment.enqueueEvent(new EnvironmentEvent(EnvironmentEvent.HELP));
		}
	    });

	//Switch to next App;
	add(new Action() {
		public String getName()
		{
		    return "switch-next-app";
		}
		public void onAction()
		{
		    Environment.switchNextApp();
		}
	    });

	//Switch to next area;
	add(new Action() {
		public String getName()
		{
		    return "switch-next-area";
		}
		public void onAction()
		{
		    Environment.switchNextArea();
		}
	    });

	//Notepad;
	add(new Action() {
		public String getName()
		{
		    return "notepad";
		}
		public void onAction()
		{
		    Application app = new org.luwrain.app.notepad.NotepadApp();
		    Dispatcher.launchApplication(app);
		}
	    });

	//Commander;
	add(new Action() {
		public String getName()
		{
		    return "commander";
		}
		public void onAction()
		{
		    Application app = new org.luwrain.app.commander.CommanderApp();
		    Dispatcher.launchApplication(app);
		}
	    });

	//News;
	add(new Action() {
		public String getName()
		{
		    return "news";
		}
		public void onAction()
		{
		    Application app = new org.luwrain.app.news.NewsReaderApp();
		    Dispatcher.launchApplication(app);
		}
	    });

	//Mail;
	add(new Action() {
		public String getName()
		{
		    return "mail";
		}
		public void onAction()
		{
		    Application app = new org.luwrain.app.mail.MailReaderApp();
		    Dispatcher.launchApplication(app);
		}
	    });

	//Fetch;
	add(new Action() {
		public String getName()
		{
		    return "fetch";
		}
		public void onAction()
		{
		    Application app = new org.luwrain.app.fetch.FetchApp();
		    Dispatcher.launchApplication(app);
		}
	    });

	//Message;
	add(new Action() {
		public String getName()
		{
		    return "message";
		}
		public void onAction()
		{
		    Application app = new org.luwrain.app.message.MessageApp();
		    Dispatcher.launchApplication(app);
		}
	    });
    }
}
