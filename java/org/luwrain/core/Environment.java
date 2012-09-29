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

import java.util.concurrent.*;
import java.util.*;
import org.luwrain.app.system.MainMenuArea;

import org.luwrain.core.events.*;
import org.luwrain.mmedia.EnvironmentSounds;

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

class ApplicationEntrySet
{
    private Vector<ApplicationEntry> entries;

    public ApplicationEntrySet(Vector<ApplicationEntry> entries)
    {
	this.entries = entries;
    }

    public int getIndexOfApp(Application app)
    {
	int index = 0;
	while(index < entries.size() && entries.elementAt(index).app != app)
	    index++;
	if (index >= entries.size())
	    return -1;
	return index;
    }

    public boolean hasApplication(Application app)
    {
	return getIndexOfApp(app) >= 0;
    }

    public ApplicationEntry getEntry(Application app)
    {
	int index = getIndexOfApp(app);
	if (index < 0 || index >= entries.size())
	    return null;
	return entries.elementAt(index);
    }

    public void chooseTopApp(Application app)
    {
	if (entries.isEmpty() || entries.size() < 2)
	    return;
	int index = getIndexOfApp(app);
	if (index < 0 || index + 1 >= entries.size())
	    return;
	ApplicationEntry xchg = entries.elementAt(index);
	entries.set(index, entries.elementAt(entries.size() - 1));
	entries.set(entries.size() - 1, xchg);
    }

    public ApplicationEntry getTopAppEntry()
    {
	if (entries.isEmpty())
	    return null;
	return entries.elementAt(entries.size() - 1);
    }

    public Application getTopApp()
    {
	ApplicationEntry entry = getTopAppEntry();
	if (entry == null)
	    return null;
	return entry.app;
    }

    public void savePreferableActiveArea(WindowManager windowManager)
    {
	Application app = windowManager.getAppOfActiveNonPopupArea();//Returns null if active is popup area;
	Area area = windowManager.getActiveNonPopupArea();
	if (app == null || area == null)
	    return;
	for(int i = 0;i < entries.size();i++)
	    if (entries.elementAt(i).app == app)
	    {
		entries.elementAt(i).preferableActiveArea = area;
		return;
	    }
	//FIXME:Warning to log;
    }

    public ApplicationEntry getPreferableEntryToSetActive(WindowManager windowManager)
    {
	ApplicationEntry bestChoice = null;
	for(int i = 0;i < entries.size();i++)
	{
	    ApplicationEntry entry = entries.elementAt(i);
	    if (entry.preferableActiveArea == null || !windowManager.hasAnyNonPopupAreaOfApp(entry.app))
		continue;
	    if (!windowManager.hasNonPopupArea(entry.preferableActiveArea))
	    {
		entry.preferableActiveArea = null;
		continue;
	    }
	    bestChoice = entry;
	}
	return bestChoice;
    }

    public Area getPreferableAreaToSetActive(WindowManager windowManager)
    {
	ApplicationEntry entry = getPreferableEntryToSetActive(windowManager);
	if (entry != null)
	    return entry.preferableActiveArea;
	return null;
    }
}

class PopupEntrySet
{
    private Application systemApp;
    private Vector<PopupEntry> entries;

    public PopupEntrySet(Application systemApp, Vector<PopupEntry> entries)
    {
	this.systemApp = systemApp;
	this.entries = entries;
    }

    public PopupEntry getActualPopupEntry()
    {
	if (entries.isEmpty())
	    return null;
	return entries.elementAt(entries.size() - 1);
    }

    public PopupEntry getSuitablePopup(WindowManager windowManager)
    {
	if (entries.isEmpty())
	    return null;
	PopupEntry entry = entries.elementAt(entries.size() - 1);
	if (entry.app == systemApp)
	    return entry;
	if (windowManager.hasAnyNonPopupAreaOfApp(entry.app))
	    return entry;
	return null;
    }

    public PopupEntry getSuitablePopup(Application app)
    {
	if (entries.isEmpty())
	    return null;
	PopupEntry entry = entries.elementAt(entries.size() - 1);
	if (entry.app == systemApp || entry.app == app)
	    return entry;
	return null;
    }

    public boolean hasPopupOfApp(Application app)
    {
	for(int i = 0;i < entries.size();i++)
	    if (entries.elementAt(i).app == app)
		return true;
	return false;
    }
}

class ScreenContentManager
{
    private WindowManager windowManager;
    private ApplicationEntrySet appSet;
    private PopupEntrySet popupSet;

    public ScreenContentManager(WindowManager windowManager, ApplicationEntrySet appSet, PopupEntrySet popupSet)
    {
	this.windowManager = windowManager;
	this.appSet = appSet;
	this.popupSet = popupSet;
    }

