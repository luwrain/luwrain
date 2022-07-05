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
import java.util.concurrent.*;
import java.net.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;
import org.luwrain.script.*;
import org.luwrain.util.*;

import static org.luwrain.script.ScriptUtils.*;

public final class OperatingSystemObj
{
    final Luwrain luwrain;
    final Object syncObj;

    OperatingSystemObj(Luwrain luwrain, Object syncObj)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(syncObj, "syncObj");
	this.luwrain = luwrain;
	this.syncObj = syncObj;
	    }
}
