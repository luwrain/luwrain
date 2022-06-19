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
import org.luwrain.core.events.*;
import org.luwrain.script.*;
import org.luwrain.util.*;

final class ConstObj implements ProxyObject
{
    static private final String
	SOUND_PREFIX = "SOUND_",
	KEY_PREFIX = "KEY_",
	MESSAGE_TYPE_PREFIX = "MESSAGE_TYPE_";

    static private final EnumSet<Sounds> ALL_SOUNDS = EnumSet.allOf(Sounds.class);
        static private final EnumSet<InputEvent.Special> ALL_KEYS = EnumSet.allOf(InputEvent.Special.class);
            static private final EnumSet<Luwrain.MessageType> ALL_MESSAGE_TYPES = EnumSet.allOf(Luwrain.MessageType.class);

    private final String[] keys;
    private final Set<String> keysSet;
private final ProxyArray keysArray;

    ConstObj()
    {
	final List<String> k = new ArrayList<>();
	for(Sounds s: ALL_SOUNDS)
	    k.add(SOUND_PREFIX + s.toString());
		for(InputEvent.Special s: ALL_KEYS)
		    k.add(KEY_PREFIX + s.toString().replaceAll("ARROW_", "MOVE_"));
				for(Luwrain.MessageType s: ALL_MESSAGE_TYPES)
		    k.add(MESSAGE_TYPE_PREFIX + s.toString());
	    this.keys = k.toArray(new String[k.size()]);
	this.keysSet = new HashSet<>(k);
	this.keysArray = ProxyArray.fromArray((Object[])keys);
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	if (keysSet.contains(name))
	{
	    if (name.startsWith("KEY_"))
		return name.substring(KEY_PREFIX.length()).replaceAll("MOVE_", "ARROW_");
	    return name;
	    }
	return null;
    }

        @Override public boolean hasMember(String name) { return keysSet.contains(name); }
    @Override public Object getMemberKeys() { return keysArray; }
    @Override public void putMember(String name, Value value) { throw new RuntimeException("The const object doesn't support updating of its variables"); }

    static Sounds getSound(String s)
    {
	NullCheck.notNull(s, "s");
	if (!s.startsWith(SOUND_PREFIX))
	    return null;
	final String ss = s.substring(SOUND_PREFIX.length());
	for(Sounds k: ALL_SOUNDS)
	    if (k.toString().equals(ss))
		return k;
	return null;
    }

        static Luwrain.MessageType getMessageType(String s)
    {
	NullCheck.notNull(s, "s");
	if (!s.startsWith(MESSAGE_TYPE_PREFIX))
	    return null;
	final String ss = s.substring(MESSAGE_TYPE_PREFIX.length());
	for(Luwrain.MessageType k: ALL_MESSAGE_TYPES)
	    if (k.toString().equals(ss))
		return k;
	return null;
    }
}