    public boolean showAppAlone(Application app)
    {
	if (app == null || !appSet.hasApplication(app))
	    return false;
	AreaLayout layout = app.getAreasToShow();
	if (layout == null)
	    return false;
	appSet.savePreferableActiveArea(windowManager);
	appSet.chooseTopApp(app);
	Area popupArea = null;
	int popupPlace = 0;
	PopupEntry popupEntry = popupSet.getSuitablePopup(app);
	if (popupEntry != null)
	{
	    popupArea = popupEntry.area;
	    popupPlace = popupEntry.popupPlace;
	}
	windowManager.takeCompleteNewLayout(app,
					    layout,
					    popupSet.hasPopupOfApp(app),
					    popupArea,
					    popupPlace);
	appSet.savePreferableActiveArea(windowManager);
	return true;
    }

    //Makes no changes in area set;
    public void preventInactiveAreas()
    {
	if (windowManager.getActiveNonPopupArea() != null)
	    return;
	if (windowManager.hasPopupArea())
	{
	    if (windowManager.isPopupAreaActive())
		return;
	    windowManager.setPopupAreaActive();
	    return;
	}
	if (!windowManager.hasAnyNonPopupArea())
	    return;
	ApplicationEntry entry = appSet.getPreferableEntryToSetActive(windowManager);
	if (entry != null)
	    windowManager.setNonPopupAreaActive(entry.app, entry.preferableActiveArea); else
	    windowManager.setActiveFirstNonPopupArea();
    }

    public void preventEmptyScreenContent()
    {
	if (windowManager.hasAnyNonPopupArea())
	{
	    PopupEntry popupEntry = popupSet.getSuitablePopup(windowManager);
	    if (popupEntry == null)
	    {
		windowManager.closePopupArea();//Even if there is none now;
		preventInactiveAreas();
		return;
	    }
	    if (!windowManager.hasPopupArea() || windowManager.getPopupArea() != popupEntry.area)
	    {
		//FIXME:windowManager.blockAreasByApp(popupEntry.app);
		windowManager.openPopupArea(popupEntry.area, popupEntry.popupPlace);
		return;
	    }
	    preventInactiveAreas();
	    return;
	}
	Application app = appSet.getTopApp();
	if (app != null)
	{
	    showAppAlone(app);
	    return;
	}
	PopupEntry popupEntry = popupSet.getSuitablePopup(windowManager);//Actually here can be only popups of systemApp;
	if (popupEntry != null)
	    windowManager.openPopupArea(popupEntry.area, popupEntry.popupPlace);
    }

    //Never faults;
    public void onNewPopupArea(PopupEntry popupEntry)
    {
	Application currentActiveApp =windowManager.getAppOfActiveNonPopupArea(); 
	if (currentActiveApp != null)
	    appSet.chooseTopApp(currentActiveApp);
	appSet.savePreferableActiveArea(windowManager);
	windowManager.openPopupArea(popupEntry.area, popupEntry.popupPlace);
    }

    public void onPopupAreaClose()
    {
	PopupEntry actualEntry = popupSet.getActualPopupEntry();
	if (actualEntry != null)
	{
	    if (actualEntry.stopCondition.continueEventLoop())//Checking if area still wants accept events;
		windowManager.openPopupArea(actualEntry.area, actualEntry.popupPlace); else
		windowManager.closePopupArea();
	    return;
	}
	windowManager.closePopupArea();
    }
}

public class Environment
{
    private static String[] cmdLineArgs = null;
    private static EventQueue eventQueue = new EventQueue();
    private static Actions actions = new Actions();
    private static org.luwrain.app.system.SystemApp systemApp = new org.luwrain.app.system.SystemApp();
    private static Vector<ApplicationEntry> applications = new Vector<ApplicationEntry>();
    private static Vector<PopupEntry> popups = new Vector<PopupEntry>();
    private static ApplicationEntrySet applicationSet;
    private static PopupEntrySet popupSet;
    private static WindowManager windowManager = new WindowManager();
    private static ScreenContentManager screenContentManager;

    //Start/stop;

    static public void  run(Interaction interaction, String[] args)
    {
	cmdLineArgs = args;
	actions.fillWithStandartActions();
	applicationSet = new ApplicationEntrySet(applications);
	popupSet = new PopupEntrySet(systemApp, popups);
	screenContentManager = new ScreenContentManager(windowManager, applicationSet, popupSet);
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

    static private ApplicationEntry resolveInstance(Object instance)
    {
	if (instance == null)
	    return null;
	for(int i = 0;i < applications.size();i++)
	    if (applications.elementAt(i).instance == instance)
		return applications.elementAt(i);
	return null;
    }

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
	try {
	    if (!app.onLaunch(o))//FIXME:catch exceptions;
	    {
		applications.remove(applications.size() - 1);
		return;
	    }
	}
	catch (OutOfMemoryError e)
	{
	    message(Langs.staticValue(Langs.INSUFFICIENT_MEMORY_FOR_APP_LAUNCH));
	    applications.remove(applications.size() - 1);
	    return;
	}
	catch (Throwable e)
	{
	    e.printStackTrace();
	    //FIXME:Log warning;
	    message(Langs.staticValue(Langs.UNEXPECTED_ERROR_AT_APP_LAUNCH));
	    applications.remove(applications.size() - 1);
	    return;
	}
	screenContentManager.showAppAlone(app);//Including popup area if needed;
    }

