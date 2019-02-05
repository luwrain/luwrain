/*
   Copyright 2012-2019 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
import java.util.function.*;
import java.util.concurrent.*;
import javax.script.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.base.*;
import org.luwrain.core.*;
import org.luwrain.core.script.Utils;

public final class LuwrainObj extends AbstractJSObject
{
    private final Luwrain luwrain;
    private final File dataDir;
    public final List<CommandLineTool> cmdLineTools = new LinkedList();
    public final List<Shortcut> shortcuts = new LinkedList();
    public final List<Command> commands = new LinkedList();
    public final List<TextEditingExtension> textEdits = new LinkedList();
    public final Map<String, List<Hook>> hooks = new HashMap();

    private final RegistryObj registryObj;
    private final PopupsObj popupsObj;
    private final SoundsObj sounds;

    public LuwrainObj(Luwrain luwrain, File dataDir)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(dataDir, "dataDir");
	this.luwrain = luwrain;
	this.dataDir = dataDir;
	this.registryObj = new RegistryObj(luwrain.getRegistry(), "/");
	this.popupsObj = new PopupsObj(luwrain);
	this.sounds = new SoundsObj(luwrain);
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	switch(name)
	{
	case "registry":
	    return registryObj;
	case "player":
	    if (luwrain.getPlayer() != null)
		return new PlayerObj(luwrain.getPlayer());
	    return super.getMember(name);
	case "popups":
	    return popupsObj;
	case "prop":
	    return new PropObj(luwrain, "");
	case "i18n":
	    return new I18nObj(luwrain);
	case "os":
	    return luwrain.xGetOsInterface();
	case "sounds":
	    return sounds;
	case "message":
	    return (Consumer)this::message;
	case "addHook":
	    return (BiPredicate)this::addHook;
	    	case "createPropertyHook":
	    return (BiPredicate)this::createPropertyHook;
	case "addCommandLineTool":
	    return (BiPredicate)this::addCommandLineTool;
	case "addApp":
	case "addShortcut":
	    return (BiPredicate)this::addShortcut;
	case "addCommand":
	    return (BiPredicate)this::addCommand;
	case "addTextEditing":
	    return (BiPredicate)this::addTextEditing;
	case "getActiveAreaText":
	    return (Function)this::getActiveAreaText;
	case "launchApp":
	    return (BiPredicate)this::launchApp;
	case "runBkg":
	    return (Predicate)this::runBkg;
	default:
	    return super.getMember(name);
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
	final String nameStr = org.luwrain.script.ScriptUtils.getStringValue(name);
	if (nameStr == null || nameStr.isEmpty()  ||
	    obj == null || !(obj instanceof JSObject))
	    return false;
	for(Shortcut s: shortcuts)
	    if (s.getExtObjName().equals(nameStr))
		return false;
	final JSObject cons = (JSObject)obj;
	if (!cons.isFunction())
	    return false;
	shortcuts.add(new org.luwrain.core.script.api.ShortcutImpl(nameStr, dataDir, cons));
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
	commands.add(new org.luwrain.core.script.api.CommandImpl(name.toString(), func));
	return true;
    }

            private boolean addHook(Object name, Object func)
    {
	if (name == null || func == null)
	    return false;
	if (!(func instanceof jdk.nashorn.api.scripting.ScriptObjectMirror))
	    return false;
	final  jdk.nashorn.api.scripting.ScriptObjectMirror funcObj = (jdk.nashorn.api.scripting.ScriptObjectMirror)func;
	if (!hooks.containsKey(name.toString()))
	    if (!funcObj.isFunction())
		return false;
	    hooks.put(name.toString(), new LinkedList());
	hooks.get(name.toString()).add(new org.luwrain.core.script.api.Hook(funcObj));
	return true;
    }

    private boolean createPropertyHook(Object propName, Object hookName)
    {
	final String prop = org.luwrain.script.ScriptUtils.getStringValue(propName);
		final String hook = org.luwrain.script.ScriptUtils.getStringValue(hookName);
		if (prop == null || prop.trim().isEmpty() || !prop.equals(prop.trim()) ||
		    hook == null || hook.trim().isEmpty() || !hook.equals(hook.trim()))
		    return false;
		return luwrain.xCreatePropertyHook(prop, hook);
    }

            private boolean addTextEditing(Object name, Object obj)
    {
	if (name == null || obj == null)
	    return false;
	if (!(obj instanceof jdk.nashorn.api.scripting.ScriptObjectMirror))
	    return false;
	for(TextEditingExtension t: textEdits)
	    if (t.getExtObjName().equals(name.toString()))
		return false;
	final JSObject func = (JSObject)obj;
	//textEdits.add(new TextEditingAdapter(name.toString(), func));
	return true;
    }

    private boolean runBkg(Object obj)
    {
	if (obj == null)
	    return false;
	if (!(obj instanceof jdk.nashorn.api.scripting.ScriptObjectMirror))
	    return false;
	final JSObject func = (JSObject)obj;
	luwrain.executeBkg(new FutureTask(()->{
		    func.call(null, new Object[0]);
	}, null));
	return true;
    }

    private String getActiveAreaText(Object type)
    {
	if (type == null)
	    return null;
	final String typeStr = type.toString();
	if (typeStr == null)
	    return null;
	final Luwrain.AreaTextType typeValue;
	switch(typeStr.toLowerCase().trim())
	{
	case "region":
	    typeValue = Luwrain.AreaTextType.REGION;
	    break;
	case "word":
	    typeValue = Luwrain.AreaTextType.WORD;
	    break;
	case "line":
	    typeValue = Luwrain.AreaTextType.LINE;
	    break;
	case "sentence":
	    typeValue = Luwrain.AreaTextType.SENTENCE;
	    break;
	case "url":
	    typeValue = Luwrain.AreaTextType.URL;
	default:
	    return null;
	}
	return luwrain.getActiveAreaText(typeValue, false);
    }

    private boolean launchApp(Object name, Object args)
    {
	if (name == null || args == null || !(args instanceof JSObject))
	    return false;
	final JSObject jsArgs = (JSObject)args;
	if (!jsArgs.isArray())
	    return false;
	final List<String> argsList = Utils.getStringArray(jsArgs);
	if (argsList == null)
	    return false;
	luwrain.launchApp(name.toString(), argsList.toArray(new String[argsList.size()]));
//FIXME:proper return value
return true;
    }
}
