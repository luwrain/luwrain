/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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
import java.net.*;

import org.luwrain.core.*;

public final class UrlUtils
{
    static public File urlToFile(String urlStr)
    {
NullCheck.notEmpty(urlStr, "urlStr");
final URL url;
try {
    url = new URL(urlStr);
}
catch(MalformedURLException e)
{
    return null;
}
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

    static public String fileToUrl(File file)
    {
	NullCheck.notNull(file, "file");
	try {
	    return file.toURI().toURL().toString();
	}
	catch(IOException e)
	{
	    return null;
	}
    }
}
