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

package org.luwrain.core.script.api;

import java.io.*;
import java.util.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

final class ShortcutImpl implements Shortcut
{
    private final String name;
    private final File dataDir;
    private final JSObject cons;

    ShortcutImpl(String name, File dataDir, JSObject cons)
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
	final Object newObj = cons.newObject(org.luwrain.script.ScriptUtils.createReadOnlyArray(args));
	if (newObj == null || !(newObj instanceof JSObject))
	    return null;
	final ScriptObjectMirror newJsObj = (ScriptObjectMirror)newObj;
	if (newJsObj.get("name") == null || newJsObj.get("type") == null)
	    return null;
	final String name = newJsObj.get("name").toString();
	if (name == null)
	    return null;
	final String type = newJsObj.get("type").toString();
	if (type == null)
	    return null;
	switch(type.trim().toLowerCase())
	{
	case "simple":
	    return new Application[]{new org.luwrain.core.script.app.Simple(name, newJsObj)};
	    	case "simple-centered":
		    return new Application[]{new org.luwrain.core.script.app.SimpleCentered(name, dataDir, newJsObj)};
	    	default:
	    return null;
	}
    }

    @Override public String getExtObjName()
    {
	return name;
    }
}
