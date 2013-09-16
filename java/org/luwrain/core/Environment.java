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
    private static AppWrapperRegistry appWrappers = new AppWrapperRegistry();
    private static FileTypes fileTypes = new FileTypes(appWrappers);
    private static boolean needForIntroduction = false;

    //Start/stop;

    static public void  run(Interaction intr, String[] args)
    {
	cmdLineArgs = args;
	interaction = intr;
	actions.fillWithStandartActions();
	appWrappers.fillWithStandardWrappers();
	screenContentManager = new ScreenContentManager(applications, popups, systemApp);
	windowManager = new WindowManager(interaction, screenContentManager);
	interaction.startInputEventsAccepting();
	    EnvironmentSounds.play(EnvironmentSounds.STARTUP);
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
	introduceActiveArea();
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
	introduceActiveArea();
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
	introduceActiveArea();
    }

    public static void switchNextArea()
    {
	screenContentManager.activateNextArea();
	windowManager.redraw();
	introduceActiveArea();
    }

    //Events;

    static public void eventLoop(EventLoopStopCondition stopCondition)
    {
	while(stopCondition.continueEventLoop())
	{
	    Event event = eventQueue.takeEvent();
	    if (event == null)
		continue;
	    switch (event.type())
	    {
	    case Event.KEYBOARD_EVENT:
		onKeyboardEvent((KeyboardEvent)event);
		break;
	    case Event.ENVIRONMENT_EVENT:
		onEnvironmentEvent((EnvironmentEvent)event);
		break;
	    default:
		Log.warning("environment", "got the event of an unknown type:" + event.type());
	    }
	    if (needForIntroduction && stopCondition.continueEventLoop())
		introduceActiveArea();
	    needForIntroduction = false;

		}
    }

    static private void onKeyboardEvent(KeyboardEvent event)
    {
	if (event == null)
	    return;
	String actionName = globalKeys.getActionName(event);
	if (actionName != null)
	{
	    if (!actions.run(actionName))
		message(Langs.staticValue(Langs.NO_REQUESTED_ACTION));//FIXME:sound;
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
	if (!event.isCommand() &&
	    event.getCharacter() == 'x' &&
	    event.withLeftAltOnly())
	{
	    runActionPopup();
	    return;
	}
	int res = ScreenContentManager.EVENT_NOT_PROCESSED;
	try {
	    res = screenContentManager.onKeyboardEvent(event);
	}
	catch (Throwable e)
	{
	    Log.error("environment", "keyboard event throws an exception:" + e.getMessage());
	    e.printStackTrace();
	    EnvironmentSounds.play(EnvironmentSounds.EVENT_NOT_PROCESSED);
	    return;
	}
	switch(res)
	{
	case ScreenContentManager.EVENT_NOT_PROCESSED:
	    EnvironmentSounds.play(EnvironmentSounds.EVENT_NOT_PROCESSED);
	    break;
	case ScreenContentManager.NO_APPLICATIONS:
	    EnvironmentSounds.play(EnvironmentSounds.NO_APPLICATIONS);
	    message(Langs.staticValue(Langs.START_WORK_FROM_MAIN_MENU));
	    break;
	}
    }

    static public void onEnvironmentEvent(EnvironmentEvent event)
    {
	int res = ScreenContentManager.EVENT_NOT_PROCESSED;
	try {
	    if (event.getCode() == EnvironmentEvent.THREAD_SYNC)
	    {
		ThreadSyncEvent threadSync = (ThreadSyncEvent)event;
		if (threadSync.getDestArea() != null)
		    res = threadSync.getDestArea().onEnvironmentEvent(event)?ScreenContentManager.EVENT_PROCESSED:ScreenContentManager.EVENT_NOT_PROCESSED; else
		    res = ScreenContentManager.EVENT_NOT_PROCESSED;
	    } else
		res = screenContentManager.onEnvironmentEvent(event);
	}
	catch (Throwable e)
	{
	    Log.error("environment", "environment event throws an exception:" + e.getMessage());
	    e.printStackTrace();
	    EnvironmentSounds.play(EnvironmentSounds.EVENT_NOT_PROCESSED);
	    return;
	}
	switch(res)
	{
	case ScreenContentManager.EVENT_NOT_PROCESSED:
	    EnvironmentSounds.play(EnvironmentSounds.EVENT_NOT_PROCESSED);
	    break;
	case ScreenContentManager.NO_APPLICATIONS:
	    EnvironmentSounds.play(EnvironmentSounds.NO_APPLICATIONS);
	    message(Langs.staticValue(Langs.START_WORK_FROM_MAIN_MENU));
	    break;
	}
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
	    introduceActiveArea();
	    windowManager.redraw();
	}
	eventLoop(new PopupEventLoopStopCondition(stopCondition));
	popups.removeLastPopup();
	screenContentManager.updatePopupState();
	needForIntroduction = true;
	windowManager.redraw();
    }

    public static void runActionPopup()
    {
	org.luwrain.popups.SimpleLinePopup popup = new org.luwrain.popups.SimpleLinePopup(new Object(), "Выполнить команду", "команда:", "");
	goIntoPopup(systemApp, popup, PopupRegistry.BOTTOM, popup.closing);
	if (popup.closing.cancelled())
	    return;
	if (!actions.run(popup.getText().trim()))
	    message(Langs.staticValue(Langs.NO_REQUESTED_ACTION));
    }

    static public void setActiveArea(Object instance, Area area)
    {
	if (instance == null || area == null)
	    return;
	Application app = instanceManager.getAppByInstance(instance);
	if (app == null)
	    return;//FIXME:Log message;
	applications.setActiveAreaOfApp(app, area);
	if (applications.isActiveApp(app) && !screenContentManager.isPopupAreaActive())
	    introduceActiveAreaNoEvent();
	windowManager.redraw();
    }

    static public void onAreaNewHotPoint(Area area)
    {
	if (area != null && area == screenContentManager.getActiveArea())
	    windowManager.redrawArea(area);
    }

    static public void onAreaNewContent(Area area)
    {
	windowManager.redrawArea(area);
    }

    static public void onAreaNewName(Area area)
    {
	windowManager.redrawArea(area);
    }

    //May return -1;
    static public int getAreaVisibleHeight(Area area)
    {
	if (area == null)
	    return -1;
	return windowManager.getAreaVisibleHeight(area);
    }

    static public void message(String text)
    {
	if (text == null || text.trim().isEmpty())
	    return;
	needForIntroduction = false;
	//FIXME:Message class for message collecting;
	Speech.say(text);
	interaction.startDrawSession();
	interaction.clearRect(0, interaction.getHeightInCharacters() - 1, interaction.getWidthInCharacters() - 1, interaction.getHeightInCharacters() - 1);
	interaction.drawText(0, interaction.getHeightInCharacters() - 1, text);
	interaction.endDrawSession();
    }

    public static void mainMenu()
    {
	//FIXME:No double opening;
	MainMenuArea mainMenuArea = systemApp.createMainMenuArea(getMainMenuItems());
	EnvironmentSounds.play(EnvironmentSounds.MAIN_MENU);
	goIntoPopup(systemApp, mainMenuArea, PopupRegistry.LEFT, mainMenuArea.closing);
	if (mainMenuArea.closing.cancelled())
	    return;
	EnvironmentSounds.play(EnvironmentSounds.MAIN_MENU_ITEM);
	if (!actions.run(mainMenuArea.getSelectedActionName()))
	    message(Langs.staticValue(Langs.NO_REQUESTED_ACTION));
    }

    static private String[] getMainMenuItems()
    {
	if (Registry.typeOf(CoreRegistryValues.MAIN_MENU_CONTENT) != Registry.STRING)
	{
	    Log.error("environment", "registry has no value \'" + CoreRegistryValues.MAIN_MENU_CONTENT + "\' needed for proper main menu appearance");
	    return new String[0];
	}
	final String content = Registry.string(CoreRegistryValues.MAIN_MENU_CONTENT);
	ArrayList<String> a = new ArrayList<String>();
	String s = "";
	if (content.trim().isEmpty())
	    return new String[0];
	for(int i = 0;i < content.length();i++)
	{
	    if (content.charAt(i) == ':')
	    {
		a.add(s.trim());
		s = "";
	    } else
		s += content.charAt(i);
	}
	a.add(s.trim());
	return a.toArray(new String[a.size()]);
    }

    static public void introduceActiveArea()
    {
	needForIntroduction = false;
	Area activeArea = screenContentManager.getActiveArea();
	if (activeArea == null)
	{
	    EnvironmentSounds.play(EnvironmentSounds.NO_APPLICATIONS);
	    Speech.say(Langs.staticValue(Langs.NO_LAUNCHED_APPS));
	    return;
	}
	if (activeArea.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.INTRODUCE)))
	    return;
	Speech.say(activeArea.getName());
    }

    static public void introduceActiveAreaNoEvent()
    {
	needForIntroduction = false;
	Area activeArea = screenContentManager.getActiveArea();
	if (activeArea == null)
	{
	    EnvironmentSounds.play(EnvironmentSounds.NO_APPLICATIONS);
	    Speech.say(Langs.staticValue(Langs.NO_LAUNCHED_APPS));
	    return;
	}
	Speech.say(activeArea.getName());
    }


    static public void increaseFontSize()
    {
	interaction.setDesirableFontSize(interaction.getFontSize() * 2); 
	windowManager.redraw();
	message(Langs.staticValue(Langs.FONT_SIZE) + " " + interaction.getFontSize());
    }

    static public void decreaseFontSize()
    {
	interaction.setDesirableFontSize(interaction.getFontSize() / 2); 
	windowManager.redraw();
	message(Langs.staticValue(Langs.FONT_SIZE) + " " + interaction.getFontSize());
    }

    static public void openFileNames(String[] fileNames)
    {
	fileTypes.openFileNames(fileNames);
    }
}
