/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

import java.util.*;
import java.io.*;

public final class Standalone
{
    static private final File STANDALONE = new File("standalone");
    static private final String ENV_APP_DATA = "APPDATA";
    static private final String ENV_USER_PROFILE = "USERPROFILE";

    private final boolean standalone;
    private final File dataDir;

    public Standalone(String unixDataDirName, String winDataDirName)
    {
	NullCheck.notEmpty(unixDataDirName, "unixDataDirName");
	NullCheck.notNull(winDataDirName, "winDataDirName");
	standalone = STANDALONE.exists() && STANDALONE.isFile();
	// Windows
	if(System.getenv().containsKey(ENV_APP_DATA) && !System.getenv().get(ENV_APP_DATA).trim().isEmpty())
	{
	    final File appData = new File(System.getenv().get(ENV_APP_DATA));
	    dataDir =new File(appData, winDataDirName);
	} else
	    if(System.getenv().containsKey(ENV_USER_PROFILE) && !System.getenv().get(ENV_USER_PROFILE).trim().isEmpty())
	    {
		final File userProfile = new File(System.getenv().get(ENV_USER_PROFILE));
		dataDir = new File(new File(new File(userProfile, "Local Settings"), "Application Data"), winDataDirName);
	    } else
	    {
		// UNIX
		final File f = new File(System.getProperty("user.home"));
		dataDir = new File(f, "." + unixDataDirName);
	    }
    }

    public boolean isStandalone()
    {
	return this.standalone;
    }

    public File getDataDir()
    {
	return this.dataDir;
    }
}
