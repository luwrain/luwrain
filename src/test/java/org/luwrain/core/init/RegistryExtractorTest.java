/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core.init;

import java.io.*;

import org.junit.*;

import org.luwrain.core.*;

public class RegistryExtractorTest extends Assert
{
    @Test public void dirCreation() throws IOException
    {
	final File tmpDir = createTempDir();
	final String proba = "DIR 1/2/3";
	final InputStream is = new ByteArrayInputStream(proba.getBytes());
	final RegistryExtractor extractor = new RegistryExtractor(tmpDir);
	extractor.extract(is);
	final File dir1 = new File(tmpDir, "1");
	final File dir2 = new File(dir1, "2");
	final File dir3 = new File(dir2, "3");
	assertTrue(dir1.exists());
	assertTrue(dir1.isDirectory());
	assertTrue(dir2.exists());
	assertTrue(dir2.isDirectory());
	assertTrue(dir3.exists());
	assertTrue(dir3.isDirectory());
	assertTrue(new File(dir3, "strings.txt").exists());
	assertTrue(new File(dir3, "booleans.txt").exists());
	assertTrue(new File(dir3, "integers.txt").exists());
    }

    static private File createTempDir() throws IOException
    {
	final File tmpFile = File.createTempFile("lwr-test-registry-extractor", "");
	if (!tmpFile.delete())
	    throw new IOException("Unable to delete temporary file " + tmpFile.getAbsolutePath());
	if (!tmpFile.mkdir())
	    throw new IOException("Unable to create temporary directory " + tmpFile.getAbsolutePath());
	return tmpFile;
    }

    //FIXME:testing of values saving
}
