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
    public final List<Shortcut> shortcuts = new LinkedList();
    public final List<Command> commands = new LinkedList();
        public final List<Worker> workers = new LinkedList();
    public final Map<String, List<Hook>> hooks = new HashMap();

    private final RegistryObj registryObj;
    private final PopupsObj popupsObj;
    private final MessageObj messageObj;
    private final SoundsObj sounds;
    private final SpokenTextObj spokenText;

    public LuwrainObj(Luwrain luwrain, File dataDir)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(dataDir, "dataDir");
	this.luwrain = luwrain;
	this.dataDir = dataDir;
	this.registryObj = new RegistryObj(luwrain.getRegistry(), "/");
	this.popupsObj = new PopupsObj(luwrain);
	this.messageObj = new MessageObj(luwrain);
	this.sounds = new SoundsObj(luwrain);
	this.spokenText = new SpokenTextObj(luwrain);
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
	case "spokenText":
	    return spokenText;
	case "message":
	    return messageObj;
	case "speak":
	    return (Consumer)this::speak;
	case "addHook":
	    return (BiPredicate)this::addHook;
	case "openUrl":
	    return (Predicate)this::openUrl;
	    	case "createPropertyHook":
	    return (BiPredicate)this::createPropertyHook;
	    	case "addWorker":
		    return createAddWorker();
	case "addApp":
	case "addShortcut":
	    return (BiPredicate)this::addShortcut;
	case "addCommand":
	    return (BiPredicate)this::addCommand;
	case "getActiveAreaText":
	    return (Function)this::getActiveAreaText;
	case "launchApp":
	    return (BiPredicate)this::launchApp;
	case "runBkg":
	    return (Predicate)this::runBkg;
	case "quit":
	    return (Supplier)this::quit;
	case "runWorker":
	    return (Predicate)this::runWorker;
	default:
	    return super.getMember(name);
	}
    }

    private void speak(Object arg)
    {
	final String text = org.luwrain.script.ScriptUtils.getStringValue(arg);
	if (text != null && !text.trim().isEmpty())
	    luwrain.speak(text);
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

    private Object createAddWorker()
    {
	return new org.luwrain.script.EmptyHookObject(){
	    @Override public boolean isFunction()
	    {
		return true;
	    }
	    @Override public Object call(Object thiz, Object[] args)
	    {
		if (args == null || args.length != 4)
		    return new Boolean(false);
		final String name = org.luwrain.script.ScriptUtils.getStringValue(args[0]);
		final Integer firstLaunchDelay = org.luwrain.script.ScriptUtils.getIntegerValue(args[1]);
		final Integer launchPeriod = org.luwrain.script.ScriptUtils.getIntegerValue(args[2]);
		final JSObject func = org.luwrain.script.ScriptUtils.toValidJsObject(args[3]);
		if (name == null || name.isEmpty())
		    return new Boolean(false);
		if (firstLaunchDelay == null || firstLaunchDelay.intValue() < 0)
		    return new Boolean(false);
		if (launchPeriod == null || launchPeriod.intValue() < 1)
		    return new Boolean(false);
		if (func == null || !func.isFunction())
		    return new Boolean(false);
		workers.add(new WorkerImpl(name, firstLaunchDelay.intValue(), launchPeriod.intValue(), func));
		return new Boolean(true);
	    }
	};
    }

            private boolean addHook(Object name, Object func)
    {
	if (name == null || func == null)
	    return false;
	if (!(func instanceof jdk.nashorn.api.scripting.ScriptObjectMirror))
	    return false;
	final  jdk.nashorn.api.scripting.ScriptObjectMirror funcObj = (jdk.nashorn.api.scripting.ScriptObjectMirror)func;
	    if (!funcObj.isFunction())
		return false;
	    	if (!hooks.containsKey(name.toString()))
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

    private Object quit()
    {
	return new Boolean(luwrain.xQuit());
    }

    private boolean openUrl(Object obj)
    {
	if (obj == null)
	    return false;
	final String  url = org.luwrain.script.ScriptUtils.getStringValue(obj);
	if (url == null || url.isEmpty())
	    return false;
	return luwrain.openUrl(url);
    }

    private boolean runWorker(Object nameObj)
    {
	final String name = org.luwrain.script.ScriptUtils.getStringValue(nameObj);
	if (name == null || name.trim().isEmpty())
	    return false;
	return luwrain.runWorker(name);
    }
}
