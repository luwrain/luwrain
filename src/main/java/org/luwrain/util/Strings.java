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

package org.luwrain.util;

public class Strings
{
    public static String[] notNullArray(String[] values)
    {
	if (values == null || values.length < 1)
	    return new String[0];
	final String[] res = new String[values.length];
	for(int i = 0;i < values.length;++i)
	    res[i] = values[i] != null?values[i]:"";
	return res;
    }

    static public String sameCharString(char c, int count)
    {
	final StringBuilder b = new StringBuilder();
	for(int i = 0;i < count;++i)
	    b.append(c);
	return new String(b);
    }
}
