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

package org.luwrain.util;

import java.util.*;
import java.util.concurrent.*;

import org.luwrain.core.*;

public final class TaskCancelling
{
    static public final class TaskId
    {
	private final long id;
	public TaskId(long id)
	{
	    if (id < 0)
		throw new IllegalArgumentException("id (" + String.valueOf(id) + ") may not be negative");
	    this.id = id;
	}
	public long getId()
	{
	    return this.id;
	}
    }

    private volatile long id = 0;
    private volatile boolean cancelled = false;

    synchronized public TaskId newTaskId()
    {
	this.id++;
	this.cancelled = false;
	return new TaskId(this.id);
    }

    synchronized public void cancel()
    {
	this.cancelled = true;
    }

    synchronized public boolean isValidTaskId(TaskId taskId)
    {
	NullCheck.notNull(taskId, "taskId");
	return this.id == taskId.getId() && !this.cancelled;
    }
}
