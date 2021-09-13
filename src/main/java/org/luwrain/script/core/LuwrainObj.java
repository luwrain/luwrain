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
import java.util.concurrent.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;
import org.luwrain.script2.*;
import org.luwrain.util.*;

import static org.luwrain.script2.ScriptUtils.*;

final class LuwrainObj implements ProxyObject
{
    static private String[] KEYS = new String[]{
	"addCommand",
	"addHook",
	"addShortcut",
	"addWorker",
	"const",
	"executeBkg",
	"i18n",
	"isDigit",
	"isLetter",
	"isLetterOrDigit",
	"isSpace",
	"launchApp",
	"log",
	"message",
	"now",
	"popups",
	"readTextFile",
	"speak",
	"quit",
    };
    static private final Set<String> KEYS_SET = new HashSet(Arrays.asList((Object[])KEYS));
    static private final ProxyArray KEYS_ARRAY = ProxyArray.fromArray((Object[])KEYS);

    private final LogObj logObj;
    private final ConstObj constObj = new ConstObj();
    private final I18nObj i18nObj;
    private final PopupsObj popups;

        final Luwrain luwrain;
    final Object syncObj = new Object();
        final Map<String, List<Value> > hooks = new HashMap<>();
        final List<ExtensionObject> extObjs = new ArrayList<>();
    final List<Command> commands = new ArrayList<>();

    LuwrainObj(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	this.logObj = new LogObj(luwrain);
	this.i18nObj = new I18nObj(luwrain);
	this.popups = new PopupsObj(luwrain);
    }

    @Override public Object getMember(String name)
    {
	if (name == null)
	    return null;
	switch(name)
	{
	    	case "addCommand":
	    return(ProxyExecutable)this::addCommand;
	case "addHook":
	    return(ProxyExecutable)this::addHook;
	    	    	case "addShortcut":
	    return(ProxyExecutable)this::addShortcut;
	    	case "addWorker":
	    return(ProxyExecutable)this::addWorker;
	case "const":
	    return constObj;
	case "executeBkg":
	    return (ProxyExecutable)this::executeBkg;
	case "i18n":
	    i18nObj.refresh();
	    return i18nObj;
	    	case "isDigit":
	    return(ProxyExecutable)this::isDigit;
	    	    	case "isLetter":
	    return(ProxyExecutable)this::isLetter;
	    	    	case "isLetterOrDigit":
	    return(ProxyExecutable)this::isLetterOrDigit;
	    	    	    	case "isSpace":
	    return(ProxyExecutable)this::isSpace;
	    	    	    	    	case "launchApp":
	    return(ProxyExecutable)this::launchApp;
	case "log":
	    return logObj;
	    	case "message":
	    return (ProxyExecutable)this::message;
	case "now":
	    return new DateTimeObj();
	case "popups":
	    return popups;
	case "readTextFile":
	    return (ProxyExecutable)this::readTextFile;
	case "speak":
	    return (ProxyExecutable)this::speak;
	    	case "quit":
	    return (ProxyExecutable)this::quit;
	default:
	    return null;
	}
    }

    @Override public boolean hasMember(String name) { return KEYS_SET.contains(name); }
    @Override public Object getMemberKeys() { return KEYS_ARRAY; }
    @Override public void putMember(String name, Value value) { throw new RuntimeException("The Luwrain object doesn't support updating of its variables"); }

            private Object addCommand(Value[] args)
    {
	if (!notNullAndLen(args, 2))
	    return false;
	if (!args[0].isString() || !args[1].canExecute())
	    return false;
	final String name = args[0].asString();
	if (name.trim().isEmpty())
	    return false;
	commands.add(new CommandWrapper(this, name.trim(), args[1]));
	return true;
	    }

    private Object addHook(Value[] args)
    {
	if (!notNullAndLen(args, 2))
	    return false;
	if (!args[0].isString() || !args[1].canExecute())
	    return false;
	final String name = args[0].asString();
	if (name.trim().isEmpty())
	    return false;
	List<Value> h = this.hooks.get(name);
	if (h == null)
	{
	    h = new ArrayList<>();
	    this.hooks.put(name, h);
	}
	h.add(args[1]);
	return true;
    }

