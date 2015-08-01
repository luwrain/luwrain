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

import java.util.*;

public class Strings
{
    public static String[] notNullArray(String[] values)
    {
	if (values == null || values.length < 1)
	    return new String[0];
	LinkedList<String> res = new LinkedList<String>();
	for(int i = 0;i < values.length;++i)
	    res.add(values[i] != null?values[i]:"");
	return res.toArray(new String[res.size()]);
    }
}
