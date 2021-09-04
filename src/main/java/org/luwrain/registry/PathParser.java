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

package org.luwrain.registry;

import java.util.*;

import org.luwrain.core.*;

public final class PathParser
{
    static public Path parse(String str)
    {
	NullCheck.notEmpty(str, "str");
	final List<String> items = new ArrayList<>();
	String current = "";
	for(int i = 0;i < str.length();++i)
	{
	    final char c = str.charAt(i);
	    if (c == '/')
	    {
		if (!current.isEmpty())
		{
		    items.add(current);
		    current = "";
		}
		continue;
	    }
	    current += c;
	}
	if (items.isEmpty() && current.isEmpty())
	    return null;
	return new Path(str.charAt(0) == '/', items.toArray(new String[items.size()]), current);
    }

    static public Path parseAsDirectory(String str)
    {
	NullCheck.notEmpty(str, "str");
	final List<String> items = new ArrayList<>();
	String current = "";
	for(int i = 0;i < str.length();++i)
	{
	    final char c = str.charAt(i);
	    if (c == '/')
	    {
		if (!current.isEmpty())
		{
		    items.add(current);
		    current = "";
		}
		continue;
	    }
	    current += c;
	}
	if (!current.isEmpty())
	    items.add(current);
	if (items.isEmpty() && str.charAt(0) != '/')
	    return null;
	return new Path(str.charAt(0) == '/', items.toArray(new String[items.size()]), "");
    }
}
