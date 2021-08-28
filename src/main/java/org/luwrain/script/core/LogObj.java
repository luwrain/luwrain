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

package org.luwrain.script.core;

import java.io.*;
import java.util.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;
import org.luwrain.script2.*;
import org.luwrain.util.*;

final class LogObj implements ProxyObject
{
    final Luwrain luwrain;

    LogObj(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    @Override public Object getMember(String name)
    {
	if (name == null)
	    return null;
	switch(name)
	{
	case "debug":
	    return(ProxyExecutable)(args)->log(args, (component, message)->Log.debug(component, message));
	case "info":
	    return(ProxyExecutable)(args)->log(args, (component, message)->Log.info(component, message));
	case "warning":
	    return(ProxyExecutable)(args)->log(args, (component, message)->Log.warning(component, message));
	case "error":
	    return(ProxyExecutable)(args)->log(args, (component, message)->Log.error(component, message));
	case "fatal":
	    return(ProxyExecutable)(args)->log(args, (component, message)->Log.fatal(component, message));
	default:
	    return null;
	}
    }

    @Override public boolean hasMember(String name)
    {
	switch(name)
	{
	case "debug":
	case "info":
	case "warning":
	case "error":
	case "fatal":
	    return true;
	default:
	    return false;
	}
    }

    @Override public Object getMemberKeys()
    {
	return new String[]{
	    "debug",
	    "info",
	    "warning",
	    "error",
	    "fatal",
	};
    }

    @Override public void putMember(String name, Value value)
    {
    }

    private Object log(Value[] args, Logger logger)
    {
	NullCheck.notNull(logger, "logger");
	if (!ScriptUtils.notNullAndLen(args, 2))
	    return null;
	final String component = ScriptUtils.asString(args[0]);
	final String message = ScriptUtils.asString(args[1]);
	if (component == null || component.trim().isEmpty() || message == null || message.trim().isEmpty())
	    return null;
	logger.logMessage(component, message);
	return null;
    }

    private interface Logger
    {
	void logMessage(String component, String message);
    }
}
