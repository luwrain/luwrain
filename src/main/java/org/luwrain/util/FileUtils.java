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

import java.io.*;
import java.util.*;

import org.luwrain.core.*;

import static org.luwrain.util.StreamUtils.*;

public final class FileUtils
{
    static public final String UTF_8 = "UTF_8";

            static public String readTextFileAsString(File file) throws IOException
    {
	NullCheck.notNull(file, "file");
	return readTextFileAsString(file, UTF_8);
    }

    static public String readTextFileAsString(File file, String charset) throws IOException
    {
	NullCheck.notNull(file, "file");
	NullCheck.notEmpty(charset, "charset");
	final InputStream is = new FileInputStream(file);
	final byte[] bytes;
	try {
	    bytes = readAllBytes(is);
	}
	finally {
	    is.close();
	}
	return new String(bytes, charset);
    }

            static public String readTextFileSingleString(File file) throws IOException
    {
	NullCheck.notNull(file, "file");
	return readTextFileAsString(file, UTF_8);
    }

        static public String readTextFileSingleString(File file, String charset) throws IOException
    {
	NullCheck.notNull(file, "file");
	NullCheck.notEmpty(charset, "charset");
	return readTextFileAsString(file, charset);
    }

    static public void writeTextFileSingleString(File file, String text, String charset) throws IOException
    {
	NullCheck.notNull(file, "file");
	NullCheck.notNull(text, "text");
	NullCheck.notEmpty(charset, "charset");
	final OutputStream os = new FileOutputStream(file);
	try {
	    writeAllBytes(os, text.getBytes(charset));
	}
	finally {
	    os.flush();
	    os.close();
	}
    }

    //lineSeparator may be null, means use default 
    static public String[] readTextFileMultipleStrings(File file, String charset, String lineSeparator) throws IOException
    {
	NullCheck.notNull(file, "file");
	NullCheck.notEmpty(charset, "charset");
	final String text = readTextFileSingleString(file, charset);
	if (text.isEmpty())
	    return new String[0];
	return text.split(lineSeparator != null?lineSeparator:System.getProperty("line.separator"), -1);
    }

    //lineSeparator may be null, means use default 
    static public void writeTextFileMultipleStrings(File file, String[] text, String charset, String lineSeparator) throws IOException
    {
	NullCheck.notNull(file, "file");
	NullCheck.notNull(text, "text");
	NullCheck.notEmpty(charset, "charset");
	final StringBuilder b = new StringBuilder();
	if (text.length > 0)
	{
	    b.append(text[0]);
	    for(int i = 1;i < text.length;++i)
		b.append((lineSeparator != null?lineSeparator:System.getProperty("line.separator")) + text[i]);
	}
	writeTextFileSingleString(file, new String(b), charset);
    }

    //On an empty line provided returns one empty line
    static public String[] universalLineSplitting(String text)
    {
	NullCheck.notNull(text, "text");
	boolean wasBN = false;
	boolean wasBR = false;
	final List<String> res = new ArrayList<>();
	StringBuilder b = new StringBuilder();
	for(int i = 0;i < text.length();++i)
	{
	    final char c = text.charAt(i);
	    switch(c)
	    {
	    case '\n':
		if (wasBR)
		{
		    //Doing nothing
		    wasBN = true;
		    continue;
		}
		if (wasBN)
		{
		    //The second encountering, it means there was an empty line
		    wasBN = false;
		    wasBR = false;
		    //b must be empty
		    res.add("");
		    continue;
		}
		//wasBR and wasBN are false
		res.add(new String(b));
		b = new StringBuilder();
		wasBN = true;
		break;
	    case '\r':
		if (wasBN)
		{
		    //Doing nothing
		    wasBR = true;
		    continue;
		}
		if (wasBR)
		{
		    //The second encountering, it means there was an empty line
		    wasBN = false;
		    wasBR = false;
		    //b must be empty
		    res.add("");
		    continue;
		}
		//wasBR and wasBN are false
		res.add(new String(b));
		b = new StringBuilder();
		wasBR = true;
		break;
	    default:
		wasBR = false;
		wasBN = false;
		b.append("" + c);
	    }
	}
	res.add(new String(b));
	return res.toArray(new String[res.size()]);
    }

    static public File ifNotAbsolute(File baseDir, String path)
    {
	NullCheck.notNull(baseDir, "baseDir");
	NullCheck.notEmpty(path, "path");
	final File file = new File(path);
	if (file.isAbsolute())
	    return file;
	return new File(baseDir, path);
    }

    static public void createSubdirs(File destDir) throws IOException
    {
	NullCheck.notNull(destDir, "destDir");

	if (destDir.exists())
	{
	    if (!destDir.isDirectory())
		throw new IOException(destDir.getAbsolutePath() + " exists and isn't a directory");
	    return;
	}
	final File parent = destDir.getParentFile();
	if (parent != null)
	    createSubdirs(parent);
	destDir.mkdir();	
    }
}
