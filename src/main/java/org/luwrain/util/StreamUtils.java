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

//LWR_API 1.0

package org.luwrain.util;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;

public final class StreamUtils
{
    static public int copyAllBytes(InputStream is, OutputStream os) throws IOException
    {
	NullCheck.notNull(is, "is");
	NullCheck.notNull(os, "os");
	final byte[] buf = new byte[2048];
	int length = 0;
	do {
	    length = is.read(buf);
	    if (length > 0)
		FileUtils.writeAllBytes(os, buf, length);
	} while(length >= 0);
	return length;
    }
}
