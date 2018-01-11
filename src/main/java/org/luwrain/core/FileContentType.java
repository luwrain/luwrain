/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.util.*;
import java.io.*;
import java.net.*;
import java.util.regex.*;

final class FileContentType
{
    private final Map<String, String> contentTypes = new HashMap();

    FileContentType()
    {
	//Raw 
		contentTypes.put(".*\\.dat", ContentTypes.DATA_BINARY_DEFAULT);
	contentTypes.put(".*\\.raw", ContentTypes.DATA_BINARY_DEFAULT);

	//Text
		contentTypes.put(".*\\.txt", ContentTypes.TEXT_PLAIN_DEFAULT);
		contentTypes.put(".*\\.htm", ContentTypes.TEXT_HTML_DEFAULT);
	contentTypes.put(".*\\.html", ContentTypes.TEXT_HTML_DEFAULT);

	//Audio
		contentTypes.put(".*\\.mp3", ContentTypes.SOUND_MP3_DEFAULT);

		contentTypes.put(".*\\.xhtml", "application/xhtml");
		contentTypes.put(".*\\.xhtm", "application/xhtml");
		contentTypes.put(".*\\.pdf", "application/pdf");
		contentTypes.put(".*\\.ps", "application/postscript");
		contentTypes.put(".*\\.zip", "application/zip");
		contentTypes.put(".*\\.doc", "application/msword");
		contentTypes.put(".*\\.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		contentTypes.put(".*\\.fb2", "application/fb2");
    }

    String suggestContentType(File file, ContentTypes.ExpectedType expectedType)
    {
	NullCheck.notNull(file, "file");
	NullCheck.notNull(expectedType, "expectedType");
	if (file.getAbsolutePath().isEmpty())
	    return "";
	final String res = find(file.getName());
	if (!res.isEmpty())
	    return res;
	switch(expectedType)
	{
	case TEXT:
	    return ContentTypes.TEXT_PLAIN_DEFAULT;
	case AUDIO:
	    return ContentTypes.SOUND_MP3_DEFAULT;
	default:
	    return ContentTypes.DATA_BINARY_DEFAULT;
	}
    }

    String suggestContentType(URL url, ContentTypes.ExpectedType expectedType)
    {
	NullCheck.notNull(url, "url");
	NullCheck.notNull(expectedType, "expectedType");
final String res = find(url.getFile());
if (!res.isEmpty())
    return res;
switch(expectedType)
{
case TEXT:
    return ContentTypes.TEXT_PLAIN_DEFAULT;
case AUDIO:
    return ContentTypes.SOUND_MP3_DEFAULT;
default:
    return ContentTypes.DATA_BINARY_DEFAULT;
}
    }

    private String find(String fileName)
    {
	if (fileName == null || fileName.isEmpty())
	    return "";
	for(Map.Entry<String, String> e: contentTypes.entrySet())
	    if (match(e.getKey(), fileName))
		return e.getValue();
	return "";
    }

    static private boolean match(String pattern, String line)
    {
	NullCheck.notEmpty(pattern, "pattern");
	NullCheck.notNull(line, "line");
	final Pattern pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
	final Matcher matcher = pat.matcher(line);
	return matcher.find();
    }
}
