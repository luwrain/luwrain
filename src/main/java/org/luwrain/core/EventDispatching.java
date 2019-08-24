/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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
import java.util.concurrent.atomic.*;
import java.nio.file.*;

import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.core.extensions.*;
import org.luwrain.popups.*;
import org.luwrain.base.*;

abstract class EventDispatching extends Areas
{
    static private final String DESKTOP_PROP_NAME = "luwrain.class.desktop";
    static private final int POPUP_BLOCKING_MAY_PROCESS = 0;
    static private final int POPUP_BLOCKING_EVENT_REJECTED = 1;
    static private final int POPUP_BLOCKING_TRY_AGAIN = 2;

    protected final GlobalKeys globalKeys;
    protected Listening listening = null;
    protected final Desktop desktop;

    protected EventDispatching(CmdLine cmdLine, Registry registry,
			       PropertiesRegistry props, String lang, org.luwrain.base.Interaction interaction)
    {
	super(cmdLine, registry, props, lang, interaction);
	this.globalKeys = new GlobalKeys(registry);
	if (props.getProperty(DESKTOP_PROP_NAME).isEmpty())
	{
	    Log.error(LOG_COMPONENT, "no property " + DESKTOP_PROP_NAME + ", unable to create a desktop");
	    throw new RuntimeException("unable to create a desktop");
	}
	this.desktop = (Desktop)org.luwrain.util.ClassUtils.newInstanceOf(this.getClass().getClassLoader(), props.getProperty(DESKTOP_PROP_NAME), Desktop.class);
	if (this.desktop == null)
	    throw new RuntimeException("unable to create a desktop");
    }

    abstract protected void onBeforeEventProcessing();
    abstract protected void onAltX();

    @Override protected boolean onEvent(Event event)
    {
	try {
	    if (event instanceof RunnableEvent)
		return onRunnableEvent((RunnableEvent)event);
	    if (event instanceof CallableEvent)
		return onCallableEvent((CallableEvent)event);
	    if (event instanceof KeyboardEvent)
		return onInputEvent(Keyboard.translate((KeyboardEvent)event));
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
		    return onSystemEvent(environmentEvent);
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
	    Log.error(LOG_COMPONENT, "an exception of class " + e.getClass().getName() + " has been thrown while processing of event of class " + event.getClass().getName() + "::" + e.getMessage());
	    e.printStackTrace();
	    return true;
	}
    }

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

    @Override public void introduce(StopCondition stopCondition)
    {
	NullCheck.notNull(stopCondition, "stopCondition");
	if (needForIntroduction && stopCondition.continueEventLoop() && listening == null)
	{
	    if (introduceApp)
		announceActiveApp(); else
		announceActiveArea();
	}
	needForIntroduction = false;
	introduceApp = false;
    }

    private boolean onRunnableEvent(RunnableEvent event)
    {
	NullCheck.notNull(event, "event");
	try {
	    event.runnable.run();
	}
	catch(Throwable e)
	{
	    Log.error(LOG_COMPONENT, "exception on processing of RunnableEvent:" + e.getClass().getName() + ":" + e.getMessage());
	}
	return true;
    }

    private boolean onCallableEvent(CallableEvent event)
    {
	NullCheck.notNull(event, "event");
	try {
	    event.setResult(event.callable.call());
	}
	catch(Throwable e)
	{
	    Log.error(LOG_COMPONENT, "exception on processing of CallableEvent:" + e.getClass().getName() + ":" + e.getMessage());
	}
	return true;
    }

    private boolean onInputEvent(KeyboardEvent event)
    {
	onBeforeEventProcessing();
	if (systemHotKey(event))
	    return true;
	switch(popupBlocking())
	{
	case POPUP_BLOCKING_TRY_AGAIN:
	    return false;
	case POPUP_BLOCKING_EVENT_REJECTED:
	    //	    areaBlockedMessage();
	    return true;
	}
	final Area activeArea = getActiveArea();
	if (activeArea == null)
	{
	    noAppsMessage();
	    return true;
	}
	unsafeAreaOperation(()->{
		final Action[] actions = activeArea.getAreaActions();
		if (actions != null)
		    for(Action a: actions)
		    {
			final KeyboardEvent actionEvent = a.keyboardEvent();
			if (actionEvent == null || !actionEvent.equals(event))
			    continue;
			if (activeArea.onSystemEvent(new ActionEvent(a)))
			    return;
			break;
		    }
		if (!activeArea.onInputEvent(event))
		    playSound(Sounds.EVENT_NOT_PROCESSED);
	    });
	return true;
    }

