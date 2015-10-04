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

import org.luwrain.os.OperatingSystem;
import org.luwrain.hardware.*;
import org.luwrain.speech.BackEnd;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.core.extensions.*;
import org.luwrain.util.RegistryAutoCheck;
import org.luwrain.popups.*;
import org.luwrain.mainmenu.MainMenu;
import org.luwrain.sounds.*;

class Environment implements EventConsumer
{
    static private final String STRINGS_OBJECT_NAME = "luwrain.environment";
    static private final String DEFAULT_MAIN_MENU_CONTENT = "control:registry";

    private String[] cmdLine;
    private final EventQueue eventQueue = new EventQueue();
    private Registry registry;
    private org.luwrain.speech.BackEnd speech;
    private OperatingSystem os;
    private Interaction interaction;

    private org.luwrain.core.extensions.Manager extensions;
    private final InterfaceManager interfaces = new InterfaceManager(this);
    private final org.luwrain.desktop.App desktop = new org.luwrain.desktop.App();
    private AppManager apps;
    private ScreenContentManager screenContentManager;
    private WindowManager windowManager;
    private GlobalKeys globalKeys;
    private final FileTypes fileTypes = new FileTypes();

    private final I18nImpl i18n = new I18nImpl();
    private final CommandManager commands = new CommandManager();
    private final ShortcutManager shortcuts = new ShortcutManager();
    private final SharedObjectManager sharedObjects = new SharedObjectManager();
    private final UniRefProcManager uniRefProcs = new UniRefProcManager();

    private HeldData clipboard = null;
    private LaunchContext launchContext;
    private Strings strings;
    private RegistryAutoCheck registryAutoCheck;
    private final RegistryKeys registryKeys = new RegistryKeys();

    private boolean needForIntroduction = false;
    private boolean introduceApp = false;
    private Luwrain speechProc;

    Environment(String[] cmdLine,
		       Registry registry,
		       org.luwrain.speech.BackEnd speech,
		       OperatingSystem os,
		       Interaction interaction,
		       LaunchContext launchContext)
    {
	this.cmdLine = org.luwrain.util.Strings.notNullArray(cmdLine);
	this.registry = registry;
	this.speech = speech;
	this.os = os;
	this.interaction = interaction;
	this.launchContext = launchContext;
	NullCheck.notNull(registry, "registry");
	NullCheck.notNull(speech, "speech");
	NullCheck.notNull(os, "os");
	NullCheck.notNull(interaction, "interaction");
	NullCheck.notNull(launchContext, "launchContext");
    }

    void run()
    {
	init();
	interaction.startInputEventsAccepting(this);
	EnvironmentSounds.play(Sounds.STARTUP);//FIXME:
	if (registry.getTypeOf(registryKeys.launchGreeting()) == Registry.STRING)
	{
	    final String text = registry.getString(registryKeys.launchGreeting());
	    if (!text.trim().isEmpty())
	    {
	try {
	    Thread.sleep(1000);
	} catch (InterruptedException ie){}
	message(text, Luwrain.MESSAGE_REGULAR);
	    }
	}
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
	registryAutoCheck = new RegistryAutoCheck(registry, "environment");
    }

