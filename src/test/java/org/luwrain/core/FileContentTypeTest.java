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

package org.luwrain.core;

import java.net.*;
import java.io.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

//FIXME:txt
//FIXME:wav

public class FileContentTypeTest
{
    @Test public void defaultValues() throws Exception
    {
	final FileContentType t = new FileContentType();

	assertTrue(t.suggestContentType(new File("test"), ContentTypes.ExpectedType.ANY).equals(ContentTypes.DATA_BINARY_DEFAULT));
		assertTrue(t.suggestContentType(new File("test"), ContentTypes.ExpectedType.AUDIO).equals(ContentTypes.SOUND_MP3_DEFAULT));
			assertTrue(t.suggestContentType(new File("test"), ContentTypes.ExpectedType.TEXT).equals(ContentTypes.TEXT_PLAIN_DEFAULT));
				assertTrue(t.suggestContentType(new File("test/"), ContentTypes.ExpectedType.ANY).equals(ContentTypes.DATA_BINARY_DEFAULT));
		assertTrue(t.suggestContentType(new File("test/"), ContentTypes.ExpectedType.AUDIO).equals(ContentTypes.SOUND_MP3_DEFAULT));
			assertTrue(t.suggestContentType(new File("test/"), ContentTypes.ExpectedType.TEXT).equals(ContentTypes.TEXT_PLAIN_DEFAULT));

			assertTrue(t.suggestContentType(new URL("http://test.org/test"), ContentTypes.ExpectedType.ANY).equals(ContentTypes.DATA_BINARY_DEFAULT));
			assertTrue(t.suggestContentType(new URL("http://test.org/test"), ContentTypes.ExpectedType.AUDIO).equals(ContentTypes.SOUND_MP3_DEFAULT));
			assertTrue(t.suggestContentType(new URL("http://test.org/test"), ContentTypes.ExpectedType.TEXT).equals(ContentTypes.TEXT_PLAIN_DEFAULT));
			assertTrue(t.suggestContentType(new URL("http://test.org/test/"), ContentTypes.ExpectedType.ANY).equals(ContentTypes.DATA_BINARY_DEFAULT));
												assertTrue(t.suggestContentType(new URL("http://test.org/test/"), ContentTypes.ExpectedType.AUDIO).equals(ContentTypes.SOUND_MP3_DEFAULT));
									assertTrue(t.suggestContentType(new URL("http://test.org/test/"), ContentTypes.ExpectedType.TEXT).equals(ContentTypes.TEXT_PLAIN_DEFAULT));

												assertTrue(t.suggestContentType(new URL("http://test.org"), ContentTypes.ExpectedType.ANY).equals(ContentTypes.DATA_BINARY_DEFAULT));
			assertTrue(t.suggestContentType(new URL("http://test.org"), ContentTypes.ExpectedType.AUDIO).equals(ContentTypes.SOUND_MP3_DEFAULT));
			assertTrue(t.suggestContentType(new URL("http://test.org"), ContentTypes.ExpectedType.TEXT).equals(ContentTypes.TEXT_PLAIN_DEFAULT));
			assertTrue(t.suggestContentType(new URL("http://test.org/"), ContentTypes.ExpectedType.ANY).equals(ContentTypes.DATA_BINARY_DEFAULT));
												assertTrue(t.suggestContentType(new URL("http://test.org/"), ContentTypes.ExpectedType.AUDIO).equals(ContentTypes.SOUND_MP3_DEFAULT));
									assertTrue(t.suggestContentType(new URL("http://test.org/"), ContentTypes.ExpectedType.TEXT).equals(ContentTypes.TEXT_PLAIN_DEFAULT));
    }

    @Test public void mp3() throws Exception
    {
	final FileContentType t = new FileContentType();

	assertTrue(t.suggestContentType(new File("test.mp3"), ContentTypes.ExpectedType.ANY).equals(ContentTypes.SOUND_MP3_DEFAULT));
	assertTrue(t.suggestContentType(new File("test.mp3"), ContentTypes.ExpectedType.AUDIO).equals(ContentTypes.SOUND_MP3_DEFAULT));
	assertTrue(t.suggestContentType(new File("test.mp3"), ContentTypes.ExpectedType.TEXT).equals(ContentTypes.SOUND_MP3_DEFAULT));
	assertTrue(t.suggestContentType(new File("TEST.MP3"), ContentTypes.ExpectedType.ANY).equals(ContentTypes.SOUND_MP3_DEFAULT));
	assertTrue(t.suggestContentType(new File("TEST.MP3"), ContentTypes.ExpectedType.AUDIO).equals(ContentTypes.SOUND_MP3_DEFAULT));
	assertTrue(t.suggestContentType(new File("TEST.MP3"), ContentTypes.ExpectedType.TEXT).equals(ContentTypes.SOUND_MP3_DEFAULT));

	assertTrue(t.suggestContentType(new URL("http://test.org/test.mp3"), ContentTypes.ExpectedType.ANY).equals(ContentTypes.SOUND_MP3_DEFAULT));
	assertTrue(t.suggestContentType(new URL("http://test.org/test.mp3"), ContentTypes.ExpectedType.AUDIO).equals(ContentTypes.SOUND_MP3_DEFAULT));
	assertTrue(t.suggestContentType(new URL("http://test.org/test.mp3"), ContentTypes.ExpectedType.TEXT).equals(ContentTypes.SOUND_MP3_DEFAULT));
	assertTrue(t.suggestContentType(new URL("http://test.org/TEST.MP3"), ContentTypes.ExpectedType.ANY).equals(ContentTypes.SOUND_MP3_DEFAULT));
	assertTrue(t.suggestContentType(new URL("http://test.org/TEST.MP3"), ContentTypes.ExpectedType.AUDIO).equals(ContentTypes.SOUND_MP3_DEFAULT));
	assertTrue(t.suggestContentType(new URL("http://test.org/TEST.MP3"), ContentTypes.ExpectedType.TEXT).equals(ContentTypes.SOUND_MP3_DEFAULT));
    }
}