    private boolean systemHotKey(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	final String commandName = globalKeys.getCommandName(event);
	if (commandName != null)
	{
	    if (!commands.run(commandName))
		message(i18n.getStaticStr("NoCommand"), Luwrain.MessageType.ERROR);
	    return true;
	}
	if (event.isSpecial())
	{
	    final KeyboardEvent.Special code = event.getSpecial();
	    if (code == KeyboardEvent.Special.CONTROL)
	    {
		speech.silence();
		soundManager.stopStartingMode();
		return true;
	    }
	    if (code == KeyboardEvent.Special.SHIFT ||
		code == KeyboardEvent.Special.CONTROL ||
		code == KeyboardEvent.Special.LEFT_ALT ||
		code == KeyboardEvent.Special.RIGHT_ALT)
		return true;
	}
	if (!event.isSpecial() &&
	    KeyboardEvent.getKeyboardLayout().onSameButton(event.getChar(), 'x') &&
	    event.withAltOnly())
	{
	    onAltX();
	    return true;
	}
	return false;
    }

    private boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	switch(popupBlocking())
	{
	case POPUP_BLOCKING_TRY_AGAIN:
	    return false;
	case POPUP_BLOCKING_EVENT_REJECTED:
	    //	    areaBlockedMessage();
	    return true;
	}
	int res = ScreenContentManager.EVENT_NOT_PROCESSED;
	try {
	    res = screenContentManager.onSystemEvent(event);
	}
	catch (Throwable e)
	{
	    Log.error(LOG_COMPONENT, "environment event throws an exception:" + e.getMessage());
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

    private boolean onBroadcastEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	apps.sendBroadcastEvent(event);
	return true;
    }

    private void announceActiveApp()
    {
	final Application app = apps.getActiveApp();
	if (app == null)
	{
	    noAppsMessage();
	    return;
	}
	if (app == desktop)
	    try {
	    	if (getObjForEnvironment().xRunHooks("luwrain.desktop.announce", new Object[0], Luwrain.HookStrategy.CHAIN_OF_RESPONSIBILITY))
		    return;
	    }
	    catch(RuntimeException e)
	    {
		Log.error(LOG_COMPONENT, "the luwrain.desktop.announce hook has thrown the runtime exception:" + e.getClass().getName() + ":" + e.getMessage());
	    }
	final AtomicReference res = new AtomicReference();
	unsafeAreaOperation(()->{
		final String value = app.getAppName();
		if (value != null)
		    res.set(value);
	    });
	final String name;
	if (res.get() != null)
	{
	    final String value = res.get().toString();
	    if (value != null && !value.trim().isEmpty())
		name = value; else
		name = app.getClass().getName();
	} else
	    name = app.getClass().getName();
	playSound(Sounds.INTRO_APP);
	speech.speak(name, 0, 0);
    }

    void announceActiveArea()
    {
	final Area activeArea = getActiveArea();
	if (activeArea == null)
	{
	    noAppsMessage();
	    return;
	}
	if (isActiveAreaBlockedByPopup() || isAreaBlockedBySecurity(activeArea))
	    return;
	final AtomicReference res = new AtomicReference();
	unsafeAreaOperation(()->{
		res.set(new Boolean(activeArea.onSystemEvent(new EnvironmentEvent(EnvironmentEvent.Code.INTRODUCE))));
	    });
	if (res.get() != null && ((Boolean)res.get()).booleanValue())
	    return;
	speech.silence();
	playSound(activeArea instanceof Popup?Sounds.INTRO_POPUP:Sounds.INTRO_REGULAR);
	unsafeAreaOperation(()->{
		final String value = activeArea.getAreaName();
		if (value != null)
		    res.set(value);
	    });
	final String name;
	if (res != null)
	{
	    final String value = res.get().toString();
	    if (value != null && !value.trim().isEmpty())
		name = value; else
		name = activeArea.getClass().getName();
	} else
	    name = activeArea.getClass().getName();
	speech.speak(name, 0, 0);
    }

    static class RunnableEvent extends Event
    {
	final Runnable runnable;

	public RunnableEvent(Runnable runnable)
	{
	    NullCheck.notNull(runnable, "runnable");
	    this.runnable = runnable;
	}
    }

    static class CallableEvent extends Event
    {
	final Callable callable;
	private Object result = null;

	CallableEvent(Callable callable)
	{
	    NullCheck.notNull(callable, "callable");
	    this.callable = callable;
	}

	void setResult(Object result)
	{
	    this.result = result;
	}

	Object getResult()
	{
	    return result;
	}
    }
}
