/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

import jdk.nashorn.api.scripting.*;
import java.util.function.*;

import org.luwrain.core.*;
import org.luwrain.i18n.*;

final class LangObj extends AbstractJSObject
{
    private final Lang lang;

    LangObj(Lang lang)
    {
	NullCheck.notNull(lang, "lang");
	this.lang = lang;
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	switch(name)
	{
	    case "getSpecialNameOfChar":
	    return (Function)this::getSpecialNameOfChar;
	default:
	    return super.getMember(name);
	}
    }

private String getSpecialNameOfChar(Object obj)
    {
	final String value = org.luwrain.script.ScriptUtils.getStringValue(obj);
	if (value == null || value.length() != 1)
	    return null;
	return lang.hasSpecialNameOfChar(value.charAt(0));
	    }
}
