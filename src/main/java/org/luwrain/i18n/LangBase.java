/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.i18n;

import java.util.*;

import org.luwrain.core.*;

abstract public class LangBase implements org.luwrain.core.Lang
{
protected final Map<String, String> staticStrings;
protected final Map<String, String> chars;

    public LangBase(Map<String, String> staticStrings,
		    Map<String, String> chars)
    {
	NullCheck.notNull(staticStrings, "staticStrings");
	NullCheck.notNull(chars, "chars");
	this.staticStrings = staticStrings;
	this.chars = chars;
    }

    @Override public String getStaticStr(String id)
    {
	NullCheck.notEmpty(id, "id");
	return staticStrings.containsKey(id)?staticStrings.get(id):"";
    }

    @Override public String hasSpecialNameOfChar(char ch)
    {
	if (Character.isDigit(ch) || Character.isLetter(ch))
	    return null;
	final String name = Character.getName(ch);
	if (name == null || name.isEmpty())
	    return null;
	return chars.containsKey(name)?chars.get(name):name;
    }
}
