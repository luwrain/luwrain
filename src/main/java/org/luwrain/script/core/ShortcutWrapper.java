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

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;

import static org.luwrain.script2.ScriptUtils.*;

final class ShortcutImpl implements Shortcut
{
    private final String name;
    private final File dataDir;
    private final Value cons;

    ShortcutImpl(String name, File dataDir, Value cons)
    {
	NullCheck.notEmpty(name, "name");
	NullCheck.notNull(dataDir, "dateDir");
	NullCheck.notNull(cons, "cons");
	this.name = name;
	this.dataDir = dataDir;
	this.cons = cons;
    }

    @Override public Application[] prepareApp(String[] args)
    {
	NullCheck.notNullItems(args, "args");
	final Value newObj = cons.newInstance(ProxyArray.fromArray((Object[])args));
	if (newObj == null || newObj.isNull())
	    return null;
	final String name = asString(getMember(newObj, "name"));
	final String type = asString(getMember(newObj, "type"));
	if (name == null || name.trim().isEmpty())
	    return null;
	if (type == null)
	    return null;
	switch(type.trim().toUpperCase())
	{
	case "SIMPLE":
	    return new Application[]{new org.luwrain.script.app.Simple(name, dataDir, newObj)};
	default:
	    return null;
	}
    }

    @Override public String getExtObjName()
    {
	return name;
    }
}
