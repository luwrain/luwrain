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

package org.luwrain.core.init;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;

public final class UserProfile
{
    static public void createUserProfile(File dataDir, File destDir, String lang) throws IOException
    {
	NullCheck.notNull(dataDir, "dataDir");
	NullCheck.notNull(destDir, "destDir");
	NullCheck.notEmpty(lang, "lang");
	destDir.mkdir();
	final File registryDir = new File(destDir, "registry");
	registryDir.mkdir();
	new File(destDir, "app").mkdir();
	new File(destDir, "extensions").mkdir();
	new File(destDir, "properties").mkdir();
	new File(destDir, "sqlite").mkdir();
	final RegistryExtractor extractor = new RegistryExtractor(registryDir);
	InputStream commonIs = null;
	InputStream langIs = null;
	try {
	    commonIs = new FileInputStream(new File(dataDir, "registry.dat"));
	    langIs = new FileInputStream(new File(dataDir, "registry." + lang + ".dat"));
	    extractor.extract(commonIs);
	    extractor.extract(langIs);
	}
	finally{
	    if (commonIs != null)
		commonIs.close();
	    if (langIs != null)
		langIs.close();
	}
    }
}
