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

//LWR_API 1.0

package org.luwrain.util;

import java.io.*;

import org.luwrain.core.*;

public final class PathUtils
{
    static public String escapeBash(String s)
    {
	boolean inApos = false;
	final StringBuilder b = new StringBuilder();
	for(int i = 0;i < s.length();i++)
	{
	    final char c = s.charAt(i);
	    if (!Character.isDigit(c) && !Character.isLetter(c) && c != '-' && c != '_')
		inApos = true;
	    if (c == '\'')
	    {
		b.append("\'\\\'\'");
		inApos = true;
		continue;
	    }
	    b.append(c);
	}
	if (inApos)
	    return "\'" + new String(b) + "\'";
	return new String(b);
    }
}
