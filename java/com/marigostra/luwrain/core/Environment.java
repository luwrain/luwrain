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
import com.marigostra.luwrain.app.MainMenuArea;

import com.marigostra.luwrain.core.events.*;

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
    public Area preferableActiveArea = null;
}

class PopupEntry
{
    Area area;
    Application app;
    EventLoopStopCondition stopCondition;
    int popupPlace;
}

class InitialEventLoopStopCondition implements EventLoopStopCondition
{
    static public boolean shouldContinue = true;

    public boolean continueEventLoop()
    {
	return shouldContinue;
    }
}

class PopupEventLoopStopCondition implements EventLoopStopCondition
{
    private EventLoopStopCondition popup = null;

public PopupEventLoopStopCondition(EventLoopStopCondition popup)
    {
	this.popup = popup;
    }

    public boolean continueEventLoop()
    {
	return InitialEventLoopStopCondition.shouldContinue && popup.continueEventLoop();
    }
}

public class Environment
{
    private static String[] cmdLineArgs = null;
    private static Vector<ApplicationEntry> applications = new Vector<ApplicationEntry>();
    private static Vector<PopupEntry> popups = new Vector<PopupEntry>();
    private static WindowManager windowManager = new WindowManager();
    private static EventQueue eventQueue = new EventQueue();
    private static Actions actions = new Actions();
    private static com.marigostra.luwrain.app.SystemApp systemApp = new com.marigostra.luwrain.app.SystemApp();

    //Start/stop;

    static public void  run(Interaction interaction, String[] args)
    {
	cmdLineArgs = args;
	actions.fillWithStandartActions();
	interaction.startInputEventsAcception();
		eventLoop(new InitialEventLoopStopCondition());
		interaction.stopInputEventsAccepting();
		Launch.exit();
    }

    static public void quit()
    {
	InitialEventLoopStopCondition.shouldContinue = false;
    }

    //Application management;

    static public void launchApplication(Application app)
    {
	if (app == null)
	    return;
	for(int i = 0;i < applications.size();i++)
	    if (applications.elementAt(i).app == app)//No double launch;
		return;
	Object o = new Object();
	ApplicationEntry entry = new ApplicationEntry();
	entry.app = app;
	entry.instance = o;
	applications.add(entry);//It is needed to let application open popupps on initialization;
	if (!app.onLaunch(o))//FIXME:catch exceptions;
	{
	    applications.remove(applications.size() - 1);
	    return;
	}
	AreaLayout layout = app.getAreasToShow();
	if (layout == null)
	    return;
	savePreferableActiveArea();
	if (!popups.isEmpty())
	{
	    PopupEntry popupEntry = popups.elementAt(popups.size() - 1);
	    windowManager.takeCompleteNewLayout(app, layout, false, popupEntry.area, popupEntry.popupPlace);//Blocking is always false since new application has no reasons to be blocked;
	} else
	    windowManager.takeCompleteNewLayout(app, layout, false, null, 0);//Blocking is always false since new application has no reasons to be blocked;
    }

    static public void closeApplication(Object instance)
    {
	int index = 0;
	while (index < applications.size() && applications.elementAt(index).instance != instance)
	    index++;
	if (index >= applications.size())
	    return;
	ApplicationEntry appEntry = applications.elementAt(index);
	if (hasPopupOfApp(appEntry.app))
	{
	    message(Langs.staticValue(Langs.APPLICATION_CLOSE_ERROR_HAS_POPUP));
	    return;
	}
	windowManager.closeAreasByApp(appEntry.app);
	applications.remove(index);
	if (windowManager.hasPopupArea())
	{
	    windowManager.setActivePopupArea();
	    return;
	}
	if (!popups.isEmpty())
	{
	    PopupEntry popupEntry = popups.elementAt(popups.size() - 1);
	    windowManager.openPopupArea(popupEntry.area, popupEntry.popupPlace);
	    return;
	}
	if (windowManager.hasNonPopup())
	{
	    windowManager.setActiveFirstNonPopup();
	    return;
	}
	//Now we are sure there are no available areas neither popup nor non-popup;
	if (applications.isEmpty())
	    return;
	ApplicationEntry lastAppEntry = applications.elementAt(applications.size() - 1);
	lastAppEntry.preferableActiveArea = null;
	AreaLayout layout = lastAppEntry.app.getAreasToShow();
	if (layout != null)
	    windowManager.takeCompleteNewLayout(lastAppEntry.app, layout, hasPopupOfApp(lastAppEntry.app), null, 0);//There cannot exist any popup, but stability is more preferable;
    }

