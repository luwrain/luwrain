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

package org.luwrain.core;

import java.util.*;
import java.io.*;

import org.luwrain.util.RegistryAutoCheck;

public class FileTypes
{
    private Map<String, String> fileTypes = new TreeMap<String, String>();

    public String[] chooseShortcuts(String[] fileNames)
    {
	if (fileNames == null)
	    throw new NullPointerException("fileNames may not be null");
	LinkedList<String> res = new LinkedList<String>();
	for(String s: fileNames)
	{
	    if (s == null)
	    {
		res.add("");
		continue;
	    }
	    final File f = new File(s);
	    if (!f.exists())
	    {
		res.add("notepad");
		continue;
	    }
	    if (f.isDirectory())
	    {
		res.add("commander");
		continue;
	    }
	    final String ext = getExtension(s);
	    if (ext == null || ext.trim().isEmpty() || !fileTypes.containsKey(ext.toLowerCase()))
	    {
		res.add("notepad");
		continue;
	    }
	    res.add(fileTypes.get(ext.toLowerCase()));
	}
	return res.toArray(new String[res.size()]);
    }

    public void load(Registry registry)
    {
	final RegistryAutoCheck check = new RegistryAutoCheck(registry, "environment");
	final String path = new RegistryKeys().fileTypes();
	final String[] values= registry.getValues(path);
	if (values == null || values.length < 1)
	    return;
	for(String v: values)
	{
	    if (v.trim().isEmpty())
		continue;
	    final String vv = check.stringAny(path + "/" + v, "");
	    if (vv.trim().isEmpty())
		continue;
	    fileTypes.put(v.trim().toLowerCase(), vv.trim());
	}
    }

    static public String getExtension(String fileName)
    {
	if (fileName == null)
	    throw new NullPointerException("fileName may not be null");
	if (fileName.isEmpty())
	    throw new IllegalArgumentException("fileName may not be empty");
	final String name = new File(fileName).getName();
	if (name.isEmpty())
	    return "";
	int dotPos = -1;
	for(int i = 0;i < name.length();++i)
	    if (name.charAt(i) == '.')
		dotPos = i;
	if (dotPos == 0 || dotPos + 1 >= name.length())
	    return "";
	return name.substring(dotPos + 1);
    }
}
