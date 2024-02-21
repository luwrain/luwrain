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

import java.io.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;

import static org.luwrain.script.ScriptUtils.*;
import static org.luwrain.core.NullCheck.*;

final class ShortcutImpl implements Shortcut
{
    private final Module module;
    private final String name;
    private final File dataDir;
    private final Value cons;

    ShortcutImpl(Module module, String name, File dataDir, Value cons)
    {
	notNull(module, "module");
	notEmpty(name, "name");
	notNull(dataDir, "dataDir");
	notNull(cons, "cons");
	//	this.luwrainObj = luwrainObj;
	this.module = module;
	this.name = name;
	this.dataDir = dataDir;
	this.cons = cons;
    }

    @Override public Application[] prepareApp(String[] args)
    {
	notNullItems(args, "args");
	synchronized(module.syncObj) {
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
		return new Application[]{new org.luwrain.script.app.Simple(name, dataDir, newObj, module.syncObj)};
	    default:
		return null;
	    }
	}
    }

    @Override public String getExtObjName()
    {
	return name;
    }
}
