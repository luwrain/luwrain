/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of LUWRAIN.

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

import java.util.*;
import java.nio.file.*;

import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.core.extensions.*;
import org.luwrain.popups.*;
import org.luwrain.os.OperatingSystem;
import org.luwrain.hardware.*;

class Environment extends EnvironmentAreas
{
    static private final String STRINGS_OBJECT_NAME = "luwrain.environment";
    static private final String DEFAULT_MAIN_MENU_CONTENT = "control:registry";


    private AreaListening listening = null;
    private final OperatingSystem os;
    private final Interaction interaction;

    private org.luwrain.core.extensions.Manager extensions;
    private final org.luwrain.desktop.App desktop = new org.luwrain.desktop.App();
    private GlobalKeys globalKeys;
    private final FileTypes fileTypes = new FileTypes();

    private final I18nImpl i18n = new I18nImpl();
    private final CommandManager commands = new CommandManager();
    private final ShortcutManager shortcuts = new ShortcutManager();
    private final SharedObjectManager sharedObjects = new SharedObjectManager();
    private final UniRefProcManager uniRefProcs = new UniRefProcManager();

    Settings.UserInterface uiSettings;

    Environment(CmdLine cmdLine, Registry registry,
		OperatingSystem os, Interaction interaction, 
		org.luwrain.base.CoreProperties coreProps, String lang)
    {
	super(cmdLine, registry, coreProps, lang);
	NullCheck.notNull(os, "os");
	NullCheck.notNull(interaction, "interaction");
	this.os = os;
	this.interaction = interaction;
	interfaces.createObjForEnvironment(this);
    }

    void run()
    {
	init();
	interaction.startInputEventsAccepting(this);
	windowManager.redraw();
	playSound(Sounds.STARTUP);//FIXME:
	final String greeting = uiSettings.getLaunchGreeting("");
	if (!greeting.trim().isEmpty())
	    try {
		Thread.sleep(1000);
		message(greeting, Luwrain.MESSAGE_REGULAR);
	    } catch (InterruptedException ie)
	    {
		Thread.currentThread().interrupt();
	    }
	soundManager.startingMode();
	eventLoop(new InitialEventLoopStopCondition());
	Log.debug("core", "event loop finished");
	interaction.stopInputEventsAccepting();
	extensions.close();
	Log.debug("core", "environment closed");
    }

    private void init()
    {
	speech = new Speech(cmdLine, registry);
	desktop.onLaunch(interfaces.requestNew(desktop, this));
	apps = new AppManager(desktop);
	screenContentManager = new ScreenContentManager(apps);
	windowManager = new WindowManager(interaction, screenContentManager);
	extensions = new org.luwrain.core.extensions.Manager(interfaces);
	extensions.load((ext)->interfaces.requestNew(ext, this), cmdLine);
	initI18n();
	initObjects();
	if (!speech.init())
	    Log.warning("core", "unable to initialize speech core, very likely LUWRAIN will be silent");
	braille.init(registry, os.getBraille(), this);
	globalKeys = new GlobalKeys(registry);
	globalKeys.loadFromRegistry();
	fileTypes.load(registry);
	//	desktop.ready(i18n.getChosenLangName(), i18n.getStrings(org.luwrain.desktop.App.STRINGS_NAME));
	sounds.init(registry, coreProps.getPathProperty("luwrain.dir.data"));
	uiSettings = Settings.createUserInterface(registry);
    }

