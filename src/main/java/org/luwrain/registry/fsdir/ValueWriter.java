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
import java.nio.charset.*;
import java.util.*;

import org.luwrain.core.Log;

class ValueWriter
{
    static void saveValuesToFile(TreeMap<String, String> values, String fileName) throws IOException
    {
	final Path path = Paths.get(fileName);
	try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8))
	    {
		for(Map.Entry<String,String> e: values.entrySet())
		{
		    writer.write("\"");
		    writer.write(escapeString(e.getKey()));
		    writer.write("\" = \"");
		    writer.write(escapeString(e.getValue()));
		    writer.write("\"");
		    writer.newLine();
		}
	    }
    }

    static private String escapeString(String str)
    {
	final StringBuilder res = new StringBuilder();
	for(int i = 0;i < str.length();++i)
	    switch(str.charAt(i))
	    {
	    case '\n':
		res.append("\\n");
		break;
	    case '\"':
		res.append("\"\"");
		break;
	    default:
		res.append(str.charAt(i));
	    }
	return res.toString();
    }
}

