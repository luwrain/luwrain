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
import java.net.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;
import org.luwrain.script2.*;
import org.luwrain.util.*;

import static org.luwrain.script2.ScriptUtils.*;

public final class LuwrainObj
{
    @HostAccess.Export public final LogObj log;
    @HostAccess.Export public final ConstObj constants = new ConstObj();
    @HostAccess.Export public final PopupsObj popups;

    final Luwrain luwrain;
    final Object syncObj = new Object();
    final Map<String, List<Value> > hooks = new HashMap<>();
    final List<ExtensionObject> extObjs = new ArrayList<>();
    final I18nObj i18nObj;
    final List<Command> commands = new ArrayList<>();

    LuwrainObj(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	this.log = new LogObj(luwrain);
	this.i18nObj = new I18nObj(luwrain);
	this.popups = new PopupsObj(luwrain);
    }

    @HostAccess.Export public final ProxyExecutable addCommand = this::addCommandImpl;
            private Object addCommandImpl(Value[] args)
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

    @HostAccess.Export public final ProxyExecutable addHook = this::addHookImpl;
    private Object addHookImpl(Value[] args)
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

    @HostAccess.Export public final ProxyExecutable addShortcut = this::addShortcutImpl;
                private Object addShortcutImpl(Value[] args)
    {
	if (!notNullAndLen(args, 2))
	    return false;
	if (!args[0].isString() || !args[1].canInstantiate())
	    return false;
	final String name = args[0].asString();
	if (name.trim().isEmpty())
	    return false;
	extObjs.add(new ShortcutImpl(this, name.trim(), luwrain.getFileProperty(Luwrain.PROP_DIR_DATA), args[1]));
	return true;
	    }