    private void initObjects()
    {
	final Command[] standardCommands = Commands.createStandardCommands(this);
	for(Command sc: standardCommands)
	    commands.add(new Luwrain(this), sc);//FIXME:
	commands.addOsCommands(getObjForEnvironment(), registry);
	shortcuts.addOsShortcuts(getObjForEnvironment(), registry);
	sharedObjects.createStandardObjects(this);
	final UniRefProc[] standardUniRefProcs = UniRefProcs.createStandardUniRefProcs(getObjForEnvironment());
	for(UniRefProc proc: standardUniRefProcs)
	    uniRefProcs.add(new Luwrain(this), proc);//FIXME:
	final LoadedExtension[] allExt = extensions.getAllLoadedExtensions();
	for(LoadedExtension e: allExt)
	{
	    final Extension ext = e.ext;
	    //Shortcuts
	    for(Shortcut s: e.shortcuts)
		if (s != null)
		{
		    if (!shortcuts.add(s))
			Log.warning("core", "shortcut \'" + s.getName() + "\' of extension " + e.getClass().getName() + " has been refused by  the shortcuts manager to be registered");
		}

	    //Shared objects
	    for(SharedObject s: e.sharedObjects)
		if (s != null)
		{
		    if (!sharedObjects.add(ext, s))
			    Log.warning("core", "the shared object \'" + s.getName() + "\' of extension " + e.getClass().getName() + " has been refused by  the shared objects manager to be registered");
		}

	    //UniRefProcs
	    for(UniRefProc p: e.uniRefProcs)
		if (p != null)
		{
		    if (!uniRefProcs.add(e.luwrain, p))
			    Log.warning("core", "the uniRefProc \'" + p.getUniRefType() + "\' of extension " + e.getClass().getName() + " has been refused by  the uniRefProcs manager to be registered");
		}

	    //Commands
	    for(Command c: e.commands)
		if (c != null)
		{
		    if (!commands.add(e.luwrain, c))
			Log.warning("core", "command \'" + c.getName() + "\' of extension " + e.getClass().getName() + " has been refused by  the commands manager to be registered");
		}

	    //speech factories
	    for(org.luwrain.speech.Factory f: e.speechFactories)
		if (!speech.addFactory(f))
		    Log.warning("core", "speech factory \'" + f.getServedChannelType() + "\' of extension " + e.getClass().getName() + " has been refused by  speech core to be registered");
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
		Log.error("core", "extension " + e.getClass().getName() + " has thrown an exception on i18n:" + ee.getMessage());
		ee.printStackTrace();
	    }
	if (!i18n.chooseLang(lang))
	{
	    Log.fatal("core", "unable to choose matching language for i18n, requested language is \'" + lang + "\'");
	    return;
	}
    }

    void quit()
    {
	final YesNoPopup popup = new YesNoPopup(new Luwrain(this), i18n.getStaticStr("QuitPopupName"), i18n.getStaticStr("QuitPopupText"), true, Popups.DEFAULT_POPUP_FLAGS);
	popup(null, popup, Popup.Position.BOTTOM, popup.closing, true, true);
	if (popup.closing.cancelled() || !popup.result())
	    return;
	InitialEventLoopStopCondition.shouldContinue = false;
    }

    //It is admissible situation if shortcut returns null
    void launchAppIface(String shortcutName, String[] args)
    {
	NullCheck.notNull(shortcutName, "shortcutName");
	NullCheck.notNullItems(args, "args");
	if (shortcutName.trim().isEmpty())
	    throw new IllegalArgumentException("shortcutName may not be empty");
	Log.info("core", "launching application \'" + shortcutName + "\' with " + args.length + " argument(s)");
	for(int i = 0;i < args.length;++i)
	    Log.info("core", "args[" + i + "]: " + args[i]);
	final Application[] app = shortcuts.prepareApp(shortcutName, args != null?args:new String[0]);
	if (app == null)
	    return;
	soundManager.stopStartingMode();
	for(Application a: app)
	    if (a != null)
		launchApp(a);
    }

    void launchApp(Application app)
    {
	NullCheck.notNull(app, "app");
	System.gc();
	//Checking is it a mono application
	if (app instanceof MonoApp)
	{
	    Log.debug("core", app.getClass().getName() + " is a mono app,  checking already launched instances");
	    final Application[] launchedApps = apps.getLaunchedApps();
	    for(Application a: launchedApps)
		if (a instanceof MonoApp && a.getClass().equals(app.getClass()))
	    {
		final MonoApp ma = (MonoApp)a;
		final MonoApp.Result res = ma.onMonoAppSecondInstance(app);
		Log.debug("core", "already launched instance found, result is " + res);
		NullCheck.notNull(res, "res");
		if (res == MonoApp.Result.SECOND_INSTANCE_PERMITTED)
		    break;
		if (res == MonoApp.Result.BRING_FOREGROUND)
		{
		    apps.setActiveApp(a);
		    onNewAreasLayout();
	needForIntroduction = true;
	introduceApp = true;
		    return;
		}
	    }
	}
	final Luwrain o = interfaces.requestNew(app, this);
	try {
	    if (!app.onLaunch(o))
	    {
		interfaces.release(o);
		return;
	    }
	}
	catch (OutOfMemoryError e)
	{
	    e.printStackTrace();
	    interfaces.release(o);
	    message(i18n.getStaticStr("AppLaunchNoEnoughMemory"), Luwrain.MESSAGE_ERROR);
	    return;
	}
	catch (Exception e)
	{
	    interfaces.release(o);
		Log.error("core", "application " + app.getClass().getName() + " has thrown an exception on onLaunch()" + e.getMessage());
	    e.printStackTrace();
	    launchAppCrash(app, e);
	    return;
	}
	if (!apps.newApp(app))
	{
	    interfaces.release(o);
	    return; 
	}
	soundManager.stopStartingMode();
	onNewAreasLayout();
	needForIntroduction = true;
	introduceApp = true;
    }

