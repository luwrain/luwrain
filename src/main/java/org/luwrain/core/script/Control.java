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

import java.util.*;
import javax.script.*;
import jdk.nashorn.api.*;
import jdk.nashorn.api.scripting.*;
import java.util.function.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

final class Control extends AbstractJSObject
{
    private final Luwrain luwrain;
    final List<CommandLineTool> cmdLineTools = new LinkedList();
    final List<Shortcut> shortcuts = new LinkedList();
    final List<Command> commands = new LinkedList();

    Control(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    
    @Override public Object newObject(Object... args)
    {
	return new Control(luwrain);
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	switch(name)
	{
	case "message":
	    return (Consumer)this::message;
	case "addCommandLineTool":
	    return (BiPredicate)this::addCommandLineTool;
	case "addApp":
	case "addShortcut":
	    return (BiPredicate)this::addShortcut;
	case "addCommand":
	    return (BiPredicate)this::addCommand;
	case "launchApp":
	    return (Predicate)this::launchApp;
	default:
	    return null;
	}
    }

    private void message(Object b)
    {
	if (b != null && !b.toString().trim().isEmpty())
	    luwrain.message(b.toString());
    }

    private boolean addCommandLineTool(Object name, Object obj)
    {
	if (name == null || obj == null)
	    return false;
	if (!(obj instanceof jdk.nashorn.api.scripting.ScriptObjectMirror))
	    return false;
	final JSObject cons = (JSObject)obj;
	final Object newObj = cons.newObject();
	final ScriptObjectMirror newJsObj = (ScriptObjectMirror)newObj;
	luwrain.message(newJsObj.get("name").toString());
	//	luwrain.message(o2.getClass().getName());
	return true;
    }

    private boolean addShortcut(Object name, Object obj)
    {
	if (name == null || obj == null)
	    return false;
	if (!(obj instanceof jdk.nashorn.api.scripting.ScriptObjectMirror))
	    return false;
	for(Shortcut s: shortcuts)
	    if (s.getExtObjName().equals(name.toString()))
		return false;
	final JSObject cons = (JSObject)obj;
	shortcuts.add(new ShortcutAdapter(name.toString(), cons));
	return true;
    }

        private boolean addCommand(Object name, Object obj)
    {
	if (name == null || obj == null)
	    return false;
	if (!(obj instanceof jdk.nashorn.api.scripting.ScriptObjectMirror))
	    return false;
	for(Command c: commands)
	    if (c.getName().equals(name.toString()))
		return false;
	final JSObject func = (JSObject)obj;
	commands.add(new CommandAdapter(name.toString(), func));
	return true;
    }

    private boolean launchApp(Object name)
    {
	if (name == null)
	    return false;
luwrain.launchApp(name.toString());
//FIXME:proper return value
return true;
    }
}