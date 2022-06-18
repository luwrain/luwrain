/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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
import java.util.regex.*;
import java.net.*;
import java.io.*;
import java.nio.file.*;

import com.google.gson.*;
import com.google.gson.annotations.*;

final class FileTypes
{
    static private final String
	LOG_COMPONENT = Base.LOG_COMPONENT,
	JOB_PREFIX = "job:";

    private final Gson gson = new Gson();
    private final Map<String, String> fileTypes = new HashMap<>();

    void load(Registry registry)
    {
	NullCheck.notNull(registry, "registry");
	registry.addDirectory(Settings.FILE_TYPES_PATH);
	final String[] values= registry.getValues(Settings.FILE_TYPES_PATH);
	for(String v: values)
	{
	    final String valuePath = Registry.join(Settings.FILE_TYPES_PATH, v);
	    if (registry.getTypeOf(valuePath) != Registry.STRING)
	    {
		Log.warning(LOG_COMPONENT, "the registry value " + valuePath + " is not a string");
		continue;
	    }
	    final String value = registry.getString(valuePath).trim();
	    if (value.isEmpty())
		continue;
	    fileTypes.put(v.trim().toLowerCase(), value);
	}
    }

    void launch(Core core, Registry registry, String[] files)
    {
	NullCheck.notNull(core, "core");
	NullCheck.notNull(registry, "registry");
	NullCheck.notNullItems(files, "files");
	final String[] shortcuts = chooseShortcuts(files);
	final Map<String, List<String> > lists = new HashMap<>();
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
	    final List<String> l = new ArrayList<>();
	    l.add(f);
	    lists.put(s, l);
	}
	for(Map.Entry<String, List<String> > e: lists.entrySet())
	{
	    if (runJob(core, e.getKey(), e.getValue()))
		continue;
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
			Log.warning(LOG_COMPONENT, "unable to generate URL for path " + toOpen[i] + " which is requested to open");
		    }
		}
	    if (!takesMultiple)
	    {
		for(String f: toOpen)
		    core.launchApp(shortcut, new String[]{f});
	    } else
		core.launchApp(shortcut, toOpen);
	}
    }

    private boolean runJob(Core core, String exp, List<String> args)
    {
	NullCheck.notNull(core, "core");
	NullCheck.notEmpty(exp, "exp");
	NullCheck.notNull(args, "args");
	if (!exp.startsWith(JOB_PREFIX))
	    return false;
	final JobValue jobValue = gson.fromJson(exp.substring(JOB_PREFIX.length()), JobValue.class);
						if (jobValue == null)
						{
						    Log.warning(LOG_COMPONENT, "unable to parse a job value for file types: " + exp.substring(JOB_PREFIX.length()));
						    return false;
						}
						if (jobValue.name == null || jobValue.name.trim().isEmpty())
						{
						    Log.warning(LOG_COMPONENT, "no job value in file types job expression: " + exp.substring(JOB_PREFIX.length()));
						    return false;
						}
						if (jobValue.escaping == null)
						    jobValue.escaping = "cmd";
						if (jobValue.args == null)
						{
						    core.luwrain.newJob(jobValue.name.trim(), new String[0], "", EnumSet.noneOf(Luwrain.JobFlags.class), null);
						    return true;
						}
						final StringBuilder b = new StringBuilder();
						for(String s: args)
						    b.append(" ").append(jobValue.escaping.isEmpty()?s:core.os.escapeString(jobValue.escaping, s));
						final String bashArgs = new String(b).trim();
						for(int i = 0;i < jobValue.args.length;i++)
						    jobValue.args[i] = jobValue.args[i].replaceAll("lwr.args.bash", Matcher.quoteReplacement(bashArgs));
						core.luwrain.newJob(jobValue.name, jobValue.args, "", EnumSet.noneOf(Luwrain.JobFlags.class), null);
						return true;
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
	    final String ext = getExt(s).trim().toLowerCase();
	    if (ext.trim().isEmpty() || !fileTypes.containsKey(ext))
	    {
		res.add("notepad");
		continue;
	    }
	    res.add(fileTypes.get(ext));
	}
	return res.toArray(new String[res.size()]);
    }

    private String getExt(String fileName)
    {
	NullCheck.notEmpty(fileName, "fileName");
	final String name = new File(fileName).getName();
	if (name.isEmpty())
	    return "";
	int dotPos = name.lastIndexOf(".");
	if (dotPos < 1 || dotPos + 1 >= name.length())
	    return "";
	return name.substring(dotPos + 1);
    }

    private String getExtension(URL url)
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

    static private final class JobValue
    {
	String
	    name = null,
	    escaping = null;
	String[] args = null;
    }
}
