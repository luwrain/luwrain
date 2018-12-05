/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core;

public final class UniRefUtils
{
    static public final String ALIAS = "link";

    static boolean isAlias(String uniref)
    {
	NullCheck.notNull(uniref, "uniref");
	if (uniref.isEmpty())
	    return false;
	return uniref.startsWith(ALIAS + ":");
    }

    static private int findAliasDelim(String aliasBody)
    {
	NullCheck.notNull(aliasBody, "aliasBody");
	int delim = 0;
	while(delim < aliasBody.length() &&
	      (aliasBody.charAt(delim) != ':' || (delim > 0 && aliasBody.charAt(delim - 1) == '\\')))
	    ++delim;
	return delim < aliasBody.length()?delim:-1;
    }
}
