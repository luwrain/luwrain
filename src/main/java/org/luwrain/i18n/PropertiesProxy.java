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

import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import java.net.*;

import org.luwrain.core.*;
import org.luwrain.script.*;
import org.luwrain.script.hooks.*;

public final class PropertiesProxy
{
    static private final String LOG_COMPONENT = "i18n";
    static private final String CHARSET = "UTF-8";

    static public <T> T create(Luwrain luwrain, String langName, URL url, String prefix, Class cl) throws java.io.IOException
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notEmpty(langName, "langName");
	NullCheck.notNull(url, "url");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(cl, "cl");
	final Properties props = new Properties();
	final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), CHARSET));
	try {
	    props.load(reader);
	}
	finally {
	    reader.close();
	}
	return create(luwrain, langName, props, prefix, cl);
    }

    @SuppressWarnings("unchecked")
    static public <T> T create(Luwrain luwrain, String langName, Properties props, String prefix, Class cl)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notEmpty(langName, "langName");
	NullCheck.notNull(props, "props");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(cl, "cl");
	return (T) java.lang.reflect.Proxy.newProxyInstance(cl.getClassLoader(), new Class[]{cl}, (object, method, args)->{
		String name = method.getName();
		if (name.length() > 1 && Character.isLowerCase(name.charAt(0)))
		    name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
		final String value = props.getProperty(prefix + name);
		if (value == null)
		{
final Object[] a;
		    if (args != null)
		    {
						a = new Object[args.length];

			for(int i = 0;i < args.length;i++)
			{
			    if (args[i] == null)
				a[i] = null; else
				if (args[i] instanceof String || args[i] instanceof Number || args[i] instanceof Boolean)
				    a[i] = args[i]; else
				    a[i] = args[i].toString();
			}
		    } else
			a = new Object[0];
		    final String hookRes = runHook(luwrain, langName, cl.getName(), a);
		    if (hookRes != null)
			return hookRes;
		    return "#No value: " + prefix + name + "#";
		}
		if (value.indexOf("$") < 0)
		    return value.trim();
		final StringBuilder b = new StringBuilder();
		for(int i = 0;i < value.length();++i)
		    if (value.charAt(i) != '$' || i + 1 >= value.length() ||
			value.charAt(i + 1) < '0' || value.charAt(i + 1) > '9')
			b.append(value.charAt(i)); else
			b.append(args[value.charAt(++i) - '1'].toString());
		return new String(b).toString();
	    });
    }

    static public String runHook(Luwrain luwrain, String langName, String name, Object[] args)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notEmpty(langName, "langName");
	NullCheck.notNull(name, "name");
	NullCheck.notNullItems(args, "args");
	final ProviderHook hook = new ProviderHook(luwrain);
	try {
	    final Object res = hook.run("luwrain.i18n." + langName + ".strings", new Object[]{name, ScriptUtils.createArray(args)});
	    if (res == null)
		return null;
	    return res.toString();
	}
	catch(Exception e)
	{
	    Log.error(LOG_COMPONENT, "unable to run the luwrain.i18n." + langName + ".strings hook:" + e.getClass().getName() + ":" + e.getMessage());
	    return null;
	}
    }
}
