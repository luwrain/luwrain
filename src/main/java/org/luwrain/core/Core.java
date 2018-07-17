/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
import java.util.concurrent.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.core.extensions.*;
import org.luwrain.popups.*;
import org.luwrain.base.*;

final class Core extends EventDispatching
{
    static private final String DESKTOP_PROP_NAME = "luwrain.class.desktop";
    static private final String STARTUP_PROP_NAME = "luwrain.func.startup";
    static private final String PLAYER_FACTORY_PROP_NAME = "luwrain.player.factory";

    final OperatingSystem os;
    final Interaction interaction;
    private final Desktop desktop;
    private final org.luwrain.shell.Conversations conversations;
    org.luwrain.player.Player player = null;
    final WavePlayers.Player wavePlayer = new WavePlayers.Player();
    Settings.UserInterface uiSettings;//FIXME:final 
    private volatile boolean wasInputEvents = false;
    final UniRefProcManager uniRefProcs = new UniRefProcManager();//FIXME:

    Core(CmdLine cmdLine, Registry registry,
	 OperatingSystem os, Interaction interaction, 
	 PropertiesRegistry props, String lang)
    {
	super(cmdLine, registry, props, lang, interaction);
	NullCheck.notNull(os, "os");
	NullCheck.notNull(interaction, "interaction");
	this.os = os;
	this.interaction = interaction;
	this.conversations = new org.luwrain.shell.Conversations(getObjForEnvironment());
	if (props.getProperty(DESKTOP_PROP_NAME).isEmpty())
	{
	    Log.error(LOG_COMPONENT, "no property " + DESKTOP_PROP_NAME + ", unable to create a desktop");
	    throw new RuntimeException("unable to create a desktop");
	}
	this.desktop = (Desktop)org.luwrain.util.ClassUtils.newInstanceOf(props.getProperty(DESKTOP_PROP_NAME), Desktop.class);
	if (this.desktop == null)
	    throw new RuntimeException("unable to create a desktop");
    }

    void run()
    {
	init();
    interaction.startInputEventsAccepting(this);
	windowManager.redraw();
	if (!props.getProperty(STARTUP_PROP_NAME).isEmpty())
	    runFunc(getObjForEnvironment(), props.getProperty(STARTUP_PROP_NAME)); else
	    Log.warning(LOG_COMPONENT, "no property " + STARTUP_PROP_NAME + ", skipping startup procedure");
	soundManager.startingMode();
	workers.doWork(objRegistry.getWorkers());
	eventLoop(mainStopCondition);
	workers.finish();
	playSound(Sounds.SHUTDOWN);
	    try {
		Thread.sleep(2500);//FIXME:
	    } catch (InterruptedException ie)
	    {
		Thread.currentThread().interrupt();
	    }
	interaction.stopInputEventsAccepting();
	extensions.close();
    }

        @Override public void onBeforeEventProcessing()
    {
		  stopAreaListening();
	  wasInputEvents = true;
    }

    @Override protected void processEventResponse(EventResponse eventResponse)
    {
	NullCheck.notNull(eventResponse, "eventResponse");
	//FIXME:access level
	eventResponse.announce(getObjForEnvironment(), (parts)->{
		NullCheck.notNullItems(parts, "parts");
		if (parts.length == 0)
		    return;
		if (parts.length == 1)
		{
		    speech.speakEventResponse(parts[0]);
		    return;
		}
		final StringBuilder b = new StringBuilder();
		b.append(parts[0]);
		for(int i = 1;i < parts.length;++i)
		{
		    b.append(", ");
		    b.append(parts[i]);
		}
		speech.speakEventResponse(new String(b));
	    });
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
	return activeArea;
    }

    @Override public void onAltX()
    {
	final String cmdName = conversations.commandPopup(commands.getCommandNames());
	if (cmdName != null && !cmdName.trim().isEmpty()&&
	    !commands.run(cmdName.trim()))
	    message(i18n.getStaticStr("NoCommand"), Luwrain.MessageType.ERROR);
    }