    void launchAppCrash(Luwrain instance, Exception e)
    {
	NullCheck.notNull(instance, "instance");
	NullCheck.notNull(e, "e");
	final Application app = interfaces.findApp(instance);
	if (app != null)
	    launchAppCrash(app, e);
    }

    void launchAppCrash(Application app, Exception e)
    {
	NullCheck.notNull(app, "app");
	NullCheck.notNull(e, "e");
	System.gc();
	final org.luwrain.app.crash.CrashApp crashApp = new org.luwrain.app.crash.CrashApp(app, e);
	final Luwrain o = interfaces.requestNew(crashApp, this);
	try {
	    if (!crashApp.onLaunch(o))
	    {
		interfaces.release(o);
		return;
	    }
	}
	catch (OutOfMemoryError ee)
	{
	    ee.printStackTrace();
	    interfaces.release(o);
	    return;
	}
	if (!apps.newApp(crashApp))
	{
	    interfaces.release(o);
	    return; 
	}
	onNewAreasLayout();
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
	    message(i18n.getStaticStr("AppCloseHasPopup"), Luwrain.MESSAGE_ERROR);
	    return;
	}
	apps.closeApp(app);
	interfaces.release(instance);
	onNewAreasLayout();
	setAppIntroduction();
    }

    void onSwitchNextAppCommand()
    {
	apps.switchNextApp();
	onNewAreasLayout();
	needForIntroduction = true;
	introduceApp = true;
    }

    void announceActiveAreaIface()
    {
	introduceActiveArea();
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
	onNewAreasLayout();
    }

    void onSwitchNextAreaCommand()
    {
	screenContentManager.activateNextArea();
	onNewAreasLayout();
	introduceActiveArea();
    }

    @Override protected boolean onEvent(Event event)
    {
	try {
	    if (event instanceof RunnableEvent)
		return onRunnableEvent((RunnableEvent)event);
	    if (event instanceof KeyboardEvent)
		return onKeyboardEvent(Keyboard.translate((KeyboardEvent)event));
	    if (event instanceof AddressedEnvironmentEvent)
		return onAddressedEnvironmentEvent((AddressedEnvironmentEvent)event);
	    if (event instanceof EnvironmentEvent)
	    {
		final EnvironmentEvent environmentEvent = (EnvironmentEvent)event;
		if (environmentEvent.getType() == null)
		{
		    Log.warning("core", "an environment event with null type in main event loop, skipping");
		    return true;
		}
		switch(environmentEvent.getType())
		{
		case REGULAR:
		    return onEnvironmentEvent(environmentEvent);
		case BROADCAST:
		    return onBroadcastEnvironmentEvent(environmentEvent);
		default:
		    return true;
		}
	    }
	    Log.warning("core", "unknown event class of the event in main event loop:" + event.getClass().getName());
	    return true;
	}
	catch (Exception e)
	{
	    Log.error("core", "an exception of class " + e.getClass().getName() + " has been thrown while processing of event of class " + event.getClass().getName() + "::" + e.getMessage());
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

    @Override public void introduce(EventLoopStopCondition stopCondition)
    {
	NullCheck.notNull(stopCondition, "stopCondition");
	if (needForIntroduction && stopCondition.continueEventLoop() && listening == null)
	{
	    if (introduceApp)
		introduceActiveApp(); else
		introduceActiveArea();
	}
	needForIntroduction = false;
	introduceApp = false;
    }

    private boolean onRunnableEvent(RunnableEvent event)
    {
	event.runnable().run();
	return true;
    }

    private boolean onKeyboardEvent(KeyboardEvent event)
    {
	stopAreaListening();
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
	final Area activeArea = getActiveArea();
	try {
	    if (activeArea == null)
	    {
		noAppsMessage();
		return true;
	    }
	    final Action[] actions = activeArea.getAreaActions();
	    if (actions != null)
		for(Action a: actions)
		{
		    final KeyboardEvent actionEvent = a.keyboardEvent();
		    if (actionEvent == null || !actionEvent.equals(event))
			continue;
		    if (activeArea.onEnvironmentEvent(new ActionEvent(a)))
			return true;
		    break;
		}
	    if (!activeArea.onKeyboardEvent(event))
		playSound(Sounds.EVENT_NOT_PROCESSED);
	    return true;
	}
	catch (Exception e)
	{
	    Log.error("core", "active area of class " + activeArea.getClass().getName() + " throws an exception on keyboard event processing:" + e.getMessage());
	    e.printStackTrace();
	    speech.silence();
	    playSound(Sounds.EVENT_NOT_PROCESSED);
	    speech.speak(e.getMessage(), 0, 0);
	    return true;
	}
    }

    private boolean keyboardEventForEnvironment(KeyboardEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	final String commandName = globalKeys.getCommandName(event);
	if (commandName != null)
	{
	    if (!commands.run(commandName))
		message(i18n.getStaticStr("NoCommand"), Luwrain.MESSAGE_ERROR);
	    return true;
	}
	if (event.isSpecial())
	{
	    final KeyboardEvent.Special code = event.getSpecial();
	    if (code == KeyboardEvent.Special.CONTROL)
	    {
		speech.silence();
		return true;
	    }
	    if (code == KeyboardEvent.Special.SHIFT ||
		code == KeyboardEvent.Special.CONTROL ||
		code == KeyboardEvent.Special.LEFT_ALT ||
		code == KeyboardEvent.Special.RIGHT_ALT)
		return true;
	}
	if (!event.isSpecial() &&
	    EqualKeys.equalKeys(event.getChar(), 'x') &&
	    event.withAltOnly())
	{
	    showCommandPopup();
	    return true;
	}
	return false;
    }

    private boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.Code.MESSAGE)
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

    private boolean onAddressedEnvironmentEvent(AddressedEnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	final Area destArea = event.getDestArea();
	if (destArea == null)
	    return true;
	//FIXME:if the area is blocked we should reject the event; 	    Log.warning("core", "thread sync event to the blocked area " + destArea.getClass().getName());
	try {
		destArea.onEnvironmentEvent(event);
		}
	catch (Throwable e)
	{
	    Log.error("core", "exception while processing addressed environment event to area of class " + destArea.getClass().getName() + ":" + e.getClass().getName() + ":" + e.getMessage());
	    e.printStackTrace();
	}
	return true;
    }

private boolean onBroadcastEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	apps.sendBroadcastEvent(event);
	return true;
    }

    void popup(Application app, Area area,
			   Popup.Position pos, EventLoopStopCondition stopCondition,
			   boolean noMultipleCopies, boolean isWeakPopup)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(pos, "pos");
	NullCheck.notNull(stopCondition, "stopCondition");
	if (noMultipleCopies)
	    apps.onNewPopupOpening(app, area.getClass());
	final PopupEventLoopStopCondition popupStopCondition = new PopupEventLoopStopCondition(stopCondition);
	apps.addNewPopup(app, area, pos, popupStopCondition, noMultipleCopies, isWeakPopup);
	screenContentManager.setPopupActive();
	onNewAreasLayout();
	introduceActiveArea();
	eventLoop(popupStopCondition);
	apps.closeLastPopup();
