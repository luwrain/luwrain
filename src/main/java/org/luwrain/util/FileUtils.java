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

//LWR_API 1.0

package org.luwrain.util;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;

public final class FileUtils
{
    static public byte [] readAllBytes(InputStream is) throws IOException
    {
	NullCheck.notNull(is, "is");
	final byte[] buf = new byte[2048];
	final ByteArrayOutputStream res = new ByteArrayOutputStream();
	int length = 0;
	do {
	    length = is.read(buf);
	    if (length > 0)
		res.write(buf, 0, length);
	} while(length >= 0);
	return res.toByteArray();
    }

    static public void writeAllBytes(OutputStream os, byte[] bytes) throws IOException
    {
	NullCheck.notNull(os, "os");
	NullCheck.notNull(bytes, "bytes");
	int pos = 0;
	do {
	    final int remaining = bytes.length - pos;
	    final int numToWrite = remaining > 2048?2048:remaining;
	    os.write(bytes, pos, numToWrite);
	    pos += numToWrite;
	} while(pos < bytes.length);
    }

    static public String readTextFileSingleString(File file, String charset) throws IOException
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
	final List<String> res = new LinkedList();
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
}
