/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

import org.luwrain.core.*;

import static org.luwrain.core.NullCheck.*;

final class AppImpl implements Application
{
    final Module module;
    final  Value construct;
    private Value instance = null;

    AppImpl(Module module, Value construct)
    {
	notNull(module, "module");
	notNull(construct, "construct");
	this.module = module;
	this.construct = construct;
    }

    @Override public void onAppClose()
    {
    }

    @Override public String getAppName()
    {
	return "";
    }

    @Override public AreaLayout getAreaLayout()
    {
	return null;
    }

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	this.instance = module.execNewInstance(construct, new Object[0]);
	return null;
    }
}
