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

package org.luwrain.registry.mem;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;

final class Loader
{
    static private final String LOG_COMPONENT = RegistryImpl.LOG_COMPONENT;

    static private final String DIR_PREFIX = "DIR ";
    static private final String FILE_PREFIX = "FILE ";

    private final Registry registry;
    private final ValueLineParser lineParser = new ValueLineParser();

    private String currentDir = null;
    private String currentFile = null;
    private final List<String> lines = new ArrayList<>();

    Loader(Registry registry)
    {
	NullCheck.notNull(registry, "registry");
	this.registry = registry;
    }

    void load(InputStream is) throws IOException
    {
	NullCheck.notNull(is, "is");
	final BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	String line = reader.readLine();
	while(line != null)
	{
	    line = line.trim();
	    if (line.isEmpty() || line.charAt(0) == '#')
	    {
		line = reader.readLine();
		continue;
	    }
	    if (line.startsWith(DIR_PREFIX))
		onDir(line.substring(DIR_PREFIX.length()).trim()); else
	    if (line.startsWith(FILE_PREFIX))
		onFile(line.substring(FILE_PREFIX.length()).trim()); else
		onValue(line);
	    line = reader.readLine();
	}
    }

    private void onDir(String path)
    {
	NullCheck.notNull(path, "path");
	if (path.isEmpty())
	    return;
	currentDir = path;
	registry.addDirectory(currentDir);
    }

    private void onFile(String fileName)
    {
	NullCheck.notNull(fileName, "fileName");
	if (fileName.isEmpty())
	    return;
	if (currentDir == null)
	    return;
	currentFile = fileName;
    }

    private void onValue(String value)
    {
	NullCheck.notNull(value, "value");
	if (value.isEmpty())
	    return;
		if (currentDir == null || currentFile == null)
	    return;

		lineParser.key = "";
		lineParser.value = "";

		if (!lineParser.parse(value))
		{
		    Log.error(LOG_COMPONENT, "unable to parse the value line \'" + value + "\' for the directory " + currentDir);
		    return;
		}
		if (lineParser.key == null || lineParser.key.isEmpty())
		{
		    Log.error(LOG_COMPONENT, "empty key in the value line \'" + value + "\' for the directory " + currentDir);
		    return;
		}
		if (lineParser.value == null)
		{
		    Log.error(LOG_COMPONENT, "the line parser returned a null value for the line \'" + value + "\' for the directory " + currentDir);
		    return;
		}
		switch(currentFile)
		{
		case "strings.txt":
		    registry.setString(Registry.join(currentDir, lineParser.key), lineParser.value);
		    return;
		case "integers.txt":
		    {
			final int intValue;
			try {
			    intValue = Integer.parseInt(lineParser.value);
			}
			catch(NumberFormatException e)
			{
			    Log.error(LOG_COMPONENT, "illegal integer value \'" + lineParser.value + "\' in the line \'" + value + "\' for the directory " + currentDir);
			    return;
			}
					    registry.setInteger(Registry.join(currentDir, lineParser.key), intValue);
					    return;
		    }
		case "booleans.txt":
		    {
		    final boolean boolValue;

		    switch (lineParser.value.trim().toLowerCase())
		    {
		    case "true":
		    case "yes":
		    case "1":
			boolValue = true;
		    break;
		    case "false":
		    case "no":
		    case "0":
			boolValue = false;
		    default:
			Log.error(LOG_COMPONENT, "unknown boolean value \'" + lineParser.value + "\' in the line \'" + value + "\' for the directory " + currentDir);
			return;
		    }
		    					    registry.setBoolean(Registry.join(currentDir, lineParser.key), boolValue);
							    return;
		    }
		default:
		    Log.warning(LOG_COMPONENT, "unknown file specification \'" + currentFile + "\' for the directory " + currentDir);
				}
    }
}
