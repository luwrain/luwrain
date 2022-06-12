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
import jdk.nashorn.api.scripting.*;

import org.luwrain.core.*;

public final class Utils
{
    //Returns null if the provided object isn't an array
    static public List<String> getStringArray(JSObject obj)
    {
	NullCheck.notNull(obj, "obj");
	final List<String> res = new ArrayList<>();
	if (!obj.isArray())
	    return null;
	int index = 0;
	while (obj.hasSlot(index))
	{
	    final Object o = obj.getSlot(index);
	    if (o == null)
		break;
	    res.add(o.toString());
	    ++index;
	}
	return res;
    }
}
