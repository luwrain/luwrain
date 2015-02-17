/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.luwrain.speech.BackEnd;
import org.luwrain.mainmenu.MainMenu;
import org.luwrain.core.events.*;
import org.luwrain.popups.*;
import org.luwrain.sounds.EnvironmentSounds;
import org.luwrain.util.RegistryAutoCheck;

class Environment implements EventConsumer
{
    private final static String STRINGS_OBJECT_NAME = "luwrain.environment";
    private static final String DEFAULT_MAIN_MENU_CONTENT = "control:registry";

    private String[] cmdLine;
    private Registry registry;
    private RegistryAutoCheck registryAutoCheck;
    private RegistryKeys registryKeys;
    private org.luwrain.speech.BackEnd speech;
    private Extension[] extensions;
    private Interaction interaction;
    private I18nImpl i18n;
    private Strings strings;
    private LaunchContext launchContext;
    private EventQueue eventQueue = new EventQueue();
    private InstanceManager appInstances;
    private ApplicationRegistry apps = new ApplicationRegistry();
    private PopupManager popups = new PopupManager();
    private ScreenContentManager screenContentManager;
    private WindowManager windowManager;
    private GlobalKeys globalKeys;
    private CommandManager commands = new CommandManager();
    private ShortcutManager shortcuts;

    private FileTypes fileTypes = new FileTypes(shortcuts);
    private boolean needForIntroduction = false;
    private String[] clipboard = null;

    public Environment(String[] cmdLine,
		       Registry registry,
		       org.luwrain.speech.BackEnd speech,
		       Interaction interaction,
		       Extension[] extensions,
		       LaunchContext launchContext)
    {
	this.cmdLine = cmdLine;
	this.registry = registry;
	this.speech = speech;
	this.interaction = interaction;
	this.extensions = extensions;
	this.launchContext = launchContext;
	if (cmdLine == null)
	    throw new NullPointerException("cmdLine may not be null");
	for(int i = 0;i < cmdLine.length;++i)
	    if (cmdLine[i] == null)
		throw new NullPointerException("cmdLine[" + i + "] may not be null");
	if (registry == null)
	    throw new NullPointerException("registry may not be null");
	if (speech == null)
	    throw new NullPointerException("speech may not be null");
	if (interaction == null)
	    throw new NullPointerException("interaction may not be null");
	if (extensions == null)
	    throw new NullPointerException("exceptions may not be null");
	for(int i = 0;i < extensions.length;++i)
	    if (extensions[i] == null)
		throw new NullPointerException("extensions[" + i + "] may not be null");
	if (launchContext == null)
	    throw new NullPointerException("launchContext may not be null");
    }

    public void  run()
    {
	registryKeys = new RegistryKeys();
	registryAutoCheck = new RegistryAutoCheck(registry, "environment");
	i18n = new I18nImpl();
	EnvironmentSounds.init(registry, launchContext);
	appInstances = new InstanceManager(this);
	shortcuts = new ShortcutManager(this);
	screenContentManager = new ScreenContentManager(apps, popups);
	windowManager = new WindowManager(interaction, screenContentManager);

	for(Extension e: extensions)
	    e.i18nExtension(i18n);
	if (!i18n.chooseLang(launchContext.lang()))
	{
	    Log.fatal("environment", "unable to choose matching language for i18n, requested language is \'" + launchContext.lang() + "\'");
	    return;
	}
	strings = (Strings)i18n.getStrings(STRINGS_OBJECT_NAME);

	if (launchContext.lang().equals("ru"))//FIXME:
	    Langs.setCurrentLang(new org.luwrain.langs.ru.Language());

	globalKeys = new GlobalKeys(registry);
	globalKeys.loadFromRegistry();

	commands.addBasicCommands(this);
	for(Extension e: extensions)
	{
	    Command[] cmds = e.getCommands();
	    if (cmds != null)
		for(Command c: cmds)
		    if (c != null)
		    {
			if (!commands.add(e, c))
			    Log.warning("environment", "command \'" + c.getName() + "\' of extension " + e.getClass().getName() + " refused by  the commands manager to be registered");
		    }
	}


	shortcuts.fillWithStandardShortcuts();
	interaction.startInputEventsAccepting(this);
	EnvironmentSounds.play(Sounds.STARTUP);//FIXME:
		eventLoop(new InitialEventLoopStopCondition());
		interaction.stopInputEventsAccepting();
    }

