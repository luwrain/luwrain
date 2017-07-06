/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.io.*;
import java.util.*;
import java.nio.file.*;

class CoreProperties implements org.luwrain.base.CoreProperties
{
    private final Properties props = new Properties();

    void load(Path systemProperties, Path userProperties)
    {
	NullCheck.notNull(systemProperties, "systemProperties");
	NullCheck.notNull(userProperties, "userProperties");
	try {
	    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(systemProperties)) {
		    for (Path p : directoryStream) 
			readProps(p);
		}
	}
	catch(IOException e)
	{
	    Log.error("init", "unable to enumerate properties file in " + systemProperties.toString() + ":" + e.getClass().getName()  + ":" + e.getMessage());
	}
	try {
	    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(userProperties)) {
		    for (Path p : directoryStream) 
			readProps(p);
		} 
	}
	catch(IOException e)
	{
	    Log.error("init", "unable to enumerate properties file in " + systemProperties.toString() + ":" + e.getClass().getName()  + ":" + e.getMessage());
	}
    }

    @Override public String getProperty(String propName)
    {
	NullCheck.notNull(propName, "propName");
	final String res = props.getProperty(propName);
	return res != null?res:"";
    }

    @Override public File getFileProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	final String res = props.getProperty(propName);
	if (res != null)
	    return new File(res);
	return null;
    }

    private void readProps(Path path)
    {
	NullCheck.notNull(path, "path");
	if (Files.isDirectory(path) || !path.getFileName().toString().endsWith(".properties"))
	    return;
	Log.debug("init", "reading properties from " + path.toString());
	try {
	    final InputStream s = Files.newInputStream(path);
	    try {
		props.load(new InputStreamReader(new BufferedInputStream(s), "UTF-8"));
	    }
	    finally {
		s.close();
	    }
	}
	catch(IOException e)
	{
	    Log.error("init", "unable to read properties file " + path.toString() + ":" + e.getClass().getName() + ":" + e.getMessage());
	}
	}
}
