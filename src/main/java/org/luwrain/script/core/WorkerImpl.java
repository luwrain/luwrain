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

package org.luwrain.script.core;

import java.io.*;
import java.util.*;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;

final class WorkerImpl implements Worker
{
    private final LuwrainObj luwrainObj;
    private final String name;
    private final int firstLaunchDelay, launchPeriod;
    private final Value func;

    WorkerImpl(LuwrainObj luwrainObj, String name, int firstLaunchDelay, int launchPeriod, Value func)
    {
	NullCheck.notNull(luwrainObj, "luwrainObj");
	NullCheck.notEmpty(name, "name");
	NullCheck.notNull(func, "func");
	this.luwrainObj = luwrainObj;
	this.name = name;
	this.firstLaunchDelay = firstLaunchDelay;
	this.launchPeriod = launchPeriod;
	this.func = func;
    }

    @Override public void run()
    {
	synchronized(luwrainObj.syncObj) {
	    func.execute(null, new Object[0]);
	}
    }

    @Override public int getFirstLaunchDelay()
    {
	return firstLaunchDelay;
    }

    @Override public int getLaunchPeriod()
    {
	return launchPeriod;
    }

    @Override public String getExtObjName()
    {
	return name;
    }
}
