
package org.luwrain.core;

import java.util.*;
import java.util.concurrent.*;
import java.nio.file.*;

import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.core.extensions.*;
import org.luwrain.popups.*;
import org.luwrain.base.*;

abstract class EventDispatching extends EnvironmentAreas
{
    static private final int POPUP_BLOCKING_MAY_PROCESS = 0;
    static private final int POPUP_BLOCKING_EVENT_REJECTED = 1;
    static private final int POPUP_BLOCKING_TRY_AGAIN = 2;

    protected final CommandManager commands = new CommandManager();
protected final GlobalKeys globalKeys;

    protected AreaListening listening = null;

    protected EventDispatching(CmdLine cmdLine, Registry registry,
			       org.luwrain.base.CoreProperties coreProps, String lang,
			       org.luwrain.base.Interaction interaction)
    {
	super(cmdLine, registry, coreProps, lang, interaction);
	this.globalKeys = new GlobalKeys(registry);
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
		return onKeyboardEvent(Keyboard.translate((KeyboardEvent)event));
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
		introduceActiveApp(); else
		introduceActiveArea();
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

    private boolean onKeyboardEvent(KeyboardEvent event)
    {
	onBeforeEventProcessing();
	if (keyboardEventForEnvironment(event))
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
	    Log.error(LOG_COMPONENT, "active area of class " + activeArea.getClass().getName() + " throws an exception on keyboard event processing:" + e.getMessage());
	    e.printStackTrace();
	    speech.silence();
	    playSound(Sounds.EVENT_NOT_PROCESSED);
	    speech.speak(e.getMessage(), 0, 0);
	    return true;
	}
    }

    private boolean keyboardEventForEnvironment(KeyboardEvent event)
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
	    onAltX();
	    return true;
	}
	return false;
    }

    private boolean onEnvironmentEvent(EnvironmentEvent event)
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
	    res = screenContentManager.onEnvironmentEvent(event);
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