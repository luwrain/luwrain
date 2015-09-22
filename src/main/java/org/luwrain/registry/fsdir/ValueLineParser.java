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

class ValueLineParser
{
    private Pattern pat = Pattern.compile("^\\s*(\"[^\"]*\"(\"[^\"]*\")*)\\s*=\\s*\"(.*)\"\\s*$", Pattern.CASE_INSENSITIVE);

    String key = "";
    public String value = "";

    public boolean parse(String line)
    {
	if (line == null)
	    throw new NullPointerException("line may not be null");
	if (line.trim().isEmpty() || line.trim().charAt(0) == '#')
	{
	    key = "";
	    value = "";
	    return true;
	}
	Matcher matcher = pat.matcher(line);
	if (matcher.find())
	{
	    key = matcher.group(1);
	    value = matcher.group(3);
	    key = key.substring(1, key.length() - 1).replaceAll("\"\"", "\"");
	    value = value./*substring(1, key.length() - 1).*/replaceAll("\"\"", "\"");
	    return true;
	}
	return false;
    }

    public static void main(String[] args)
    {
	ValueLineParser parser = new ValueLineParser();
	String line = "";
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	while(true)
	{
	    System.out.print("What to parse?>");
	    try {
		line = br.readLine();
	    } 
	    catch (IOException e) 
	    {
		e.printStackTrace();
		System.exit(1);
	    }
	    if (parser.parse(line))
	    {
		System.out.println("OK!");
		System.out.println("Key: \'" + parser.key + "\'");
		System.out.println("Value: \'" + parser.value + "\'");
	    } else
		System.out.println("Error!");
	}
    }
}
