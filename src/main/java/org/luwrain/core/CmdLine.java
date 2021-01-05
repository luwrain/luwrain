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

package org.luwrain.core;

import java.util.*;

public final class CmdLine
{
    private final String[] cmdLine;

    public CmdLine(String[] cmdLine)
    {
	NullCheck.notNullItems(cmdLine, "cmdLine");
	this.cmdLine = cmdLine.clone();
    }

    public boolean used(String option)
    {
	NullCheck.notNull(option, "option");
	for(String s: cmdLine)
	    if (s.equals(option))
		return true;
	return false;
    }

    public String getFirstArg(String prefix)
    {
	NullCheck.notEmpty(prefix, "prefix");
	for(String s: cmdLine)
	{
	    if (s.length() < prefix.length() || !s.startsWith(prefix))
		continue;
	    return s.substring(prefix.length());
	}
	return null;
    }

    public String[] getArgs(String prefix)
    {
	NullCheck.notNull(prefix, "prefix");
		final List<String> res = new LinkedList();
	for(String s: cmdLine)
	{
	    if (s.length() < prefix.length() || !s.startsWith(prefix))
		continue;
	    res.add(s.substring(prefix.length()));
	}
	return res.toArray(new String[res.size()]);
    }
}
