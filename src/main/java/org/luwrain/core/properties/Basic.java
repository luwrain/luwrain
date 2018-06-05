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

package org.luwrain.core.properties;

import java.io.*;
import java.util.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

public final class Basic implements CorePropertiesProvider, org.luwrain.base.CoreProperties
{
    private final File dataDir;
    private final File userDataDir;
    private final File userHomeDir;

    private CorePropertiesProvider.Listener listener = null;

    public Basic(File dataDir,
	 File userDataDir,
	 File userHomeDir)
    {
	NullCheck.notNull(dataDir, "dataDir");
	NullCheck.notNull(userDataDir, "userDataDir");
	NullCheck.notNull(userHomeDir, "userHomeDir");
	this.dataDir = dataDir;
	this.userDataDir = userDataDir;
	this.userHomeDir = userHomeDir;
    }

    @Override public String getExtObjName()
    {
	return this.getClass().getName();
    }

    @Override public String[] getPropertiesRegex()
    {
	return new String[]{
	    "luwrain.dir.userhome",
	    "luwrain.dir.data",
	    "luwrain.dir.scripts",
	    "luwrain.dir.js",
	    "luwrain.dir.properties",
	    "luwrain.dir.sounds",
	    "luwrain.dir.userdata",
	    "luwrain.dir.appdata"};
    }

    @Override public Set<org.luwrain.base.CorePropertiesProvider.Flags> getPropertyFlags(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	final File value = getFileProperty(propName);
	if (value != null)
	    return EnumSet.of(org.luwrain.base.CorePropertiesProvider.Flags.PUBLIC,
			      org.luwrain.base.CorePropertiesProvider.Flags.FILE);
	return null;
    }

    @Override public File getFileProperty(String propName)
    {
	NullCheck.notNull(propName, "propName");
	switch(propName)
	{
	case "luwrain.dir.userhome":
	    return userHomeDir;
	case "luwrain.dir.data":
	    return dataDir;
	case "luwrain.dir.scripts":
	    return new File(dataDir, "scripts");
	case "luwrain.dir.js":
	    return new File(dataDir, "js");
	case "luwrain.dir.properties":
	    return new File(dataDir, "properties");
	case "luwrain.dir.sounds":
	    return new File(dataDir, "sounds");
	case "luwrain.dir.userdata":
	    return userDataDir;
	case "luwrain.dir.appdata":
	    return new File(userDataDir, "app");
	default:
	    return null;
	}
    }

    @Override public boolean setFileProperty(String propName, File value)
    {
	NullCheck.notEmpty(propName, "propName");
	NullCheck.notNull(value, "value");
	return false;
    }

    @Override public String getProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	return null;
    }

    @Override public boolean setProperty(String propName, String value)
    {
	NullCheck.notEmpty(propName, "propName");
	NullCheck.notNull(value, "value");
	return false;
    }

    @Override public void setListener(org.luwrain.base.CorePropertiesProvider.Listener listener)
    {
	this.listener = listener;
    }
}