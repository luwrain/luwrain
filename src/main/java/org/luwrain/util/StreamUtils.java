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
    public interface Progress
    {
	void processed(int chunkNumBytes, long totalNumBytes);
    }

    public interface Interrupting
    {
	boolean interrupting();
    }

    static public long copyAllBytes(InputStream is, OutputStream os) throws IOException
    {
	return copyAllBytes(is, os, null, null);
    }

    static public long copyAllBytes(InputStream is, OutputStream os, Progress progress, Interrupting interrupting) throws IOException
    {
	NullCheck.notNull(is, "is");
	NullCheck.notNull(os, "os");
	long totalBytes = 0;
	final byte[] buf = new byte[2048];
	while(true)
	{
	    if (interrupting != null && interrupting.interrupting())
		return totalBytes;
	    final int length = is.read(buf);
	    if (length < 0)
		return totalBytes;
	    FileUtils.writeAllBytes(os, buf, length);
	    totalBytes += length;
	    if (progress != null)
		progress.processed(length, totalBytes);
	}
    }
}
