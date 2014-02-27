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

import org.luwrain.core.*;
import org.luwrain.core.events.*;

class TaskStatusUpdateEvent extends ThreadSyncEvent
{
    private Task task;
    private int state;
    private int percent;

    public TaskStatusUpdateEvent(Area area,
				 Task task,
				 int state,
				 int percent)
    {
	super(area);
	this.task = task;
	this.state = state;
	this.percent = percent;
    }

    public Task getTask()
    {
	return task;
    }

    public int getState()
    {
	return state;
    }

    public int getPercent()
    {
	return percent;
    }
}
