/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.util;

import java.util.*;

import java.io.*;

import org.luwrain.core.*;

public final class LinesSaver
{
    static public void saveLines(File file, Lines lines) throws IOException
    {
	NullCheck.notNull(file, "file");
	NullCheck.notNull(lines, "lines");
	FileOutputStream s = null;
	BufferedWriter w = null;
	try {
	    s = new FileOutputStream(file);
	    w = new BufferedWriter(new OutputStreamWriter(s));
	    final int count = lines.getLineCount();
	    for(int i = 0;i < count;++i)
	    {
		w.write(lines.getLine(i));
		w.newLine();
	    }
	}
	finally {
	    if (w != null)
	    {
		w.flush();
		w.close();
	    }
	    if (s != null)
	    {
		s.flush();
		s.close();
	    }
	}
    }
}
