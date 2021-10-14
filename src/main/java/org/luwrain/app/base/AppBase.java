/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

//LWR_API 2.0

package org.luwrain.app.base;

import java.util.concurrent.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;

abstract public class AppBase<S> extends TaskCancelling implements Application
{
    private Luwrain luwrain = null;
    private S strings = null;
    final String stringsName;
    final Class<S> stringsClass;
    private final String helpSection;
    private AreaLayoutHelper layout = null;
    private String appName = "";
    private Area[] visibleAreas = new Area[0];
    private FutureTask task = null;

    public interface TaskRunnable
    {
	void run() throws Exception;
    }

    public AppBase(String stringsName, Class<S> stringsClass, String helpSection)
    {
	NullCheck.notEmpty(stringsName, "stringsName");
	NullCheck.notNull(stringsClass, "stringsClass");
	this.stringsName = stringsName;
	this.stringsClass = stringsClass;
	this.helpSection = helpSection;
    }

        public AppBase(String stringsName, Class<S> stringsClass)
    {
	this(stringsName, stringsClass, null);
    }

    abstract protected AreaLayout onAppInit() throws Exception;

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Object o = luwrain.i18n().getStrings(stringsName);
	if (o == null || !stringsClass.isInstance(o))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, stringsName);
	this.strings = stringsClass.cast(o);
	this.luwrain = luwrain;
	final AreaLayout initialLayout;
	try {
	    initialLayout = onAppInit();
	    	if (initialLayout == null)
		    throw new Exception("The application is unable to initialize");
	}
	catch(Throwable e)
	{
	    return new InitResult(e);
	}
	this.layout = new AreaLayoutHelper(()->{
		this.setVisibleAreas(layout.getLayout().getAreas());
		luwrain.onNewAreaLayout();
	    }, initialLayout);
			this.setVisibleAreas(layout.getLayout().getAreas());
	return new InitResult();
    }

    @Override public void closeApp()
    {
	luwrain.closeApp();
    }

    @Override public String getAppName()
    {
	return this.appName;
    }

    protected void setAppName(String appName)
    {
	NullCheck.notEmpty(appName, "appName");
	this.appName = appName;
    }

    @Override public AreaLayout getAreaLayout()
    {
	return this.layout.getLayout();
    }

    public void onCancelledTask()
    {
    }

    public boolean onEscape()
    {
	return false;
    }

    public boolean onInputEvent(Area area, InputEvent event, Runnable closing)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(event, "event");
	if (!event.isSpecial() || event.isModified())
	    return false;
	switch(event.getSpecial())
	{
	case ESCAPE:
	    if (isBusy())
	    {
	    cancelTask();
	    return true;
	    }
	    if (closing != null)
	    {
		closing.run();
		return true;
	    }
	    return onEscape();
	case TAB:
	    {
		final Area nextArea = layout.getLayout().getNextArea(area);
		if (nextArea == null)
		    return false;
		luwrain.setActiveArea(nextArea);
		return true;
	    }
	}
	return false;
    }

        public boolean onInputEvent(Area area, InputEvent event)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(event, "event");
	return onInputEvent(area, event, null);
    }

    public boolean onSystemEvent(Area area, SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != SystemEvent.Type.REGULAR)
	    return false;
	switch(event.getCode())
	{
	case HELP:
	    if (helpSection == null || helpSection.isEmpty())
		return false;
	    return luwrain.openHelp(helpSection);
	case CLOSE:
	    closeApp();
	    return true;
	default:
	    return false;
	}
    }

    public boolean onSystemEvent(Area area, SystemEvent event, LayoutBase.Actions actions)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() == SystemEvent.Type.REGULAR)
	switch(event.getCode())
	{
	case ACTION:
	    if (actions.onActionEvent(event))
		return true;
	}
	return onSystemEvent(area, event);
    }

    public boolean onAreaQuery(Area area, AreaQuery query)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(query, "query");
			    switch(query.getQueryCode())
		    {
		    case AreaQuery.BACKGROUND_SOUND:
			if (isBusy())
			{
			    ((BackgroundSoundQuery)query).answer(new BackgroundSoundQuery.Answer(BkgSounds.FETCHING));
			    return true;
			}
			return false;
		    default:
			return false;
		    }
    }

    void setVisibleAreas(Area[] visibleAreas)
    {
	NullCheck.notNullItems(visibleAreas, "visibleAreas");
	this.visibleAreas = visibleAreas.clone();
    }

    private boolean runTask(FutureTask task)
    {
	NullCheck.notNull(task, "task");
	if (isBusy())
	    return false;
	this.task = task;
	luwrain.executeBkg(this.task);
	for(Area a: visibleAreas)
	    luwrain.onAreaNewBackgroundSound(a);
	return true;
    }

    public boolean runTask(TaskId taskId, TaskRunnable runnable)
    {
	NullCheck.notNull(taskId, "taskId");
	NullCheck.notNull(runnable, "runnable");
	return runTask(new FutureTask<>(()->{
		    try {
			try {
			    runnable.run();
			}
			catch(Throwable e)
			{
			    finishedTask(taskId, ()->onException(e));
			}
		    }
		    finally {
			finishedTask(taskId, ()->{});
		    }
	}, null));
    }

    public synchronized void finishedTask(TaskId taskId, Runnable runnable)
    {
	NullCheck.notNull(taskId, "taskId");
	NullCheck.notNull(runnable, "runnable");
	if (!isBusy() || !taskId.finish())
	    return;
	luwrain.runUiSafely(()->{
		if (!isRunningTaskId(taskId))
		    return;
		resetTask();
		runnable.run();
	    });
    }

    @Override public void cancelTask()
    {
	if (!isBusy())
	    return;
		super.cancelTask();
	task.cancel(true);
	luwrain.playSound(Sounds.CLICK);
	resetTask();
	onCancelledTask();
    }

    void resetTask()
    {
	if (this.task == null)
	    return;
	this.task = null;
	for(Area a: visibleAreas)
	    luwrain.onAreaNewBackgroundSound(a);
    }

    public boolean isBusy()
    {
	return task != null && !task.isDone();
    }

    public void onException(Throwable e)
    {
	 luwrain.crash(e);
	 }

    protected AreaLayoutHelper getLayout()
    {
	return this.layout;
    }

    public void setAreaLayout(LayoutBase layout)
    {
	NullCheck.notNull(layout, "layout");
	getLayout().setBasicLayout(layout.getAreaLayout());
    }

    public Luwrain getLuwrain()
    {
	return this.luwrain;
    }

    public S getStrings()
    {
	return this.strings;
    }

    public void crash(Throwable t)
    {
	NullCheck.notNull(t, "t");
	luwrain.crash(t);
    }

        public org.luwrain.i18n.I18n getI18n()
    {
	return luwrain.i18n();
    }

    public void setEventResponse(EventResponse resp)
    {
	NullCheck.notNull(resp, "resp");
	luwrain.setEventResponse(resp);
    }

    public void message(String text, Luwrain.MessageType type)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(type, "type");
	luwrain.message(text, type);
    }

    public void message(String text)
    {
	NullCheck.notNull(text, "text");
	luwrain.message(text);
    }
}
