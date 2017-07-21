/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core;

import java.util.*;
import java.net.*;
import java.io.*;
import java.nio.file.*;

public class FileTypes
{
    private final HashMap<String, String> fileTypes = new HashMap<String, String>();

    void load(Registry registry)
    {
	NullCheck.notNull(registry, "registry");
	final String path = Settings.FILE_TYPES_PATH;
	final String[] values= registry.getValues(path);
	if (values.length < 1)
	    return;
	for(String v: values)
	{
	    if (v.trim().isEmpty())
		continue;
	    final String valuePath = Registry.join(path, v);
	    if (registry.getTypeOf(valuePath) != Registry.STRING)
	    {
		Log.warning("core", "registry value " + valuePath + " is not a string");
		continue;
	    }
	    final String value = registry.getString(valuePath).trim();
	    if (value.isEmpty())
		continue;
	    fileTypes.put(v.trim().toLowerCase(), value);
	}
    }

    void launch(Environment env, Registry registry, String[] files)
    {
	NullCheck.notNull(env, "env");
	NullCheck.notNull(registry, "registry");
	NullCheck.notNullItems(files, "files");
	final String[] shortcuts = chooseShortcuts(files);
	final HashMap<String, LinkedList<String> > lists = new HashMap<String, LinkedList<String> >();
	for(int i = 0;i < files.length;++i)
	{
	    final String s = shortcuts[i];
	    final String f = files[i];
	    if (s.isEmpty())
		continue;
	    if (lists.containsKey(s))
	    {
		lists.get(s).add(f);
		continue;
	    }
	    final LinkedList<String> l = new LinkedList<String>();
	    l.add(f);
	    lists.put(s, l);
	}
	for(Map.Entry<String, LinkedList<String> > e: lists.entrySet())
	{
	    final String shortcut = e.getKey();
	    final Settings.FileTypeAppInfo info = Settings.createFileTypeAppInfo(registry, Registry.join(Settings.FILE_TYPES_APP_INFO_PATH, shortcut));
	    final boolean takesMultiple = info.getTakesMultiple(false);
	    final boolean takesUrls = info.getTakesUrls(false);
	    final String[] toOpen = e.getValue().toArray(new String[e.getValue().size()]);
	    if (takesUrls)
		for(int i = 0;i < toOpen.length;++i)
		{
		    final Path p = Paths.get(toOpen[i]);
		    try {
			toOpen[i] = p.toUri().toURL().toString();
		    }
		    catch(java.net.MalformedURLException exc)
		    {
			Log.warning("core", "unable to generate URL for path " + toOpen[i] + " which is requested to open");
		    }
		}
	    if (!takesMultiple)
	    {
		for(String f: toOpen)
		    env.launchAppIface(shortcut, new String[]{f});
	    } else
		env.launchAppIface(shortcut, toOpen);
	}
    }

    private String[] chooseShortcuts(String[] fileNames)
    {
	NullCheck.notEmptyItems(fileNames, "fileNames");
	final LinkedList<String> res = new LinkedList<String>();
	for(String s: fileNames)
	{
	    if (s.isEmpty())
	    {
		res.add("");
		continue;
	    }
	    final Path path = Paths.get(s);
	    if (!Files.exists(path))
	    {
		res.add("notepad");
		continue;
	    }
	    if (Files.isDirectory(path))
	    {
		res.add("commander");
		continue;
	    }
	    final String ext = getExtension(s).trim().toLowerCase();
	    if (ext.trim().isEmpty() || !fileTypes.containsKey(ext))
	    {
		res.add("notepad");
		continue;
	    }
	    res.add(fileTypes.get(ext));
	}
	return res.toArray(new String[res.size()]);
    }

    static public String getExtension(String fileName)
    {
	NullCheck.notEmpty(fileName, "fileName");
	final Path path = Paths.get(fileName);
	final String name = path.getFileName().toString();
	if (name.isEmpty())
	    return "";
	int dotPos = name.lastIndexOf(".");
	if (dotPos < 1 || dotPos + 1 >= name.length())
	    return "";
	return name.substring(dotPos + 1);
    }

    static public String getExtension(URL url)
    {
	NullCheck.notEmpty(url, "url");
	final String name = url.getFile();
	if (name.isEmpty())
	    return "";
	final int dotPos = name.lastIndexOf(".");
	if (dotPos < 1 || dotPos + 1 >= name.length())
	    return "";
	return name.substring(dotPos + 1);
    }

}
