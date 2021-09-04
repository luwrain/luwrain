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

import static org.luwrain.script2.ScriptUtils.*;

final class LangObj implements ProxyObject
{
    static private String[] KEYS = new String[]{
	"getSpecialNameOfChar",
    };
    static private final Set<String> KEYS_SET = new HashSet<>(Arrays.asList(KEYS));
    static private final ProxyArray KEYS_ARRAY = ProxyArray.fromArray((Object[])KEYS);

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
	    return (ProxyExecutable)this::getSpecialNameOfChar;
	default:
	    return null;
	}
    }

    @Override public boolean hasMember(String name) { return KEYS_SET.contains(name); }
    @Override public Object getMemberKeys() { return KEYS_ARRAY; }
    @Override public void putMember(String name, Value value) { throw new RuntimeException("The lang object doesn't support updating of its variables"); }

    /*
    private String exp(String name, Object argsObj)
    {
	if (argsObj == null || !(argsObj instanceof JSObject))
	    return null;
	final JSObject args = (JSObject)argsObj;
	return lang.getTextExp(name, (argName)->args.getMember(argName.toString()));
    }
    */

    private String getSpecialNameOfChar(Value[] values)
    {
	if (!notNullAndLen(values, 1))
	    return null;
	if (!values[0].isString() || values[0].asString().length() != 1)
	    return null;
	return lang.hasSpecialNameOfChar(values[0].asString().charAt(0));
    }
}