    static public void closeApplication(Object instance)
    {
	int index = 0;
	while (index < applications.size() && applications.elementAt(index).instance != instance)
	    index++;
	if (index >= applications.size())
	    return;
	ApplicationEntry appEntry = applications.elementAt(index);
	if (popupSet.hasPopupOfApp(appEntry.app))
	{
	    message(Langs.staticValue(Langs.APPLICATION_CLOSE_ERROR_HAS_POPUP));
	    return;
	}
	windowManager.closeNonPopupAreasByApp(appEntry.app);
	applications.remove(index);
	screenContentManager.preventEmptyScreenContent();
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
	applicationSet.savePreferableActiveArea(windowManager);
	if (newAppEntry.preferableActiveArea != null && layout.hasArea(newAppEntry.preferableActiveArea))
	    windowManager.replaceAreasOfAppWithNewLayout(oldApp, newApp, layout, popupSet.hasPopupOfApp(newApp), newAppEntry.preferableActiveArea); else//Desired active is exactly desired, windowManager may ignore this parameter;
	    windowManager.replaceAreasOfAppWithNewLayout(oldApp, newApp, layout, popupSet.hasPopupOfApp(newApp), null);
	newAppEntry.preferableActiveArea = null;
    }

    public static void switchNextApplication()
    {
	//If no active app, select with popup;
	if (windowManager.isPopupAreaActive())
	    return;
	if (applications.isEmpty())
	    return;
	Application oldApp = windowManager.getAppOfActiveNonPopupArea();
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
	    if (!windowManager.hasAnyNonPopupAreaOfApp(e.app) && layout != null)
		break;
	    pos++;
	}
	if (pos == oldAppPos)//There is nothing to switch to;
	    return;
	switchApplication(oldApp, applications.elementAt(pos).app);
    }

    public static void switchNextArea()
    {
	windowManager.gotoNextArea();
	applicationSet.savePreferableActiveArea(windowManager);
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
		{
		    EnvironmentSounds.play(EnvironmentSounds.EVENT_NOT_PROCESSED);
		    Speech.say(Langs.staticValue(Langs.NO_ACTIVE_AREA));//FIXME:Marked intonation;
		}
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
	if (event.isCommand() && event.getCommand() == KeyboardEvent.TAB && !event.withAlt() && event.withControl() && !event.withShift())
	{
	    switchNextArea();
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
	if (!event.isCommand() && event.getCharacter() == 'x' && event.withLeftAlt())//FIXME:withLeftAltOnly;
	{
	    runAction();
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
	{
	    EnvironmentSounds.play(EnvironmentSounds.EVENT_NOT_PROCESSED);
	    Speech.say(Langs.staticValue(Langs.NO_ACTIVE_AREA));//FIXME:Marked intonation;
	}
    }

    static public void enqueueEvent(Event e)
    {
	eventQueue.putEvent(e);
    }

    //Popup area processing;

    static void goIntoPopup(Application app, Area area, int popupPlace, EventLoopStopCondition stopCondition)
    {
	PopupEntry entry = new PopupEntry();
	entry.area = area;
	entry.app = app;
	entry.popupPlace = popupPlace;
	entry.stopCondition = stopCondition;
	screenContentManager.onNewPopupArea(entry);
	popups.add(entry);
	eventLoop(new PopupEventLoopStopCondition(stopCondition));
	popups.remove(popups.size() - 1);
	screenContentManager.onPopupAreaClose();
	screenContentManager.preventEmptyScreenContent();
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
	EnvironmentSounds.play(EnvironmentSounds.MAIN_MENU);
	goIntoPopup(systemApp, mainMenuArea, WindowManager.POPUP_LEFT, mainMenuArea);
	if (mainMenuArea.wasCancelled())
	    return;
	EnvironmentSounds.play(EnvironmentSounds.MAIN_MENU_ITEM);
	if (!actions.run(mainMenuArea.getSelectedAction()))
	    message(Langs.staticValue(Langs.NO_REQUESTED_ACTION));
    }

    public static void runAction()
    {
	org.luwrain.core.popups.SimpleLinePopup popup = new org.luwrain.core.popups.SimpleLinePopup(new Object(), "Выполнить команду", "Выполнить команду:", "");
	goIntoPopup(systemApp, popup, WindowManager.POPUP_BOTTOM, popup);
	if (popup.wasCancelled())
	    return;
	if (!actions.run(popup.getText().trim()))
	    message(Langs.staticValue(Langs.NO_REQUESTED_ACTION));
    }

    //API;

    static public void setActiveArea(Object instance, Area area)
    {
	ApplicationEntry entry = resolveInstance(instance);
	if (entry == null)
	    return;
	windowManager.setNonPopupAreaActive(entry.app, area);
	applicationSet.savePreferableActiveArea(windowManager);
    }

    static public void onAreaNewHotPoint(Area area)
    {
	//FIXME:
    }

    static public void onAreaNewContent(Area area)
    {
	windowManager.onAreaNewContent(area);
    }

    static public void onAreaNewName(Area area)
    {
	//FIXME:
    }

    static public void message(String text)
    {
	//FIXME:Message class for message collecting;
	Speech.say(text);
    }
}
