/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

import org.luwrain.core.Job.*;
import static org.luwrain.core.Job.*;

public class EmptyJobInstance implements Job.Instance
{
    protected final Listener listener;
    protected final String name;
    private Status status = Status.RUNNING;
    private int exitCode = EXIT_CODE_INVALID;
    private String state = "";
    private String[] multilineState = new String[0], nativeState = new String[0];

    public EmptyJobInstance(Listener listener, String name)
    {
	NullCheck.notEmpty(name, "name");
	this.listener = listener;
	this.name = name;
    }

    public EmptyJobInstance(String name)
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

    public void setSingleLineState(String value)
    {
	NullCheck.notNull(value, "value");
	if (this.status != Status.RUNNING)
	    throw new IllegalStateException("The job '" + this.name + "' is not running");
	this.state = value;
	if (this.listener != null)
	    this.listener.onSingleLineStateChange(this);
    }

		@Override public String getSingleLineState()
    {
	return this.state;
    }

    public void setMultilineState(String[] value)
    {
	NullCheck.notNullItems(value, "value");
		if (this.status != Status.RUNNING)
	    throw new IllegalStateException("The job '" + this.name + "' is not running");
		this.multilineState = value.clone();
		if (this.listener != null)
		    this.listener.onMultilineStateChange(this);
    }

		@Override public String[] getMultilineState()
    {
		    return this.multilineState.clone();
    }

    public void setNativeState(String[] value)
    {
	NullCheck.notNullItems(value, "value");
		if (this.status != Status.RUNNING)
	    throw new IllegalStateException("The job '" + this.name + "' is not running");
		this.nativeState = value.clone();
		if (this.listener != null)
		this.listener.onNativeStateChange(this);
    }

		@Override public String[] getNativeState()
    {
	return this.nativeState.clone();
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
