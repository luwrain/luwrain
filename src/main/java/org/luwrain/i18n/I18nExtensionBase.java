/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

//LWR_API 2.0

package org.luwrain.i18n;

import java.net.*;
import java.io.*;
import java.util.*;

import org.luwrain.core.*;

public class I18nExtensionBase extends EmptyExtension
{
    static public final String COMMAND_PREFIX = "command.";
    static public final String STATIC_PREFIX = "static.";
    static public final String STRINGS_PREFIX = "strings.";
    static public final String CHARS_PREFIX = "chars.";

    protected ClassLoader classLoader = null;
    protected Luwrain luwrain = null;
    protected final String langName;
    protected final Map<String, String> staticStrings = new HashMap();
    protected final Map<String, String> chars = new HashMap<>();

    public I18nExtensionBase(String langName)
    {
	NullCheck.notEmpty(langName, "langName");
	this.classLoader = classLoader;
	this.langName = langName;
    }

    protected void init(ClassLoader classLoader, Luwrain luwrain)
    {
	NullCheck.notNull(classLoader, "classLoader");
	NullCheck.notNull(luwrain, "luwrain");
	this.classLoader = classLoader;
	this.luwrain = luwrain;
    }

    protected void loadProperties(String resourcePath, I18nExtension ext) throws IOException
    {
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
	    processPropItem(k, v, ext, resourcePath);
	}
    }

    protected void processPropItem(String k, String v, I18nExtension ext, String resourcePath)
    {
	NullCheck.notEmpty(k, "k");
	NullCheck.notNull(v, "v");
	NullCheck.notNull(ext, "ext");
	NullCheck.notNull(resourcePath, "resourcePath");
	//commands
	if (k.trim().startsWith(COMMAND_PREFIX))
	{
	    final String c = k.trim().substring(COMMAND_PREFIX.length());
	    if (c.trim().isEmpty())
	    {
		Log.warning(langName, "the illegal key \'" + k + "\' in resource file " + resourcePath);
		return;
	    }
	    ext.addCommandTitle(langName, c.trim(), v.trim());
	    return;
	}

	//statics
	if (k.trim().startsWith(STATIC_PREFIX))
	{
	    final String c = k.trim().substring(STATIC_PREFIX.length());
	    if (c.trim().isEmpty())
	    {
		Log.warning(langName, "the illegal key \'" + k + "\' in resource file " + resourcePath);
		return;
	    }
	    staticStrings.put(c.trim(), v.trim());
	    return;
	}

	//chars
	if (k.trim().startsWith(CHARS_PREFIX))
	{
	    final String c = k.trim().substring(CHARS_PREFIX.length());
	    if (c.trim().isEmpty())
	    {
		Log.warning(langName, "the illegal key \'" + k + "\' in resource file " + resourcePath);
		return;
	    }
	    chars.put(c.trim(), v.trim());
	    return;
	}

	//strings
	if (k.trim().startsWith(STRINGS_PREFIX))
	{
	    final String c = k.trim().substring(STRINGS_PREFIX.length());
	    if (c.trim().isEmpty())
	    {
		Log.warning(langName, "the illegal key \'" + k + "\' in resource file " + resourcePath);
		return;
	    }
	    if (!addProxyByClassName(c.trim(), v.trim(), resourcePath, ext))
		Log.warning(langName, "unable to create proxy strings object \'" + c + "\' for interface " + v.trim());
	    return;
	}
    }

    protected boolean addProxyByClass(String name, Class stringsClass, String propertiesResourceName, I18nExtension ext)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notEmpty(name, "name");
	NullCheck.notNull(stringsClass, "stringsClass");
	NullCheck.notEmpty(propertiesResourceName, "propertiesResourceName");
	NullCheck.notNull(ext, "ext");
	final Object strings;
	try {
	    strings = PropertiesProxy.create(luwrain, langName, classLoader.getResource(propertiesResourceName), name + ".", stringsClass);
	}
	catch(java.io.IOException e)
	{
	    e.printStackTrace();
	    return false;
	}
	ext.addStrings(langName, "luwrain." + name, strings);
	return true;
    }

    protected boolean addProxyByClassName(String name, String className, String propertiesResourceName, I18nExtension ext)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notEmpty(name, "name");
	NullCheck.notEmpty(className, "className");
	NullCheck.notEmpty(propertiesResourceName, "propertiesResourceName");
	NullCheck.notNull(ext, "ext");
	final Class cl;
	try {
	    cl = Class.forName(className, true, classLoader);
	}
	catch (Throwable e)
	{
	    Log.error(langName, "unable to find the class " + className + ":" + e.getClass().getName() + ":" + e.getMessage());
	    return false;
	}
	return addProxyByClass(name, cl, propertiesResourceName, ext);
    }
}
