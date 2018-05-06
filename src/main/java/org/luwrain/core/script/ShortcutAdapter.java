/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

// https://docs.oracle.com/javase/8/docs/jdk/api/nashorn/jdk/nashorn/api/scripting/ScriptObjectMirror.html

package org.luwrain.core.script;

import java.io.*;
import java.util.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

final class ShortcutAdapter implements Shortcut
{
    private final String name;
    private final JSObject cons;

    ShortcutAdapter(String name, JSObject cons)
    {
	NullCheck.notEmpty(name, "name");
	NullCheck.notNull(cons, "cons");
	this.name = name;
	this.cons = cons;
    }

    @Override public Application[] prepareApp(String[] args)
    {
	NullCheck.notNullItems(args, "args");
	final Object newObj = cons.newObject();
	if (newObj == null || !(newObj instanceof JSObject))
	    return null;
	final ScriptObjectMirror newJsObj = (ScriptObjectMirror)newObj;
	if (newJsObj.get("name") == null || !(newJsObj.get("name") instanceof JSObject))
	    return null;
	if (newJsObj.get("type") == null || !(newJsObj.get("type") instanceof JSObject))
	    return null;
	final String name = newJsObj.get("name").toString();
	if (name == null || name.trim().isEmpty())
	    return null;
	return null;
    }

    @Override public String getExtObjName()
    {
	return name;
    }
}
