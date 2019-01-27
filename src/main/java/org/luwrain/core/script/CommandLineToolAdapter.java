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

// https://docs.oracle.com/javase/8/docs/jdk/api/nashorn/jdk/nashorn/api/scripting/ScriptObjectMirror.html

package org.luwrain.core.script;

import java.io.*;
import java.util.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

class CommandLineToolAdapter implements CommandLineTool
{
    private final String name;
    private final JSObject cons;

    CommandLineToolAdapter(String name, JSObject cons)
    {
	NullCheck.notEmpty(name, "name");
	NullCheck.notNull(cons, "cons");
	this.name = name;
	this.cons = cons;
    }

    @Override public Instance launch(Listener listener, String[] args)
    {
	NullCheck.notNull(listener, "listener");
	NullCheck.notNullItems(args, "args");
	final Object newObj = cons.newObject();
	if (newObj == null || !(newObj instanceof JSObject))
	    return null;
	final ScriptObjectMirror newJsObj = (ScriptObjectMirror)newObj;
	if (newJsObj.get("name") == null || !(newJsObj.get("name") instanceof JSObject))
	    return null;
	if (newJsObj.get("cmdLine") == null || !(newJsObj.get("cmdLine") instanceof JSObject))
	    return null;
	final String name = newJsObj.get("name").toString();
	if (name == null || name.trim().isEmpty())
	    return null;
	final List<String> cmdLine = getStringArray((JSObject)newJsObj.get("cmdLine"));
	if (cmdLine == null || cmdLine.isEmpty())
	    return null;
	final ProcessBuilder builder = new ProcessBuilder(cmdLine);
	try {
	    return new Instance(listener, name, newJsObj, builder.start());
	}
	catch(IOException e)
	{
	    //FIXME:
	    return null;
	}
    }

    @Override public Set<Flags> getToolFlags()
    {
	//FIXME:
	return null;
    }

    @Override public String getExtObjName()
    {
	return name;
    }

    static private List<String> getStringArray(JSObject obj)
    {
	NullCheck.notNull(obj, "obj");
	final List<String> res = new LinkedList();
	if (!obj.isArray())
	    return null;
	int index = 0;
	while (obj.hasSlot(index))
	{
	    final Object o = obj.getSlot(index);
	    if (o == null || !(o instanceof JSObject))
		break;
	    res.add(o.toString());
	    ++index;
	}
	return res;
    }

    static private class Instance implements CommandLineTool.Instance
    {
	private final Listener listener;
	private final String name;
	private final ScriptObjectMirror jsObj;
	private final Process proc;
	private final Thread thread;
	private boolean finished = false;
	private int exitCode = 0;
	private String state = "";
	private String[] multilineState = new String[0];
	private String[] nativeState = new String[0];

	Instance(Listener listener, String name, ScriptObjectMirror jsObj, Process proc)
	{
	    NullCheck.notNull(listener, "listener");
	    NullCheck.notEmpty(name, "name");
	    NullCheck.notNull(jsObj, "jsObj");
	    NullCheck.notNull(proc, "proc");
	    this.listener = listener;
	    this.name = name;
	    this.jsObj = jsObj;
	    this.proc = proc;
	    this.thread = new Thread(()->readOutput());
	    this.thread.start();
	}

	@Override synchronized public void stop()
	{
	    proc.destroy();
	    finished = true;
	    exitCode = -1;
	}

	@Override public String getInstanceName()
	{
	    return name;
	}

	@Override public Status getStatus()
	{
	    return finished?CommandLineTool.Status.FINISHED:CommandLineTool.Status.RUNNING;
	}

	@Override public int getExitCode()
	{
	    return exitCode;
	}

	@Override public boolean isFinishedSuccessfully()
	{
	    return finished && exitCode == 0;
	}

	@Override public String getSingleLineState()
	{
	    return state;
	}

	@Override public String[] getMultilineState()
	{
	    return multilineState;
	}

	@Override public String[] getNativeState()
	{
	    return nativeState;
	}

	private void readOutput()
	{
	    try {
		final InputStream is = proc.getInputStream();
		final BufferedReader r = new BufferedReader(new InputStreamReader(is));
		try {
		    String line = null;
		    synchronized(this) {
			line = r.readLine();
		    }
		    while (line != null)
		    {
			onLine(line);
			synchronized(this) {
			    line = r.readLine();
			}
		    }
		    synchronized(this) {
			proc.waitFor();
			this.finished = true;
			this.exitCode = proc.exitValue();
			//FIXME:notification
		    }
		}
		finally {
		    r.close();
		    is.close();
		}
	    }
	    catch(InterruptedException e)
	    {
		Thread.currentThread().interrupt();
	    }
	    catch(IOException e)
	    {
		//FIXME:
	    }
	}

	private void readUpdatedState()
	{
	    if (jsObj.get("state") == null && (jsObj.get("state") instanceof JSObject))
	    {
		final String value = jsObj.get("state").toString();
		if (value != null && !state.equals(value))
		{
		    this.state = value;
		    listener.onSingleLineStateChange(this);
		}
	    }
	    if (jsObj.get("multilineState") != null && (jsObj.get("multilineState") instanceof JSObject))
	    {
		final List<String> value = getStringArray((JSObject)jsObj.get("multilineState"));
		if (value != null)
		{
		    final String[] v = value.toArray(new String[value.size()]);
		    if (!theSameMultilineState(v))
		    {
			this.multilineState = v;
					    listener.onMultilineStateChange(this);
		    }
		}
	    }
	}

	private boolean theSameMultilineState(String[] value)
	{
	    NullCheck.notNullItems(value, "value");
	    if (value.length != multilineState.length)
		return  false;
	    for(int i = 0;i < value.length;++i)
		if (!value[i].equals(multilineState[i]))
		    return false;
	    return true;
	}

	private void onLine(String line)
	{
	}
    }
}
