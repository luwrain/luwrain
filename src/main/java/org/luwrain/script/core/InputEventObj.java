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

//LWR_API 1.0

package org.luwrain.script.core;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class InputEventObj extends MapScriptObject
{
    protected final InputEvent event;

    public InputEventObj(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	this.event = event;
	members.put("special", event.isSpecial()?event.getSpecial().toString():null);
	members.put("ch", event.isSpecial()?null:new String(new StringBuilder().append(event.getChar())));
	members.put("withAlt", new Boolean(event.withAlt()));
	members.put("withAltOnly", new Boolean(event.withAltOnly()));
	members.put("withControl", new Boolean(event.withControl()));
	members.put("withControlOnly", new Boolean(event.withControlOnly()));
	members.put("withShift", new Boolean(event.withShift()));
	members.put("withShiftOnly", new Boolean(event.withShiftOnly()));
	members.put("modified", new Boolean(event.isModified()));
    }
}
