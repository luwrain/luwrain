/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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
import jdk.nashorn.api.scripting.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

final class WorkerImpl implements Worker
{
    private final String name;
    private final int firstLaunchDelay;
    private final int launchPeriod;
    private final JSObject func;

    WorkerImpl(String name, int firstLaunchDelay, int launchPeriod, JSObject func)
    {
	NullCheck.notEmpty(name, "name");
	NullCheck.notNull(func, "func");
	this.name = name;
	this.firstLaunchDelay = firstLaunchDelay;
	this.launchPeriod = launchPeriod;
	this.func = func;
    }

    @Override public void run()
    {
	func.call(null, new Object[0]);
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
