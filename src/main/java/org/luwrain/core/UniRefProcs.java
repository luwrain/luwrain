/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

final class UniRefProcs
{
    static UniRefProc[] createStandardUniRefProcs(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	return new UniRefProc[]{

	    //file
	    new UniRefProc() {
		static private final String PREFIX = "file:";
		@Override public String getUniRefType()
		{
		    return "file";
		}
		@Override public UniRefInfo getUniRefInfo(String uniRef)
		{
		    NullCheck.notEmpty(uniRef, "uniRef");
		    if (!uniRef.startsWith(PREFIX))
			return null;
		    return new UniRefInfo(uniRef, luwrain.i18n().getStaticStr("UniRefPrefixFile"), uniRef.substring(5));
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
		    return new UniRefInfo(uniRef, "", uniRef.substring(PREFIX.length()));
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
		    return "static";
		}
		@Override public UniRefInfo getUniRefInfo(String uniRef)
		{
		    NullCheck.notEmpty(uniRef, "uniRef");
		    if (!uniRef.startsWith(PREFIX))
			return null;
		    return new UniRefInfo(uniRef, "", uniRef.substring(PREFIX.length()));
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
		    return new UniRefInfo(uniRef, "", "");
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
		    return "command";
		}
		@Override public UniRefInfo getUniRefInfo(String uniRef)
		{
		    if (uniRef == null || uniRef.isEmpty())
			return null;
		    if (!uniRef.startsWith("command:"))
			return null;
		    return new UniRefInfo(uniRef, "", luwrain.i18n().getCommandTitle(uniRef.substring(8)));
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
		static private final String PREFIX = "link:";
		@Override public String getUniRefType()
		{
		    return "link";
		}
		@Override public UniRefInfo getUniRefInfo(String uniRef)
		{
		    NullCheck.notNull(uniRef, "uniRef");
		    if (!uniRef.startsWith(PREFIX))
			return null;
		    final String body = uniRef.substring(PREFIX.length());
		    if (body.isEmpty())
			return null;
		    final int delim = findDelim(body);
		    if (delim < 0)
			return null;
		    return new UniRefInfo(uniRef, "", body.substring(0, delim).replaceAll("\\\\:", ":"));
		}
		@Override public boolean openUniRef(String uniRef, Luwrain luwrain)
		{
		    NullCheck.notNull(uniRef, "uniRef");
		    NullCheck.notNull(luwrain, "luwrain");
		    if (!uniRef.startsWith(PREFIX))
			return false;
		    final String body = uniRef.substring(PREFIX.length());
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

		    //script
	    new UniRefProc() {
		static private final String PREFIX = "script:";
		@Override public String getUniRefType()
		{
		    return "script";
		}
		@Override public UniRefInfo getUniRefInfo(String uniRef)
		{
		    NullCheck.notNull(uniRef, "uniRef");
		    if (!uniRef.startsWith(PREFIX))
			return null;
		    final String body = uniRef.substring(PREFIX.length());
		    if (body.isEmpty())
			return null;
		    return new UniRefInfo(uniRef, "", body);
		}
		@Override public boolean openUniRef(String uniRef, Luwrain luwrain)
		{
		    NullCheck.notNull(uniRef, "uniRef");
		    NullCheck.notNull(luwrain, "luwrain");
		    if (!uniRef.startsWith(PREFIX))
			return false;
		    final String body = uniRef.substring(PREFIX.length());
		    if (body.isEmpty())
			return false;
		    try {
			final String text = FileUtils.readTextFileSingleString(new File(body), "UTF-8");
			luwrain.xExecScript(luwrain.getFileProperty("luwrain.dir.data"), text);
			return true;
		    }
		    catch(Exception e)
		    {
			luwrain.message(luwrain.i18n().getExceptionDescr(e), Luwrain.MessageType.ERROR);
			return true;
		    }
		}
	    },
	};

    
    }
}
