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
import org.luwrain.script.*;

final class I18nObj extends EmptyHookObject
{
    private final Luwrain luwrain;
    private final LangObj activeLangObj;
    private final LangsObj langsObj;

    I18nObj(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	this.activeLangObj = new LangObj(luwrain.i18n().getActiveLang());
	this.langsObj = new LangsObj(luwrain.i18n());
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	switch(name)
	{
	case "lang":
	    return activeLangObj;
	case "langs":
	    return langsObj;
	case "static":
	    return new AbstractJSObject(){
		    @Override public Object getMember(String name)
		{
		    NullCheck.notNull(name, "name");
		    if (name.isEmpty())
			return super.getMember(name);
		    if (name.length() >= 2)
			return luwrain.i18n().getStaticStr(Character.toUpperCase(name.charAt(0)) + name.substring(1));
		    return luwrain.i18n().getStaticStr(name);
		}
	    };
	case "isLetter":
	    return (Predicate)this::isLetter;
	case "isDigit":
	    return (Predicate)this::isDigit;
	case "isLetterOrDigit":
	    return (Predicate)this::isLetterOrDigit;
	case "isSpace":
	    return (Predicate)this::isSpace;
	default:
	    return super.getMember(name);
	}
    }

    private boolean isLetter(Object obj)
    {
	final String value = org.luwrain.script.ScriptUtils.getStringValue(obj);
	if (value == null || value.length() != 1)
	    return false;
	return Character.isLetter(value.charAt(0));
    }

    private boolean isDigit(Object obj)
    {
	final String value = org.luwrain.script.ScriptUtils.getStringValue(obj);
	if (value == null || value.length() != 1)
	    return false;
	return Character.isDigit(value.charAt(0));
    }

    private boolean isLetterOrDigit(Object obj)
    {
	final String value = org.luwrain.script.ScriptUtils.getStringValue(obj);
	if (value == null || value.length() != 1)
	    return false;
	return Character.isLetterOrDigit(value.charAt(0));
    }

    private boolean isSpace(Object obj)
    {
	final String value = org.luwrain.script.ScriptUtils.getStringValue(obj);
	if (value == null || value.length() != 1)
	    return false;
	return Character.isSpaceChar(value.charAt(0));
    }
}
