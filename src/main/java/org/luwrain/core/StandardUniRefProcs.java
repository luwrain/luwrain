		/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

import java.util.*;

class StandardUniRefProcs
{
public static UniRefProc[] createStandardUniRefProcs(Strings s)
    {
	if (s == null)
	    throw new NullPointerException("s may not be null");
	final Strings str = s;
	LinkedList<UniRefProc> res = new LinkedList<UniRefProc>();

	//file;
	res.add(new UniRefProc() {
		private Strings strings = str;
		public String getUniRefType()
		{
		    return "file";
		}
		@Override public UniRefInfo getUniRefInfo(String uniRef)
		{
		    if (uniRef == null || uniRef.isEmpty())
			return null;
		    if (!uniRef.startsWith("file:"))
			return null;
		    return new UniRefInfo(strings.uniRefPrefix("file"), uniRef.substring(5));
		}
		public void openUniRef(String uniRef, Luwrain luwrain)
		{
		    if (uniRef == null || uniRef.isEmpty())
			return;
		    if (!uniRef.startsWith("file:"))
			return;
		    luwrain.openFile(uniRef.substring(5));
		}
	    });

	//command;
	res.add(new UniRefProc() {
		private Strings strings = str;
		public String getUniRefType()
		{
		    return "command";
		}
		@Override public UniRefInfo getUniRefInfo(String uniRef)
		{
		    if (uniRef == null || uniRef.isEmpty())
			return null;
		    if (!uniRef.startsWith("command:"))
			return null;
		    return new UniRefInfo(strings.uniRefPrefix("command"), uniRef.substring(8));
		}
		public void openUniRef(String uniRef, Luwrain luwrain)
		{
		    if (uniRef == null || uniRef.isEmpty())
			return;
		    if (!uniRef.startsWith("command:"))
			return;
		    luwrain.runCommand(uniRef.substring(8));
		}
	    });

	return res.toArray(new UniRefProc[res.size()]);
    }
}
