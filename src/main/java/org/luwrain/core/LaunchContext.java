/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

import java.io.*;

public class LaunchContext
{
    private String userHomeDir = "";
    private String dataDir = "";
    private String lang = "";

    public LaunchContext(String dataDir,
			 String userHomeDir,
			 String lang)
    {
	this.dataDir = dataDir;
	this.userHomeDir = userHomeDir;
	this.lang = lang;
	if (dataDir == null)
	    throw new NullPointerException("dataDir may not be null");
	if (userHomeDir == null)
	    throw new NullPointerException("userHomeDir may not be null");
	if (lang == null)
	    throw new NullPointerException("lang may not be null");
    }

    public String userHomeDir()
    {
	return userHomeDir;
    }

    public File userHomeDirAsFile()
    {
	return new File(userHomeDir);
    }

    public String dataDir()
    {
	return dataDir;
    }

    public File dataDirAsFile()
    {
	return new File(dataDir);
    }

    public String lang()
    {
	return lang;
    }
}