                private Object addShortcut(Value[] args)
    {
	if (!notNullAndLen(args, 2))
	    return false;
	if (!args[0].isString() || !args[1].canInstantiate())
	    return false;
	final String name = args[0].asString();
	if (name.trim().isEmpty())
	    return false;
	extObjs.add(new ShortcutWrapper(this, name.trim(), luwrain.getFileProperty(Luwrain.PROP_DIR_DATA), args[1]));
	return true;
	    }

        private Object addWorker(Value[] args)
    {
	if (!notNullAndLen(args, 4))
	    return false;
	if (!args[0].isString() ||
	    !args[1].isNumber() ||
	    !args[2].isNumber() ||
!args[3].canExecute())
	    return false;
	final String name = args[0].asString();
	final int firstLaunchDelay = args[1].asInt();
	final int launchPeriod = args[2].asInt();
	if (name.trim().isEmpty())
	    return false;
	if (firstLaunchDelay == 0 || launchPeriod == 0)
	    return false;
	extObjs.add(new WorkerWrapper(this, name.trim(), firstLaunchDelay, launchPeriod, args[3]));
	return true;
    }

        private Object executeBkg(Value[] values)
    {
	if (!notNullAndLen(values, 1))
	    return false;
	if (values[0].isNull() || !values[0].canExecute())
	    return false;
	final FutureTask<Object> task = new FutureTask<>(()->{
		synchronized(syncObj) {
		    values[0].execute(new Object[0]);
		}
		return null;
	    });
	luwrain.executeBkg(task);
	return true;
    }

    private Object isDigit(Value[] values)
    {
	if (!notNullAndLen(values, 1))
	    return false;
	if (!values[0].isString() || values[0].asString().length() != 1)
	    return false;
	return Character.isDigit(values[0].asString().charAt(0));
    }

            private Object isLetter(Value[] values)
    {
	if (!ScriptUtils.notNullAndLen(values, 1))
	    return false;
	if (!values[0].isString() || values[0].asString().length() != 1)
	    return false;
	return Character.isLetter(values[0].asString().charAt(0));
    }

            private Object isLetterOrDigit(Value[] values)
    {
	if (!ScriptUtils.notNullAndLen(values, 1))
	    return false;
	if (!values[0].isString() || values[0].asString().length() != 1)
	    return false;
	return Character.isLetterOrDigit(values[0].asString().charAt(0));
    }

                private Object isSpace(Value[] values)
    {
	if (!ScriptUtils.notNullAndLen(values, 1))
	    return false;
	if (!values[0].isString() || values[0].asString().length() != 1)
	    return false;
	return Character.isWhitespace(values[0].asString().charAt(0));
    }

            private Object launchApp(Value[] values)
    {
	if (!notNullAndLen(values, 1))
	    return false;
	if (!values[0].isString())
	    return false;
	luwrain.launchApp(values[0].asString());
	return true;
    }

        private Object message(Value[] values)
    {
	if (!notNullAndLen(values, 1))
	    return false;
	if (!values[0].isString())
	    return false;
	luwrain.message(values[0].asString());
	return true;
    }

    private Object readTextFile(Value[] args)
    {
	if (!notNullAndLen(args, 1))
	    return new ScriptException("readTextFile takes exactly one non-null argument");
	final String fileName = ScriptUtils.asString(args[0]);
	if (fileName == null || fileName.isEmpty())
	    throw new ScriptException("readTextFile() takes a non-empty string with the name of the file as the furst argument");
	try {
	    final String text = FileUtils.readTextFileSingleString(new File(fileName), "UTF-8");
	    return ProxyArray.fromArray((Object[])FileUtils.universalLineSplitting(text));
	}
	catch(IOException e)
	{
	    throw new ScriptException(e);
	}
    }

        private Object quit(Value[] values)
    {
	if (values != null && values.length > 0)
	    return false;
	luwrain.xQuit();
	return true;
	}


    private Object speak(Value[] values)
    {
	if (notNullAndLen(values, 2))
		{
		    if (!values[0].isString() || !values[1].isString())
	    return false;
		    final String text = values[0].asString();
		    final String sound = values[1].asString();
		    if (sound.isEmpty())
			return false;
		    final Sounds s = ConstObj.getSound(sound);
		    if (s == null)
			return false;
		    luwrain.playSound(s);
	luwrain.speak(text);
	return true;
		}
	if (!notNullAndLen(values, 1))
	    return false;
	if (!values[0].isString())
	    return false;
	luwrain.speak(values[0].asString());
	return true;
    }
}
