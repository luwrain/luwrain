/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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
import java.util.concurrent.atomic.*;
import java.io.*;

import org.luwrain.core.events.*;
import org.luwrain.core.listening.*;
import org.luwrain.script.Hooks;

import static org.luwrain.core.NullCheck.*;

final class Core extends EventDispatching
{
    static private final String
	DESKTOP_PROP_NAME = "luwrain.class.desktop",
	PLAYER_FACTORY_PROP_NAME = "luwrain.player.factory";

    private final ClassLoader classLoader;
    final OperatingSystem os;
    final Interaction interaction;
    final boolean standalone;
    private final org.luwrain.core.shell.Conversations conversations;
    org.luwrain.player.Player player = null;
        private Application desktop = null;
    final WavePlayers.Player wavePlayer = new WavePlayers.Player();
    Settings.UserInterface uiSettings;//FIXME:final 
    private volatile boolean wasInputEvents = false;
    final UniRefProcManager uniRefProcs = new UniRefProcManager();//FIXME:

    Core(CmdLine cmdLine, ClassLoader classLoader, Registry registry,
	 OperatingSystem os, Interaction interaction, 
	 PropertiesRegistry props, String lang, boolean standalone)
    {
	super(cmdLine, registry, props, lang, interaction);
	notNull(classLoader, "classLoader");
	notNull(os, "os");
	notNull(interaction, "interaction");
	this.classLoader = classLoader;
	this.os = os;
	this.interaction = interaction;
	this.conversations = new org.luwrain.core.shell.Conversations(luwrain);
	this.standalone = standalone;
    }

