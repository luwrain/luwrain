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
import java.security.*;

import org.luwrain.core.*;

public final class Sha1
{
    static public String getSha1(InputStream inputStream) throws IOException, NoSuchAlgorithmException
    {
	NullCheck.notNull(inputStream, "inputStream");
	final MessageDigest sha1;
	sha1 = MessageDigest.getInstance("SHA-1");
	sha1.reset();
	final byte[] buf = new byte[2048];
	while (true)
	{
	    final int len = inputStream.read(buf);
	    if (len <= 0)
		break;
	    sha1.update(buf, 0, len);
	}
	final StringBuilder res = new StringBuilder();
	for(byte b: sha1.digest())
	{
	    int value = Byte.valueOf(b).intValue();
	    if (value < 0)
		value = 256 + value;
	    final String hex = Integer.toHexString(value);
	    if (hex.length() < 2)
		res.append("0");
	    res.append(hex);
	}
	return new String(res);
    }

    static public String getSha1(String str, String charset)
    {
	NullCheck.notNull(str, "str");
	NullCheck.notEmpty(charset, "charset");
	try {
	    final ByteArrayInputStream s = new ByteArrayInputStream(str.getBytes(charset));
	    return getSha1(s);
	}
	catch(IOException | NoSuchAlgorithmException e)
	{
	    throw new RuntimeException(e);
	}
    }

        static public String getSha1(byte[] bytes)
    {
	NullCheck.notNull(bytes, "bytes");
	try {
	    final ByteArrayInputStream s = new ByteArrayInputStream(bytes);
	    return getSha1(s);
	}
	catch(IOException | NoSuchAlgorithmException e)
	{
	    throw new RuntimeException(e);
	}
    }
}
