/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.luwrain.os.OperatingSystem;
import org.luwrain.hardware.*;
import org.luwrain.speech.BackEnd;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.core.extensions.*;
import org.luwrain.popups.*;
import org.luwrain.mainmenu.MainMenu;
import org.luwrain.sounds.EnvironmentSounds;
import org.luwrain.util.*;

class Environment implements EventConsumer
{
    private final static String STRINGS_OBJECT_NAME = "luwrain.environment";
    private static final String DEFAULT_MAIN_MENU_CONTENT = "control:registry";

    private String[] cmdLine;
    private EventQueue eventQueue = new EventQueue();
    private Registry registry;
    private org.luwrain.speech.BackEnd speech;
    private OperatingSystem os;
    private Interaction interaction;

    private org.luwrain.core.extensions.Manager extensions;
    private InterfaceManager interfaces = new InterfaceManager(this);
    private org.luwrain.desktop.App desktop = new org.luwrain.desktop.App();
    private AppManager apps;
    private ScreenContentManager screenContentManager;
    private WindowManager windowManager;
    private GlobalKeys globalKeys;
    private FileTypes fileTypes = new FileTypes();

    private I18nImpl i18n = new I18nImpl();
    private CommandManager commands = new CommandManager();
    private ShortcutManager shortcuts = new ShortcutManager();
    private SharedObjectManager sharedObjects = new SharedObjectManager();
    private UniRefProcManager uniRefProcs = new UniRefProcManager();

    private HeldData clipboard = null;
    private LaunchContext launchContext;
    private Strings strings;
    private RegistryAutoCheck registryAutoCheck;
    private RegistryKeys registryKeys;

    private boolean needForIntroduction = false;
    private boolean introduceApp = false;
    private Luwrain speechProc;

    public Environment(String[] cmdLine,
		       Registry registry,
		       org.luwrain.speech.BackEnd speech,
		       OperatingSystem os,
		       Interaction interaction,
		       LaunchContext launchContext)
    {
	this.cmdLine = cmdLine;
	this.registry = registry;
	this.speech = speech;
	this.os = os;
	this.interaction = interaction;
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
	if (launchContext == null)
	    throw new NullPointerException("launchContext may not be null");
    }

    public void run()
    {
	init();
	interaction.startInputEventsAccepting(this);
	EnvironmentSounds.play(Sounds.STARTUP);//FIXME:
	try {
	    Thread.sleep(1000);
	} catch (InterruptedException ie){}
	message(strings.startWorkFromMainMenu(), Luwrain.MESSAGE_REGULAR);
	eventLoop(new InitialEventLoopStopCondition());
	interaction.stopInputEventsAccepting();
	extensions.close();
    }

    private void init()
    {
	speechProc = new Luwrain(this);
	desktop.onLaunch(interfaces.requestNew(desktop));
	apps = new AppManager(desktop);
	screenContentManager = new ScreenContentManager(apps);
	windowManager = new WindowManager(interaction, screenContentManager);
	extensions = new org.luwrain.core.extensions.Manager(interfaces);
	extensions.load();
	globalKeys = new GlobalKeys(registry);
	globalKeys.loadFromRegistry();
	fileTypes.load(registry);

	initI18n();
	initObjects();
	desktop.ready(i18n.getChosenLangName(), i18n.getStrings(org.luwrain.desktop.App.STRINGS_NAME));
	EnvironmentSounds.init(registry, launchContext);
	registryKeys = new RegistryKeys();
	registryAutoCheck = new RegistryAutoCheck(registry, "environment");
    }