    void run()
    {
	init();
    interaction.startInputEventsAccepting(this);
	windowManager.redraw();
	//soundManager.startingMode();
	workers.doWork(objRegistry.getWorkers());
	try {
	    Hooks.chainOfResponsibility(luwrain, Hooks.STARTUP, new Object[0]);
	}
	catch(Throwable e)
	{
	    error(e, "Unable to run the startup hook");
	}
	eventLoop(mainStopCondition);
	workers.finish();
	playSound(Sounds.SHUTDOWN);
	    try {
		Thread.sleep(3000);//FIXME:
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
	notNull(eventResponse, "eventResponse");
	//FIXME:access level
	final EventResponse.Speech s = new org.luwrain.core.speech.EventResponseSpeech(speech, i18n, speakingText);
	eventResponse.announce(luwrain, s);
    }

    Area getActiveArea(boolean speakMessages)
    {
	final Area activeArea = tiles.getActiveArea();
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
	final String cmdName = conversations.command(commands.getCommandNames());
	if (cmdName == null || cmdName.trim().isEmpty())
	    return;
	if (cmdName.trim().startsWith("app "))
	{
	    if (!runAppCommand(cmdName.trim()))
			    message(i18n.getStaticStr("NoCommand"), Luwrain.MessageType.ERROR);
	    return;
	}
	if (!commands.run(cmdName.trim()))
	    message(i18n.getStaticStr("NoCommand"), Luwrain.MessageType.ERROR);
    }

        private boolean runAppCommand(String command)
    {
	notEmpty(command, "command");
	if (!command.startsWith("app "))
	    return false;
	final String params = command.substring("app ".length());
	Log.debug("proba", "params " + params);
	String shortcut = null;
	final List<String> args = new ArrayList<>();
	//FIXME:quotes
	for(String s: params.split(" ", -1))
	    if (!s.trim().isEmpty())
	    {
		if (shortcut == null)
		    shortcut = s.trim(); else
		    args.add(s.trim());
	    }
	if (shortcut == null)
	    return false;
	launchApp(shortcut, args.toArray(new String[args.size()]));
	return true;
    }

    private void init()
    {
	extensions.load((ext)->interfaces.requestNew(ext), cmdLine, this.classLoader);
	initObjects();
	for (ScriptFile f: extensions.getScriptFiles("core"))
	    try {
		loadScript(f);
	    }
	    catch(ExtensionException e)
	    {
		error(e, "unable to load script " + f.toString());
	    }
	initI18n();
	objRegistry.add(null, new StartingModeProperty());
	speech.init(objRegistry.getSpeechEngines());
	braille.init(registry, os.getBraille(), this);
	globalKeys.loadFromRegistry();
	fileTypes.load(registry);
	loadPlayer();
	loadDesktop();
	props.setProviders(objRegistry.getPropertiesProviders());
	uiSettings = Settings.createUserInterface(registry);
    }

    String loadScript(ScriptFile scriptFile) throws ExtensionException
    {
	notNull(scriptFile, "scriptFile");
	mainCoreThreadOnly();
	final var ext = new org.luwrain.script.core.ScriptExtension(scriptFile.toString());
	ext.init(interfaces.requestNew(ext));
	try {
	    ext.getScriptCore().load(scriptFile);
	}
	catch(Throwable e)
	{
	    interfaces.release(ext.getLuwrainObj());
	    throw new ExtensionException(e);
	}
	final var entry = new ExtensionsManager.Entry(ext, ext.getLuwrainObj());
	extensions.extensions.add(entry);
	objRegistry.takeObjects(ext, ext.getLuwrainObj());
	for(Command c: ext.getCommands(ext.getLuwrainObj()))
	    commands.add(ext.getLuwrainObj(), c);
	return entry.id;
    }

    private void initObjects()
    {
	for(Command sc: Commands.getCommands(this, conversations))
	    commands.add(luwrain, sc);//FIXME:
	if (!standalone)
	    for(Command sc: Commands.getNonStandaloneCommands(this, conversations))
		commands.add(luwrain, sc);//FIXME:
	final UniRefProc[] standardUniRefProcs = UniRefProcs.createStandardUniRefProcs(luwrain);
	for(UniRefProc proc: standardUniRefProcs)
	    uniRefProcs.add(luwrain, proc);//FIXME:
	for(final var e: extensions.extensions)
	{
	    objRegistry.takeObjects(e.ext, e.luwrain);
	    //	    final Extension ext = e.ext;
	    //FIXME:
	    for(UniRefProc p: e.ext.getUniRefProcs(e.luwrain))
		if (!uniRefProcs.add(luwrain, p))
		    warn("the uniRefProc \'" + p.getUniRefType() + "\' of extension " + e.getClass().getName() + " has been refused by  the uniRefProcs manager to be registered");
	    //FIXME:
	    for(Command c: e.ext.getCommands(e.luwrain))
		if (!commands.add(luwrain, c))
		    warn("command \'" + c.getName() + "\' of extension " + e.getClass().getName() + " has been refused by  the commands manager to be registered");
	}
    }

    private void initI18n()
    {
	for(final var e: extensions.extensions)
	    try {
		e.ext.i18nExtension(e.luwrain, i18n);
	    }
	    catch (Exception ex)
	    {
		error(ex, "extension " + e.getClass().getName() + " thrown an exception on i18n");
	    }
	if (!i18n.chooseLang(lang))
	{
	    Log.fatal("core", "unable to choose matching language for i18n, requested language is \'" + lang + "\'");
	    return;
	}
    }

    private void loadPlayer()
    {
	this.player =null;
	if (props.getProperty(PLAYER_FACTORY_PROP_NAME).isEmpty())
	{
	    warn("no player functionality, the property " + PLAYER_FACTORY_PROP_NAME + " is empty");
	    return;
	}
	final String playerFactoryName = props.getProperty(PLAYER_FACTORY_PROP_NAME);
	final Object o = org.luwrain.util.ClassUtils.newInstanceOf(classLoader, playerFactoryName, org.luwrain.player.Factory.class);
	final org.luwrain.player.Factory factory = (org.luwrain.player.Factory)o;
	try {
	    final org.luwrain.player.Factory.Params params = new org.luwrain.player.Factory.Params();
	    params.luwrain = luwrain;
	    this.player = factory.newPlayer(params);
	    if (this.player == null)
	    {
		error("player factory of the class " + playerFactoryName + " returned null, no player");
		return;
	    }
	    debug("loaded player instance of class " + this.player.getClass().getName());
	    for (PropertiesProvider p: props.getBasicProviders())
		if (p instanceof org.luwrain.core.properties.Player)
		{
		    player.addListener((org.luwrain.player.Listener)p);
		    break;
		}
	}
	catch(Throwable e)
	{
	    error(e, "unable to load player class " + playerFactoryName);
	    this.player = null;
	    return;
	}
    }

    private void loadDesktop()
    {
	if (props.getProperty(DESKTOP_PROP_NAME).isEmpty())
	{
	    error("no property " + DESKTOP_PROP_NAME + ", unable to create a desktop");
	    throw new RuntimeException("unable to create a desktop");
	}
	this.desktop = (Application)org.luwrain.util.ClassUtils.newInstanceOf(this.getClass().getClassLoader(), props.getProperty(DESKTOP_PROP_NAME), Application.class);
	if (this.desktop == null)
	    throw new RuntimeException("unable to create a desktop");
	desktop.onLaunchApp(interfaces.requestNew(desktop));
	//	desktop.setConversations(conversations);
	apps.setDefaultApp(desktop);
    }

    void quit()
    {
	    mainStopCondition.stop();
    }

    //It is admissible situation if shortcut returns null
    void launchApp(String shortcutName, String[] args)
    {
	notEmpty(shortcutName, "shortcutName");
	notNullItems(args, "args");
	debug("launching the app \'" + shortcutName + "\' with " + args.length + " argument(s)");
	mainCoreThreadOnly();
	for(int i = 0;i < args.length;++i)
	    debug("args[" + i + "]: " + args[i]);
	final Shortcut shortcut = objRegistry.getShortcut(shortcutName);
	if (shortcut == null)
	{
	    message("Нет приложения с именем " + shortcutName, Luwrain.MessageType.ERROR);//FIXME:
	    return;
	}
	final var appRef = new AtomicReference<Application[]>();
	unsafeAreaOperation(()->{
		appRef.set(shortcut.prepareApp(args));
	    });
	final Application[] app = appRef.get();
	if (app == null)
	{
	    message("Приложение " + shortcutName + " не готово к запуску", Luwrain.MessageType.ERROR);//FIXME:
	    return;
	}
	for(Application a: app)
	    if (a == null)
	    {
		message("Приложение " + shortcutName + " не готово к запуску", Luwrain.MessageType.ERROR);//FIXME:
		return;
	    }
	soundManager.stopStartingMode();
	for(Application a: app)
	    launchApp(a);
    }

    void launchApp(Application app)
    {
	notNull(app, "app");
	mainCoreThreadOnly();
	//Checking if it is a mono app
	if (app instanceof MonoApp)
	{
	    final Application[] launchedApps = apps.getLaunchedApps();
	    for(Application a: launchedApps)
		if (a instanceof MonoApp && a.getClass().equals(app.getClass()))
		{
		    final MonoApp ma = (MonoApp)a;
		    final var ref = new AtomicReference<MonoApp.Result>();
		    unsafeAreaOperation(()->{
			    final MonoApp.Result value = ma.onMonoAppSecondInstance(app);
			    if (value != null)
				ref.set(value);
			});
		    if (ref.get() == null)
			continue;
		    final MonoApp.Result res = ref.get();
		    switch(res)
		    {
		    case SECOND_INSTANCE_PERMITTED:
			break;
		    case BRING_FOREGROUND:
			apps.setActiveApp(a);
			onNewAreasLayout();
			this.announcement = AnnouncementType.APP;
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
		error("no enough memory to launch the app of the class " + app.getClass().getName());
		message(i18n.getStaticStr("AppLaunchNoEnoughMemory"), Luwrain.MessageType.ERROR);
		return;
	    }
	    catch (Throwable e)
	    {
		error(e, "application " + app.getClass().getName() + " has thrown an exception on onLaunch()");
		launchAppCrash(new org.luwrain.app.crash.App(e, app, null));
		return;
	    }
if (initResult.getType() != InitResult.Type.OK)
{
    launchAppCrash(new org.luwrain.app.crash.App(new org.luwrain.app.crash.InitResultException(initResult), app, null));
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
	this.announcement = AnnouncementType.APP;
	    }

    void launchAppCrash(Luwrain instance, Throwable e)
    {
	notNull(instance, "instance");
	notNull(e, "e");
	final Application app = interfaces.findApp(instance);
	if (app != null)
	    launchAppCrash(new org.luwrain.app.crash.App(e, app, null));
    }

    void launchAppCrash(org.luwrain.app.crash.App app)
    {
	notNull(app, "app");
	final Luwrain o = interfaces.requestNew(app);
	final InitResult initResult;
	try {
	    initResult = app.onLaunchApp(o);
	}
	catch (OutOfMemoryError ee)
	{
	    interfaces.release(o);
	    return;
	}
	if (initResult == null || !initResult.isOk())
	    {
		interfaces.release(o);
		return;
	    }
	if (!apps.newApp(app))
	{
	    interfaces.release(o);
	    return; 
	}
	onNewAreasLayout();
	this.announcement = AnnouncementType.APP;
    }

    void closeApp(Luwrain instance)
    {
	notNull(instance, "instance");
	mainCoreThreadOnly();
	if (instance == luwrain)
	    throw new IllegalArgumentException("Trying to close an application through the special interface object");
	final Application app = interfaces.findApp(instance);
	if (app == null)
	    throw new IllegalArgumentException("Trying to close an application through an illegal interface object");
	if (desktop != null && app == desktop)
	{
	    quit();
	    return;
	}
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
	this.announcement = AnnouncementType.APP;
	    }

    void announceActiveAreaIface()
    {
	announceActiveArea();
    }

    void onNewAreaLayout(Luwrain instance)
    {
		notNull(instance, "instance");
	mainCoreThreadOnly();
	final Application app = interfaces.findApp(instance);
	if (app == null)
	{
	    warn("trying to update area layout with the unknown app instance");
	    return;
	}
	apps.updateAppAreaLayout(app);
	onNewAreasLayout();
    }

    void onSwitchNextAreaCommand()
    {
	mainCoreThreadOnly();
	tiles.activateNextArea();
	onNewAreasLayout();
	announceActiveArea();
    }

    void popup(Application app, Area area, Popup.Position pos, StopCondition stopCondition, boolean noMultipleCopies, boolean isWeakPopup)
    {
	notNull(area, "area");
	notNull(pos, "pos");
	notNull(stopCondition, "stopCondition");
	mainCoreThreadOnly();
	if (noMultipleCopies)
	    apps.onNewPopupOpening(app, area.getClass());
	final PopupStopCondition popupStopCondition = new PopupStopCondition(mainStopCondition, stopCondition);
	apps.addNewPopup(app, area, pos, popupStopCondition, noMultipleCopies, isWeakPopup);
	tiles.setPopupActive();
	onNewAreasLayout();
	announceActiveArea();
	eventLoop(popupStopCondition);
	apps.closeLastPopup();
onNewAreasLayout();
	setAreaIntroduction();
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

    void message(String text, Luwrain.MessageType messageType)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(messageType, "messageType");
	mainCoreThreadOnly();
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
	case ALERT:
	    message(text, Sounds.ALERT);
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

    void message(String text, Sounds sound)
    {
	mainCoreThreadOnly();
	if (text == null || text.trim().isEmpty())
	    return;
	this.announcement = null;
	if (sound != null)
	    playSound(sound);
	speech.speak(i18n.getSpeakableText(text, Luwrain.SpeakableTextType.NATURAL), Speech.PITCH_MESSAGE, 0);
	interaction.startDrawSession();
	interaction.clearRect(0, interaction.getHeightInCharacters() - 1, interaction.getWidthInCharacters() - 1, interaction.getHeightInCharacters() - 1);
	interaction.drawText(0, interaction.getHeightInCharacters() - 1, text, true);
	interaction.endDrawSession();
    }

    void fontSizeInc()
    {
	mainCoreThreadOnly();
	interaction.setDesirableFontSize(interaction.getFontSize() + 5); 
	windowManager.redraw();
	apps.sendBroadcastEvent(new SystemEvent(SystemEvent.Type.BROADCAST, SystemEvent.Code.FONT_SIZE_CHANGED));
	message(i18n.getStaticStr("FontSize") + " " + interaction.getFontSize(), Luwrain.MessageType.REGULAR);
    }

    void fontSizeDec()
    {
	mainCoreThreadOnly();
	if (interaction.getFontSize() < 15)
	    return;
	interaction.setDesirableFontSize(interaction.getFontSize() - 5); 
	windowManager.redraw();
	apps.sendBroadcastEvent(new SystemEvent(SystemEvent.Type.BROADCAST, SystemEvent.Code.FONT_SIZE_CHANGED));
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

            boolean runCommand(String command)
    {
	notNull(command, "command");
	mainCoreThreadOnly();
	if (command.trim().isEmpty())
	    return false;
	return commands.run(command.trim());
    }

    void startAreaListening()
    {
	final Area activeArea = getActiveArea(true);
	if (activeArea == null)
	    return;
	stopAreaListening();
	speech.silence();
	this.listening = new Listening(luwrain, speech, activeArea, ()->listeningProp.setStatus(false));
	final AtomicBoolean res = new AtomicBoolean();
	unsafeAreaOperation(()->res.set(listening.start()));
	if (res.get())
	    listeningProp.setStatus(true); else
	    eventNotProcessedMessage();
    }

    void stopAreaListening()
    {
	if (listening == null)
	    return;
	listening.cancel();
	listening = null;
    }

    private final class StartingModeProperty implements PropertiesProvider
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
	@Override public Set<PropertiesProvider.Flags> getPropertyFlags(String propName)
	{
	    NullCheck.notEmpty(propName, "propName");
	    if (propName.equals(PROP_NAME))
		return EnumSet.of(PropertiesProvider.Flags.PUBLIC,
				  PropertiesProvider.Flags.READ_ONLY);
	    return null;
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
	@Override public void setListener(PropertiesProvider.Listener listener)
	{
	}
    }
}