    private void init()
    {
	desktop.onLaunchApp(interfaces.requestNew(desktop));
	desktop.setConversations(conversations);
	apps.setDefaultApp(desktop);
	extensions.load((ext)->interfaces.requestNew(ext), cmdLine);
	initDynamicExtensions();
	initI18n();
			initObjects();
	objRegistry.add(null, new StartingModeProperty());
	if (!speech.init(objRegistry.getSpeechFactories()))
	    Log.warning(LOG_COMPONENT, "unable to initialize speech core, very likely LUWRAIN will be silent");
	braille.init(registry, os.getBraille(), this);
	globalKeys.loadFromRegistry();
	fileTypes.load(registry);
	//loading player
	if (!props.getProperty(PLAYER_FACTORY_PROP_NAME).isEmpty())
	{
	    final String playerFactoryName = props.getProperty(PLAYER_FACTORY_PROP_NAME);
	    try {
		final Object o = Class.forName(playerFactoryName).newInstance();
		if (o instanceof org.luwrain.player.Factory)
		{
		    final org.luwrain.player.Factory factory = (org.luwrain.player.Factory)o;
		    final org.luwrain.player.Factory.Params params = new org.luwrain.player.Factory.Params();
		    params.luwrain = getObjForEnvironment();
		    this.player = factory.newPlayer(params);
		    if (this.player != null)
			Log.debug(LOG_COMPONENT, "loaded player instance of class " + this.player.getClass().getName()); else
			Log.error(LOG_COMPONENT, "player factory of the class " + playerFactoryName + " returned null, no player");
		} else
		{
		    Log.error(LOG_COMPONENT, "the player factory class " + playerFactoryName + " is not an instance of org.luwrain.player.Factory ");
		    this.player =null;
		}
	    }
	    catch(Throwable e)
	    {
		Log.error(LOG_COMPONENT, "unable to load player class " + playerFactoryName + ":" + e.getClass().getName() + ":" + e.getMessage());
		this.player = null;
	    }
	} else
	    Log .warning(LOG_COMPONENT, "no player functionality, the property " + PLAYER_FACTORY_PROP_NAME + " is empty");
	desktop.ready();
	props.setProviders(objRegistry.getPropertiesProviders());
	uiSettings = Settings.createUserInterface(registry);
    }

    private void initDynamicExtensions()
    {
	final File jsDir = props.getFileProperty("luwrain.dir.js");
	if (jsDir.exists() && jsDir.isDirectory())
	{
	    final File[] files = jsDir.listFiles();
	    if (files != null)
		for(File f: files)
		{
		    if (!f.exists() || f.isDirectory())
			continue;
		    if (!f.getName().toLowerCase().endsWith(".js"))
			continue;
		    try {
			Log.debug(LOG_COMPONENT, "Loading " + f.getAbsolutePath());
			final String id = loadScriptExtensionFromFile(f);
		    }
		    catch(org.luwrain.core.extensions.DynamicExtensionException e)
		    {
			Log.error(LOG_COMPONENT, "unable to load the script extension " + f.getAbsolutePath() + ":" + e.getMessage());
		    }
		}
	} else
	    Log.warning(LOG_COMPONENT, "the directory " + jsDir.getAbsolutePath() + " does not exist, skipping loading of dynamic script extensions");
	//Checking packs
	final File[] packs = getInstalledPacksDirs();
	for(File pack: packs)
	{
	    final File textExtDir = new File(pack, "text");
	    if (!textExtDir.exists() || !textExtDir.isDirectory())
		continue;
	    final File[] files = textExtDir.listFiles();
	    if (files == null)
		continue;
	    for(File f: files)
	    {
		if (!f.exists() || f.isDirectory())
		    continue;
		if (!f.getName().toLowerCase().endsWith(".ext"))
		    continue;
		final File dataDir = new File(pack, "data");
		if (dataDir.exists() && !dataDir.isDirectory())
		{
		    Log.error(LOG_COMPONENT, dataDir.getAbsolutePath() + " exists, skipping the pack");
		    continue;
		}
		if (!dataDir.exists() && !dataDir.mkdir())
		{
		    Log.error(LOG_COMPONENT, "unable to create " + dataDir.getAbsolutePath() + ", skipping the pack");
		    continue;
		}
		try {
		    Log.debug(LOG_COMPONENT, "Loading " + f.getAbsolutePath());
		    final String id = loadTextExtensionFromFile(f, dataDir);
		}
		catch(org.luwrain.core.extensions.DynamicExtensionException e)
		{
		    Log.error(LOG_COMPONENT, "unable to load the script extension " + f.getAbsolutePath() + ":" + e.getMessage());
		}
	    }
	}
    }

