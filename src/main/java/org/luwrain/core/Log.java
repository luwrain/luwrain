/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core;

//TODO:Thread safety;

public class Log
{
    public static void debug(String component, String message)
    {
	if (component == null || message == null)
	    return;
	if (component.isEmpty())
	    System.out.println(message); else
	    System.out.println(component + ":" + message);
    }

    public static void info(String component, String message)
    {
	if (component == null || message == null)
	    return;
	if (component.isEmpty())
	    System.out.println(message); else
	    System.out.println(component + ":" + message);
    }

    public static void warning(String component, String message)
    {
	if (component == null || message == null)
	    return;
	if (component.isEmpty())
	    System.out.println("WARNING:" + message); else
	    System.out.println("WARNING:" + component + ":" + message);
    }

    public static void error(String component, String message)
    {
	if (component == null || message == null)
	    return;
	if (component.isEmpty())
	    System.out.println("ERROR:" + message); else
	    System.out.println("ERROR:" + component + ":" + message);
    }

    public static void fatal(String component, String message)
    {
	if (component == null || message == null)
	    return;
	if (component.isEmpty())
	    System.out.println("FATAL:" + message); else
	    System.out.println("FATAL:" + component + ":" + message);
    }



}
