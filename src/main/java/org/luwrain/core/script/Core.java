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

package org.luwrain.core.script;

import java.util.*;
import java.io.*;

import javax.script.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.core.*;

public final class Core
    {
    static final String LOG_COMPONENT = "script";

	public final class ExecResult
	{
	    private final Extension ext;
	    private final Luwrain luwrain;
	    private final Exception exception;

	    ExecResult(Extension ext, Luwrain luwrain, Exception exception )
	    {
		this.ext = ext;
		this.luwrain = luwrain;
		this.exception = exception;
	    }

	    public Extension getExtension()
	    {
		return ext;
	    }

	    public Luwrain getLuwrain()
	    {
		return luwrain;
	    }

	    public Exception getException()
	    {
		return exception;
	    }

	    public boolean isOk()
	    {
		return ext != null && exception == null;
	    }
	}

    private final InterfaceManager interfaces;

    public Core(InterfaceManager interfaces)
    {
	NullCheck.notNull(interfaces, "interfaces");
	this.interfaces = interfaces;
    }

	public java.util.concurrent.Callable execFuture(Luwrain luwrain, File dataDir, Context context, String text)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(dataDir, "dataDir");
	    NullCheck.notNull(context, "context");
	    NullCheck.notNull(text, "text");
	    final Map<String, Object> objs = new HashMap<>();
	    if (context.output != null)
		objs.put("Output", new Wrappers.Output(context.output));
	    final Instance instance = new Instance(luwrain, dataDir, objs);
	    return ()->{
		instance.exec(text);
		return null;
	    };
	}

	public ScriptCallable createCallable(Luwrain luwrain, String text, Map<String, Object> objs, File dataDir)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(text, "text");
	    NullCheck.notNull(objs, "objs");
	    NullCheck.notNull(dataDir, "dataDir");
	    final Instance instance = new Instance(luwrain, dataDir, objs);
	    return new ScriptCallable(){
		@Override public Object call() throws Exception
		{
		instance.exec(text);
		return null;
	    }
	    };
	}


	public ExecResult exec(File dataDir, String text)
	{
	    NullCheck.notNull(dataDir, "dataDir");
	    NullCheck.notNull(text, "text");
	    final ScriptExtension ext = new ScriptExtension("fixme");
	    final Luwrain luwrain = interfaces.requestNew(ext);
	    Luwrain toRelease = luwrain;
	    try {
		ext.init(luwrain);
		final Instance instance = new Instance(luwrain, dataDir, new HashMap<>());
		ext.setInstance(instance);
		try {
		    instance.exec(text);
		}
		catch(Exception e)
		{
		    Log.error(LOG_COMPONENT, "unable to execute JavaScript:" + e.getClass().getName() + ":" + e.getMessage());
		    return new ExecResult(null, null, e);
		}
		toRelease = null;
		return new ExecResult(ext, luwrain, null);
	    }
	    finally {
		if (toRelease != null)
		    interfaces.release(toRelease);
	    }
	}
}
