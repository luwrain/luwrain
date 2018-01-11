
package org.luwrain.core;

import java.net.*;
import java.io.*;

import org.junit.*;

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
    }
    }
