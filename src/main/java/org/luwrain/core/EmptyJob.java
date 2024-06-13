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

//LWR_API 1.0

package org.luwrain.core;

import java.util.*;

import org.luwrain.core.JobLauncher.*;

import static org.luwrain.core.JobLauncher.*;
import static org.luwrain.core.NullCheck.*;

public class EmptyJob implements Job
{
    protected final Listener listener;
    protected final String name;
    protected Map<String, List<String>> info = new HashMap<>();
    private Status status = Status.RUNNING;
    private int exitCode = EXIT_CODE_INVALID;

    public EmptyJob(Listener listener, String name)
    {
	notEmpty(name, "name");
	this.listener = listener;
	this.name = name;
    }

    public EmptyJob(String name)
    {
	this(null, name);
    }

	@Override public String getInstanceName()
    {
	return this.name;
    }

		@Override public Status getStatus()
    {
	return this.status;
    }

	@Override public int getExitCode()
    {
	if (this.status != Status.FINISHED)
	    throw new IllegalStateException("The job '" + this.name + "' is still running");
	    return this.exitCode;
}

		@Override public boolean isFinishedSuccessfully()
    {
	return this.status == Status.FINISHED && this.exitCode == EXIT_CODE_OK;
    }

    public void setInfo(String infoType, List<String> value)
    {
	notEmpty(infoType, "infoType");
	notNull(value, "value");
	if (this.status != Status.RUNNING)
	    throw new IllegalStateException("The job '" + this.name + "' is not running");
	this.info.put(infoType, value);
	if (this.listener != null)
	    this.listener.onInfoChange(this, infoType, value);
    }

		@Override public List<String> getInfo(String infoType)
    {
	notEmpty(infoType, "infoType");
	final var res = this.info.get(infoType);
	return res != null?res:Arrays.asList();
	    }

    public void stop(int exitCode)
    {
			if (this.status != Status.RUNNING)
	    throw new IllegalStateException("The job '" + this.name + "' is not running");
			this.status = Status.FINISHED;
			this.exitCode = exitCode;
			if (this.listener != null)
			    this.listener.onStatusChange(this);
    }

    @Override public void stop()
    {
    }
}
