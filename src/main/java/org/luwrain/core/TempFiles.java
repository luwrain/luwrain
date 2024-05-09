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

import java.io.*;
import java.nio.file.*;
import org.apache.logging.log4j.*;

import static java.nio.file.Files.*;

final class TempFiles
{
    static private final Logger log = LogManager.getLogger();
    static private final String
	ENV_TMP_DIR = System.getenv("TMPDIR"),
	PROP_TMP_DIR = System.getProperty("java.io.tmpdir"),
		PROP_USER_HOME = System.getProperty("user.home");
    private Path tmpDir = null;

    void init()
    {
	if (tmpDir != null)
	    return;
			try {
	if (ENV_TMP_DIR != null && !ENV_TMP_DIR.trim().isEmpty() && new File(ENV_TMP_DIR).exists())
	{
	    log.trace("Using the value from $TMPDIR for LUWRAIN temporary directory, $TMPDIR=" + ENV_TMP_DIR);
tmpDir = createTempDirectory(Paths.get(ENV_TMP_DIR), ".luwrain-");
	} else
	    	if (PROP_TMP_DIR != null && !PROP_TMP_DIR.trim().isEmpty() && new File(PROP_TMP_DIR).exists())
	{
	    log.trace("Using the value from the java.io.tmpdir system property for LUWRAIN temporary directory, java.io.tmpdir=" + PROP_TMP_DIR);
tmpDir = createTempDirectory(Paths.get(PROP_TMP_DIR), ".luwrain-");
	} else
		{
		    	    log.warn("Using the value from user.home system property for LUWRAIN temporary directory, user.home=" + PROP_USER_HOME);
tmpDir = createTempDirectory(Paths.get(PROP_USER_HOME), ".luwrain-");
		}
	tmpDir.toFile().deleteOnExit();
	}
	catch(IOException e)
	{
	    throw new RuntimeException(e);
	}
    }

    File createTempFile(String prefix)
    {
	NullCheck.notNull(prefix, "prefix");
	    init();
	    try {
	    final var res = Files.createTempFile(tmpDir, prefix + "-", ".tmp");
	    log.trace("Created temporary file " + res.toString());
	    res.toFile().deleteOnExit();
	    return res.toFile();
	    }
	    catch(IOException e)
	    {
		throw new RuntimeException(e);
	    }
    }
}
