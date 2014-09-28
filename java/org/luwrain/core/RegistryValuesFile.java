/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.core;

import java.io.*;
import java.nio.file.*;
import java.util.regex.*;
import java.util.*;
import org.luwrain.core.registry.Registry;

public class RegistryValuesFile
{
    private Pattern dirPattern = Pattern.compile("^ *dir *([^ ]+) *$", Pattern.CASE_INSENSITIVE);
    private Pattern intPattern = Pattern.compile("^ *int *([^ ]+) +([0123456789]+) *$", Pattern.CASE_INSENSITIVE);
    private Pattern boolTruePattern = Pattern.compile("^ *bool *([^ ]+) +true *$", Pattern.CASE_INSENSITIVE);
    private Pattern boolFalsePattern = Pattern.compile("^ *bool *([^ ]+) +false *$", Pattern.CASE_INSENSITIVE);
    private Pattern strPattern = Pattern.compile("^ *string *([^ ]+) *(.*) *$", Pattern.CASE_INSENSITIVE);

    private Registry registry;

    public RegistryValuesFile(Registry registry)
    {
	this.registry = registry;
    }

    public boolean readValuesFromFile(String fileName)
    {
	if (fileName == null || fileName.isEmpty())
	    return false;
	try {
	    Path path = Paths.get(fileName);
	    try (Scanner scanner =  new Scanner(path, "utf-8")) 
	    {
		while (scanner.hasNextLine())
		    onValueLine(fileName, scanner.nextLine());
	    }
	}
	catch(IOException e)
	{
	    Log.error("registry-values-file", "problem reading values file:" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    private void onValueLine(String fileName, String line)
    {
	if (fileName == null || line == null || line.trim().isEmpty())
	    return;
	if (line.trim().charAt(0) == '#')
	    return;
	Matcher matcher = dirPattern.matcher(line);
	if (matcher.find())
	{
	    if (!registry.addDirectory(matcher.group(1)))
		Log.warning("registry-values-file", fileName + ":registry rejected creating new directory " + matcher.group(1));
	    return;
	}
	matcher = intPattern.matcher(line);
	if (matcher.find())
	{
	    int value;
	    try {
		value = Integer.parseInt(matcher.group(2));
	    }
	    catch (NumberFormatException e)
	    {
		Log.error("registry-values-file", fileName + ":error parsing integer value \'" + matcher.group(2) + "\'");
		return;
	    }
	    if (!registry.setInteger(matcher.group(1), value))
		Log.warning("registry-values-file", fileName + ":registry rejected new integer value " + value + " for parameter " + matcher.group(1));
	    return;
	}
	matcher = boolTruePattern.matcher(line);
	if (matcher.find())
	{
	    if (!registry.setBoolean(matcher.group(1), true))
		Log.warning("registry-values-file", fileName + ":registry rejected new value \'TRUE\' for parameter "  + matcher.group(1));
	    return;
	}
	matcher = boolFalsePattern.matcher(line);
	if (matcher.find())
	{
	    if (!registry.setBoolean(matcher.group(1), false))
		Log.warning("registry-values-file", fileName + ":registry rejected new value \'FALSE\' for parameter "  + matcher.group(1));
	    return;
	}

	matcher = strPattern.matcher(line);
	if (matcher.find())
	{
	    if (!registry.setString(matcher.group(1), matcher.group(2).trim()))
		Log.warning("registry-values-file", fileName + ":registry rejected new string value \'" + matcher.group(2).trim() + "\' for parameter " + matcher.group(1));
	    return;
	}
	Log.error("registry-values-file", fileName + ":invalid line:" + line);
    }
}
