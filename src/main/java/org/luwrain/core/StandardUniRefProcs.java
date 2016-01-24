/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core;

class StandardUniRefProcs
{
    static UniRefProc[] createStandardUniRefProcs(Luwrain l, Strings s)
    {
	final Luwrain luwrain = l;
	final Strings strings = s;
	return new UniRefProc[]{

	    //file;
	    new UniRefProc() {
		@Override public String getUniRefType()
		{
		    return "file";
		}
		@Override public UniRefInfo getUniRefInfo(String uniRef)
		{
		    if (uniRef == null || uniRef.isEmpty())
			return null;
		    if (!uniRef.startsWith("file:"))
			return null;
		    return new UniRefInfo(uniRef, strings.uniRefPrefix("file"), uniRef.substring(5));
		}
		public void openUniRef(String uniRef, Luwrain luwrain)
		{
		    if (uniRef == null || uniRef.isEmpty())
			return;
		    if (!uniRef.startsWith("file:"))
			return;
		    luwrain.openFile(uniRef.substring(5));
		}
	    },

	    //command;
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
		    return new UniRefInfo(uniRef, "", luwrain.i18n().commandTitle(uniRef.substring(8)));
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
