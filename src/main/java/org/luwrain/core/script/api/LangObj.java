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

package org.luwrain.core.script.api;

import jdk.nashorn.api.scripting.*;
import java.util.function.*;

import org.luwrain.core.*;
import org.luwrain.i18n.*;
import org.luwrain.script.*;

final class LangObj extends EmptyHookObject
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
	case "exp":
	    return new EmptyHookObject(){
		@Override public Object getMember(String name)
		{
		    NullCheck.notEmpty(name, "name");
		    final String expName = Utils.buildNameWithDashes(name);
		    return (Function)(args)->exp(expName, args);
		}
	    };
	    case "getSpecialNameOfChar":
	    return (Function)this::getSpecialNameOfChar;
	default:
	    return super.getMember(name);
	}
    }

    private String exp(String name, Object argsObj)
    {
	if (argsObj == null || !(argsObj instanceof JSObject))
	    return null;
	final JSObject args = (JSObject)argsObj;
	return lang.getTextExp(name, (argName)->args.getMember(argName.toString()));
    }

private String getSpecialNameOfChar(Object obj)
    {
	final String value = org.luwrain.script.ScriptUtils.getStringValue(obj);
	if (value == null || value.length() != 1)
	    return null;
	return lang.hasSpecialNameOfChar(value.charAt(0));
	    }
}
