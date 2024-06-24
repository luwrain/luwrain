/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.util;

import java.io.*;

import org.luwrain.core.*;

import static org.luwrain.core.NullCheck.*;

public final class ResourceUtils
{
    static public String readStringResource(Class c, String resourceName, String charset, String lineSeparator) throws IOException
    {
	notNull(c, "c");
	notEmpty(resourceName, "resourceName");
	notEmpty(charset, "charset");
	notEmpty(lineSeparator, "lineSeparator");
	final StringBuilder b = new StringBuilder();
	try (final BufferedReader r = new BufferedReader(new InputStreamReader(c.getResourceAsStream(resourceName), charset))) {
	    {
		String line = r.readLine();
		while (line != null)
		{
		    b.append(line).append(lineSeparator);
		    line = r.readLine();
		}
		return new String(b);
	    }
	    
	}
    }

    static public String getStringResource(Class c, String resourceName) throws IOException
    {
	return readStringResource(c, resourceName, "UTF-8", System.lineSeparator());
    }
}
