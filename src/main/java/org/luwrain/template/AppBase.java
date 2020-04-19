/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.template;

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
    private AreaLayoutHelper layout = null;
    private String appName = "";
    private Area[] visibleAreas = new Area[0];

    private FutureTask task = null;

    public AppBase(String stringsName, Class stringsClass)
    {
	NullCheck.notEmpty(stringsName, "stringsName");
	NullCheck.notNull(stringsClass, "stringsClass");
	this.stringsName = stringsName;
	this.stringsClass = stringsClass;
    }

    abstract protected boolean onAppInit() throws Exception;
    abstract protected AreaLayout getDefaultAreaLayout();

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Object o = luwrain.i18n().getStrings(stringsName);
	if (o == null || !stringsClass.isInstance(o))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, stringsName);
	this.strings = stringsClass.cast(o);
	this.luwrain = luwrain;
	try {
	    onAppInit();
	}
	catch(Exception e)
	{
	    return new InitResult(e);
	}
	this.layout = new AreaLayoutHelper(()->{
		this.setVisibleAreas(layout.getLayout().getAreas());
		luwrain.onNewAreaLayout();
	    }, getDefaultAreaLayout());
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

    public boolean onInputEvent(Area area, KeyboardEvent event)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(event, "event");
	if (!event.isSpecial() || event.isModified())
	    return false;
	switch(event.getSpecial())
	{
	case ESCAPE:
	    if (!isBusy())
		return false;
	    cancelTask();
	    return true;
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

    public boolean onSystemEvent(Area area, EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != EnvironmentEvent.Type.REGULAR)
	    return false;
	switch(event.getCode())
	{
	case CLOSE:
	    closeApp();
	    return true;
	default:
	    return false;
	}
    }

    public boolean onSystemEvent(Area area, EnvironmentEvent event, LayoutBase.Actions actions)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() == EnvironmentEvent.Type.REGULAR)
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

    public boolean runTask(FutureTask task)
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

    public boolean runTask(TaskId taskId, Runnable runnable)
    {
	NullCheck.notNull(taskId, "taskId");
	NullCheck.notNull(runnable, "runnable");
	return runTask(new FutureTask(()->{
		    try {
			try {
		    runnable.run();
			}
			catch(Exception e)
			{
			    luwrain.crash(e);
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
	if (!isBusy() || !isRunningTaskId(taskId))
	    return;
	luwrain.runUiSafely(()->{
		runnable.run();
			resetTask();
		});
    }

    @Override public void cancelTask()
    {
	if (!isBusy())
	    return;
	task.cancel(true);
	super.cancelTask();
	luwrain.playSound(Sounds.ERROR);
	resetTask();
    }

    public void resetTask()
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

    protected AreaLayoutHelper getLayout()
    {
	return this.layout;
    }

    

    public Luwrain getLuwrain()
    {
	return this.luwrain;
    }

    public org.luwrain.i18n.I18n getI18n()
    {
	return luwrain.i18n();
    }

    public S getStrings()
    {
	return this.strings;
    }
}
