/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
		    if (uniRef.indexOf("ncc.html") >= 0)
			return new UniRefInfo(uniRef, "Учебник", "\"Обществознание\"");
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

	};
    }
}