    private void initObjects()
    {
	final Command[] standardCommands = StandardCommands.createStandardCommands(this);
	for(Command sc: standardCommands)
	    commands.add(new Luwrain(this), sc);//FIXME:
	commands.addOsCommands(interfaces.getObjForEnvironment(), registry);

	final UniRefProc[] standardUniRefProcs = StandardUniRefProcs.createStandardUniRefProcs(strings);
	for(UniRefProc proc: standardUniRefProcs)
	    uniRefProcs.add(new Luwrain(this), proc);//FIXME:

	final LoadedExtension[] allExt = extensions.getAllLoadedExtensions();
	for(LoadedExtension e: allExt)
	{
	    final Extension ext = e.ext;
	    //Shortcuts;
	    for(Shortcut s: e.shortcuts)
		if (s != null)
		{
		    if (!shortcuts.add(s))
			Log.warning("environment", "shortcut \'" + s.getName() + "\' of extension " + e.getClass().getName() + " has been refused by  the shortcuts manager to be registered");
		}
	    //Shared objects
	    for(SharedObject s: e.sharedObjects)
		if (s != null)
		{
		    if (!sharedObjects.add(ext, s))
			    Log.warning("environment", "the shared object \'" + s.getName() + "\' of extension " + e.getClass().getName() + " has been refused by  the shared objects manager to be registered");
		}
	    //UniRefProcs
	    for(UniRefProc p: e.uniRefProcs)
		if (p != null)
		{
		    if (!uniRefProcs.add(e.luwrain, p))
			    Log.warning("environment", "the uniRefProc \'" + p.getUniRefType() + "\' of extension " + e.getClass().getName() + " has been refused by  the uniRefProcs manager to be registered");
		}
	    //Commands
	    for(Command c: e.commands)
		if (c != null)
		{
		    if (!commands.add(e.luwrain, c))
			Log.warning("environment", "command \'" + c.getName() + "\' of extension " + e.getClass().getName() + " has been refused by  the commands manager to be registered");
		}
	}
    }

    private void initI18n()
    {
	final LoadedExtension[] allExt = extensions.getAllLoadedExtensions();
	for(LoadedExtension e: allExt)
	    try {
		e.ext.i18nExtension(e.luwrain, i18n);
	    }
	    catch (Exception ee)
	    {
		Log.error("environment", "extension " + e.getClass().getName() + " has thrown an exception on i18n:" + ee.getMessage());
		ee.printStackTrace();
	    }
	if (!i18n.chooseLang(launchContext.lang()))
	{
	    Log.fatal("environment", "unable to choose matching language for i18n, requested language is \'" + launchContext.lang() + "\'");
	    return;
	}
	strings = (Strings)i18n.getStrings(STRINGS_OBJECT_NAME);
    }

    public void quit()
    {
	YesNoPopup popup = new YesNoPopup(new Luwrain(this), strings.quitPopupName(), strings.quitPopupText(), true);
	popupImpl(null, popup, Popup.BOTTOM, popup.closing, true, true);
	if (popup.closing.cancelled() || !popup.result())
	    return;
	InitialEventLoopStopCondition.shouldContinue = false;
    }

    public void launchAppIface(String shortcutName, String[] args)
    {
	if (shortcutName == null)
	    throw new NullPointerException("shortcutName may not be null");
	if (shortcutName.trim().isEmpty())
	    throw new IllegalArgumentException("shortcutName may not be emptyL");
	final String[] argsNotNull = org.luwrain.util.Strings.notNullArray(args);
	final Application[] app = shortcuts.prepareApp(shortcutName, argsNotNull);
	if (app != null)
	    for(Application a: app)
		if (a != null)
		    launchApp(a);
    }

