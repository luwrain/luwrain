/*
   Copyright 2012-2019 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
    private final File dataDir;
    private final File destDir;
    private final String regVersion;
    private final String lang;

    public UserProfile(File dataDir, File destDir, String regVersion, String lang)
    {
	NullCheck.notNull(dataDir, "dataDir");
	NullCheck.notNull(destDir, "destDir");
	NullCheck.notEmpty(regVersion, "regVersion");
	NullCheck.notEmpty(lang, "lang");
	this.dataDir = dataDir;
	this.destDir = destDir;
	this.regVersion = regVersion;
	this.lang = lang;
    }

    public void userProfileReady() throws IOException
    {
	mkDirIfNotExists(destDir);
	mkDirIfNotExists(new File(destDir, "var"));
	mkDirIfNotExists(new File(destDir, "extensions"));
	mkDirIfNotExists(new File(destDir, "properties"));
	mkDirIfNotExists(new File(destDir, "sqlite"));
    }

    public void registryDirReady() throws IOException
    {
	final File registryDir = new File(destDir, "registry");
	mkDirIfNotExists(registryDir);
	final File target = new File(registryDir, regVersion);
	if (target.isDirectory())
	    return;
	if (target.isFile())
	    throw new IOException(target.getAbsolutePath() + " exists, but not a directory");
	if (!target.mkdir())
	    throw new IOException("Unable to create " + target.getAbsolutePath() + " needed for registry data");
	final RegistryExtractor extractor = new RegistryExtractor(target);
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

    static private void mkDirIfNotExists(File file) throws IOException
    {
	if (file.isDirectory())
	    return;
	if (file.isFile())
	    throw new IOException(file.getAbsolutePath() + " exists, but is a regular file");
	if (file.exists())
	    throw new IOException(file.getAbsolutePath() + " exists, but not a directory");
	if (!file.mkdir())
	    throw new IOException("Unable to create " + file.getAbsolutePath());
    }
}
