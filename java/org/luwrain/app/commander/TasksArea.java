/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.app.commander;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

class TasksArea extends NavigateArea
{
    private Luwrain luwrain;
    private StringConstructor stringConstructor;
    private Actions actions;
    Vector<Task> tasks = new Vector<Task>();

    public TasksArea(Luwrain luwrain,
		     Actions actions,
		     StringConstructor stringConstructor)
    {
	super(new DefaultControlEnvironment(luwrain));
	this.luwrain = luwrain;
	this.stringConstructor = stringConstructor;
	this.actions = actions;
    }

    public void addTask(Task task)
    {
	if (task != null)
	    tasks.add(task);
    }

    public int getLineCount()
    {
	return tasks.size() + 1;
    }

    public String getLine(int index)
    {
	return index < tasks.size()?constructStringForScreen(tasks.get(index)):"";
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event.isCommand() && !event.isModified() &&
	    event.getCommand() == KeyboardEvent.TAB)
	{
	    actions.gotoLeftPanel();
	    return true;
	}
	return super.onKeyboardEvent(event);
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	switch(event.getCode())
	{
	case EnvironmentEvent.THREAD_SYNC:
	    if (event instanceof TaskStatusUpdateEvent)
		onTaskStatusUpdateEvent((TaskStatusUpdateEvent)event);
	    return true;
	case EnvironmentEvent.CLOSE:
	    actions.close();
	    return true;
	case EnvironmentEvent.INTRODUCE:
	    Speech.say(stringConstructor.appName() + " " + stringConstructor.tasksAreaName());
	    return true;
	default:
	    return super.onEnvironmentEvent(event);
	}
    }

    public String getName()
    {
	return stringConstructor.tasksAreaName();
    }

    private void onTaskStatusUpdateEvent(TaskStatusUpdateEvent event)
    {
	int index = 0;
	while(index < tasks.size() && tasks.get(index) != event.getTask())
	    index++;
	if (index >= tasks.size())
	{
	    Log.warning("commander", "received the TaskStatusUpdateEvent for an unknown task");
	    return;
	}
	Task task = tasks.get(index);
	task.state = event.getState();
	task.percent = event.getPercent();
	if (task.state == Task.DONE)
	{
	    Speech.say(stringConstructor.done());
	    actions.refresh();
	}
	if (task.state == Task.FAILED)
	{
	    Speech.say(stringConstructor.failed());
	    actions.refresh();
	}
	luwrain.onAreaNewContent(this);
    }

    private String constructStringForScreen(Task task)
    {
	if (task == null)
	    return "";
	switch(task.state)
	{
	case Task.LAUNCHED:
	return task.title + ": " + task.percent + "%";
	case Task.DONE:
	    return task.title + ": " + stringConstructor.done();
	case Task.FAILED:
	    return task.title + ": " + stringConstructor.failed();
	default:
	    return "";
	}
    }
}
