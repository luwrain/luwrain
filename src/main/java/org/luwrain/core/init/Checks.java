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

public final class Checks
{
    static private final String LOG_COMPONENT = Init.LOG_COMPONENT;

    static private final String DEFAULT_LANG = "en";

    static private final String ENV_LANG = "LUWRAIN_LANG";
        static public final String CMDARG_LANG = "--lang=";

    static private final String ENV_APP_DATA = "APPDATA";
    static private final String ENV_USER_PROFILE = "USERPROFILE";
    static private final String DEFAULT_USER_DATA_DIR_WINDOWS = "Luwrain";
    static private final String DEFAULT_USER_DATA_DIR_LINUX = ".luwrain";

    static public File detectUserDataDir()
    {
	//Windows: in Application Data
	if(System.getenv().containsKey(ENV_APP_DATA) && !System.getenv().get(ENV_APP_DATA).trim().isEmpty())
	{
	    final File appData = new File(System.getenv().get(ENV_APP_DATA));
	    return new File(appData, DEFAULT_USER_DATA_DIR_WINDOWS);
	}
	if(System.getenv().containsKey(ENV_USER_PROFILE) && !System.getenv().get(ENV_USER_PROFILE).trim().isEmpty())
	{
	    final File userProfile = new File(System.getenv().get(ENV_USER_PROFILE));
	    return new File(new File(new File(userProfile, "Local Settings"), "Application Data"), DEFAULT_USER_DATA_DIR_WINDOWS);
	}
	
	//We are likely on Linux
	final File f = new File(System.getProperty("user.home"));
	return new File(f, DEFAULT_USER_DATA_DIR_LINUX);
    }

    static public boolean isProfileInstalled(File userDataDir)
    {
	NullCheck.notNull(userDataDir, "userDataDir");
	//Checking only if the directory for the registry exists
	final File registryDir = new File(userDataDir, "registry");
	return registryDir.exists() && registryDir.isDirectory();
    }

    static public String detectLang(CmdLine cmdLine)
    {
		    NullCheck.notNull(cmdLine, "cmdLine");
		    final String cmdLineArg = cmdLine.getFirstArg(CMDARG_LANG);
		    if (cmdLineArg != null)
			switch(cmdLineArg.trim().toLowerCase())
		    {
		    case "ru":
		    case "en":
		    case "ro":
			return cmdLineArg.trim().toLowerCase();
		    default:
			Log.error(LOG_COMPONENT, "unknown language \'" + cmdLineArg + "\' in the command line options");
			return "";
		    }
		if(System.getenv().containsKey(ENV_LANG) && !System.getenv().get(ENV_LANG).trim().isEmpty())
		{
		    final String lang = System.getenv().get(ENV_LANG).toLowerCase().trim();
		    if (lang.equals("en") || lang.equals("ru"))
			return lang;
		    Log.warning(LOG_COMPONENT, "the environment variable " + ENV_LANG + " contains the improper value \'" + lang + "\', ignoring it");
		}
		final String lang = Locale.getDefault().getISO3Language().trim().toLowerCase();
		switch(lang)
		{
		case "eng":
		    return "en";
		case "rus":
		    return "ru";
		default:
		    Log.warning(LOG_COMPONENT, "locale detects the UI language as " + lang + ", but it isn\'t supported, using the default language " + DEFAULT_LANG);
		    return DEFAULT_LANG;
		}
    }
}
