/*
   Copyright 2012-2023 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.io.json;

import java.util.*;
import java.lang.reflect.*;

import com.google.gson.*;
import com.google.gson.reflect.*;
import lombok.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

@Data
@NoArgsConstructor
public class HotKey
{
    static public final Type LIST_TYPE = new TypeToken<List<HotKey>>(){}.getType();

    static private Gson gson = null;

    private InputEvent.Special special = null;
    private Character ch = null;

    private Boolean
	withAlt = null,
	withControl = null,
	withShift = null;

    public HotKey(InputEvent.Special special, Character ch, Boolean withAlt, Boolean withControl, Boolean withShift)
    {
	this.special = special;
	this.ch = ch;
	this.withAlt = withAlt;
	this.withControl = withControl;
	this.withShift = withShift;
    }

    public boolean getWithAltNotNull() { return withAlt != null?withAlt.booleanValue():false; }
        public boolean getWithControlNotNull() { return withControl != null?withControl.booleanValue():false; }
            public boolean getWithShiftNotNull() { return withShift != null?withShift.booleanValue():false; }

    public EnumSet<InputEvent.Modifiers> getModifiers()
    {
	final EnumSet<InputEvent.Modifiers> res = EnumSet.noneOf(InputEvent.Modifiers.class);
	if (getWithAltNotNull())
	    res.add(InputEvent.Modifiers.ALT);
	if (getWithControlNotNull())
	    res.add(InputEvent.Modifiers.CONTROL);
	if (getWithShiftNotNull())
	    res.add(InputEvent.Modifiers.SHIFT);
	return res;
    }

    static public String toJsonArray(HotKey[] hotKeys)
    {
	if (gson == null)
	    gson = new Gson();
	return gson.toJson(hotKeys);
    }

    static public HotKey[]  fromJsonArray(String s)
    {
	NullCheck.notNull(s, "s");
	if (gson == null)
	    gson = new Gson();
	final List<HotKey> res = new Gson().fromJson(s, LIST_TYPE);
	return res != null?res.toArray(new HotKey[res.size()]):new HotKey[0];
    }
}
