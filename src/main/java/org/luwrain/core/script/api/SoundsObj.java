/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

import java.util.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.base.*;
import org.luwrain.core.*;
import org.luwrain.player.*;

final class SoundsObj extends AbstractJSObject
{
    private final Luwrain luwrain;
    private final EnumSet<Sounds> allSounds = EnumSet.allOf(Sounds.class);

    SoundsObj(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	if (name.isEmpty())
	    return super.getMember(name);
	for(Sounds s: allSounds)
	    if (convertName(s.toString()).equals(name))
		return new AbstractJSObject(){
		    @Override public Object call(Object th, Object[] args)
		    {
			if (args != null && args.length > 0)
			return false;
			luwrain.playSound(s);
			return true;
		    }
		    @Override public boolean isFunction()
		    {
			return true;
		    }
		};
	return false;
	    }

    static private String convertName(String s)
    {
	NullCheck.notNull(s, "s");
	final StringBuilder b = new StringBuilder();
	boolean cap = false;
	for(int i = 0;i < s.length();i++)
	    switch(s.charAt(i))
	    {
	    case '_':
		cap = true;
		continue;
	    default:
		if (cap)
		    		    b.append(s.charAt(i)); else
		    b.append(Character.toLowerCase(s.charAt(i)));
		cap = false;
	    }
	return new String(b);
    }
}
