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
import java.util.*;

import org.luwrain.core.Log;

class ValueReader
{
    static public Map<String, String> readValuesFromFile(String fileName) throws IOException
    {
	if (fileName == null)
	    throw new NullPointerException("fileName may not be null");
	if (fileName.isEmpty())
	    throw new IllegalArgumentException("fileName may not be empty");
	Map<String, String> values = new TreeMap<String, String>();
	ValueLineParser parser = new ValueLineParser();
	int lineNum = 0;
	Path path = Paths.get(fileName);
	try (Scanner scanner =  new Scanner(path, "utf-8")) 
	    {
		while (scanner.hasNextLine())
		{
		    ++lineNum;
		    final String line = scanner.nextLine();
		    if (parser.parse(line))
		    {
			if (parser.key.isEmpty() && parser.value.isEmpty())
			    continue;
			if (parser.key.isEmpty() && !parser.value.isEmpty())
			{
			Log.warning("fsdir", "skipping the line with an empty key and a non-empty value:" + fileName + ":" + lineNum + ":" + line);
			continue;
			}
			values.put(parser.key, parser.value);
			} else
			Log.warning("fsdir", "skipping a invalid line:" + fileName + ":" + lineNum + ":" + line);
		}
	    }
	return values;
    }
}
