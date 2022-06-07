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

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;
import org.luwrain.core.Job.Instance;
import org.luwrain.script2.*;

public final class JobInstanceObj
{
    public final Instance instance;

    public JobInstanceObj(Instance instance )
    {
	NullCheck.notNull(instance, "instance");
	this.instance = instance;
    }

    @HostAccess.Export
    public final ProxyExecutable stop = this::stopImpl;
    private Object stopImpl(Value[] args)
    {
	instance.stop();
	return true;
    }
}
