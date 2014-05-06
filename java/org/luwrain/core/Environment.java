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

//FIXME:Open event in open popup should do nothing;
//FIXME:action popup likely should be of some another class to prevent confusing on no multiple instances checking;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import org.luwrain.core.registry.Registry;
import org.luwrain.app.system.MainMenuArea;
import org.luwrain.pim.PimManager;
import org.luwrain.core.events.*;
import org.luwrain.popups.*;
import org.luwrain.mmedia.EnvironmentSounds;

class Environment implements EventConsumer
{
    private String[] cmdLine;
    private Registry registry;
    private Interaction interaction;
    private EventQueue eventQueue = new EventQueue();
    private InstanceManager appInstances = new InstanceManager();
    private ApplicationRegistry apps = new ApplicationRegistry();
    private PopupManager popups = new PopupManager();
    private ScreenContentManager screenContentManager;
    private WindowManager windowManager;
    private PimManager pimManager;
    private GlobalKeys globalKeys;
    private Actions actions = new Actions();
    private AppWrapperRegistry appWrappers = new AppWrapperRegistry();
    private org.luwrain.app.system.SystemApp systemApp = new org.luwrain.app.system.SystemApp();

    private FileTypes fileTypes = new FileTypes(appWrappers);
    private boolean needForIntroduction = false;
    private String[] clipboard = null;

    public Environment(String[] cmdLine,
		       Registry registry,
		       Interaction interaction,
		       PimManager pimManager)
    {
	this.cmdLine = cmdLine;
	this.registry = registry;
	this.interaction = interaction;
	this.pimManager = pimManager;
    }

    public void  run()
    {
	if (screenContentManager != null || windowManager != null)
	{
	    Log.fatal("environment", "the environment is tried to launch twice but that is prohibited");
	    return;
	}
	screenContentManager = new ScreenContentManager(apps, popups, systemApp);
	windowManager = new WindowManager(interaction, screenContentManager);
	globalKeys = new GlobalKeys(registry);
	globalKeys.loadFromRegistry();
	actions.fillWithStandardActions(this);
	appWrappers.fillWithStandardWrappers();
	interaction.startInputEventsAccepting(this);
	EnvironmentSounds.play(EnvironmentSounds.STARTUP);//FIXME:
		eventLoop(new InitialEventLoopStopCondition());
		interaction.stopInputEventsAccepting();
    }

    public void quit()
    {
	YesNoPopup popup = new YesNoPopup(null, Langs.staticValue(Langs.QUIT_CONFIRM_NAME), Langs.staticValue(Langs.QUIT_CONFIRM), true);
	goIntoPopup(systemApp, popup, PopupManager.BOTTOM, popup.closing, true);
	if (popup.closing.cancelled() || !popup.getResult())
	    return;
	InitialEventLoopStopCondition.shouldContinue = false;
    }

    //Application management;

    //Always full screen;
    public void launchApp(Application app)
    {
	if (app == null)
	    return;
	Object o = appInstances.registerApp(app);
	try {
	    if (!app.onLaunch(o))
	    {
		appInstances.releaseInstance(o);
		return;
	    }
	}
	catch (OutOfMemoryError e)
	{
	    appInstances.releaseInstance(o);
	    message(Langs.staticValue(Langs.INSUFFICIENT_MEMORY_FOR_APP_LAUNCH));
	    return;
	}
	catch (Throwable e)
	{
	    appInstances.releaseInstance(o);
	    e.printStackTrace();
	    //FIXME:Log warning;
	    message(Langs.staticValue(Langs.UNEXPECTED_ERROR_AT_APP_LAUNCH));
	    return;
	}
	AreaLayout layout = app.getAreasToShow();
	if (layout == null)
	{
	    appInstances.releaseInstance(o);
	    return;
	}
	Area activeArea = layout.getDefaultArea();
	if (activeArea == null)
	{
	    appInstances.releaseInstance(o);
	    return;
	}
	apps.registerAppSingleVisible(app, activeArea);
	screenContentManager.updatePopupState();
	windowManager.redraw();
	introduceActiveArea();
    }

    public void closeApp(Object instance)
    {
	if (instance == null)
	    return;
	Application app = appInstances.getAppByInstance(instance);
	if (app == null)
	    return;
	if (popups.hasPopupOfApp(app))
	{
	    message(Langs.staticValue(Langs.APPLICATION_CLOSE_ERROR_HAS_POPUP));
	    return;
	}
	apps.releaseApp(app);
	appInstances.releaseInstance(instance);
	screenContentManager.updatePopupState();
	windowManager.redraw();
	introduceActiveArea();
    }

    public void switchNextApp()
    {
	if (screenContentManager.isPopupAreaActive())
	{
	    EnvironmentSounds.play(EnvironmentSounds.EVENT_NOT_PROCESSED);//FIXME:Probably not well suited sound; 
	    return;
	}
	apps.switchNextInvisible();
	screenContentManager.updatePopupState();
	windowManager.redraw();
	introduceActiveArea();
    }

