/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.registry.fsdir;

import java.io.*;
import java.nio.file.*;
import java.util.regex.*;
import java.util.*;

import org.luwrain.core.NullCheck;

class ValueLineParser
{
    private Pattern pat = Pattern.compile("^\\s*(\"[^\"]*\"(\"[^\"]*\")*)\\s*=\\s*\"(.*)\"\\s*$", Pattern.CASE_INSENSITIVE);

    String key = "";
    String value = "";

    boolean parse(String line)
    {
	NullCheck.notNull(line, "line");
	if (line.trim().isEmpty() || line.trim().charAt(0) == '#')
	{
	    key = "";
	    value = "";
	    return true;
	}
	final Matcher matcher = pat.matcher(line);
	if (!matcher.find())
	    return false;
	key = matcher.group(1);
	value = matcher.group(3);
	key = key.substring(1, key.length() - 1).replaceAll("\"\"", "\"");
	//	value = value.substring(1, key.length() - 1).replaceAll("\"\"", "\"");
	value = value.replaceAll("\"\"", "\"");
	value = value.replaceAll("\\\\n", "\n");
	    return true;
    }
}
