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

package org.luwrain.controls;

import java.io.*;
import java.nio.file.*;

import org.luwrain.core.NullCheck;

public class CommanderFilters
{
    static public class AllFiles implements CommanderArea.Filter
    {
	@Override public boolean commanderEntrySuits(CommanderArea.Entry entry)
	{
	    return true;
	}
    }

    static public class NoHidden implements CommanderArea.Filter
    {
	@Override public boolean commanderEntrySuits(CommanderArea.Entry entry)
	{
	    NullCheck.notNull(entry, "entry");
	    if (entry.parent())
		return true;
	    try {
		return !Files.isHidden(entry.path());
	    }
	    catch(IOException e)
	    {
		e.printStackTrace();
		return true;
	    }
	}
    }
}
