
package org.luwrain.core;

import java.net.*;
import java.io.*;

import org.junit.*;

//FIXME:txt
//FIXME:wav

public class FileContentTypeTest extends Assert
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