    public static void switchApplication(Application oldApp, Application newApp)
    {
	if (oldApp == null || newApp == null || oldApp == newApp)
	    return;
	int oldAppPos = 0, newAppPos = 0;
	while(oldAppPos < applications.size() && applications.elementAt(oldAppPos).app != oldApp)
	    oldAppPos++;
	while(newAppPos < applications.size() && applications.elementAt(newAppPos).app != newApp)
	    newAppPos++;
	if (oldAppPos >= applications.size() || newAppPos >= applications.size())
	    return;
	ApplicationEntry oldAppEntry = applications.elementAt(oldAppPos), newAppEntry = applications.elementAt(newAppPos);
	AreaLayout layout = newApp.getAreasToShow();
	if (layout == null)
	    return;
	savePreferableActiveArea();
	if (newAppEntry.preferableActiveArea != null && layout.hasArea(newAppEntry.preferableActiveArea))
	    windowManager.replaceApplication(oldApp, newApp, layout, hasPopupOfApp(newApp), newAppEntry.preferableActiveArea); else//Desired active is exactly desired, windowManager may ignore this parameter;
	    windowManager.replaceApplication(oldApp, newApp, layout, hasPopupOfApp(newApp), null);
	newAppEntry.preferableActiveArea = null;
    }

    //Does nothing if there is no application associated with the active area (there is no active area or it is popup);
    public static void switchNextApplication()
    {
	if (windowManager.isPopupAreaActive())
	    return;
	if (applications.isEmpty())
	    return;
	Application oldApp = windowManager.getNonPopupActiveAreaApp();
	if (oldApp == null)
	    return;
	int pos;
	for(pos = 0;pos < applications.size();pos++)
	    if (applications.elementAt(pos).app == oldApp)
		break;
	if (pos >= applications.size())
	{
	    //FIXME:Log warning;
	    return;
	}
	final int oldAppPos = pos;
	pos++;
	while(true)
	{
	    if (pos >= applications.size())
		pos = 0;
	    if (pos == oldAppPos)
		break;
	    ApplicationEntry e = applications.elementAt(pos);
	    AreaLayout layout = e.app.getAreasToShow();
	    if (!windowManager.hasAreaNonPopupOfApp(e.app) && layout != null)
		break;
	    pos++;
	}
	if (pos == oldAppPos)//There is nothing to switch to;
	    return;
	switchApplication(oldApp, applications.elementAt(pos).app);


    }

    //Events;

    static public void eventLoop(EventLoopStopCondition stopCondition)
    {
	while(stopCondition.continueEventLoop())
	{
	    Event event = eventQueue.takeEvent();
	    if (event == null)
		continue;
	    if (event.type() == Event.KEYBOARD_EVENT)
	    {
		processKeyboardEvent((KeyboardEvent)event);
		continue;
	    }
	    if (event.type() == Event.ENVIRONMENT_EVENT)
	    {
		if (!windowManager.onEnvironmentEvent((EnvironmentEvent)event))
		    Speech.say(Langs.staticValue(Langs.NO_ACTIVE_AREA));//FIXME:Marked intonation;
		continue;
	    }
	}
    }

    static private void processKeyboardEvent(KeyboardEvent event)
    {
	if (event.isCommand() && event.getCommand() == KeyboardEvent.TAB && event.withLeftAlt() && !event.withControl() && !event.withShift())
	{
	    switchNextApplication();
	    return;
	}
	if (event.isCommand())
	{
	    final int code = event.getCommand();
	    if (code == KeyboardEvent.SHIFT ||
		code == KeyboardEvent.CONTROL ||
		code == KeyboardEvent.LEFT_ALT ||
		code == KeyboardEvent.RIGHT_ALT)
		return;
	}
	if (event.isCommand() && event.getCommand() == KeyboardEvent.ESCAPE)//FIXME:Just for debugging;
		{
		    quit();
		    return;
		}
	if (event.isCommand() && event.getCommand() == KeyboardEvent.WINDOWS)
		{
		    mainMenu();
		    return;
		}
	if (event.isCommand() && event.getCommand() == KeyboardEvent.F4)
	{
	    enqueueEvent(new EnvironmentEvent(EnvironmentEvent.CLOSE));
	    return;
	}
	if (!windowManager.onKeyboardEvent(event))
	    Speech.say(Langs.staticValue(Langs.NO_ACTIVE_AREA));//FIXME:Marked intonation;
    }

