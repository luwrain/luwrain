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

//LWR_API 1.0

package org.luwrain.core;

public final class ErrorJob implements Job.Instance
{
    private final String name;
    private final String message;

    public ErrorJob(String name, String message)
    {
	NullCheck.notEmpty(name, "name");
	NullCheck.notEmpty(message, "message");
	this.name = name;
	this.message = message;
    }

    @Override public void stop()
    {
    }

    @Override public String getInstanceName()
    {
	return name;
    }

    @Override public Job.Status getStatus()
    {
	return Job.Status.FINISHED;
    }

    @Override public int getExitCode()
    {
	return 1;
    }

    @Override public boolean isFinishedSuccessfully()
    {
	return false;
    }

    @Override public String getSingleLineState()
    {
	return message;
    }

    @Override public String[] getMultilineState()
    {
	return new String[]{message};
    }

    @Override public String[] getNativeState()
    {
	return new String[]{message};
    }
}
