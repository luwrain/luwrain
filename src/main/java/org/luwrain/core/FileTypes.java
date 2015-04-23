/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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
import java.io.*;

class FileTypes
{
    public String[] chooseShortcuts(String[] fileNames)
    {
	if (fileNames == null)
	    throw new NullPointerException("fileNames may not be null");
	Vector<String> res = new Vector<String>();
	for(String s: fileNames)
	{
	    if (s == null)
	    {
		res.add("");
		continue;
	    }
	    File f = new File(s);
	    if (!f.exists())
	    {
		res.add("notepad");
		continue;
	    }
	    if (f.isDirectory())
	    {
		res.add("commander");
		continue;
	    }
	    final String name = f.getName();
	    final int dotPos = name.lastIndexOf('.');
	    if (dotPos < 0)
	    {
		res.add("");
		continue;
	    }
	    final String ext = name.substring(dotPos);
	    if (ext.toLowerCase().equals(".doc"))
	    {
		res.add("reader");
		continue;
	    }
	    if (ext.toLowerCase().equals(".html"))
	    {
		res.add("reader");
		continue;
	    }
	    res.add("notepad");
	}
	return res.toArray(new String[res.size()]);
    }
}
