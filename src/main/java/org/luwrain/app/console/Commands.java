/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.app.console;

import java.util.*;
import java.io.*;
import org.apache.logging.log4j.*;

import org.luwrain.core.*;

final class Commands
{
    static private final Logger LOG = LogManager.getLogger();

    
    static final class Prop implements ConsoleCommand
    {
	private final Luwrain luwrain;
	Prop(Luwrain luwrain)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    this.luwrain = luwrain;
	}
	@Override public boolean onCommand(String text, App app)
	{
	    if (!Utils.firstWord(text).equals("prop"))
		return false;
	    final int pos = text.indexOf(" ");
	    if (pos < 0)
	    {
		LOG.trace("prop: no argument");
		return true;
	    }
	    final String arg = text.substring(pos).trim();
	    if (arg.isEmpty())
	    {
				LOG.trace("prop: no argument");
				return true;
	    }
	    final File fileValue = luwrain.getFileProperty(arg);
	    if (fileValue != null)
	    {
		LOG.trace("file: " + fileValue.toString() + " (" + fileValue.getAbsolutePath() + ")");
		return true;
	    }
	    final String value = luwrain.getProperty(arg);
	    if (!value.isEmpty())
		LOG.trace(value); else
		LOG.trace("empty");
	    return true;
	}
    }
}