    private void initObjects()
    {
	final Command[] standardCommands = Commands.createStandardCommands(this, conversations);
	for(Command sc: standardCommands)
	    commands.add(getObjForEnvironment(), sc);//FIXME:
	final UniRefProc[] standardUniRefProcs = UniRefProcs.createStandardUniRefProcs(getObjForEnvironment());
	for(UniRefProc proc: standardUniRefProcs)
	    uniRefProcs.add(getObjForEnvironment(), proc);//FIXME:
	final LoadedExtension[] allExt = extensions.getAllLoadedExtensions();
	for(LoadedExtension e: allExt)
	{
	    objRegistry.takeObjects(e);
	    final Extension ext = e.ext;
	    //FIXME:
	    for(UniRefProc p: e.uniRefProcs)
		if (!uniRefProcs.add(e.luwrain, p))
		    Log.warning("core", "the uniRefProc \'" + p.getUniRefType() + "\' of extension " + e.getClass().getName() + " has been refused by  the uniRefProcs manager to be registered");
	    //FIXME:
	    for(Command c: e.commands)
		if (!commands.add(e.luwrain, c))
		    Log.warning("core", "command \'" + c.getName() + "\' of extension " + e.getClass().getName() + " has been refused by  the commands manager to be registered");
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
		Log.error(LOG_COMPONENT, "extension " + e.getClass().getName() + " has thrown an exception on i18n:" + ee.getMessage());
	    }
	if (!i18n.chooseLang(lang))
	{
	    Log.fatal("core", "unable to choose matching language for i18n, requested language is \'" + lang + "\'");
	    return;
	}
    }

    public void quit()
    {
	if (conversations.quitConfirmation())
	    mainStopCondition.stop();
    }

