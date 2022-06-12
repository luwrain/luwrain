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

import java.util.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;
import org.luwrain.popups.*;

import static org.luwrain.script2.ScriptUtils.*;

public class HotPointObj implements ProxyObject
{
    static private final String[] KEYS = new String[]{ "x", "y" };
        static private final Set<String> KEYS_SET = new HashSet<>(Arrays.asList(KEYS));
    static private final ProxyArray KEYS_ARRAY = ProxyArray.fromArray((Object[])KEYS);

    private final HotPoint hotPoint;

    public HotPointObj(HotPoint hotPoint)
    {
	NullCheck.notNull(hotPoint, "hotPoint");
	this.hotPoint = hotPoint;
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notEmpty(name, "name");
	switch(name)
	{
	case "x":
	    return new Integer(hotPoint.getHotPointX());
	case "y":
	    return new Integer(hotPoint.getHotPointY());
	default:
	    return null;
	}
    }

        @Override public boolean hasMember(String name) { return KEYS_SET.contains(name); }
    @Override public Object getMemberKeys() { return KEYS_ARRAY; }
    @Override public void putMember(String name, Value value) { throw new UnsupportedOperationException("The hot point object doesn't support updating of its variables"); }
}
