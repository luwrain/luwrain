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

import java.util.*;
import com.marigostra.luwrain.core.events.*;

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

	//Notepad;
	add(new Action() {
		public String getName()
		{
		    return "notepad";
		}
		public void onAction()
		{
		    Application app = new com.marigostra.luwrain.app.notepad.NotepadApp();
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
		    Application app = new com.marigostra.luwrain.app.commander.CommanderApp();
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
		    Application app = new com.marigostra.luwrain.app.news.NewsReaderApp();
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
		    Application app = new com.marigostra.luwrain.app.mail.MailReaderApp();
		    Dispatcher.launchApplication(app);
		}
	    });
    }
}
