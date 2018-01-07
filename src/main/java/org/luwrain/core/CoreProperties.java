/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
    static private final String LOG_COMPONENT = "init";
    
    private final Properties props = new Properties();

    void load(File systemProperties, File userProperties)
    {
	NullCheck.notNull(systemProperties, "systemProperties");
	NullCheck.notNull(userProperties, "userProperties");
	try {
	    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(systemProperties.toPath())) {
		    for (Path p : directoryStream) 
			readProps(p.toFile());
		}
	}
	catch(IOException e)
	{
	    Log.error(LOG_COMPONENT, "unable to enumerate properties file in " + systemProperties.toString() + ":" + e.getClass().getName()  + ":" + e.getMessage());
	}
	try {
	    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(userProperties.toPath())) {
		    for (Path p : directoryStream) 
			readProps(p.toFile());
		} 
	}
	catch(IOException e)
	{
	    Log.error(LOG_COMPONENT, "unable to enumerate properties file in " + systemProperties.toString() + ":" + e.getClass().getName()  + ":" + e.getMessage());
	}
    }

    @Override public String getProperty(String propName)
    {
	NullCheck.notNull(propName, "propName");
	switch(propName)
	{
	case "luwrain.sounds.iconsvol":
	    return "100";
	default:
	    {
		final String res = props.getProperty(propName);
		return res != null?res:"";
	    }
	}
    }

    @Override public File getFileProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	final String res = props.getProperty(propName);
	if (res != null)
	    return new File(res);
	return null;
    }

    private void readProps(File file)
    {
	NullCheck.notNull(file, "file");
	if (file.isDirectory() || !file.getName().endsWith(".properties"))
	    return;
	Log.debug(LOG_COMPONENT, "reading properties from " + file.getAbsolutePath());
	try {
	    final InputStream s = new FileInputStream(file);
	    try {
		props.load(new InputStreamReader(new BufferedInputStream(s), "UTF-8"));
	    }
	    finally {
		s.close();
	    }
	}
	catch(IOException e)
	{
	    Log.error(LOG_COMPONENT, "unable to read properties file " + file.getAbsolutePath() + ":" + e.getClass().getName() + ":" + e.getMessage());
	}
	}
}
