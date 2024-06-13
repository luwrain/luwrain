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

package org.luwrain.core;

import java.util.*;

import static org.luwrain.core.NullCheck.*;

public final class ErrorJobInstance implements Job
{
    private final String name;
    private final String message;

    public ErrorJobInstance(String name, String message)
    {
	notEmpty(name, "name");
	notEmpty(message, "message");
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

    @Override public List<String> getInfo(String infoType)
    {
	notEmpty(infoType, "infoType");
	if (infoType.equals("main"))
	    return Arrays.asList(message);
	return Arrays.asList();
    }
}
