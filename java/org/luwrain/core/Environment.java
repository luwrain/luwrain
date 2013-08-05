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

import java.util.concurrent.*;
import java.util.*;
import org.luwrain.app.system.MainMenuArea;
import org.luwrain.core.events.*;
import org.luwrain.mmedia.EnvironmentSounds;

public class Environment
{
    private static String[] cmdLineArgs = null;
    private static Interaction interaction;
    private static EventQueue eventQueue = new EventQueue();
    private static Actions actions = new Actions();
    private static org.luwrain.app.system.SystemApp systemApp = new org.luwrain.app.system.SystemApp();
    private static InstanceManager instanceManager = new InstanceManager();
    private static ApplicationRegistry applications = new ApplicationRegistry();
    private static PopupRegistry popups = new PopupRegistry();
    private static ScreenContentManager screenContentManager;
    private static WindowManager windowManager;
    private static GlobalKeys globalKeys = new GlobalKeys();

    //Start/stop;

    static public void  run(Interaction intr, String[] args)
    {
	cmdLineArgs = args;
	interaction = intr;
	actions.fillWithStandartActions();
	screenContentManager = new ScreenContentManager(applications, popups, systemApp);
	windowManager = new WindowManager(interaction, screenContentManager);
	interaction.startInputEventsAccepting();
		eventLoop(new InitialEventLoopStopCondition());
		interaction.stopInputEventsAccepting();
		Launch.exit();
    }

    static public void quit()
    {
	InitialEventLoopStopCondition.shouldContinue = false;
    }

    //Application management;

    //Always full screen;
    static public void launchApplication(Application app)
    {
	if (app == null)
	    return;
	Object o = instanceManager.registerApp(app);
	try {
	    if (!app.onLaunch(o))
	    {
		instanceManager.releaseInstance(o);
		return;
	    }
	}
	catch (OutOfMemoryError e)
	{
	    instanceManager.releaseInstance(o);
	    message(Langs.staticValue(Langs.INSUFFICIENT_MEMORY_FOR_APP_LAUNCH));
	    return;
	}
	catch (Throwable e)
	{
	    instanceManager.releaseInstance(o);
	    e.printStackTrace();
	    //FIXME:Log warning;
	    message(Langs.staticValue(Langs.UNEXPECTED_ERROR_AT_APP_LAUNCH));
	    return;
	}
	AreaLayout layout = app.getAreasToShow();
	if (layout == null)
	{
	    instanceManager.releaseInstance(o);
	    return;
	}
	Area activeArea = layout.getDefaultArea();
	if (activeArea == null)
	{
	    instanceManager.releaseInstance(o);
	    return;
	}
	applications.registerAppSingleVisible(app, activeArea);
	screenContentManager.updatePopupState();
	windowManager.redraw();
	screenContentManager.introduceActiveArea();
    }

    static public void closeApplication(Object instance)
    {
	if (instance == null)
	    return;
	Application app = instanceManager.getAppByInstance(instance);
	if (app == null)
	    return;
	if (popups.hasPopupOfApp(app))
	{
	    message(Langs.staticValue(Langs.APPLICATION_CLOSE_ERROR_HAS_POPUP));
	    return;
	}
	applications.releaseApp(app);
	instanceManager.releaseInstance(instance);
	screenContentManager.updatePopupState();//Actually not needed but for consistency;
	windowManager.redraw();
	screenContentManager.introduceActiveArea();
    }

    public static void switchNextApp()
    {
	if (screenContentManager.isPopupAreaActive())
	{
	    EnvironmentSounds.play(EnvironmentSounds.EVENT_NOT_PROCESSED);//FIXME:Probably not well suited sound; 
	    return;
	}
	applications.switchNextInvisible();
	screenContentManager.updatePopupState();
	windowManager.redraw();
	screenContentManager.introduceActiveArea();
    }

