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

import java.net.*;
import java.lang.reflect.*;

class ClassPath
{
    //Until the better days, when guys from OpenJDK stops the mess with classpath manipulation
    static boolean addUrl(URL url)
    {
	try {
	    final URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
	    final Class c = URLClassLoader.class;
	    final Method method = c.getDeclaredMethod("addURL", new Class[] { URL.class });
	    method.setAccessible(true);
	    method.invoke(classLoader, new Object[] { url });
	    return true;
	}
	catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
	{
	    e.printStackTrace();
	    return false;
	}
    }
}
