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

package org.luwrain.script.core;

import java.util.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;
import org.luwrain.popups.*;

import static org.luwrain.script.ScriptUtils.*;

public final class PopupsObj
{
    private final Luwrain luwrain;
    PopupsObj(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    @HostAccess.Export public final ProxyExecutable confirmDefaultYes = this::confirmDefaultYesImpl;
    private Boolean confirmDefaultYesImpl(Value[] args)
    {
		if (!notNullAndLen(args, 2))
	    return false;
	if (!args[0].isString() || !args[1].isString())
	    return false;
	return new Boolean(Popups.confirmDefaultYes(luwrain, args[0].asString(), args[1].asString()));
    }

    @HostAccess.Export public final ProxyExecutable confirmDefaultNo = this::confirmDefaultNoImpl;
    private Boolean confirmDefaultNoImpl(Value[] args)
    {
	if (!notNullAndLen(args, 2))
	    return false;
	if (!args[0].isString() || !args[1].isString())
	    return false;
	return new Boolean(Popups.confirmDefaultNo(luwrain, args[0].asString(), args[1].asString()));
    }

    @HostAccess.Export public final ProxyExecutable text = this::textImpl;
    private String textImpl(Value[] args)
    {
	if (!notNullAndLen(args, 3))
	    return null;
	for(int i = 0;i < args.length;i++)
	    if (!args[i].isString())
		return null;
	final String name = args[0].asString();
	final String text = args[1].asString();
	final String defaultValue = args[2].asString();
	return Popups.text(luwrain, name, text, defaultValue);
    };
}