onNewAreasLayout();
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
	onNewAreasLayout();
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

    void onAreaNewBackgroundSound(Luwrain instance, Area area)
    {
	NullCheck.notNull(area, "area");
	final Area effectiveArea = getEffectiveAreaFor(instance, area);
	if (effectiveArea == null)//Area isn't known by the applications manager, generally admissible situation
	    return;
	updateBackgroundSound(effectiveArea);
    }


    //May return -1
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
	    return -1;
	return windowManager.getAreaVisibleHeight(effectiveArea);
    }

    int getScreenWidthIface()
    {
	return interaction.getWidthInCharacters();
    }

    int getScreenHeightIface()
    {
	return interaction.getHeightInCharacters();
    }

    //May return -1
    int getAreaVisibleWidthIface(Luwrain instance, Area area)
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
	    return -1;
	return windowManager.getAreaVisibleWidth(effectiveArea);
    }

    void message(String text, int semantic)
    {
	if (text == null || text.trim().isEmpty())
	    return;
	needForIntroduction = false;
	switch(semantic)
	{
	case Luwrain.MESSAGE_ERROR:
	    playSound(Sounds.ERROR);
	    break;
	case Luwrain.MESSAGE_OK:
	    playSound(Sounds.OK);
	    break;
	case Luwrain.MESSAGE_DONE:
	    playSound(Sounds.DONE);
	    break;
	case Luwrain.MESSAGE_NOT_READY:
	    playSound(Sounds.BLOCKED);
	    break;
	}
	//	speechProc.silence();
	speech.speak(text, Luwrain.PITCH_MESSAGE, 0);
	interaction.startDrawSession();
	interaction.clearRect(0, interaction.getHeightInCharacters() - 1, interaction.getWidthInCharacters() - 1, interaction.getHeightInCharacters() - 1);
	interaction.drawText(0, interaction.getHeightInCharacters() - 1, text, true);
	interaction.endDrawSession();
    }

    void message(String text, Sounds sound)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(sound, "sound");
	if (text.trim().isEmpty())
	    return;
	needForIntroduction = false;
	    playSound(sound);
	speech.speak(text, Luwrain.PITCH_MESSAGE, 0);
	interaction.startDrawSession();
	interaction.clearRect(0, interaction.getHeightInCharacters() - 1, interaction.getWidthInCharacters() - 1, interaction.getHeightInCharacters() - 1);
	interaction.drawText(0, interaction.getHeightInCharacters() - 1, text, true);
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
	speech.silence();
	playSound(Sounds.INTRO_APP);
	if (name != null && !name.trim().isEmpty())
	    speech.speak(name, 0, 0); else
	    speech.speak(app.getClass().getName(), 0, 0);
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
	    activeArea.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.Code.INTRODUCE)))
	    return;
	speech.silence();
	playSound(activeArea instanceof Popup?Sounds.INTRO_POPUP:Sounds.INTRO_REGULAR);
	speech.speak(activeArea.getAreaName(), 0, 0);
    }

    void onIncreaseFontSizeCommand()
    {
	interaction.setDesirableFontSize(interaction.getFontSize() + 5); 
	windowManager.redraw();
	message(i18n.getStaticStr("FontSize") + " " + interaction.getFontSize(), Luwrain.MESSAGE_REGULAR);
    }

    void onDecreaseFontSizeCommand()
    {
	if (interaction.getFontSize() < 15)
	    return;
	interaction.setDesirableFontSize(interaction.getFontSize() - 5); 
	windowManager.redraw();
	message(i18n.getStaticStr("FontSize") + " " + interaction.getFontSize(), Luwrain.MESSAGE_REGULAR);
    }

    void openFiles(String[] fileNames)
    {
	NullCheck.notEmptyItems(fileNames, "fileNames");
	if (fileNames.length < 1)
	    return;
	fileTypes.launch(this, registry, fileNames);
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
	    popup(null, popup, Popup.Position.BOTTOM, stopCondition,
		      popup.getPopupFlags().contains(Popup.Flags.NO_MULTIPLE_COPIES), popup.getPopupFlags().contains(Popup.Flags.WEAK));
	    return;
	}
	final Application app = interfaces.findApp(luwrainObject);
	if (app == null)
	{
	    Log.warning("core", "somebody is trying to get a popup with fake Luwrain object");
	    throw new IllegalArgumentException("the luwrain object provided by a popup is fake");
	}
	popup(app, popup, Popup.Position.BOTTOM, stopCondition, 
		  popup.getPopupFlags().contains(Popup.Flags.NO_MULTIPLE_COPIES), popup.getPopupFlags().contains(Popup.Flags.WEAK));
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

    void mainMenu()
    {
	final MainMenu mainMenu = MainMenu.newMainMenu(getObjForEnvironment());
	if (mainMenu == null)
	    return;
	popup(null, mainMenu, Popup.Position.LEFT, mainMenu.closing, true, true);
	if (mainMenu.closing.cancelled())
	    return;
	final UniRefInfo result = mainMenu.result();
	openUniRefIface(result.value());
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
	final EditListPopup popup = new EditListPopup(new Luwrain(this), new EditListPopupUtils.FixedModel(commands.getCommandNames()),
						      i18n.getStaticStr("CommandPopupName"), i18n.getStaticStr("CommandPopupPrefix"), "", EnumSet.noneOf(Popup.Flags.class)){
		@Override public boolean onAreaQuery(AreaQuery query)
		{
		    NullCheck.notNull(query, "query");
		    switch(query.getQueryCode())
		    {
		    case AreaQuery.OBJECT_UNIREF:
			if (text.trim().isEmpty())
			    return false;
			((ObjectUniRefQuery)query).answer("command:" + text().trim());
			return true;
		    default:
			return super.onAreaQuery(query);
		    }
		}
	    };
	popup(null, popup, Popup.Position.BOTTOM, popup.closing, true, true);
	if (popup.closing.cancelled())
	    return;
	    if (!commands.run(popup.text().trim()))
		message(i18n.getStaticStr("NoCommand"), Luwrain.MESSAGE_ERROR);
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

    Hardware getHardware()
    {
	return os.getHardware();
    }

    org.luwrain.cpanel.Factory[] getControlPanelFactories()
    {
	final LinkedList<org.luwrain.cpanel.Factory> res = new LinkedList<org.luwrain.cpanel.Factory>();
	res.add(new SpeechControlPanelFactory(getObjForEnvironment(), speech));
	final LoadedExtension[] allExt = extensions.getAllLoadedExtensions();
	for(LoadedExtension e: allExt)
	    if (e.controlPanelFactories != null)
		for(org.luwrain.cpanel.Factory f: e.controlPanelFactories)
		    if (f != null)
			res.add(f);
	return res.toArray(new org.luwrain.cpanel.Factory[res.size()]);
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
	onNewAreasLayout();
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
	final ContextMenu menu = new ContextMenu(getObjForEnvironment(), actions);
	popup(null, menu, Popup.Position.RIGHT, menu.closing, true, true);
	if (menu.closing.cancelled())
	    return;
	final Object selected = menu.selected();
	if (selected == null || !(selected instanceof Action))//Should never happen
	    return;
	if (!activeArea.onEnvironmentEvent(new ActionEvent((Action)selected)))
	    areaInaccessibleMessage();
    }

    void reloadComponent(Luwrain.ReloadComponents component)
    {
	switch(component)
	{
	case ENVIRONMENT_SOUNDS:
	    sounds.init(registry, coreProps.getPathProperty("luwrain.dir.data"));
	    break;
	}
    }

    private void areaBlockedMessage()
    {
	message(i18n.getStaticStr("AppBlockedByPopup"), Luwrain.MESSAGE_ERROR);
    }

    @Override Area getValidActiveArea(boolean speakMessages)
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

    void startAreaListening()
    {
	final Area activeArea = getValidActiveArea(true);
	if (activeArea == null)
	    return;
	stopAreaListening();
	speech.silence();
	listening = new AreaListening(getObjForEnvironment(), speech, activeArea);
	listening.start();
    }

    void stopAreaListening()
    {
	if (listening == null)
	    return;
	listening.cancel();
	listening = null;
    }

    String currentAreaWordIface(boolean issueErrorMessages)
    {
	final Area activeArea = getValidActiveArea(issueErrorMessages);
	if (activeArea == null)
	    return null;
	return new AreaText(activeArea).currentWord();
    }

    void onSayCurrentWordCommand()
    {
	final String word = currentAreaWordIface(false);
	if (word != null && !word.trim().isEmpty())
	    message(word, Luwrain.MESSAGE_REGULAR); else
	    areaInaccessibleMessage();
    }

    String[] getAllShortcutNames()
    {
	return shortcuts.getShortcutNames();
    }
}
