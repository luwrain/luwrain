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

package org.luwrain.script.core;

import java.util.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;
import org.luwrain.popups.*;

import static org.luwrain.core.NullCheck.*;
import static org.luwrain.script.ScriptUtils.*;

public class PropertiesObj
{
    protected Properties properties;

    public PropertiesObj(Properties properties)
    {
	notNull(properties, "properties");
	this.properties = properties;
    }

    @HostAccess.Export
	public final ProxyExecutable getProperty = this::getPropertyImpl;
    private Object getPropertyImpl(Value[] args)
    {
	if (!notNullAndLen(args, 1) || !args[0].isString() || args[0].asString().trim().isEmpty())
	    throw new IllegalArgumentException("getProperty() takes exactly one non-empty string argument");
	return properties.getProperty(args[0].asString().trim());	
    }

    @HostAccess.Export
	public final ProxyExecutable setProperty = this::setPropertyImpl;
    private Object setPropertyImpl(Value[] args)
    {
	if (args == null || args.length != 2)
	    throw new IllegalArgumentException("setProperty() takes two argument");
	if (args[0] == null || args[0].isNull() || args[0].asString().trim().isEmpty())
	    throw new IllegalArgumentException("setProperty() takes a non-empty string as the first argument");
	if (args[1] != null && !args[1].isNull() && !args[1].isString())
	    throw new IllegalArgumentException("setProperty() takes null or a string as the second argument");
	properties.setProperty(args[0].asString().trim(), (args[1] == null || args[1].isNull())?null:args[1].asString());
	return this;
    }
}
