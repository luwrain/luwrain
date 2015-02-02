/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
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
    public static Map<String, String> readValuesFromFile(String fileName) throws IOException
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
			values.put(parser.key, parser.value); else
			Log.warning("registry", "skipping invalid line:" + fileName + ":" + lineNum + ":" + line);
		}
	    }
	return values;
    }
}
