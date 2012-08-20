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

import java.util.concurrent.*;
import java.util.*;

class EventQueue
{
    private LinkedBlockingQueue<Event> events = new LinkedBlockingQueue<Event>(1024);

    void putEvent(Event e)
    {
	try {
	    events.put(e);
	}
	catch (InterruptedException ex)
	{
	    ex.printStackTrace();//FIXME:
	}
    }

    Event takeEvent()
    {
	try {
	    return events.take();
	}
	catch (InterruptedException ex)
	{
	    ex.printStackTrace();
	    return null;
	}
    }
}

class ApplicationEntry
{
    public Application app = null;
    public Object instance = null;
}

public class Environment
{
    private static String[] cmdLineArgs = null;
    private static Vector<ApplicationEntry> applications = new Vector<ApplicationEntry>();
    private static WindowManager windowManager = new WindowManager();
    private static EventQueue eventQueue = new EventQueue();
    private static DispatcherImpl dispatcher = new DispatcherImpl();
    private static com.marigostra.luwrain.app.SystemApp systemApp = new com.marigostra.luwrain.app.SystemApp();

    static public Dispatcher dispatcher()
    {
	return dispatcher;
    }

    static public void  run(String[] args)
    {
	cmdLineArgs = args;
	launchApplication(systemApp);//FIXME:System application should not be launched this way as any usual;
	Thread eventSourceKeyboard = new Thread(new EventSourceKeyboard());
	eventSourceKeyboard.start();
		eventLoop(new InitialEventLoopStopCondition());
		Launch.exit();
    }

    static public void quit()
    {
	InitialEventLoopStopCondition.shouldContinue = false;
    }

    static public void launchApplication(Application app)
    {
	if (app == null)
	    return;
	for(int i = 0;i < applications.size();i++)
	    if (applications.get(i).app == app)
		return;
	Object o = new Object();
	if (!app.onLaunch(o))
	    return;
	ApplicationEntry entry = new ApplicationEntry();
	entry.instance = o;
	entry.app = app;
	applications.add(entry);
	AreaLayout layout = app.getAreasToShow();
	if (layout != null)
	    windowManager.takeNewLayout(app, layout);
    }

    static public void eventLoop(EventLoopStopCondition stopCondition)
    {
	while(stopCondition.continueEventLoop())
	{
	    Event event = eventQueue.takeEvent();
	    if (event == null)
		continue;
	    if (event.type() == Event.KEYBOARD_EVENT)
	    {
		KeyboardEvent key = (KeyboardEvent)event;
		processKeyboardEvent(key);
		continue;
	    }
	}
    }

    static public void enqueueEvent(Event e)
    {
	eventQueue.putEvent(e);
    }

    static public void setActiveArea(Object instance, Area area)
    {
	for(int i = 0;i < applications.size();i++)
	{
	    ApplicationEntry entry = applications.get(i);
	    if (entry.instance == instance)
	    {
		windowManager.setActiveArea(entry.app, area);
		break;
	    }
	}
    }

    static public void onAreaNewHotPoint(Area area, int x, int y)
    {
	//FIXME:
    }

    static public void onNewAreaContent(Area area)
    {
	//FIXME:
    }

    static private void processKeyboardEvent(KeyboardEvent event)
    {
	if (event.isCommand() && event.getCommand() == KeyboardEvent.ESCAPE)//FIXME:Just for debugging;
		{
		    quit();
		    return;
		}
	windowManager.onKeyboardEvent(event);
    }
}
