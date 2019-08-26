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

package org.luwrain.util;

import java.io.*;
import java.net.*;

import org.luwrain.core.*;

public final class Urls
{
    static public File toFile(URL url)
    {
NullCheck.notNull(url, "url");
if (url.getProtocol() == null || !url.getProtocol().toLowerCase().equals("file"))
    return null;
try {
return new File(url.toURI());
}
catch(URISyntaxException e)
{
    return new File(url.getPath());
}
    }

    static public URL toUrl(File file)
    {
	NullCheck.notNull(file, "file");
	try {
	    return file.toURI().toURL();
	}
	catch(IOException e)
	{
	    return null;
	}
    }
}
