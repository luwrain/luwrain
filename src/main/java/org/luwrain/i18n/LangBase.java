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

//LWR_API 2.0

package org.luwrain.i18n;

import java.util.*;
import java.net.*;
import java.io.*;

import org.luwrain.core.*;

abstract public class LangBase implements Lang
{
    protected final String langName;
protected final Map<String, String> staticStrings;
protected final Map<String, String> chars;

    public LangBase(String langName, Map<String, String> staticStrings, Map<String, String> chars)
    {
	NullCheck.notEmpty(langName, "langName");
	NullCheck.notNull(staticStrings, "staticStrings");
	NullCheck.notNull(chars, "chars");
	this.langName = langName;
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
	if (Character.isLetterOrDigit(ch))
	    return null;
	final String name = Character.getName(ch);
	if (name == null || name.isEmpty())
	    return null;
	final String newName = name.toLowerCase().replaceAll(" ", "_").replaceAll("-", "_");
	return chars.containsKey(newName)?chars.get(newName):newName;
    }

        @Override public String getNumberStr(Number num, GrammaticalAttr gramAttr, Word depWord)
    {
	return null;
    }

    @Override public Word[] getWord(String word)
    {
	return new Word[0];
    }

    @Override public InputStream getResource(String resourceName)
    {
	NullCheck.notEmpty(resourceName, "resourceName");
	final URL url = getClass().getClassLoader().getResource("org/luwrain/i18n/" + langName + "/" + resourceName);
	if (url == null)
	    return null;
	try {
	    return url.openStream();
	}
	catch(IOException e)
	{
	    Log.error(langName, "unable to open stream for the lang resource '" + resourceName + "\':" + e.getClass().getName() + ":" + e.getMessage());
return null;
	}
    }
}
