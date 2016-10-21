
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

    @Override public Path getPathProperty(String propName)
    {
	NullCheck.notNull(propName, "propName");
	final String res = props.getProperty(propName);
	if (res != null)
	    return Paths.get(res);
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
		props.load(s);
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
