/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.util;

public class RegistryPath
{
    static public String join(String part1, String part2)
    {
	if (part1 == null)
	    throw new NullPointerException("part1 may not be null");
	if (part1.isEmpty())
	    throw new IllegalArgumentException("part1 may not be empty");
	if (part2 == null)
	    throw new NullPointerException("part2 may not be null");
	if (part2.isEmpty())
	    throw new IllegalArgumentException("part2 may not be empty");
	if (part2.charAt(0) == '/')
	    throw new IllegalArgumentException("part2 may not begin with a slash");
	if (part1.endsWith("/"))
	    return part1 + part2;
	return part1 + "/" + part2;
    }
}