    public void launchApp(Application app)
    {
	NullCheck.notNull(app, "app");
	Log.debug("core", "launching app " + app.getClass().getName());
	System.gc();
	printMemInfo();
	final Luwrain o = interfaces.requestNew(app);
	try {
	    if (!app.onLaunch(o))
	    {
		interfaces.release(o);
		return;
	    }
	}
	catch (OutOfMemoryError e)
	{
	    interfaces.release(o);
	    message(strings.appLaunchNoEnoughMemory(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	catch (Throwable e)
	{
	    interfaces.release(o);
		Log.info("core", "application " + app.getClass().getName() + " has thrown an exception on onLaunch()" + e.getMessage());
	    e.printStackTrace();
	    message(strings.appLaunchUnexpectedError(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	if (!apps.newApp(app))
	    return; 
	screenContentManager.updatePopupState();
	windowManager.redraw();
	needForIntroduction = true;
	introduceApp = true;
    }

    public void closeApp(Luwrain instance)
    {
	NullCheck.notNull(instance, "instance");
	if (instance == interfaces.getObjForEnvironment())
	    throw new IllegalArgumentException("Trying to close an application through the special interface object designed for environment operations");
	final Application app = interfaces.findApp(instance);
	if (app == null)
	    throw new IllegalArgumentException("Trying to close an application through an unknown interface object or this object doesn\'t identify an application");
	if (app == desktop)
	    throw new IllegalArgumentException("Trying to close a desktop");
	if (apps.hasPopupOfApp(app))
	{
	    message(strings.appCloseHasPopup(), Luwrain.MESSAGE_ERROR);
	    return;
	}
	apps.closeApp(app);
	interfaces.release(instance);
	onNewScreenLayout();
	setAppIntroduction();
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
	apps.switchNextApp();
	screenContentManager.updatePopupState();
	windowManager.redraw();
	needForIntroduction = true;
	introduceApp = true;
    }

    public void onNewAreaLayoutIface(Luwrain instance)
    {
	if (instance == null)
	    throw new NullPointerException("instance may not be null");
	final Application app = interfaces.findApp(instance);
	if (app == null)
	    throw new IllegalArgumentException("Using the unknown instance object");
	final Area activeArea = apps.getEffectiveActiveAreaOfApp(app);
	final AreaLayout newLayout = app.getAreasToShow();
	if (newLayout == null)
	    throw new NullPointerException("New area layout may not be null");
	final Area[] areas = newLayout.getAreas();
	int index = 0;
	while (index < areas.length && areas[index] != activeArea)
	    ++index;
	if (index >= areas.length)
	{
	    apps.setActiveAreaOfApp(app, newLayout.getDefaultArea());
	needForIntroduction = true;
	}
	screenContentManager.updatePopupState();//Probably needless
	windowManager.redraw();
    }

    public void switchNextArea()
    {
	screenContentManager.activateNextArea();
	windowManager.redraw();
	introduceActiveArea();
    }

    private void eventLoop(EventLoopStopCondition stopCondition)
    {
	if (stopCondition == null)
	    throw new NullPointerException("stopCondition may not be null");
	while(stopCondition.continueEventLoop())
	{
	    needForIntroduction = false;
	    introduceApp = false;
	    final Event event = eventQueue.takeEvent();
	    if (event == null)
		continue;
	    if (!onEvent(event))
	    {
		eventQueue.onceAgain(event);
		continue;
	    }
	    event.markAsProcessed();
	    if (!eventQueue.hasAgain())
		introduce(stopCondition);
	}
    }

    //True means the event is processed and there is no need to process it again;
    private boolean onEvent(Event event)
    {
	try {
	    switch (event.eventType())
	    {
	    case Event.KEYBOARD_EVENT:
		return onKeyboardEvent(translateKeyboardEvent((KeyboardEvent)event));
	    case Event.ENVIRONMENT_EVENT:

		if (event instanceof ThreadSyncEvent)
		    return onThreadSyncEvent((ThreadSyncEvent)event);
		return onEnvironmentEvent((EnvironmentEvent)event);
	    default:
		Log.warning("environment", "the event of an unknown type:" + event.eventType());
		return true;
	    }
	}
	catch (Throwable e)
	{
	    Log.error("core", "got an exception during event processing:" + e.getMessage());
	    e.printStackTrace();
	    return true;
	}
    }

    static private final int POPUP_BLOCKING_MAY_PROCESS = 0;
    static private final int POPUP_BLOCKING_EVENT_REJECTED = 1;
    static private final int POPUP_BLOCKING_TRY_AGAIN = 2;
    private int popupBlocking()
    {
	final Application nonPopupDest = screenContentManager.isNonPopupDest();
	if (nonPopupDest != null && apps.hasAnyPopup())//Non-popup area activated but some popup opened
	{
	    final Application lastPopupApp = apps.getAppOfLastPopup();
	    if (lastPopupApp == null || apps.isLastPopupWeak())//Weak environment popup
	    {
		apps.cancelLastPopup();
		return POPUP_BLOCKING_TRY_AGAIN;
	    }
	    if (nonPopupDest == lastPopupApp)//User tries to deal with app having opened popup
	    {
		if (apps.isLastPopupWeak())
		{
		    //Weak popup;
		    apps.cancelLastPopup();
		    return POPUP_BLOCKING_TRY_AGAIN;
		} else //Strong popup
		    return POPUP_BLOCKING_EVENT_REJECTED;
	    } else
		if (apps.hasPopupOfApp(nonPopupDest))//The destination application has a popup and it is somewhere in the stack, it is not the popup on top;
		    return POPUP_BLOCKING_EVENT_REJECTED;
	}
	return POPUP_BLOCKING_MAY_PROCESS;
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
	if (keyboardEventForEnvironment(event))
	    return true;

	    switch(popupBlocking())
	    {
	    case POPUP_BLOCKING_TRY_AGAIN:
		return false;
	    case POPUP_BLOCKING_EVENT_REJECTED:
		areaBlockedMessage();
		return true;
	    }

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
	    noAppsMessage();
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
	    if (!commands.run(commandName))
		message(strings.noCommand(), Luwrain.MESSAGE_ERROR);
	    return true;
	}
	if (event.isCommand())
	{
	    final int code = event.getCommand();
	    if (code == KeyboardEvent.CONTROL)
	    {
		speech.silence();
		return true;
	    }
	    if (code == KeyboardEvent.SHIFT ||
		code == KeyboardEvent.CONTROL ||
		code == KeyboardEvent.LEFT_ALT ||
		code == KeyboardEvent.RIGHT_ALT)
		return true;
	}
	if (!event.isCommand() &&
	    EqualKeys.equalKeys(event.getCharacter(), 'x') &&
	    event.withLeftAltOnly())
	{
	    showCommandPopup();
	    return true;
	}
	return false;
    }

    private boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	    switch(popupBlocking())
	    {
	    case POPUP_BLOCKING_TRY_AGAIN:
		return false;
	    case POPUP_BLOCKING_EVENT_REJECTED:
		areaBlockedMessage();
		return true;
	    }

	int res = ScreenContentManager.EVENT_NOT_PROCESSED;
	try {
		res = screenContentManager.onEnvironmentEvent(event);
	}
	catch (Throwable e)
	{
	    Log.error("core", "environment event throws an exception:" + e.getMessage());
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
	    noAppsMessage();
	    break;
	}
	return true;
    }

    private boolean onThreadSyncEvent(ThreadSyncEvent event)
    {
	final Area destArea = event.getDestArea();
	if (destArea == null)
	{
	    Log.warning("core", "thread sync event to the blocked area " + destArea.getClass().getName());
	    return true;
	}
	//FIXME:if the area is blocked we should reject the event;
	try {
		destArea.onEnvironmentEvent(event);
		}
	catch (Throwable e)
	{
	    Log.error("core", "exception while processing thread sync event:" + e.getMessage());
	    e.printStackTrace();
	}
	return true;
    }

    @Override public void enqueueEvent(Event e)
    {
	eventQueue.putEvent(e);
    }

    private void popupImpl(Application app, Area area,
			   int popupPos, EventLoopStopCondition stopCondition,
			   boolean noMultipleCopies, boolean isWeakPopup)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(stopCondition, "stopCondition");
	if (popupPos != Popup.TOP && popupPos != Popup.BOTTOM && 
	    popupPos != Popup.LEFT && popupPos != Popup.RIGHT)
	    throw new IllegalArgumentException("Illegal popup position " + popupPos);
	if (noMultipleCopies)
	    apps.onNewPopupOpening(app, area.getClass());
	final PopupEventLoopStopCondition popupStopCondition = new PopupEventLoopStopCondition(stopCondition);
	apps.addNewPopup(app, area, popupPos, popupStopCondition, noMultipleCopies, isWeakPopup);
	if (screenContentManager.setPopupActive())
	{
	    introduceActiveArea();
	    windowManager.redraw();
	}
	eventLoop(popupStopCondition);
	apps.closeLastPopup();
	onNewScreenLayout();
	setAreaIntroduction();
    }

    public void setActiveAreaIface(Luwrain instance, Area area)
    {
	NullCheck.notNull(instance, "instance");
	NullCheck.notNull(area, "area");
	final Application app = interfaces.findApp(instance);
	if (app == null)
	    throw new IllegalArgumentException("Provided an unknown application instance");
	apps.setActiveAreaOfApp(app, area);
	if (apps.isAppActive(app) && !screenContentManager.isPopupActive())
	    setAreaIntroduction();
	onNewScreenLayout();
    }

    public void onAreaNewHotPointIface(Luwrain instance, Area area)
    {
	NullCheck.notNull(area, "area");
	if (screenContentManager == null)//FIXME:
	    return;
	Area effectiveArea = null;
	if (instance != null)
	{
	    final Application app = interfaces.findApp(instance);
	    if (app != null)
	    {
		if (!apps.isAppLaunched(app))
		    return;
		effectiveArea = apps.getCorrespondingEffectiveArea(app, area);
	    }
	}
	if (effectiveArea == null)
	    effectiveArea = apps.getCorrespondingEffectiveArea(area);
	if (effectiveArea == null)
	{
	    Log.info("core", "unable to find the corresponding effective area for " + area.getClass().getName() + " needed in onAreaNewHotPoint()");
	    return;
	}
	if (effectiveArea == screenContentManager.getActiveArea())
	    windowManager.redrawArea(effectiveArea);
    }

    public void onAreaNewContentIface(Luwrain instance, Area area)
    {
	NullCheck.notNull(area, "area");
	Area effectiveArea = null;
	if (instance != null)
	{
	    final Application app = interfaces.findApp(instance);
	    if (app != null)
	    {
		if (!apps.isAppLaunched(app))
		    return;
		effectiveArea = apps.getCorrespondingEffectiveArea(app, area);
	    }
	}
	if (effectiveArea == null)
	    effectiveArea = apps.getCorrespondingEffectiveArea(area);
	if (effectiveArea == null)
	{
	    Log.info("core", "unable to find the corresponding effective area for " + area.getClass().getName() + " needed in onAreaNewContent()");
	    return;
	}
	windowManager.redrawArea(effectiveArea);
    }

    public void onAreaNewNameIface(Luwrain instance, Area area)
    {
	NullCheck.notNull(area, "area");
	Area effectiveArea = null;
	if (instance != null)
	{
	    final Application app = interfaces.findApp(instance);
	    if (app != null)
	    {
		if (!apps.isAppLaunched(app))
		    return;
		effectiveArea = apps.getCorrespondingEffectiveArea(app, area);
	    }
	}
	if (effectiveArea == null)
	    effectiveArea = apps.getCorrespondingEffectiveArea(area);
	if (effectiveArea == null)
	{
	    Log.info("core", "unable to find the corresponding effective area for " + area.getClass().getName() + " needed in onAreaNewName()");
	    return;
	}
	windowManager.redrawArea(effectiveArea);
    }

    //May return -1;
    public int getAreaVisibleHeightIface(Luwrain instance, Area area)
    {
	NullCheck.notNull(area, "area");
	Area effectiveArea = null;
	if (instance != null)
	{
	    final Application app = interfaces.findApp(instance);
	    if (app != null)
	    {
		if (!apps.isAppLaunched(app))
		    return -1;
		effectiveArea = apps.getCorrespondingEffectiveArea(app, area);
	    }
	}
	if (effectiveArea == null)
	    effectiveArea = apps.getCorrespondingEffectiveArea(area);
	if (effectiveArea == null)
	{
	    Log.info("core", "unable to find the corresponding effective area for " + area.getClass().getName() + " needed in getAreaVisibleHeight()");
	    return -1;
	}
	return windowManager.getAreaVisibleHeight(effectiveArea);
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
	    playSound(Sounds.MESSAGE_OK);
	    break;
	case Luwrain.MESSAGE_DONE:
	    playSound(Sounds.MESSAGE_DONE);
	    break;
	case Luwrain.MESSAGE_NOT_READY:
	    playSound(Sounds.MESSAGE_NOT_READY);
	    break;
	}
	speechProc.silence();
	speechProc.say(text, Luwrain.PITCH_MESSAGE);
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
	    noAppsMessage();
	    return;
	}
	final String name = app.getAppName();
	speechProc.silence();
	playSound(Sounds.INTRO_APP);
	if (name != null && !name.trim().isEmpty())
	    speechProc.say(name); else
	    speechProc.say(app.getClass().getName());
    }

    public void introduceActiveArea()
    {
	final Area activeArea = getActiveArea();
	if (activeArea == null)
	{
	    noAppsMessage();
	    return;
	}
	if (!isActiveAreaBlockedByPopup() && !isAreaBlockedBySecurity(activeArea) &&
	    activeArea.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.INTRODUCE)))
	    return;
	speechProc.silence();
	playSound(activeArea instanceof Popup?Sounds.INTRO_POPUP:Sounds.INTRO_REGULAR);
	speechProc.say(activeArea.getAreaName());
    }

    public void increaseFontSize()
    {
	interaction.setDesirableFontSize(interaction.getFontSize() + 5); 
	windowManager.redraw();
	message(strings.fontSize(interaction.getFontSize()), Luwrain.MESSAGE_REGULAR);
    }

    public void decreaseFontSize()
    {
	interaction.setDesirableFontSize(interaction.getFontSize() - 5); 
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
	final String[] shortcuts = fileTypes.chooseShortcuts(fileNames);
	if (shortcuts.length != fileNames.length)
	    return;
	for(int i = 0;i < shortcuts.length;++i)
	    launchAppIface(shortcuts[i], new String[]{fileNames[i]});
    }

    public Registry  registry()
    {
	return registry;
    }

    public Object getPimManager()
    {
	return null;
    }

    public void popupIface(Popup popup)
    {
	NullCheck.notNull(popup, "popup");
	final Luwrain luwrainObject = popup.getLuwrainObject();
	final EventLoopStopCondition stopCondition = popup.getStopCondition();
	NullCheck.notNull(luwrainObject, "luwrainObject");
	NullCheck.notNull(stopCondition, "stopCondition");
	if (interfaces.isSuitsForEnvironmentPopup(luwrainObject))
	{
	    popupImpl(null, popup, Popup.BOTTOM, stopCondition, popup.noMultipleCopies(), popup.isWeakPopup());
	    return;
	}
	final Application app = interfaces.findApp(luwrainObject);
	if (app == null)
	{
	    Log.warning("core", "somebody is trying to get a popup with fake Luwrain object");
	    throw new IllegalArgumentException("the luwrain object provided by a popup is fake");
	}
	popupImpl(app, popup, Popup.BOTTOM, stopCondition, popup.noMultipleCopies(), popup.isWeakPopup());
    }

    /*
    public void setClipboard(String[] value)
    {
	clipboard = value;
    }
    */

    /*
    public String[] getClipboard()
    {
	return clipboard;
    }
    */

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
	case KeyboardEvent.DELETE:
	    return new KeyboardEvent(true, KeyboardEvent.ALTERNATIVE_DELETE, ' ');
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
	MainMenu mainMenu = new org.luwrain.mainmenu.Builder(interfaces.getObjForEnvironment()).build();
	playSound(Sounds.MAIN_MENU);
	popupImpl(null, mainMenu, Popup.LEFT, mainMenu.closing, true, true);
	if (mainMenu.closing.cancelled())
	    return;
	mainMenu.getSelectedItem().doMMAction(interfaces.getObjForEnvironment());//FIXME:Need to have an interface for the particular extension;
    }

    public boolean runCommand(String command)
    {
	if (command == null)
	    throw new NullPointerException("command may not be null");
	if (command.trim().isEmpty())
	    return false;
	return commands.run(command.trim());
    }

    private void showCommandPopup()
    {
	EditListPopup popup = new EditListPopup(new Luwrain(this), new FixedListPopupModel(commands.getCommandNames()),
					strings.commandPopupName(), strings.commandPopupPrefix(), "");
	popupImpl(null, popup, Popup.BOTTOM, popup.closing, true, true);
	if (popup.closing.cancelled())
	    return;
	    if (!commands.run(popup.text().trim()))
		message(strings.noCommand(), Luwrain.MESSAGE_ERROR);
    }

    public OperatingSystem os()
    {
	return os;
    }

    public Object getSharedObject(String id)
    {
	if (id == null)
	    throw new NullPointerException("id may not be null");
	if (id.trim().isEmpty())
	    throw new IllegalArgumentException("id may not be empty");
	return sharedObjects.getSharedObject(id);
    }

    public UniRefInfo getUniRefInfo(String uniRef)
    {
	return uniRefProcs.getInfo(uniRef);
    }

    public boolean openUniRef(String uniRef)
    {
	return uniRefProcs.open(uniRef);
    }

    public Hardware getHardware()
    {
	return os.getHardware();
    }

    public org.luwrain.cpanel.Section[] getControlPanelSections()
    {
	final LinkedList<org.luwrain.cpanel.Section> res = new LinkedList<org.luwrain.cpanel.Section>();
	final LoadedExtension[] allExt = extensions.getAllLoadedExtensions();
	for(LoadedExtension e: allExt)
	    if (e.controlPanelSections != null)
		for(org.luwrain.cpanel.Section s: e.controlPanelSections)
		    res.add(s);
	return res.toArray(new org.luwrain.cpanel.Section[res.size()]);

    }

    public org.luwrain.browser.Browser createBrowser(Luwrain instance)
    {
	return interaction.createBrowser();
    }

    public void activateAreaSearch()
    {
	final Area activeArea = getValidActiveArea(true);
	if (activeArea == null)
	    return;
	final Environment e = this;
	apps.setReviewAreaWrapper(activeArea,
				  new ReviewAreaWrapperFactory() {
				      @Override public Area createReviewAreaWrapper(Area areaToWrap, AreaWrappingBase wrappingBase)
				      {
					  return new SearchAreaWrapper(areaToWrap, e, wrappingBase);
				      }
				  });
	onNewScreenLayout();
    }

    public String onCurrentAreaRegionIface()
    {
	//FIXME:
	return null;
    }

    public void onIntroduceLineCommand()
    {
	final Area activeArea = getValidActiveArea(true);
	if (activeArea == null)
	    return;
	final int hotPointY = activeArea.getHotPointY();
	if (hotPointY >= activeArea.getLineCount())
	{
	    failureMessage();
	    return;
	}
	//FIXME:Offer to the area to introduce line by itself;
	final String line = activeArea.getLine(hotPointY);
	if (line == null)
	{
	    failureMessage();
	    return;
	}
	if (!line.trim().isEmpty())
	    speechProc.say(line); else
	    speechProc.hint(Hints.EMPTY_LINE);
	needForIntroduction = false;
    }

    public void onRegionPointCommand()
    {
	final Area activeArea = getValidActiveArea(true);
	if (activeArea == null)
	    return;
	if (activeArea.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.REGION_POINT)))
	    speechProc.say(strings.regionPointSet()); else
	    objInaccessibleMessage();
    }

    /**
     * Performs the copying of the content of the currently active area. This
     * method gets currently active area, checks that it isn't blocked and
     * performs region query, saving the result in the clipboard.  If {@code
     * onRegionPointCommand} method has been called on the same area, the
     * region is restricted by two points, otherwise the entire area content
     * must be copied. The method fails if there is no active area, it is
     * blocked or the area refuses to perform the region query. 
     *
     * @param speakAnnouncement Issue messages to the user to describe  the result of the operation (if false, everything goes silently)
     * @return True if the clipboard got new content, false otherwise
     */
    public boolean onCopyCommand(boolean speakAnnouncement)
    {
	final Area activeArea = getValidActiveArea(speakAnnouncement);
	if (activeArea == null)
	    return false;
	final RegionQuery query = new RegionQuery();
	if (!activeArea.onAreaQuery(query))
	{
	    if (speakAnnouncement)
		objInaccessibleMessage();
	    return false;
	}
	if (!query.containsResult())
	{
	    if (speakAnnouncement)
		objInaccessibleMessage();
	    return false;
	}
	final HeldData res = query.getData();
	if (res == null)
	{
	    if (speakAnnouncement)
		objInaccessibleMessage();
	    return false;
	}
	clipboard = res;
	if (speakAnnouncement)
	    speechProc.say(strings.linesCopied(res.strings.length));
	return true;
    }

    public void onDeleteCommand()
    {
	message("delete", Luwrain.MESSAGE_NOT_READY);
    }

    public void onCutCommand()
    {
	if (!onCopyCommand(false))
	    return;
	if (!getActiveArea().onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.CUT)))
	{
	    objInaccessibleMessage();
	    return;
	}
    }

    public void onPasteCommand()
    {
	if (clipboard == null || clipboard.isEmpty())
	{
	    message(strings.noClipboardContent(), Luwrain.MESSAGE_NOT_READY);
	    return;
	}
	final Area activeArea = getValidActiveArea(true);
	if (activeArea == null)
	    return;
	final InsertEvent event = new InsertEvent(clipboard);
	if (activeArea.onEnvironmentEvent(event))
	    speechProc.say(strings.linesInserted(clipboard.strings.length)); else
	    objInaccessibleMessage();
    }

    public void onOpenCommand()
    {
	File f = openPopup();
	if (f == null)
	    return;
	if (!f.isAbsolute())
	    f = new File(launchContext.userHomeDirAsFile(), f.getPath());
	openFiles(new String[]{f.getAbsolutePath()});
    }

    public void onNewScreenLayout()
    {
	screenContentManager.updatePopupState();
	windowManager.redraw();
    }

    public void setAreaIntroduction()
    {
	needForIntroduction = true;
    }

    public void setAppIntroduction()
    {
	needForIntroduction = true;
	introduceApp = true;
    }

    //This method may not return an unwrapped area, there should be at least ta security wrapper
    private Area getActiveArea()
    {
	//FIXME:Ensure that there is a security wrapper
	final Area area = screenContentManager.getActiveArea();
	if (!(area instanceof AreaWrapper))
	    Log.warning("core", "area " + area.getClass().getName() + " goes through Environment.getActiveArea() not being wrapped by any instance of core.AreaWrapper");
	return area;
    }

    private void noAppsMessage()
    {
	speechProc.silence(); 
	playSound(Sounds.NO_APPLICATIONS);
	speechProc.say(strings.noLaunchedApps());
    }

    private void areaBlockedMessage()
    {
	speechProc.silence(); 
		    playSound(Sounds.EVENT_NOT_PROCESSED);
		    speechProc.say(strings.appBlockedByPopup(), Luwrain.MESSAGE_REGULAR);
    }

    private void failureMessage()
    {
	speechProc.silence();
	playSound(Sounds.EVENT_NOT_PROCESSED);
    }

    private void objInaccessibleMessage()
    {
	speechProc.silence();
	    playSound(Sounds.EVENT_NOT_PROCESSED);
    }

    private File openPopup()
    {
	final FilePopup popup = new FilePopup(interfaces.getObjForEnvironment(), strings.openPopupName(),
					      strings.openPopupPrefix(), launchContext.userHomeDirAsFile());
	popupImpl(null, popup, Popup.BOTTOM, popup.closing, true, true);
	if (popup.closing.cancelled())
	    return null;
	return popup.getFile();
    }

    private boolean isActiveAreaBlockedByPopup()
    {
	return screenContentManager.isActiveAreaBlockedByPopup();
    }

    private boolean isAreaBlockedBySecurity(Area area)
    {
	return false;
    }

    private Area getValidActiveArea(boolean speakMessages)
    {
	final Area activeArea = getActiveArea();
	if (activeArea == null)
	{
	    if (speakMessages)
		noAppsMessage();
	    return null;
	}
	if (isAreaBlockedBySecurity(activeArea))
	{
	    if (speakMessages)
		areaBlockedMessage();
	    return null;
	}
	if (isActiveAreaBlockedByPopup())
	{
	    if (speakMessages)
		areaBlockedMessage();
	    return null;
	}
	return activeArea;
    }

    private void printMemInfo()
    {
	final Runtime runtime = Runtime.getRuntime();
	final java.text.NumberFormat format = java.text.NumberFormat.getInstance();
	final long maxMemory = runtime.maxMemory();
	final long allocatedMemory = runtime.totalMemory();
	final long freeMemory = runtime.freeMemory();
	Log.debug("core", "Memory usage information:");
	Log.debug("core", "free memory: " + format.format(freeMemory / 1048576) + "M");
	Log.debug("core", "allocated memory: " + format.format(allocatedMemory / 1046576) + "M");
	Log.debug("core", "max memory: " + format.format(maxMemory / 1048576) + "M");
	//	Log.debug("core", "total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024));
    }
}
