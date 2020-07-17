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

package org.luwrain.util;

import java.io.*;

import org.junit.*;

import org.luwrain.core.*;

public class Sha1Test extends Assert
{
    @Test public void emptyStream() throws Exception
    {
	final byte[] emptyBuf = new byte[0];
final ByteArrayInputStream s = new ByteArrayInputStream(emptyBuf);
final String res = Sha1.getSha1(s);
assertTrue(res.equals("da39a3ee5e6b4b0d3255bfef95601890afd80709"));
    }
}
