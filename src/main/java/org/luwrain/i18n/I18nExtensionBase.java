/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.i18n;

import java.net.*;
import java.io.*;
import java.util.*;

import org.luwrain.core.*;

public class I18nExtensionBase extends org.luwrain.core.extensions.EmptyExtension
{
    static public final String COMMAND_PREFIX = "command.";
    static public final String STATIC_PREFIX = "static.";
    static public final String STRINGS_PREFIX = "strings.";
    static public final String CHARS_PREFIX = "chars.";

    protected final Map<String, String> staticStrings = new HashMap<String, String>();
    protected final Map<String, String> chars = new HashMap<String, String>();

    protected void loadProperties(String langName, ClassLoader classLoader, String resourcePath, I18nExtension ext) throws IOException
    {
	NullCheck.notEmpty(langName, "langName");
	NullCheck.notNull(classLoader, "classLoader");
		NullCheck.notEmpty(resourcePath, "resourcePath");
	NullCheck.notNull(ext, "ext");
	final Properties props = new Properties();
	final URL url = classLoader.getResource(resourcePath);
	if (url == null)
	{
	    Log.error(langName, "No resource " + resourcePath);
	    return;
	}
	props.load(new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8")));
	final Enumeration e = props.propertyNames();
	while(e.hasMoreElements())
	{
	    final String k = (String)e.nextElement();
	    final String v = props.getProperty(k);
	    if (v == null)
	    {
		Log.warning(langName, "key \'" + k + "\' in resource file " + resourcePath+ " doesn\'t have value");
		continue;
	    }

	    //commands
	    if (k.trim().startsWith(COMMAND_PREFIX))
	    {
		final String c = k.trim().substring(COMMAND_PREFIX.length());
	    if (c.trim().isEmpty())
	    {
		Log.warning(langName, "illegal key \'" + k + "\' in resource file " + resourcePath);
		continue;
	    }
	    ext.addCommandTitle(langName, c.trim(), v.trim());
	continue;
	    }

	    //statics
	    if (k.trim().startsWith(STATIC_PREFIX))
	    {
		final String c = k.trim().substring(STATIC_PREFIX.length());
	    if (c.trim().isEmpty())
	    {
		Log.warning(langName, "illegal key \'" + k + "\' in resource file " + resourcePath);
		continue;
	    }
	    staticStrings.put(c.trim(), v.trim());
	continue;
	    }

	    //chars
	    if (k.trim().startsWith(CHARS_PREFIX))
	    {
		final String c = k.trim().substring(CHARS_PREFIX.length());
	    if (c.trim().isEmpty())
	    {
		Log.warning(langName, "illegal key \'" + k + "\' in resource file " + resourcePath);
		continue;
	    }
chars.put(c.trim(), v.trim());
	continue;
	    }

	    //strings
	    if (k.trim().startsWith(STRINGS_PREFIX))
	    {
		final String c = k.trim().substring(STRINGS_PREFIX.length());
	    if (c.trim().isEmpty())
	    {
		Log.warning(langName, "illegal key \'" + k + "\' in resource file " + resourcePath);
		continue;
	    }
	    if (!addProxyByClassName(langName, classLoader, c.trim(), v.trim(), resourcePath, ext))
	Log.warning(langName, "unable to create proxy strings object \'" + c + "\' for interface " + v.trim());
	continue;
	    }
	}
    }

    protected boolean addProxyByClass(String langName, ClassLoader classLoader, String name, Class stringsClass, 
				      String propertiesResourceName, I18nExtension ext)
    {
	NullCheck.notNull(langName, "langName");
	NullCheck.notNull(classLoader, "classLoader");
	NullCheck.notEmpty(name, "name");
	NullCheck.notNull(stringsClass, "stringsClass");
	NullCheck.notEmpty(propertiesResourceName, "propertiesResourceName");
	NullCheck.notNull(ext, "ext");
	final Object strings;
	try {
	    strings = PropertiesProxy.create(classLoader.getResource(propertiesResourceName), name + ".", stringsClass);
	}
	catch(java.io.IOException e)
	{
	    e.printStackTrace();
	    return false;
	}
	ext.addStrings(langName, "luwrain." + name, strings);
	return true;
	}

    protected boolean addProxyByClassName(String langName, ClassLoader classLoader, String name, String className, 
					  String propertiesResourceName, I18nExtension ext)
    {
	NullCheck.notNull(langName, "langName");
	NullCheck.notNull(classLoader, "classLoader");
	NullCheck.notEmpty(name, "name");
	NullCheck.notEmpty(className, "className");
	NullCheck.notEmpty(propertiesResourceName, "propertiesResourceName");
	NullCheck.notNull(ext, "ext");
	final Class cl;
	try {
	    cl = Class.forName(className, true, classLoader);
	}
	catch (ClassNotFoundException e)
	{
	    return false;
	}
	return addProxyByClass(langName, classLoader, name, cl, propertiesResourceName, ext);
    }
}