    public static void switchNextArea()
    {
	screenContentManager.activateNextArea();
	windowManager.redraw();
	screenContentManager.introduceActiveArea();
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
		if (!screenContentManager.onEnvironmentEvent((EnvironmentEvent)event))
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
	if (event == null)
	    return;
	String actionName = globalKeys.getActionName(event);
	if (actionName != null)
	{
	    if (!actions.run(actionName))
		message(Langs.staticValue(Langs.NO_REQUESTED_ACTION));
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
	if (!event.isCommand() && event.getCharacter() == 'x' && event.withLeftAltOnly())
	{
	    runActionPopup();
	    return;
	}
	if (!screenContentManager.onKeyboardEvent(event))//FIXME:Exception;
	{
	    EnvironmentSounds.play(EnvironmentSounds.EVENT_NOT_PROCESSED);
	    Speech.say(Langs.staticValue(Langs.NO_ACTIVE_AREA));//FIXME:Marked intonation;
	} else 
	    windowManager.redraw();
    }

    static public void enqueueEvent(Event e)
    {
	eventQueue.putEvent(e);
    }

    //Popup area processing;

    static void goIntoPopup(Application app,
			    Area area,
			    int popupPlace,
			    EventLoopStopCondition stopCondition)
    {
	if (app == null ||
	    area == null ||
	    stopCondition == null)
	    return;
	if (popupPlace != PopupRegistry.TOP &&
	    popupPlace != PopupRegistry.BOTTOM && 
	    popupPlace != PopupRegistry.LEFT &&
	    popupPlace != PopupRegistry.RIGHT)
	    return;
	popups.addNewPopup(app, area, popupPlace, new PopupEventLoopStopCondition(stopCondition));
	if (screenContentManager.setPopupAreaActive())
	{
	    screenContentManager.introduceActiveArea();
	    windowManager.redraw();
	}
	eventLoop(new PopupEventLoopStopCondition(stopCondition));
	popups.removeLastPopup();
	screenContentManager.updatePopupState();
	if (!popups.hasPopups() || !popups.isLastPopupDiscontinued())
	{
	    screenContentManager.introduceActiveArea();
	    windowManager.redraw();
	}
    }

    public static void mainMenu()
    {
	/*
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
	    FIXME:
	}
	*/
	MainMenuArea mainMenuArea = systemApp.createMainMenuArea();
EnvironmentSounds.play(EnvironmentSounds.MAIN_MENU);
goIntoPopup(systemApp, mainMenuArea, PopupRegistry.LEFT, mainMenuArea);
	if (mainMenuArea.wasCancelled())
	    return;
	EnvironmentSounds.play(EnvironmentSounds.MAIN_MENU_ITEM);
	if (!actions.run(mainMenuArea.getSelectedAction()))
	    message(Langs.staticValue(Langs.NO_REQUESTED_ACTION));
    }

    public static void runActionPopup()
    {
	org.luwrain.popups.SimpleLinePopup popup = new org.luwrain.popups.SimpleLinePopup(new Object(), "Выполнить команду", "Выполнить команду:", "");
	goIntoPopup(systemApp, popup, PopupRegistry.BOTTOM, popup.closing);
	if (popup.closing.cancelled())
	    return;
	if (!actions.run(popup.getText().trim()))
	    message(Langs.staticValue(Langs.NO_REQUESTED_ACTION));
    }

    //API;

    static public void setActiveArea(Object instance, Area area)
    {
	if (instance == null || area == null)
	    return;
	Application app = instanceManager.getAppByInstance(instance);
	if (app == null)
	    return;//FIXME:Log message;
	applications.setActiveAreaOfApp(app, area);
	if (applications.isActiveApp(app) && !screenContentManager.isPopupAreaActive())
	    screenContentManager.introduceActiveArea();
	windowManager.redraw();
    }

    static public void onAreaNewHotPoint(Area area)
    {
	windowManager.redraw();//FIXME:Area may be inactive;
    }

    static public void onAreaNewContent(Area area)
    {
	windowManager.redraw();//FIXME:Area may be inactive;
    }

    static public void onAreaNewName(Area area)
    {
	windowManager.redraw();//FIXME:Area may be inactive;
	screenContentManager.introduceActiveArea();
    }

    static public void message(String text)
    {
	//FIXME:Message class for message collecting;
	Speech.say(text);
    }
}
