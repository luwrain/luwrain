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
import java.io.*;
import java.net.*;

public class JniLoader
{
    static private final String
	LOG_COMPONENT = Base.LOG_COMPONENT,
	AUTOLOAD = "autoload.txt";

    private final String
	arch = System.getProperty("sun.arch.data.model"),
	os = System.getProperty("os.name");
    private final String path;

    public JniLoader()
    {
	this.path = "org/luwrain/jni/" + os + "/" + arch + "/";
    }

    void autoload(ClassLoader classLoader)
    {
	if (classLoader == null)
	{
	    Log.error(LOG_COMPONENT, "unable to load JNI: classLoader is null");
	    return;
	}
	final String[] libs = getAutoloadList(classLoader);
	if (libs == null || libs.length == 0)
	    return;
	for(String l: libs)
	{
	    Log.debug(LOG_COMPONENT, "loading library " + l);
	    load(classLoader, l);
	}
    }

    private String[] getAutoloadList(ClassLoader classLoader)
    {
	final String name = path + AUTOLOAD;
	final URL url = classLoader.getResource(name);
	if (url == null)
	{
	    Log.debug(LOG_COMPONENT, "no resource " + name + ", skipping JNI autoloading");
	    return null;
	}
	try {
	    try (final BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
		final List<String> res = new ArrayList<>();
		String line = r.readLine();
		while (line != null)
		{
		    res.add(line);
		    line = r.readLine();
		}
		return res.toArray(new String[res.size()]);
	    }
	}
	catch(IOException e)
	{
	    Log.error(LOG_COMPONENT, "JNI loading failed: unable to read the resource with name " + name + ": " + e.getClass().getName() + ": " + e.getMessage());
	    return null;
	}
    }

    public boolean load(ClassLoader classLoader, String name)
    {
	if (classLoader == null || name == null || name.isEmpty())
	    return false;
	final String resName = path + name;
	final URL url = classLoader.getResource(resName);
	if (url == null)
	{
	    Log.error(LOG_COMPONENT, "unable to load JNI library: no such resource: " + resName);
	    return false;
	}
	try {
	    final File tmpFile = File.createTempFile(".lwr.jni.", "." + name + ".tmp");
	    try (final InputStream is = url.openStream()) {
		java.nio.file.Files.copy(is, tmpFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
	    }
	    tmpFile.deleteOnExit();
	    try {
		System.load(tmpFile.getAbsolutePath());
	    }
	    catch(Throwable e)
	    {
		Log.error(LOG_COMPONENT, "unable to load JNI library " + name + ": " + e.getClass().getName() + ": " + e.getMessage());
		return false;
	    }
	    return true;
	}
	catch(IOException e)
	{
	    Log.error(LOG_COMPONENT, "unable to load JNI library:" + e.getClass().getName() + ":" + e.getMessage());
	    return false;
	}
    }

    public boolean loadByShortName(ClassLoader classLoader, String name)
    {
	try {
	    Log.debug(LOG_COMPONENT, "trying native library loading from the native lain file: " + name);
	    System.loadLibrary(name);
	    return true;
	}
	catch(Throwable e)
	{
	    Log.debug(LOG_COMPONENT, "not a problem, the library can't be loaded from the native file, will try from a resource: " + e.getClass().getName() + ": " + e.getMessage());
	}
	if (os.toLowerCase().startsWith("linux"))
	    return load(classLoader, "lib" + name + ".so");
	    if (os.toLowerCase().startsWith("windows"))
		return load(classLoader, name + ".dll");
	    Log.warning(LOG_COMPONENT, "unknown OS name: " + os + ", loading JNI by the original name '" + name + "'" );
	    return load(classLoader, name);
    }
}