    @HostAccess.Export public final ProxyExecutable AddWorker = this::addWorkerImpl;
        private Object addWorkerImpl(Value[] args)
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
	extObjs.add(new WorkerImpl(this, name.trim(), firstLaunchDelay, launchPeriod, args[3]));
	return true;
    }

    @HostAccess.Export public final ProxyExecutable executeBkg = this::executeBkgImpl;
    private Object executeBkgImpl(Value[] values)
    {
	if (!notNullAndLen(values, 1))
	    return false;
	if (values[0].isNull() || !values[0].canExecute())
	    return false;
	final FutureTask<Object> task = new FutureTask<>(()->{
		synchronized(syncObj) {
		    try {
			values[0].execute(new Object[0]);
		    }
		    catch(Throwable e)
		    {
			luwrain.crash(e);
		    }
		}
		return null;
	    });
	luwrain.executeBkg(task);
	return true;
    }

    @HostAccess.Export public final ProxyExecutable i18n = this::i18nImpl;
    private Object i18nImpl(Value[] args)
    {
	i18nObj.refresh();
	return i18nObj;
    }

    @HostAccess.Export public final ProxyExecutable isDigit = this::isDigitImpl;
    private Object isDigitImpl(Value[] values)
    {
	if (!notNullAndLen(values, 1))
	    return false;
	if (!values[0].isString() || values[0].asString().length() != 1)
	    return false;
	return Character.isDigit(values[0].asString().charAt(0));
    }

    @HostAccess.Export public final ProxyExecutable isLetter = this::isLetterImpl;
            private Object isLetterImpl(Value[] values)
    {
	if (!ScriptUtils.notNullAndLen(values, 1))
	    return false;
	if (!values[0].isString() || values[0].asString().length() != 1)
	    return false;
	return Character.isLetter(values[0].asString().charAt(0));
    }

    @HostAccess.Export public final ProxyExecutable isLetterOrDigit = this::isLetterOrDigitImpl;
            private Object isLetterOrDigitImpl(Value[] values)
    {
	if (!ScriptUtils.notNullAndLen(values, 1))
	    return false;
	if (!values[0].isString() || values[0].asString().length() != 1)
	    return false;
	return Character.isLetterOrDigit(values[0].asString().charAt(0));
    }

    @HostAccess.Export public final ProxyExecutable isSpace = this::isSpaceImpl;
                private Object isSpaceImpl(Value[] values)
    {
	if (!ScriptUtils.notNullAndLen(values, 1))
	    return false;
	if (!values[0].isString() || values[0].asString().length() != 1)
	    return false;
	return Character.isWhitespace(values[0].asString().charAt(0));
    }

    @HostAccess.Export public final ProxyExecutable launchApp = this::launchAppImpl;
            private Object launchAppImpl(Value[] values)
    {
	if (notNullAndLen(values, 2))
	{
	if (!values[0].isString() || !values[1].hasArrayElements())
	    return false;
	final String[] args = asStringArray(values[1]);
	if (args == null)
	    return false;
	luwrain.launchApp(values[0].asString(), args);
	return true;
    }
	if (!notNullAndLen(values, 1))
	    return false;
	if (!values[0].isString())
	    return false;
	luwrain.launchApp(values[0].asString());
	return true;
    }

    //FIXME: Speak numbers (or anything other than String)
    @HostAccess.Export public final ProxyExecutable message = this::messageImpl;
        private Object messageImpl(Value[] values)
    {
	if (!notNullAndLen(values, 1))
	    return false;
	if (!values[0].isString())
	    return false;
	luwrain.message(values[0].asString());
	return true;
    }

    @HostAccess.Export public final ProxyExecutable newJob = this::newJobImpl;
    private JobInstanceObj newJobImpl(Value[] values)
    {
	if (values.length < 2 || values.length > 4)
	    return null;
	if (values[0] == null || values[0].isNull() || !values[0].isString())
	    return null;
	final String name = values[0].asString();
	final String[] args = asStringArray(values[1]);
	final String dir;
	if (values.length < 3 || values[2] == null || values[2].isNull() || !values[2].isString())
	    dir = ""; else
	    dir = values[2].asString();
	final Value finishedFunc;
	if (values.length < 4 || values[3] == null || values[3].isNull() || !values[3].canExecute())
	    finishedFunc = null; else
	    finishedFunc = values[3];
	final Job.Instance res = luwrain.newJob(name, args != null?args:new String[0], dir, EnumSet.noneOf(Luwrain.JobFlags.class), new Job.Listener(){
		@Override public void onStatusChange(Job.Instance instance)
		{
		    NullCheck.notNull(instance, "instance");
		    if (finishedFunc == null || instance.getStatus() != Job.Status.FINISHED)
			return;
		    synchronized(syncObj) {
			finishedFunc.execute(new Object[]{new Boolean(instance.isFinishedSuccessfully()), new Integer(instance.getExitCode())});
		    }
		}
		@Override public void onSingleLineStateChange(Job.Instance instance) {}
		@Override public void onMultilineStateChange(Job.Instance instance) {}
		@Override public void onNativeStateChange(Job.Instance instance) {}
	    });
	return res != null?new JobInstanceObj(res):null;
    }

    @HostAccess.Export public final ProxyExecutable openUrl = this::openUrlImpl;
    private Object openUrlImpl(Value[] args)
    {
	if (!notNullAndLen(args, 1))
	    return false;
	if (!args[0].isString())
	    return false;
	luwrain.runUiSafely(()->{
		luwrain.openUrl(args[0].asString());
	    });
	return true;
    }

    @HostAccess.Export public final ProxyExecutable readTextFile = this::readTextFileImpl;
    private Object readTextFileImpl(Value[] args)
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

    @HostAccess.Export public final ProxyExecutable quit = this::quitImpl;
        private Object quitImpl(Value[] values)
    {
	if (values != null && values.length > 0)
	    return false;
	luwrain.xQuit();
	return true;
	}

    @HostAccess.Export public final ProxyExecutable urlGet = this::urlGetImpl;
    private Object urlGetImpl(Value[] args)
    {
	if (!notNullAndLen(args, 1))
	    return null;
	if (!args[0].isString())
	    return null;
	try {
	    try (final BufferedReader r = new BufferedReader(new InputStreamReader(new URL(args[0].asString()).openStream()))) {
		final StringBuilder b = new StringBuilder();
		String line = r.readLine();
		while (line != null)
		{
		    b.append(line).append(System.lineSeparator());
		    line = r.readLine();
		}
		return new String(b);
	    }
	}
	catch(Throwable e)
	{
	    throw new ScriptException(e);
	}
    }

    @HostAccess.Export public final ProxyExecutable speak = this::speakImpl;
    private Object speakImpl(Value[] values)
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
