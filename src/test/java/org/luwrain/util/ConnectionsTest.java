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

package org.luwrain.util;

import java.io.*;
import java.net.*;

import org.junit.*;

import org.luwrain.core.*;

@Ignore public class ConnectionsTest extends Assert
{
    static private final String url = "http://download.luwrain.org/pdf/presentation-HongKongOSConference-en-2015-06-27.pdf";

    @Ignore @Test public void fullSize() throws Exception
    {
	final URLConnection con = Connections.connect(new URI(url), 0);
	final InputStream is = con.getInputStream();
	final byte[] buf = new byte[512];
	int numRead = 0;
	int totalCount = 0;
	while ( (numRead = is.read(buf)) >= 0)
	    totalCount += numRead;
	assertTrue(totalCount == 77249);
	is.close();
    }

    @Ignore @Test public void fullSha1() throws Exception
    {
	final URLConnection con = Connections.connect(new URI(url), 0);
	final InputStream is = con.getInputStream();
	final String sha1 = Sha1.getSha1(is);
	assertTrue(sha1.toLowerCase().equals("58602629e630b1509a3e22110a1fdcedaf1de354"));
	is.close();
    }

    @Ignore @Test public void partialSize() throws Exception
    {
	final URLConnection con = Connections.connect(new URI(url), 65535);
	final InputStream is = con.getInputStream();
	final byte[] buf = new byte[512];
	int numRead = 0;
	int totalCount = 0;
	while ( (numRead = is.read(buf)) >= 0)
	    totalCount += numRead;
	assertTrue(totalCount == (77249 - 65535));
	is.close();
    }

    @Ignore @Test public void partialSha1() throws Exception
    {
	final URLConnection con = Connections.connect(new URI(url), 65535);
	final InputStream is = con.getInputStream();
	final String sha1 = Sha1.getSha1(is);
	assertTrue(sha1.toLowerCase().equals("758d421f15b1307ea1826c50eb183a9bb6882e4c"));
	is.close();
    }

    @Test public void redirect() throws Exception
    {
	final URLConnection con = Connections.connect(new URI("http://github.com"), 0);
	final InputStream is = con.getInputStream();
	//Checking only that the connection is established
	is.close();
    }

    @Ignore @Test public void https() throws Exception
    {
	final URLConnection con = Connections.connect(new URI("https://github.com"), 0);
	final InputStream is = con.getInputStream();
	//Checking only that the connection is established
	is.close();
    }
}
