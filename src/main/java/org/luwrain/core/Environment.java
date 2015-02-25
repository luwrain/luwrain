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

import org.luwrain.os.OperatingSystem;
import org.luwrain.speech.BackEnd;
import org.luwrain.core.events.*;
import org.luwrain.popups.*;
import org.luwrain.mainmenu.MainMenu;
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
    private OperatingSystem os;
    private Interaction interaction;
    private I18nImpl i18n;
    private Strings strings;
    private LaunchContext launchContext;
    private EventQueue eventQueue = new EventQueue();
    private InstanceManager appInstances;
    private Luwrain specialLuwrain = new Luwrain(this);
    private Luwrain privilegedLuwrain = new Luwrain(this);
    private ApplicationRegistry apps = new ApplicationRegistry();
    private PopupManager popups = new PopupManager();
    private ScreenContentManager screenContentManager;
    private WindowManager windowManager;
    private GlobalKeys globalKeys;
    private CommandManager commands = new CommandManager();
    private ShortcutManager shortcuts;
    private FileTypes fileTypes = new FileTypes(shortcuts);

    private boolean needForIntroduction = false;
    private boolean introduceApp = false;
    private String[] clipboard = null;

    public Environment(String[] cmdLine,
		       Registry registry,
		       org.luwrain.speech.BackEnd speech,
		       OperatingSystem os,
		       Interaction interaction,
		       Extension[] extensions,
		       LaunchContext launchContext)
    {
	this.cmdLine = cmdLine;
	this.registry = registry;
	this.speech = speech;
	this.os = os;
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
	if (os == null)
	    throw new NullPointerException("os may not be null");
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
	shortcuts = new ShortcutManager();
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

	globalKeys = new GlobalKeys(registry);
	globalKeys.loadFromRegistry();

	shortcuts.addBasicShortcuts();
	commands.addBasicCommands(this);

	for(Extension e: extensions)
	{
	    Shortcut[] s = e.getShortcuts();
	    if (s != null)
		for(Shortcut ss: s)
		    if (ss != null)
		    {
			if (!shortcuts.add(e, ss))
			    Log.warning("environment", "shortcut \'" + ss.getName() + "\' of extension " + e.getClass().getName() + " refused by  the shortcuts manager to be registered");
		    }
	}

	for(Extension e: extensions)
	{
	    Command[] cmds = e.getCommands(specialLuwrain);
	    if (cmds != null)
		for(Command c: cmds)
		    if (c != null)
		    {
			if (!commands.add(e, c))
			    Log.warning("environment", "command \'" + c.getName() + "\' of extension " + e.getClass().getName() + " refused by  the commands manager to be registered");
		    }
	}

	interaction.startInputEventsAccepting(this);
	EnvironmentSounds.play(Sounds.STARTUP);//FIXME:
		eventLoop(new InitialEventLoopStopCondition());
		interaction.stopInputEventsAccepting();
    }

    public void quit()
    {
	YesNoPopup popup = new YesNoPopup(new Luwrain(this), strings.quitPopupName(), strings.quitPopupText(), true);
	goIntoPopup(null, popup, PopupManager.BOTTOM, popup.closing, true, true);
	if (popup.closing.cancelled() || !popup.getResult())
	    return;
	InitialEventLoopStopCondition.shouldContinue = false;
    }

    //Application management;

    public void launchApp(String shortcutName, String[] args)
    {
	if (shortcutName == null)
	    throw new NullPointerException("shortcutName may not be null");
	if (shortcutName.trim().isEmpty())
	    throw new IllegalArgumentException("shortcutName may not be emptyL");
	if (args == null)
	    throw new NullPointerException("args may not be null");
	for(int i = 0;i < args.length;++i)
	    if (args[i] == null)
		throw new NullPointerException("args[" + i + "] may not be null");
	Application[] app = shortcuts.prepareApp(shortcutName, args);
	if (app != null)
	    for(Application a: app)
		if (a != null)
		    launchApp(a);
    }

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
	    message(strings.appLaunchNoEnoughMemory(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	catch (Throwable e)
	{
	    appInstances.releaseInstance(o);
	    e.printStackTrace();
	    //FIXME:Log warning;
	    message(strings.appLaunchUnexpectedError(), Luwrain.MESSAGE_ERROR);
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
	//	introduceActiveArea();
	needForIntroduction = true;
	introduceApp = true;
    }

    public void closeApp(Object instance)
    {
	if (instance == null)
	    throw new NullPointerException("instance may not be null");
	if (instance == specialLuwrain || instance == privilegedLuwrain)
	    throw new IllegalArgumentException("trying to close an application through specialLuwrain or privilegedLuwrain objects");
	Application app = appInstances.getAppByInstance(instance);
	if (app == null)
	    throw new NullPointerException("trying to close an application through an unknown instance object");
	if (popups.hasPopupOfApp(app))
	{
	    message(strings.appCloseHasPopup(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	apps.releaseApp(app);
	appInstances.releaseInstance(instance);
	screenContentManager.updatePopupState();
	windowManager.redraw();
	//	introduceActiveArea();
	needForIntroduction = true;
	introduceApp = true;
    }

    public void switchNextApp()
    {
	/*
	if (screenContentManager.isPopupAreaActive())
	{
	    EnvironmentSounds.play(Sounds.EVENT_NOT_PROCESSED);//FIXME:Probably not well suited sound; 
	    return;
	}
	*/
	apps.switchNextInvisible();
	screenContentManager.updatePopupState();
	windowManager.redraw();
	//	introduceActiveArea();
	needForIntroduction = true;
	introduceApp = true;
    }

    public void switchNextArea()
    {
	screenContentManager.activateNextArea();
	windowManager.redraw();
	introduceActiveArea();
    }

    //Events;

    private void eventLoop(EventLoopStopCondition stopCondition)
    {
	if (stopCondition == null)
	    throw new NullPointerException("stopCondition may not be null");
	while(stopCondition.continueEventLoop())
	{
	    final Event event = eventQueue.takeEvent();
	    if (event == null)
		continue;
	    switch (event.type())
	    {
	    case Event.KEYBOARD_EVENT:
		if (!onKeyboardEvent(translateKeyboardEvent((KeyboardEvent)event)))
		{
		    eventQueue.onceAgain(event);
		    continue;
		}
		break;
	    case Event.ENVIRONMENT_EVENT:
		if (!onEnvironmentEvent((EnvironmentEvent)event))
		{
		    eventQueue.onceAgain(event);
		    continue;
		}
		break;
	    default:
		Log.warning("environment", "the event of an unknown type:" + event.type());
	    }
	    introduce(stopCondition);
		}
    }

    private void introduce(EventLoopStopCondition stopCondition)
    {
	if (stopCondition == null)
	    throw new NullPointerException("stopCondition may not be null");
	if (needForIntroduction && stopCondition.continueEventLoop())
	{
	    if (introduceApp)
		introduceActiveApp(); else
		introduceActiveArea();
	}
	needForIntroduction = false;
	introduceApp = false;
    }

    private boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	if (keyboardEventForEnvironment(event))
	    return true;
	//Open popup protection for non-popup areas of the same application;
	final Application nonPopupDest = screenContentManager.isNonPopupDest();
	if (nonPopupDest != null && popups.hasAny())//Non-popup area activated but some popup opened;
	{
	    final Application lastPopupApp = popups.getAppOfLastPopup();
	    if (lastPopupApp == null || popups.isWeakLastPopup())//Weak environment popup;
	    {
		popups.cancelLastPopup();
		return false;
	    }
	    if (nonPopupDest == lastPopupApp)//User tries to deal with app having opened popup;
	    {
		if (popups.isWeakLastPopup())
		{
		    //Weak popup;
		    popups.cancelLastPopup();
		    return false;
		} else
		{
		    //Strong popup;
		    playSound(Sounds.EVENT_NOT_PROCESSED);
		    message(strings.appBlockedByPopup(), Luwrain.MESSAGE_REGULAR);
		    return true;
		}
	    } else
		if (popups.hasPopupOfApp(nonPopupDest))
		{
		    //The destination application has a popup and it is somewhere in the stack, it is not a top popup;
		    playSound(Sounds.EVENT_NOT_PROCESSED);
		    message(strings.appBlockedByPopup(), Luwrain.MESSAGE_REGULAR);
		    return true;
		}
	}
	//OK, now we are sure that the application has no popups;
	int res = ScreenContentManager.EVENT_NOT_PROCESSED;
	try {
	    res = screenContentManager.onKeyboardEvent(event);
	}
	catch (Throwable e)
	{
	    Log.error("environment", "keyboard event throws an exception:" + e.getMessage());
	    e.printStackTrace();
	    playSound(Sounds.EVENT_NOT_PROCESSED);
	    return true;
	}
	switch(res)
	{
	case ScreenContentManager.EVENT_NOT_PROCESSED:
	    playSound(Sounds.EVENT_NOT_PROCESSED);
	    break;
	case ScreenContentManager.NO_APPLICATIONS:
	    playSound(Sounds.NO_APPLICATIONS);
	    message(strings.startWorkFromMainMenu(), Luwrain.MESSAGE_REGULAR);
	    break;
	}
	return true;
    }

    private boolean keyboardEventForEnvironment(KeyboardEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	final String commandName = globalKeys.getCommandName(event);
	if (commandName != null)
	{
	    if (!commands.run(commandName, new Luwrain(this)))
		message(strings.noCommand(), Luwrain.MESSAGE_ERROR);
	    return true;
	}
	if (event.isCommand())
	{
	    final int code = event.getCommand();
	    if (code == KeyboardEvent.SHIFT ||
		code == KeyboardEvent.CONTROL ||
		code == KeyboardEvent.LEFT_ALT ||
		code == KeyboardEvent.RIGHT_ALT)
		return true;
	}
	if (!event.isCommand() &&
	    event.getCharacter() == 'x' &&
	    event.withLeftAltOnly())
	{
	    showCommandPopup();
	    return true;
	}
	return false;
    }

    private boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	//ThreadSyncEvent goes anyway;
	    if (event.getCode() == EnvironmentEvent.THREAD_SYNC)
	    {
		onThreadSyncEvent(event);
		return true;
	    }
	//Open popup protection for non-popup areas of the same application;
	final Application nonPopupDest = screenContentManager.isNonPopupDest();
	if (nonPopupDest != null && popups.hasAny())//Non-popup area activated but some popup opened;
	{
	    final Application lastPopupApp = popups.getAppOfLastPopup();
	    if (lastPopupApp == null || popups.isWeakLastPopup())//Weak environment popup;
	    {
		popups.cancelLastPopup();
		return false;
	    }
	    if (nonPopupDest == lastPopupApp)//User tries to deal with app having opened popup;
	    {
		if (popups.isWeakLastPopup())
		{
		    //Weak popup;
		    popups.cancelLastPopup();
		    return false;
		} else
		{
		    //Strong popup;
		    if (onBasicEnvironmentEvent(event))
			return true;
		    playSound(Sounds.EVENT_NOT_PROCESSED);
		    message(strings.appBlockedByPopup(), Luwrain.MESSAGE_REGULAR);
		    return true;
		}
	    } else
		if (popups.hasPopupOfApp(nonPopupDest))
		{
		    //The destination application has a popup and it is somewhere in the stack, it is not a top popup;
		    if (onBasicEnvironmentEvent(event))
			return true;
		    playSound(Sounds.EVENT_NOT_PROCESSED);
		    message(strings.appBlockedByPopup(), Luwrain.MESSAGE_REGULAR);
		    return true;
		}
	}
	//OK, now we are sure that the application has no popups;
	int res = ScreenContentManager.EVENT_NOT_PROCESSED;
	try {
	    //Paste;
	    if (event.getCode() == EnvironmentEvent.PASTE)
	    {
		if (clipboard == null || clipboard.length < 1)
		    return true;
		screenContentManager.onEnvironmentEvent(new InsertEvent(clipboard));
		return true;
	    }
	    //Open;
	    if (event.getCode() == EnvironmentEvent.OPEN)
	    {
		if (screenContentManager.onEnvironmentEvent(event) == ScreenContentManager.EVENT_PROCESSED)
		    return true;
		showOpenPopup();
		return true;
	    }
		res = screenContentManager.onEnvironmentEvent(event);
	}
	catch (Throwable e)
	{
	    Log.error("environment", "environment event throws an exception:" + e.getMessage());
	    e.printStackTrace();
	    playSound(Sounds.EVENT_NOT_PROCESSED);
	    return true;
	}
	switch(res)
	{
	case ScreenContentManager.EVENT_NOT_PROCESSED:
	    playSound(Sounds.EVENT_NOT_PROCESSED);
	    break;
	case ScreenContentManager.NO_APPLICATIONS:
	    playSound(Sounds.NO_APPLICATIONS);
	    message(strings.startWorkFromMainMenu(), Luwrain.MESSAGE_REGULAR);
	    break;
	}
	return true;
    }

    private void onThreadSyncEvent(EnvironmentEvent event)
    {
	ThreadSyncEvent threadSync = (ThreadSyncEvent)event;
	final Area destArea = threadSync.getDestArea();
	try {
	    if (destArea != null)
		destArea.onEnvironmentEvent(event);
		}
	catch (Throwable t)
	{
	    Log.error("environment", "exception while transmitting thread sync event:" + t.getMessage());
	}
    }

    //Called if the application is blocked with a popup;
    private boolean onBasicEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.OPEN)
	{
	    showOpenPopup();
	    return true;
	}
	return false;
    }

    @Override public void enqueueEvent(Event e)
    {
	eventQueue.putEvent(e);
    }

    private void goIntoPopup(Application app,
			    Area area,
			    int popupPlace,
			    EventLoopStopCondition stopCondition,
			     boolean noMultipleCopies,
			     boolean isWeakPopup)
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
	popups.addNew(app, area, popupPlace, popupStopCondition, noMultipleCopies, isWeakPopup);
	if (screenContentManager.setPopupAreaActive())
	{
	    introduceActiveArea();
	    windowManager.redraw();
	}
	eventLoop(popupStopCondition);
	popups.removeLast();
	screenContentManager.updatePopupState();
	needForIntroduction = true;
	windowManager.redraw();
    }

    public void setActiveArea(Object instance, Area area)
    {
	if (instance == null)
	    throw new NullPointerException("instance may not be null");
	if (area == null)
	    throw new NullPointerException("area may not be null");
	if (instance == specialLuwrain)
	    throw new IllegalArgumentException("instance doesn\'t have enough privilege to change active areas");
	if (instance == privilegedLuwrain)
	    throw new NullPointerException("using of privilegedLuwrain object doesn\'t allow changing of active area");
	final Application app = appInstances.getAppByInstance(instance);
	if (app == null)
	    throw new IllegalArgumentException("an unknown application instance is provided");
	apps.setActiveAreaOfApp(app, area);
	if (apps.isActiveApp(app) && !screenContentManager.isPopupAreaActive())
	    needForIntroduction = true;
	    //	    introduceActiveArea();
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

    public void message(String text, int semantic)
    {
	if (text == null || text.trim().isEmpty())
	    return;
	needForIntroduction = false;
	switch(semantic)
	{
	case Luwrain.MESSAGE_ERROR:
	    playSound(Sounds.GENERAL_ERROR);
	    break;
	case Luwrain.MESSAGE_OK:
	    //FIXME:
	    break;
	}
	speech.silence();
	speech.say(text, Luwrain.PITCH_MESSAGE);
	interaction.startDrawSession();
	interaction.clearRect(0, interaction.getHeightInCharacters() - 1, interaction.getWidthInCharacters() - 1, interaction.getHeightInCharacters() - 1);
	interaction.drawText(0, interaction.getHeightInCharacters() - 1, text);
	interaction.endDrawSession();
    }

    private void introduceActiveApp()
    {
	final Application app = apps.getActiveApp();
	if (app == null)
	{
	    playSound(Sounds.NO_APPLICATIONS);
	    speech.silence(); 
	    speech.say(strings.noLaunchedApps());
	    return;
	}
	final String name = app.getAppName();
	speech.silence();
	playSound(Sounds.INTRO_APP);
	if (name != null && !name.trim().isEmpty())
	    speech.say(name); else
	    speech.say(app.getClass().getName());
    }

    private void introduceActiveArea()
    {
	//	needForIntroduction = false;
	Area activeArea = screenContentManager.getActiveArea();
	if (activeArea == null)
	{
	    speech.silence(); 
	    playSound(Sounds.NO_APPLICATIONS);
	    speech.say(strings.noLaunchedApps());
	    return;
	}
	if (activeArea.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.INTRODUCE)))
	    return;
	speech.silence();
	playSound(activeArea instanceof Popup?Sounds.INTRO_POPUP:Sounds.INTRO_REGULAR);
	speech.say(activeArea.getName());
    }

    /*
    private void introduceActiveAreaNoEvent()
    {
	needForIntroduction = false;
	Area activeArea = screenContentManager.getActiveArea();
	if (activeArea == null)
	{
	    EnvironmentSounds.play(Sounds.NO_APPLICATIONS);
	    speech.silence();
	    speech.say(strings.noLaunchedApps());
	    return;
	}
	speech.say(activeArea.getName());
    }
    */

    public void increaseFontSize()
    {
	interaction.setDesirableFontSize(interaction.getFontSize() * 2); 
	windowManager.redraw();
	message(strings.fontSize(interaction.getFontSize()), Luwrain.MESSAGE_REGULAR);
    }

    public void decreaseFontSize()
    {
	interaction.setDesirableFontSize(interaction.getFontSize() / 2); 
	windowManager.redraw();
	message(strings.fontSize(interaction.getFontSize()), Luwrain.MESSAGE_REGULAR);
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
	if (popup == null)
	    throw new NullPointerException("popup may not be null");
	final Object instance = popup.getLuwrainObject();
	final EventLoopStopCondition stopCondition = popup.getStopCondition();
	if (instance == null)
	    throw new NullPointerException("instance may not be null");
	if (stopCondition == null)
	    throw new NullPointerException("stopCondition may not be null");
	if (instance == specialLuwrain)
	    throw new IllegalArgumentException("popup has provided the luwrain object which hasn\'t enough permission to open a popup");
	if (instance == privilegedLuwrain)
	{
	    goIntoPopup(null, popup, PopupManager.BOTTOM, stopCondition, popup.noMultipleCopies(), popup.isWeakPopup());
	return;
	}
	final Application app = appInstances.getAppByInstance(instance);
	if (app == null)
	    throw new IllegalArgumentException("the luwrain object provided by a popup is fake");
	goIntoPopup(app, popup, PopupManager.BOTTOM, stopCondition, popup.noMultipleCopies(), popup.isWeakPopup());
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

    public void mainMenu()
    {
	MainMenu mainMenu = new org.luwrain.mainmenu.Builder(specialLuwrain, specialLuwrain).build();
	playSound(Sounds.MAIN_MENU);
	goIntoPopup(null, mainMenu, PopupManager.LEFT, mainMenu.closing, true, true);
	if (mainMenu.closing.cancelled())
	    return;
	playSound(Sounds.MAIN_MENU_ITEM);
	mainMenu.getSelectedItem().doAction(specialLuwrain);
    }

    private File openPopup()
    {
	final FilePopup popup = new FilePopup(privilegedLuwrain,
					      strings.openPopupName(),
					      strings.openPopupPrefix(),
					      launchContext.userHomeDirAsFile());
	goIntoPopup(null, popup, PopupManager.BOTTOM, popup.closing, true, true);
	if (popup.closing.cancelled())
	    return null;
	return popup.getFile();
    }

    private void showOpenPopup()
    {
	final File f = openPopup();
	if (f == null)
	    return;
	final String[] fileNames = new String[1];
	fileNames[0] = f.getAbsolutePath();
	openFiles(fileNames);
    }

    public boolean runCommand(String command)
    {
	if (command == null)
	    throw new NullPointerException("command may not be null");
	if (command.trim().isEmpty())
	    return false;
	return commands.run(command.trim(), specialLuwrain);
    }

    private void showCommandPopup()
    {
	EditListPopup popup = new EditListPopup(new Luwrain(this), new FixedListPopupModel(commands.getCommandsName()),
					strings.commandPopupName(), strings.commandPopupPrefix(), "");
	goIntoPopup(null, popup, PopupManager.BOTTOM, popup.closing, true, true);
	if (popup.closing.cancelled())
	    return;
	    if (!commands.run(popup.getText().trim(), new Luwrain(this)))
		message(strings.noCommand(), Luwrain.MESSAGE_ERROR);
    }

    public OperatingSystem os()
    {
	return os;
    }
}
