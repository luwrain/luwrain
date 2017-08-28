/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

class UniRefProcs
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
		@Override public void openUniRef(String uniRef, Luwrain luwrain)
		{
		    NullCheck.notEmpty(uniRef, "uniRef");
		    NullCheck.notNull(luwrain, "luwrain");
		    if (!uniRef.startsWith(PREFIX))
			return;
		    luwrain.openFile(uniRef.substring(PREFIX.length()));
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
		@Override public void openUniRef(String uniRef, Luwrain luwrain)
		{
		    NullCheck.notEmpty(uniRef, "uniRef");
		    NullCheck.notNull(luwrain, "luwrain");
		    if (!uniRef.startsWith(PREFIX))
			return;
		    luwrain.launchApp("reader", new String[]{uniRef.substring(PREFIX.length())});
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
		@Override public void openUniRef(String uniRef, Luwrain luwrain)
		{
		    if (uniRef == null || uniRef.isEmpty())
			return;
		    if (!uniRef.startsWith("command:"))
			return;
		    luwrain.runCommand(uniRef.substring(8));
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
		@Override public void openUniRef(String uniRef, Luwrain luwrain)
		{
		    NullCheck.notNull(uniRef, "uniRef");
		    NullCheck.notNull(luwrain, "luwrain");
		    if (!uniRef.startsWith(PREFIX))
			return;
		    final String body = uniRef.substring(PREFIX.length());
		    if (body.isEmpty())
			return;
		    final int delim = findDelim(body);
		    if (delim < 0 || delim + 1 >= body.length() )
			return;
		    final String newUniRef = body.substring(delim + 1);
		    if (!newUniRef.isEmpty())
			luwrain.openUniRef(newUniRef);
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