    static public void enqueueEvent(Event e)
    {
	eventQueue.putEvent(e);
    }

    //Popup area processing;

    static void goIntoPopup(Application app, Area area, int popupPlace, EventLoopStopCondition stopCondition)
    {
	savePreferableActiveArea();
	PopupEntry entry = new PopupEntry();
	entry.area = area;
	entry.app = app;
	entry.popupPlace = popupPlace;
	entry.stopCondition = stopCondition;
	popups.add(entry);
	windowManager.openPopupArea(area, popupPlace);
	eventLoop(new PopupEventLoopStopCondition(stopCondition));
	popups.remove(popups.size() - 1);
	if (!popups.isEmpty())
	{
	    PopupEntry popupEntry = popups.elementAt(popups.size() - 1);
	    if (popupEntry.stopCondition.continueEventLoop())//Checking if area still wants accept events;
		windowManager.openPopupArea(popupEntry.area, popupEntry.popupPlace); else
		windowManager.closePopupArea();
	    return;
	}
	windowManager.closePopupArea();
	for(int i = 0;i < applications.size();i++)
	{
	    ApplicationEntry appEntry = applications.elementAt(i);
	    if (appEntry.preferableActiveArea == null || !windowManager.hasNonPopupArea(appEntry.preferableActiveArea))//FIXME:Clear value if area is already invisible;
		continue;
	    windowManager.setActiveAreaNonPopup(appEntry.app, appEntry.preferableActiveArea);
	    appEntry.preferableActiveArea = null;
	    break;
	}
    }

    public static boolean hasPopupOfApp(Application app)
    {
	for(int i = 0;i < popups.size();i++)
	    if (popups.elementAt(i).app == app)
		return true;
	return false;
    }

    public static void mainMenu()
    {
	for(int i = 0;i < popups.size();i++)
	{
	    MainMenuArea mainMenuArea;
	    try {
		mainMenuArea = (MainMenuArea)popups.elementAt(i).area;//It looks like a hack but there is nothing dangerous and everything works as we want;
	    }
	    catch (ClassCastException e)
	    {
		continue;
	    }
	    mainMenuArea.cancel();
	}
	MainMenuArea mainMenuArea = systemApp.createMainMenuArea();
	goIntoPopup(systemApp, mainMenuArea, WindowManager.POPUP_LEFT, mainMenuArea);
	if (mainMenuArea.wasCancelled())
	    return;
	if (!actions.run(mainMenuArea.getSelectedAction()))
	    message(Langs.staticValue(Langs.NO_REQUESTED_ACTION));
    }

    //API;

    static public void setActiveArea(Object instance, Area area)
    {
	for(int i = 0;i < applications.size();i++)
	{
	    ApplicationEntry entry = applications.get(i);
	    if (entry.instance == instance)
	    {
		windowManager.setActiveAreaNonPopup(entry.app, area);
		break;
	    }
	}
    }

    static public void onAreaNewHotPoint(Area area)
    {
	//FIXME:
    }

    static public void onAreaNewContent(Area area)
    {
	windowManager.onAreaNewContent(area);
    }

    public static void message(String text)
    {
	//FIXME:Message class for message collecting;
	Speech.say(text);
    }

    private static void savePreferableActiveArea()
    {
	Application app = windowManager.getNonPopupActiveAreaApp();
	Area area = windowManager.getNonPopupActiveArea();
	if (app == null || area == null)
	    return;
	for(int i = 0;i < applications.size();i++)
	    if (applications.elementAt(i).app == app)
	    {
		applications.elementAt(i).preferableActiveArea = area;
		return;
	    }
    }
}
