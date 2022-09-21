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

//LWR_API 2.0

package org.luwrain.i18n;

import java.lang.reflect.*;
import java.io.*;
import java.util.*;

import org.luwrain.core.*;

public final class ResourceStringsObj implements InvocationHandler
{
    static private final String
	LOG_COMPONENT = "i18n",
	CHARSET = "UTF-8";

    private final Luwrain luwrain;
    private final ClassLoader classLoader;
    private final Properties props;

    public ResourceStringsObj(Luwrain luwrain, ClassLoader classLoader, Class cl, String resName) throws IOException
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(classLoader, "classLoader");
	NullCheck.notNull(cl, "cl");
	NullCheck.notEmpty(resName, "resName");
	this.luwrain = luwrain;
	this.classLoader = classLoader;
	this.props = new Properties();
	try (final BufferedReader r = new BufferedReader(new InputStreamReader(cl.getResourceAsStream(resName), CHARSET))) {
	    props.load(r);
	}
    }

    @SuppressWarnings("unchecked")
    public <T> T create(String langName, Class cl)
    {
	NullCheck.notEmpty(langName, "langName");
	NullCheck.notNull(cl, "cl");
	return (T) java.lang.reflect.Proxy.newProxyInstance(classLoader, new Class[]{cl}, this);
    }

    @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
	{
	    String name = method.getName();
	    if (name.length() > 1 && Character.isLowerCase(name.charAt(0)))
		name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
	    final String value = props.getProperty(name);
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
		return "#No value: " + name + "#";
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
	}
    }
}    
