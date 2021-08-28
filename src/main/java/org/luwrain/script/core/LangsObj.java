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

import java.util.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;
import org.luwrain.i18n.*;
import org.luwrain.script2.*;

import static org.luwrain.script2.ScriptUtils.*;

final class LangsObj implements ProxyObject
{
        private final Map<String, LangObj> langs = new HashMap();
    private final Set<String> keysSet;
    private final ProxyArray keysArray;

    LangsObj(I18n i18n)
    {
	NullCheck.notNull(i18n, "i18n");
	final List<String> keys = new ArrayList();
	for(Map.Entry<String, Lang> e: i18n.getAllLangs().entrySet())
	{
	    keys.add(e.getKey());
	    this.langs.put(e.getKey(), new LangObj(e.getValue()));
	}
	this.keysSet = new HashSet(keys);
	this.keysArray = ProxyArray.fromArray((Object[])keys.toArray(new String[keys.size()]));
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	    return langs.get(name);
}

        @Override public boolean hasMember(String name) { return keysSet.contains(name); }
    @Override public Object getMemberKeys() { return keysArray; }
    @Override public void putMember(String name, Value value) { throw new RuntimeException("The langs object doesn't support updating of its variables"); }
}
