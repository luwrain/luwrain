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

package org.luwrain.core.script;

import javax.script.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.core.*;
import org.luwrain.core.extensions.*;

public final class Core
    {
    static private final String LOG_COMPONENT = "script";

	public final class ExecResult
	{
	    private final DynamicExtension ext;
	    private final Luwrain luwrain;
	    private final Exception exception;

	    ExecResult(DynamicExtension ext, Luwrain luwrain, Exception exception )
	    {
		this.ext = ext;
		this.luwrain = luwrain;
		this.exception = exception;
	    }

	    public DynamicExtension getExtension()
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

	public ExecResult exec(String text)
	{
	    NullCheck.notNull(text, "text");
	    final ScriptExtension ext = new ScriptExtension("fixme");
	    final Luwrain luwrain = interfaces.requestNew(ext);
	    Luwrain toRelease = luwrain;
	    try {
		ext.init(luwrain);
		final Instance instance = new Instance(luwrain);
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