    public void quit()
    {
	YesNoPopup popup = new YesNoPopup(new Luwrain(this), strings.quitPopupName(), strings.quitPopupText(), true);
	goIntoPopup(null, popup, PopupManager.BOTTOM, popup.closing, true);
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
	Luwrain o = appInstances.registerApp(app);
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
	    message(strings.appLaunchNoEnoughMemory());
	    return;
	}
	catch (Throwable e)
	{
	    appInstances.releaseInstance(o);
	    e.printStackTrace();
	    //FIXME:Log warning;
	    message(strings.appLaunchUnexpectedError());
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
	    message(strings.appCloseHasPopup());
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
	    EnvironmentSounds.play(Sounds.EVENT_NOT_PROCESSED);//FIXME:Probably not well suited sound; 
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
	String commandName = globalKeys.getActionName(event);
	if (commandName != null)
	{
	    if (!commands.run(commandName, new Luwrain(this)))
		message(strings.noCommand());//FIXME:Error mark;
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
	    runCommandPopup();
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
	    EnvironmentSounds.play(Sounds.EVENT_NOT_PROCESSED);
	    return;
	}
	switch(res)
	{
	case ScreenContentManager.EVENT_NOT_PROCESSED:
	    EnvironmentSounds.play(Sounds.EVENT_NOT_PROCESSED);
	    break;
	case ScreenContentManager.NO_APPLICATIONS:
	    EnvironmentSounds.play(Sounds.NO_APPLICATIONS);
	    message(strings.startWorkFromMainMenu());
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
		File f = openPopupByApp(null, null, null, null);
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
	    EnvironmentSounds.play(Sounds.EVENT_NOT_PROCESSED);
	    return;
	}
	switch(res)
	{
	case ScreenContentManager.EVENT_NOT_PROCESSED:
	    EnvironmentSounds.play(Sounds.EVENT_NOT_PROCESSED);
	    break;
	case ScreenContentManager.NO_APPLICATIONS:
	    EnvironmentSounds.play(Sounds.NO_APPLICATIONS);
	    message(strings.startWorkFromMainMenu());
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
	if (area == null ||
	    stopCondition == null)
	    return;
	if (popupPlace != PopupManager.TOP &&
	    popupPlace != PopupManager.BOTTOM && 
	    popupPlace != PopupManager.LEFT &&
	    popupPlace != PopupManager.RIGHT)
	{
	    Log.warning("environment", "trying to get a popup with illegal place (" + popupPlace + ")");
	    return;
	}
	if (noMultipleCopies)
	    popups.onNewInstanceLaunch(app, area.getClass());
	PopupEventLoopStopCondition popupStopCondition = new PopupEventLoopStopCondition(stopCondition);
	popups.addNewP(app, area, popupPlace, popupStopCondition, noMultipleCopies);
	if (screenContentManager.setPopupAreaActive())
	{
	    Log.debug("environment", "screen content manager accepted new popup");
	    introduceActiveArea();
	    windowManager.redraw();
	}
	Log.debug("environment", "starting new event loop for popup");
	eventLoop(popupStopCondition);
	Log.debug("environment", "new event loop finished");
	popups.removeLast();
	screenContentManager.updatePopupState();
	needForIntroduction = true;
	windowManager.redraw();
    }

    public void runCommandPopup()
    {
	ListPopup popup = new ListPopup(new Luwrain(this), new FixedListPopupModel(commands.getCommandsName()),
					strings.commandPopupName(), strings.commandPopupPrefix(), "");
	goIntoPopup(null, popup, PopupManager.BOTTOM, popup.closing, true);
	    Log.debug("environment", "after popup");
	if (popup.closing.cancelled())
	    return;
	    Log.debug("environment", "popup " + popup.getText());
	    if (!commands.run(popup.getText().trim(), new Luwrain(this)))
		message(strings.noCommand());
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
	speech.silence();
	speech.say(text);//, BackEnd.LOW);
	interaction.startDrawSession();
	interaction.clearRect(0, interaction.getHeightInCharacters() - 1, interaction.getWidthInCharacters() - 1, interaction.getHeightInCharacters() - 1);
	interaction.drawText(0, interaction.getHeightInCharacters() - 1, text);
	interaction.endDrawSession();
    }

    public void mainMenu()
    {
	MainMenu mainMenu = new org.luwrain.mainmenu.Builder(new Luwrain(this)).build();
	EnvironmentSounds.play(Sounds.MAIN_MENU);
	goIntoPopup(null, mainMenu, PopupManager.LEFT, mainMenu.closing, true);
	if (mainMenu.closing.cancelled())
	    return;
	EnvironmentSounds.play(Sounds.MAIN_MENU_ITEM);
	/*
	if (!actions.run(mainMenu.getSelectedActionName()))
	    message(Langs.staticValue(Langs.NO_REQUESTED_ACTION));
	*/
    }

    public void introduceActiveArea()
    {
	needForIntroduction = false;
	Area activeArea = screenContentManager.getActiveArea();
	if (activeArea == null)
	{
	    EnvironmentSounds.play(Sounds.NO_APPLICATIONS);
	    speech.say(strings.noLaunchedApps());
	    return;
	}
	if (activeArea.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.INTRODUCE)))
	    return;
	speech.say(activeArea.getName());
    }

    public void introduceActiveAreaNoEvent()
    {
	needForIntroduction = false;
	Area activeArea = screenContentManager.getActiveArea();
	if (activeArea == null)
	{
	    EnvironmentSounds.play(Sounds.NO_APPLICATIONS);
	    speech.say(strings.noLaunchedApps());
	    return;
	}
	speech.say(activeArea.getName());
    }

    public void increaseFontSize()
    {
	interaction.setDesirableFontSize(interaction.getFontSize() * 2); 
	windowManager.redraw();
	message(strings.fontSize(interaction.getFontSize()));
    }

    public void decreaseFontSize()
    {
	interaction.setDesirableFontSize(interaction.getFontSize() / 2); 
	windowManager.redraw();
	message(strings.fontSize(interaction.getFontSize()));
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

    public Registry  registry()
    {
	return registry;
    }

    public Object getPimManager()
    {
	return null;
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
	final String chosenName = (name != null && !name.trim().isEmpty())?name.trim():strings.openPopupName();
	final String chosenPrefix = (prefix != null && !prefix.trim().isEmpty())?prefix.trim():strings.openPopupPrefix();
	File chosenDefaultValue = null;
	if (defaultValue == null)
		chosenDefaultValue = launchContext.userHomeDirAsFile(); else
	    chosenDefaultValue = defaultValue;
	FilePopup popup = new FilePopup(null, chosenName, chosenPrefix, chosenDefaultValue);
	goIntoPopup(app, popup, PopupManager.BOTTOM, popup.closing, true);
	if (popup.closing.cancelled())
	    return null;
	return popup.getFile();
    }

    public BackEnd speech()
    {
	return speech;
    }

    /**
     * @return true if this hint should be spoken as well
     */
    public boolean onStandardHint(int code)
    {
	return true;
    }

    public I18n i18n()
    {
	return i18n;
    }

    public void playSound(int code)
    {
	EnvironmentSounds.play(code);
    }

    public LaunchContext launchContext()
    {
	return launchContext;
    }
}