    public void switchNextArea()
    {
	screenContentManager.activateNextArea();
	windowManager.redraw();
	introduceActiveArea();
    }

    //Events;

    public void eventLoop(EventLoopStopCondition stopCondition)
    {
	while(stopCondition.continueEventLoop())
	{
	    Event event = eventQueue.takeEvent();
	    if (event == null)
		continue;
	    switch (event.type())
	    {
	    case Event.KEYBOARD_EVENT:
		onKeyboardEvent(translateKeyboardEvent((KeyboardEvent)event));
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

    private void onKeyboardEvent(KeyboardEvent event)
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

    public void onEnvironmentEvent(EnvironmentEvent event)
    {
	int res = ScreenContentManager.EVENT_NOT_PROCESSED;
	try {
	    //Paste;
	    if (event.getCode() == EnvironmentEvent.PASTE)
	    {
		if (clipboard == null || clipboard.length < 1)
		    return;
		onEnvironmentEvent(new InsertEvent(clipboard));
		return;
	    }
	    //Open;
	    if (event.getCode() == EnvironmentEvent.OPEN)
	    {
		if (screenContentManager.onEnvironmentEvent(event) == ScreenContentManager.EVENT_PROCESSED)
		    return;
		File f = openPopupByApp(systemApp, null, null, null);
		if (f == null)
		    return;
		String[] fileNames = new String[1];
		fileNames[0] = f.getAbsolutePath();
		openFiles(fileNames);
		return;
	    }
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

    public void enqueueEvent(Event e)
    {
	eventQueue.putEvent(e);
    }

    //Popups;

    public void goIntoPopup(Application app,
			    Area area,
			    int popupPlace,
			    EventLoopStopCondition stopCondition,
boolean noMultipleCopies)
    {
	if (app == null ||
	    area == null ||
	    stopCondition == null)
	    return;
	if (popupPlace != PopupManager.TOP &&
	    popupPlace != PopupManager.BOTTOM && 
	    popupPlace != PopupManager.LEFT &&
	    popupPlace != PopupManager.RIGHT)
	    return;
	if (noMultipleCopies)
	    popups.onNewInstanceLaunch(app, area.getClass());
	PopupEventLoopStopCondition popupStopCondition = new PopupEventLoopStopCondition(stopCondition);
	popups.addNewPopup(app, area, popupPlace, popupStopCondition, noMultipleCopies);
	if (screenContentManager.setPopupAreaActive())
	{
	    introduceActiveArea();
	    windowManager.redraw();
	}
	eventLoop(popupStopCondition);
	popups.removeLastPopup();
	screenContentManager.updatePopupState();
	needForIntroduction = true;
	windowManager.redraw();
    }

    public void runActionPopup()
    {
	ListPopup popup = new ListPopup(null, new FixedListPopupModel(actions.getActionsName()),
					systemApp.stringConstructor().runActionTitle(), systemApp.stringConstructor().runAction(), "");
	goIntoPopup(systemApp, popup, PopupManager.BOTTOM, popup.closing, true);
	if (popup.closing.cancelled())
	    return;
	if (!actions.run(popup.getText().trim()))
	    message(Langs.staticValue(Langs.NO_REQUESTED_ACTION));
    }

    public void setActiveArea(Object instance, Area area)
    {
	if (instance == null || area == null)
	    return;
	Application app = appInstances.getAppByInstance(instance);
	if (app == null)
	    return;//FIXME:Log message;
	apps.setActiveAreaOfApp(app, area);
	if (apps.isActiveApp(app) && !screenContentManager.isPopupAreaActive())
	    introduceActiveAreaNoEvent();
	windowManager.redraw();
    }

    public void onAreaNewHotPoint(Area area)
    {
	if (area != null && area == screenContentManager.getActiveArea())
	    windowManager.redrawArea(area);
    }

    public void onAreaNewContent(Area area)
    {
	windowManager.redrawArea(area);
    }

    public void onAreaNewName(Area area)
    {
	windowManager.redrawArea(area);
    }

    //May return -1;
    public int getAreaVisibleHeight(Area area)
    {
	if (area == null)
	    return -1;
	return windowManager.getAreaVisibleHeight(area);
    }

    public void message(String text)
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

    public void mainMenu()
    {
	MainMenuArea mainMenuArea = systemApp.createMainMenuArea(getMainMenuItems());
	EnvironmentSounds.play(EnvironmentSounds.MAIN_MENU);
	goIntoPopup(systemApp, mainMenuArea, PopupManager.LEFT, mainMenuArea.closing, true);
	if (mainMenuArea.closing.cancelled())
	    return;
	EnvironmentSounds.play(EnvironmentSounds.MAIN_MENU_ITEM);
	if (!actions.run(mainMenuArea.getSelectedActionName()))
	    message(Langs.staticValue(Langs.NO_REQUESTED_ACTION));
    }

    private String[] getMainMenuItems()
    {
	if (registry.getTypeOf(CoreRegistryValues.MAIN_MENU_CONTENT) != Registry.STRING)
	{
	    Log.error("environment", "registry has no value \'" + CoreRegistryValues.MAIN_MENU_CONTENT + "\' needed for proper main menu appearance");
	    return new String[0];
	}
	final String content = registry.getString(CoreRegistryValues.MAIN_MENU_CONTENT);
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

    public void introduceActiveArea()
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

    public void introduceActiveAreaNoEvent()
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

    public void increaseFontSize()
    {
	interaction.setDesirableFontSize(interaction.getFontSize() * 2); 
	windowManager.redraw();
	message(Langs.staticValue(Langs.FONT_SIZE) + " " + interaction.getFontSize());
    }

    public void decreaseFontSize()
    {
	interaction.setDesirableFontSize(interaction.getFontSize() / 2); 
	windowManager.redraw();
	message(Langs.staticValue(Langs.FONT_SIZE) + " " + interaction.getFontSize());
    }

    public void openFiles(String[] fileNames)
    {
	if (fileNames == null || fileNames.length < 1)
	    return;
	for(String s: fileNames)
	    if (s == null)
		return;
	fileTypes.openFileNames(fileNames);
    }

    public Registry  getRegistry()
    {
	return registry;
    }

    public PimManager getPimManager()
    {
	return pimManager;
    }

    public void popup(Popup popup)
    {
	if (popup == null ||
	    popup.getInstance() == null ||
	    popup.getStopCondition() == null)
	    return;
	Application app = appInstances.getAppByInstance(popup.getInstance());
	if (app == null)
	{
	    Log.warning("environment", "somebody tries to launch a popup with fake application instance");
	    return;
	}
	goIntoPopup(app, popup, PopupManager.BOTTOM, popup.getStopCondition(), popup.noMultipleCopies());
    }

    public void setClipboard(String[] value)
    {
	clipboard = value;
    }

    public String[] getClipboard()
    {
	return clipboard;
    }

    private KeyboardEvent translateKeyboardEvent(KeyboardEvent event)
    {
	if (event == null)
	    return null;
	if (!event.isCommand() || !event.withControlOnly())
	    return event;
	switch (event.getCommand())
	{
	case KeyboardEvent.ARROW_UP:
	    return new KeyboardEvent(true, KeyboardEvent.ALTERNATIVE_ARROW_UP, ' ');

	case KeyboardEvent.ARROW_DOWN:
	    return new KeyboardEvent(true, KeyboardEvent.ALTERNATIVE_ARROW_DOWN, ' ');
	case KeyboardEvent.ARROW_LEFT:
	    return new KeyboardEvent(true, KeyboardEvent.ALTERNATIVE_ARROW_LEFT, ' ');
	case KeyboardEvent.ARROW_RIGHT:
	    return new KeyboardEvent(true, KeyboardEvent.ALTERNATIVE_ARROW_RIGHT, ' ');

	case KeyboardEvent.PAGE_DOWN:
	    return new KeyboardEvent(true, KeyboardEvent.ALTERNATIVE_PAGE_DOWN, ' ');
	case KeyboardEvent.PAGE_UP:
	    return new KeyboardEvent(true, KeyboardEvent.ALTERNATIVE_PAGE_UP, ' ');
	case KeyboardEvent.HOME:
	    return new KeyboardEvent(true, KeyboardEvent.ALTERNATIVE_HOME, ' ');
	case KeyboardEvent.END:
	    return new KeyboardEvent(true, KeyboardEvent.ALTERNATIVE_END, ' ');
	default:
	    return event;
	}
    }

    public File openPopup(Object instance,
			    String name,
			    String prefix,
			    File defaultValue)
    {
	if (instance == null)
	    return null;
	Application app = appInstances.getAppByInstance(instance);
	if (app == null)
	    return null;
	return openPopupByApp(app, name, prefix, defaultValue);
    }

    private File openPopupByApp(Application app,
			    String name,
			    String prefix,
			    File defaultValue)
    {
	if (app == null)
	    return null;
	final String chosenName = (name != null && !name.trim().isEmpty())?name.trim():Langs.staticValue(Langs.OPEN_POPUP_NAME);
	final String chosenPrefix = (prefix != null && !prefix.trim().isEmpty())?prefix.trim():Langs.staticValue(Langs.OPEN_POPUP_PREFIX);
	File chosenDefaultValue = null;
	if (defaultValue == null)
	{
	    if (registry.getTypeOf(CoreRegistryValues.INSTANCE_USER_HOME_DIR) == Registry.STRING)
		chosenDefaultValue = new File(registry.getString(CoreRegistryValues.INSTANCE_USER_HOME_DIR)); else
		chosenDefaultValue = new File("/");//FIXME:System dependent slash;
	} else
	    chosenDefaultValue = defaultValue;
	FilePopup popup = new FilePopup(null, chosenName, chosenPrefix, chosenDefaultValue);
	goIntoPopup(app, popup, PopupManager.BOTTOM, popup.closing, true);
	if (popup.closing.cancelled())
	    return null;
	return popup.getFile();
    }
}