    private void initObjects()
    {
	final Command[] standardCommands = StandardCommands.createStandardCommands(this);
	for(Command sc: standardCommands)
	    commands.add(new Luwrain(this), sc);//FIXME:
	commands.addOsCommands(interfaces.getObjForEnvironment(), registry);

	final UniRefProc[] standardUniRefProcs = StandardUniRefProcs.createStandardUniRefProcs(interfaces.getObjForEnvironment(), strings);
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

    void quit()
    {
	YesNoPopup popup = new YesNoPopup(new Luwrain(this), strings.quitPopupName(), strings.quitPopupText(), true);
	popupImpl(null, popup, Popup.BOTTOM, popup.closing, true, true);
	if (popup.closing.cancelled() || !popup.result())
	    return;
	InitialEventLoopStopCondition.shouldContinue = false;
    }

    void launchAppIface(String shortcutName, String[] args)
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

    void launchApp(Application app)
    {
	NullCheck.notNull(app, "app");
	System.gc();
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

    void closeAppIface(Luwrain instance)
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

    void onSwitchNextAppCommand()
    {
	apps.switchNextApp();
	screenContentManager.updatePopupState();
	windowManager.redraw();
	needForIntroduction = true;
	introduceApp = true;
    }

    void onNewAreaLayoutIface(Luwrain instance)
    {
	NullCheck.notNull(instance, "instance");
	final Application app = interfaces.findApp(instance);
	if (app == null)
	{
	    Log.info("core", "somebody is trying to change area layout using a fake instance object");
	    return;
	}
	apps.refreshAreaLayoutOfApp(app);
	onNewScreenLayout();
    }

    void onSwitchNextAreaCommand()
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
	if (event.getCode() == EnvironmentEvent.MESSAGE)
	{
	    if (!(event instanceof MessageEvent))
		return true;
	    final MessageEvent messageEvent = (MessageEvent)event;
	    message(messageEvent.text(), messageEvent.semantic());
	    return true;
	}

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

    void setActiveAreaIface(Luwrain instance, Area area)
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

    //Returns an effective area for the specified one
    //Returns null if specified area not known in applications and areas managers 
    //Instance is not mandatory but can increase speed of search
    private Area getEffectiveAreaFor(Luwrain instance, Area area)
    {
	Area effectiveArea = null;
	if (instance != null)
	{
	    final Application app = interfaces.findApp(instance);
	    if (app != null && apps.isAppLaunched(app))
		effectiveArea = apps.getCorrespondingEffectiveArea(app, area);
	}
	//No provided instance or it didn't help;
	if (effectiveArea == null)
	    effectiveArea = apps.getCorrespondingEffectiveArea(area);
	return effectiveArea;
    }

    void onAreaNewHotPointIface(Luwrain instance, Area area)
    {
	NullCheck.notNull(area, "area");
	if (screenContentManager == null)//FIXME:
	    return;
	final Area effectiveArea = getEffectiveAreaFor(instance, area);
	if (effectiveArea == null)//Area isn't known by the applications manager, generally admissible situation
	    return;
	if (effectiveArea == screenContentManager.getActiveArea())
	    windowManager.redrawArea(effectiveArea);
    }

    void onAreaNewContentIface(Luwrain instance, Area area)
    {
	NullCheck.notNull(area, "area");
	final Area effectiveArea = getEffectiveAreaFor(instance, area);
	if (effectiveArea == null)//Area isn't known by the applications manager, generally admissible situation
	    return;
	windowManager.redrawArea(effectiveArea);
    }

    void onAreaNewNameIface(Luwrain instance, Area area)
    {
	NullCheck.notNull(area, "area");
	final Area effectiveArea = getEffectiveAreaFor(instance, area);
	if (effectiveArea == null)//Area isn't known by the applications manager, generally admissible situation
	    return;
	windowManager.redrawArea(effectiveArea);
    }

    //May return -1;
    int getAreaVisibleHeightIface(Luwrain instance, Area area)
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

    void message(String text, int semantic)
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

    void introduceActiveArea()
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

    void onIncreaseFontSizeCommand()
    {
	interaction.setDesirableFontSize(interaction.getFontSize() + 5); 
	windowManager.redraw();
	message(strings.fontSize(interaction.getFontSize()), Luwrain.MESSAGE_REGULAR);
    }

    void onDecreaseFontSizeCommand()
    {
	interaction.setDesirableFontSize(interaction.getFontSize() - 5); 
	windowManager.redraw();
	message(strings.fontSize(interaction.getFontSize()), Luwrain.MESSAGE_REGULAR);
    }

    void openFiles(String[] fileNames)
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

    Registry  registry()
    {
	return registry;
    }

    void popupIface(Popup popup)
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

    BackEnd speech()
    {
	return speech;
    }

    /**
     * @return true if this hint should be spoken as well
     */
    boolean onStandardHint(int code)
    {
	return true;
    }

    I18n i18nIface()
    {
	return i18n;
    }

    void playSound(int code)
    {
	EnvironmentSounds.play(code);
    }

    LaunchContext launchContextIface()
    {
	return launchContext;
    }

    void mainMenu()
    {
	final MainMenu mainMenu = new org.luwrain.mainmenu.Builder(interfaces.getObjForEnvironment()).build();
	playSound(Sounds.MAIN_MENU);
	popupImpl(null, mainMenu, Popup.LEFT, mainMenu.closing, true, true);
	if (mainMenu.closing.cancelled())
	    return;
	mainMenu.getSelectedItem().doMMAction(interfaces.getObjForEnvironment());//FIXME:Need to have an interface for the particular extension;
    }

    boolean runCommand(String command)
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

    OperatingSystem os()
    {
	return os;
    }

    Object getSharedObjectIface(String id)
    {
	if (id == null)
	    throw new NullPointerException("id may not be null");
	if (id.trim().isEmpty())
	    throw new IllegalArgumentException("id may not be empty");
	return sharedObjects.getSharedObject(id);
    }

    UniRefInfo getUniRefInfoIface(String uniRef)
    {
	return uniRefProcs.getInfo(uniRef);
    }

    boolean openUniRefIface(String uniRef)
    {
	return uniRefProcs.open(uniRef);
    }

    public Hardware getHardware()
    {
	return os.getHardware();
    }

    org.luwrain.cpanel.Section[] getControlPanelSections()
    {
	final LinkedList<org.luwrain.cpanel.Section> res = new LinkedList<org.luwrain.cpanel.Section>();
	final LoadedExtension[] allExt = extensions.getAllLoadedExtensions();
	for(LoadedExtension e: allExt)
	    if (e.controlPanelSections != null)
		for(org.luwrain.cpanel.Section s: e.controlPanelSections)
		    res.add(s);
	return res.toArray(new org.luwrain.cpanel.Section[res.size()]);
    }

    org.luwrain.browser.Browser createBrowserIface(Luwrain instance)
    {
	return interaction.createBrowser();
    }

    void activateAreaSearch()
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

    void onContextMenuCommand()
    {
	final Area activeArea = getValidActiveArea(true);
	if (activeArea == null)
	    return;
	final Action[] actions = activeArea.getAreaActions();
	if (actions == null || actions.length < 1)
	{
	    areaInaccessibleMessage();
	    return;
	}
	final ContextMenu menu = new ContextMenu(speechProc, actions);
	popupImpl(null, menu, Popup.RIGHT, menu.closing, true, true);
	if (menu.closing.cancelled())
	    return;
	final Object selected = menu.selected();
	if (selected == null || !(selected instanceof Action))//Should never happen
	    return;
	if (!activeArea.onEnvironmentEvent(new ActionEvent((Action)selected)))
	    areaInaccessibleMessage();
    }

    void onIntroduceLineCommand()
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

    void onRegionPointCommand()
    {
	final Area activeArea = getValidActiveArea(true);
	if (activeArea == null)
	    return;
	if (activeArea.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.REGION_POINT)))
	    speechProc.say(strings.regionPointSet()); else
	    areaInaccessibleMessage();
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
    boolean onCopyCommand(boolean speakAnnouncement)
    {
	final Area activeArea = getValidActiveArea(speakAnnouncement);
	if (activeArea == null)
	    return false;
	final RegionQuery query = new RegionQuery();
	if (!activeArea.onAreaQuery(query))
	{
	    if (speakAnnouncement)
		areaInaccessibleMessage();
	    return false;
	}
	if (!query.containsResult())
	{
	    if (speakAnnouncement)
		areaInaccessibleMessage();
	    return false;
	}
	final HeldData res = query.getData();
	if (res == null)
	{
	    if (speakAnnouncement)
		areaInaccessibleMessage();
	    return false;
	}
	clipboard = res;
	if (speakAnnouncement)
	    speechProc.say(strings.linesCopied(res.strings.length));
	return true;
    }

    HeldData currentAreaRegionIface(boolean issueErrorMessages)
    {
	final Area activeArea = getValidActiveArea(issueErrorMessages);
	if (activeArea == null)
	    return null;
	final RegionQuery query = new RegionQuery();
	if (!activeArea.onAreaQuery(query) || !query.containsResult())
	    return null;
	return query.getData();
    }

    void onDeleteCommand()
    {
	message("delete", Luwrain.MESSAGE_NOT_READY);
    }

    void onCutCommand()
    {
	if (!onCopyCommand(false))
	    return;
	if (!getActiveArea().onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.CUT)))
	{
	    areaInaccessibleMessage();
	    return;
	}
    }

    void onPasteCommand()
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
	    areaInaccessibleMessage();
    }

    void onOpenCommand()
    {
	File f = openPopup();
	if (f == null)
	    return;
	if (!f.isAbsolute())
	    f = new File(launchContext.userHomeDirAsFile(), f.getPath());
	openFiles(new String[]{f.getAbsolutePath()});
    }

    void onCopyObjectUniRefCommand()
    {
	final Area activeArea = getValidActiveArea(true);
	if (activeArea == null)
	    return;
	final ObjectUniRefQuery query = new ObjectUniRefQuery();
	if (!activeArea.onAreaQuery(query) || !query.containsResult())
	{
	    failureMessage();
	    return;
	}
	final String uniRef = query.getUniRef();
	if (uniRef == null || uniRef.trim().isEmpty())
	{
	    failureMessage();
	    return;
	}
	final UniRefInfo uniRefInfo = getUniRefInfoIface(uniRef);
	if (uniRefInfo == null)
	{
	    failureMessage();
	    return;
	}
	message(uniRefInfo.toString(), Luwrain.MESSAGE_REGULAR);
	clipboard = new HeldData(new String[]{uniRef});
    }

    void onNewScreenLayout()
    {
	screenContentManager.updatePopupState();
	windowManager.redraw();
    }

    void setAreaIntroduction()
    {
	needForIntroduction = true;
    }

    void setAppIntroduction()
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
		    message(strings.appBlockedByPopup(), Luwrain.MESSAGE_ERROR);
    }

    private void failureMessage()
    {
	speechProc.silence();
	playSound(Sounds.EVENT_NOT_PROCESSED);
    }

    private void areaInaccessibleMessage()
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
    }
}