    //It is admissible situation if shortcut returns null
    void launchAppIface(String shortcutName, String[] args)
    {
	NullCheck.notEmpty(shortcutName, "shortcutName");
	NullCheck.notNullItems(args, "args");
	Log.info("core", "launching application \'" + shortcutName + "\' with " + args.length + " argument(s)");
	mainCoreThreadOnly();
	for(int i = 0;i < args.length;++i)
	    Log.info("core", "args[" + i + "]: " + args[i]);
	final Application[] app = objRegistry.prepareApp(shortcutName, args);
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
	mainCoreThreadOnly();
	//Checking if it is a mono app
	if (app instanceof MonoApp)
	{
	    Log.debug(LOG_COMPONENT, app.getClass().getName() + " is a mono app,  checking already launched instances");
	    final Application[] launchedApps = apps.getLaunchedApps();
	    for(Application a: launchedApps)
		if (a instanceof MonoApp && a.getClass().equals(app.getClass()))
		{
		    final MonoApp ma = (MonoApp)a;
		    final MonoApp.Result res = ma.onMonoAppSecondInstance(app);
		    Log.debug(LOG_COMPONENT, "already launched instance found, result is " + res);
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
final Luwrain o = interfaces.requestNew(app);
Luwrain toRelease = o;//Must be cleaned to null when we sure the app is completely acceptable
	final InitResult initResult;
	try {
	    try {
		initResult = app.onLaunchApp(o);
	    }
	    catch (OutOfMemoryError e)
	    {
		Log.error(LOG_COMPONENT, "no enough memory to launch the app of the class " + app.getClass().getName());
		message(i18n.getStaticStr("AppLaunchNoEnoughMemory"), Luwrain.MessageType.ERROR);
		return;
	    }
	    catch (Throwable e)
	    {
		Log.error(LOG_COMPONENT, "application " + app.getClass().getName() + " has thrown an exception on onLaunch()" + e.getMessage());
		launchAppCrash(app, e);
		return;
	    }
	    if (initResult == null || !initResult.isOk())
	    {
		//FIXME:message
		return;
	    }
	    if (!apps.newApp(app))
		return;
	    toRelease = null;//We sure that the app is completely accepted
	}
	finally {
	    if (toRelease != null)
		interfaces.release(toRelease);
	}
	soundManager.stopStartingMode();
	onNewAreasLayout();
	needForIntroduction = true;
	introduceApp = true;
    }

    void launchAppCrash(Luwrain instance, Throwable e)
    {
	NullCheck.notNull(instance, "instance");
	NullCheck.notNull(e, "e");
	final Application app = interfaces.findApp(instance);
	if (app != null)
	    launchAppCrash(app, e);
    }

    void launchAppCrash(Application app, Throwable e)
    {
	NullCheck.notNull(app, "app");
	NullCheck.notNull(e, "e");
	final org.luwrain.app.crash.CrashApp crashApp = new org.luwrain.app.crash.CrashApp(app, e);
	final Luwrain o = interfaces.requestNew(crashApp);
	final InitResult initResult;
	try {
	    initResult = crashApp.onLaunchApp(o);
	}
	catch (OutOfMemoryError ee)
	{
	    ee.printStackTrace();
	    interfaces.release(o);
	    return;
	}
	if (initResult == null || !initResult.isOk())
	    {
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
	mainCoreThreadOnly();
	if (instance == getObjForEnvironment())
	    throw new IllegalArgumentException("Trying to close an application through the special interface object designed for environment operations");
	final Application app = interfaces.findApp(instance);
	if (app == null)
	    throw new IllegalArgumentException("Trying to close an application through an unknown interface object or this object doesn\'t identify an application");
	if (app == desktop)
	    throw new IllegalArgumentException("Trying to close a desktop");
	if (apps.hasPopupOfApp(app))
	{
	    message(i18n.getStaticStr("AppCloseHasPopup"), Luwrain.MessageType.ERROR);
	    return;
	}
	apps.closeApp(app);
	interfaces.release(instance);
	onNewAreasLayout();
	setAppIntroduction();
    }

    void onSwitchNextAppCommand()
    {
	mainCoreThreadOnly();
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
	mainCoreThreadOnly();
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
	mainCoreThreadOnly();
	screenContentManager.activateNextArea();
	onNewAreasLayout();
	introduceActiveArea();
    }

    public void popup(Application app, Area area,
			   Popup.Position pos, StopCondition stopCondition,
			   boolean noMultipleCopies, boolean isWeakPopup)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(pos, "pos");
	NullCheck.notNull(stopCondition, "stopCondition");
	mainCoreThreadOnly();
	if (noMultipleCopies)
	    apps.onNewPopupOpening(app, area.getClass());
	final PopupStopCondition popupStopCondition = new PopupStopCondition(mainStopCondition, stopCondition);
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
	mainCoreThreadOnly();
	final Application app = interfaces.findApp(instance);
	if (app == null)
	    throw new IllegalArgumentException("Provided an unknown application instance");
	apps.setActiveAreaOfApp(app, area);
	if (apps.isAppActive(app) && !screenContentManager.isPopupActive())
	    setAreaIntroduction();
	onNewAreasLayout();
    }

    public void onAreaNewHotPointIface(Luwrain instance, Area area)
    {
	NullCheck.notNull(area, "area");
	mainCoreThreadOnly();
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
	mainCoreThreadOnly();
	final Area effectiveArea = getEffectiveAreaFor(instance, area);
	if (effectiveArea == null)//Area isn't known by the applications manager, generally admissible situation
	    return;
	windowManager.redrawArea(effectiveArea);
    }

    void onAreaNewNameIface(Luwrain instance, Area area)
    {
	NullCheck.notNull(area, "area");
	mainCoreThreadOnly();
	final Area effectiveArea = getEffectiveAreaFor(instance, area);
	if (effectiveArea == null)//Area isn't known by the applications manager, generally admissible situation
	    return;
	windowManager.redrawArea(effectiveArea);
    }

    void onAreaNewBackgroundSound(Luwrain instance, Area area)
    {
	NullCheck.notNull(area, "area");
	mainCoreThreadOnly();
	final Area effectiveArea = getEffectiveAreaFor(instance, area);
	if (effectiveArea == null)//Area isn't known by the applications manager, generally admissible situation
	    return;
	updateBackgroundSound(effectiveArea);
    }

    //May return -1
    int getAreaVisibleHeightIface(Luwrain instance, Area area)
    {
	NullCheck.notNull(area, "area");
	mainCoreThreadOnly();
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
	mainCoreThreadOnly();
	return interaction.getWidthInCharacters();
    }

    int getScreenHeightIface()
    {
	mainCoreThreadOnly();
	return interaction.getHeightInCharacters();
    }

    //May return -1
    int getAreaVisibleWidthIface(Luwrain instance, Area area)
    {
	NullCheck.notNull(area, "area");
	mainCoreThreadOnly();
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

    public void message(String text, Luwrain.MessageType messageType)
    {
	mainCoreThreadOnly();
	NullCheck.notNull(text, "text");
	NullCheck.notNull(messageType, "messageType");
	switch(messageType)
	{
	case ERROR:
	    message(text, Sounds.ERROR);
	    break;
	case OK:
	    message(text, Sounds.OK);
	    break;
	case DONE:
	    message(text, Sounds.DONE);
	    break;
	case ANNOUNCEMENT:
	    message(text, Sounds.ANNOUNCEMENT);
	    break;
	case UNAVAILABLE:
	    message(text, Sounds.BLOCKED);
	    break;
	case NONE:
	    message(text, (Sounds)null);
	    break;
	    case REGULAR:
	default:
	    message(text,Sounds.MESSAGE);
	}
    }

    public void message(String text, Sounds sound)
    {
	mainCoreThreadOnly();
	if (text == null || text.trim().isEmpty())
	    return;
	needForIntroduction = false;
	if (sound != null)
	    playSound(sound);
	speech.speak(text, Speech.PITCH_MESSAGE, 0);
	interaction.startDrawSession();
	interaction.clearRect(0, interaction.getHeightInCharacters() - 1, interaction.getWidthInCharacters() - 1, interaction.getHeightInCharacters() - 1);
	interaction.drawText(0, interaction.getHeightInCharacters() - 1, text, true);
	interaction.endDrawSession();
    }

    void onIncreaseFontSizeCommand()
    {
	mainCoreThreadOnly();
	interaction.setDesirableFontSize(interaction.getFontSize() + 5); 
	windowManager.redraw();
	message(i18n.getStaticStr("FontSize") + " " + interaction.getFontSize(), Luwrain.MessageType.REGULAR);
    }

    void onDecreaseFontSizeCommand()
    {
	mainCoreThreadOnly();
	if (interaction.getFontSize() < 15)
	    return;
	interaction.setDesirableFontSize(interaction.getFontSize() - 5); 
	windowManager.redraw();
	message(i18n.getStaticStr("FontSize") + " " + interaction.getFontSize(), Luwrain.MessageType.REGULAR);
    }

    void openFiles(String[] fileNames)
    {
	NullCheck.notEmptyItems(fileNames, "fileNames");
	mainCoreThreadOnly();
	if (fileNames.length < 1)
	    return;
	fileTypes.launch(this, registry, fileNames);
    }

    Registry  registry()
    {
	return registry;
    }

    public void popupIface(Popup popup)
    {
	NullCheck.notNull(popup, "popup");
	mainCoreThreadOnly();
	final Luwrain luwrainObject = popup.getLuwrainObject();
	final StopCondition stopCondition = ()->popup.isPopupActive();
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

    //FIXME:
    public I18n i18nIface()
    {
	return i18n;
    }

    void mainMenu()
    {
	mainCoreThreadOnly();
	final org.luwrain.shell.MainMenu mainMenu = org.luwrain.shell.MainMenu.newMainMenu(getObjForEnvironment());
	if (mainMenu == null)
	    return;
	popup(null, mainMenu, Popup.Position.LEFT, ()->mainMenu.closing.continueEventLoop(), true, true);
	if (mainMenu.closing.cancelled())
	    return;
	final UniRefInfo result = mainMenu.result();
	openUniRefIface(result.getValue());
    }

    boolean runCommand(String command)
    {
	mainCoreThreadOnly();
	if (command == null)
	    throw new NullPointerException("command may not be null");
	if (command.trim().isEmpty())
	    return false;
	return commands.run(command.trim());
    }

    boolean openUniRefIface(String uniRef)
    {
	return uniRefProcs.open(uniRef);
    }

    org.luwrain.cpanel.Factory[] getControlPanelFactories()
    {
	final List<org.luwrain.cpanel.Factory> res = new LinkedList();
	res.add(new SpeechControlPanelFactory(getObjForEnvironment(), objRegistry));
	final LoadedExtension[] allExt = extensions.getAllLoadedExtensions();
	for(LoadedExtension e: allExt)
	    if (e.controlPanelFactories != null)
		for(org.luwrain.cpanel.Factory f: e.controlPanelFactories)
		    if (f != null)
			res.add(f);
	return res.toArray(new org.luwrain.cpanel.Factory[res.size()]);
    }

    void activateAreaSearch()
    {
	final Area activeArea = getValidActiveArea(true);
	if (activeArea == null)
	    return;
	if (!apps.setAreaWrapper(activeArea,
				  new AreaWrapperFactory() {
				      @Override public Area createAreaWrapper(Area areaToWrap, Disabling disabling)
				      {
					  return new Search(areaToWrap, Core.this, disabling);
				      }
				  }))
	    playSound(Sounds.EVENT_NOT_PROCESSED);
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
	final org.luwrain.shell.ContextMenu menu = new org.luwrain.shell.ContextMenu(getObjForEnvironment(), actions);
	popup(null, menu, Popup.Position.RIGHT, ()->menu.isPopupActive(), true, true);
	if (menu.wasCancelled())
	    return;
	final Object selected = menu.selected();
	if (selected == null || !(selected instanceof Action))//Should never happen
	    return;
	if (!activeArea.onSystemEvent(new ActionEvent((Action)selected)))
	    areaInaccessibleMessage();
    }

    void startAreaListening()
    {
	final Area activeArea = getValidActiveArea(true);
	if (activeArea == null)
	    return;
	stopAreaListening();
	speech.silence();
	listening = new Listening(getObjForEnvironment(), speech, activeArea);
	listening.start();
    }

    void stopAreaListening()
    {
	if (listening == null)
	    return;
	listening.cancel();
	listening = null;
    }

    private final class StartingModeProperty implements org.luwrain.base.PropertiesProvider
    {
	static private final String PROP_NAME = "luwrain.startingmode";
        @Override public String getExtObjName()
	{
	    return this.getClass().getName();
	}
	@Override public String[] getPropertiesRegex()
	{
	    return new String[0];
	}
	@Override public Set<org.luwrain.base.PropertiesProvider.Flags> getPropertyFlags(String propName)
	{
	    NullCheck.notEmpty(propName, "propName");
	    if (propName.equals(PROP_NAME))
		return EnumSet.of(PropertiesProvider.Flags.PUBLIC,
				  PropertiesProvider.Flags.READ_ONLY);
	    return null;
	}
	@Override public File getFileProperty(String propName)
	{
	    NullCheck.notNull(propName, "propName");
	    return null;
	}
	@Override public boolean setFileProperty(String propName, File value)
	{
	    NullCheck.notEmpty(propName, "propName");
	    NullCheck.notNull(value, "value");
	    return false;
	}
	@Override public String getProperty(String propName)
	{
	    NullCheck.notEmpty(propName, "propName");
	    if (propName.equals(PROP_NAME))
		return wasInputEvents?"0":"1";
	    return null;
	}
	@Override public boolean setProperty(String propName, String value)
	{
	    NullCheck.notEmpty(propName, "propName");
	    NullCheck.notNull(value, "value");
	    return false;
	}
	@Override public void setListener(org.luwrain.base.PropertiesProvider.Listener listener)
	{
	}
    }
}
