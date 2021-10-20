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

package org.luwrain.core;

import java.io.*;

import org.luwrain.util.*;

public final class UniRefProcs
{
    static public final String
	TYPE_ALIAS = "alias",
	TYPE_COMMAND = "command",
	TYPE_EMPTY = "empty",
	TYPE_FILE = "file",
	TYPE_SECTION = "section",
	TYPE_STATIC = "static",
	TYPE_URL = "url";

    static UniRefProc[] createStandardUniRefProcs(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	return new UniRefProc[]{

	    //file
	    new UniRefProc() {
		static private final String PREFIX = TYPE_FILE + ":";
		@Override public String getUniRefType()
		{ return TYPE_FILE; }
		@Override public UniRefInfo getUniRefInfo(String uniRef)
		{
		    NullCheck.notEmpty(uniRef, "uniRef");
		    if (!uniRef.startsWith(PREFIX))
			return null;
		    final File file = new File(uniRef.substring(PREFIX.length()));
		    return new UniRefInfo(uniRef, TYPE_FILE, file.getAbsolutePath(), file.getName());
		}
		@Override public boolean openUniRef(String uniRef, Luwrain luwrain)
		{
		    NullCheck.notEmpty(uniRef, "uniRef");
		    NullCheck.notNull(luwrain, "luwrain");
		    if (!uniRef.startsWith(PREFIX))
			return false;
		    luwrain.openFile(uniRef.substring(PREFIX.length()));
		    return true;
		}
	    },

	    //url
	    new UniRefProc() {
		static private final String PREFIX = "url:";
		@Override public String getUniRefType()
		{
		    return "url";
		}
		@Override public UniRefInfo getUniRefInfo(String uniRef)
		{
		    NullCheck.notEmpty(uniRef, "uniRef");
		    if (!uniRef.startsWith(PREFIX))
			return null;
		    return new UniRefInfo(uniRef, "url", uniRef.substring(PREFIX.length()), uniRef.substring(PREFIX.length()));
		}
		@Override public boolean openUniRef(String uniRef, Luwrain luwrain)
		{
		    NullCheck.notEmpty(uniRef, "uniRef");
		    NullCheck.notNull(luwrain, "luwrain");
		    if (!uniRef.startsWith(PREFIX))
			return false;
		    luwrain.launchApp("reader", new String[]{uniRef.substring(PREFIX.length())});
		    return true;
		}
	    },

	    //static
	    new UniRefProc() {
		static private final String PREFIX = "static:";
		@Override public String getUniRefType()
		{
		    return TYPE_STATIC;
		}
		@Override public UniRefInfo getUniRefInfo(String uniRef)
		{
		    NullCheck.notEmpty(uniRef, "uniRef");
		    if (!uniRef.startsWith(PREFIX))
			return null;
		    return new UniRefInfo(uniRef, TYPE_STATIC, "", uniRef.substring(PREFIX.length()));
		}
		@Override public boolean openUniRef(String uniRef, Luwrain luwrain)
		{
		    NullCheck.notEmpty(uniRef, "uniRef");
		    return false;
		}
	    },

	    	    //section
	    new UniRefProc() {
		static private final String PREFIX = "section:";
		@Override public String getUniRefType()
		{
		    return "section";
		}
		@Override public UniRefInfo getUniRefInfo(String uniRef)
		{
		    NullCheck.notEmpty(uniRef, "uniRef");
		    if (!uniRef.startsWith(PREFIX))
			return null;
		    return new UniRefInfo(uniRef, "section", "", uniRef.substring(PREFIX.length()));
		}
		@Override public boolean openUniRef(String uniRef, Luwrain luwrain)
		{
		    NullCheck.notEmpty(uniRef, "uniRef");
		    return false;
		}
	    },

	    	    	    //empty
	    new UniRefProc() {
		static private final String PREFIX = "empty:";
		@Override public String getUniRefType()
		{
		    return "empty";
		}
		@Override public UniRefInfo getUniRefInfo(String uniRef)
		{
		    NullCheck.notEmpty(uniRef, "uniRef");
		    if (!uniRef.startsWith(PREFIX))
			return null;
		    return new UniRefInfo(uniRef, "empty", "", "");
		}
		@Override public boolean openUniRef(String uniRef, Luwrain luwrain)
		{
		    NullCheck.notEmpty(uniRef, "uniRef");
		    return false;
		}
	    },

	    //command
	    new UniRefProc() {
		@Override public String getUniRefType()
		{
		    return TYPE_COMMAND;
		}
		@Override public UniRefInfo getUniRefInfo(String uniRef)
		{
		    NullCheck.notNull(uniRef, "uniRef");
		    final String prefix = TYPE_COMMAND + ":";
		    if (!uniRef.startsWith(prefix))
			return null;
		    final String command = uniRef.substring(prefix.length());
		    return new UniRefInfo(uniRef, TYPE_COMMAND, command, luwrain.i18n().getCommandTitle(command));
		}
		@Override public boolean openUniRef(String uniRef, Luwrain luwrain)
		{
		    if (uniRef == null || uniRef.isEmpty())
			return false;
		    if (!uniRef.startsWith("command:"))
			return false;
		    luwrain.runCommand(uniRef.substring(8));
		    return true;
		}
	    },

	    //link
	    new UniRefProc() {
		@Override public String getUniRefType()
		{
		    return TYPE_ALIAS;
		}
		@Override public UniRefInfo getUniRefInfo(String uniRef)
		{
		    NullCheck.notNull(uniRef, "uniRef");
		    		final String prefix = TYPE_ALIAS + ":";
		    if (!uniRef.startsWith(prefix))
			return null;
		    final String body = uniRef.substring(prefix.length());
		    if (body.isEmpty())
			return null;
		    final int delim = findDelim(body);
		    if (delim < 0)
			return null;
		    return new UniRefInfo(uniRef, TYPE_ALIAS, body.substring(delim + 1), body.substring(0, delim).replaceAll("\\\\:", ":"));
		}
		@Override public boolean openUniRef(String uniRef, Luwrain luwrain)
		{
		    NullCheck.notNull(uniRef, "uniRef");
		    NullCheck.notNull(luwrain, "luwrain");
		    		    		final String prefix = TYPE_ALIAS + ":";
		    if (!uniRef.startsWith(prefix))
			return false;
		    final String body = uniRef.substring(prefix.length());
		    if (body.isEmpty())
			return false;
		    final int delim = findDelim(body);
		    if (delim < 0 || delim + 1 >= body.length() )
			return false;
		    final String newUniRef = body.substring(delim + 1);
		    if (!newUniRef.isEmpty())
			return luwrain.openUniRef(newUniRef);
		    return false;
		}
		private int findDelim(String str)
		{
		    NullCheck.notNull(str, "str");
		    int delim = 0;
		    while(delim < str.length() &&
			  (str.charAt(delim) != ':' || (delim > 0 && str.charAt(delim - 1) == '\\')))
			++delim;
		    return delim < str.length()?delim:-1;
		}
	    },

	};
    }
}
