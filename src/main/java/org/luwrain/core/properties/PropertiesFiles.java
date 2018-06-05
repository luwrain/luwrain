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

public final class PropertiesFiles implements CorePropertiesProvider, org.luwrain.base.CoreProperties
{
    static private final String LOG_COMPONENT = Init.LOG_COMPONENT;

    private final Properties props = new Properties();
    private CorePropertiesProvider.Listener listener = null;

    public void load(File systemProperties, File userProperties)
    {
	NullCheck.notNull(systemProperties, "systemProperties");
	NullCheck.notNull(userProperties, "userProperties");
	for (File f: systemProperties.listFiles())
	    if (!f.isDirectory())
		readProps(f);
	for(File f: userProperties.listFiles())
	    if (!f.isDirectory())
		readProps(f);
    }

    @Override public String getExtObjName()
    {
	return this.getClass().getName();
    }

    @Override public String[] getPropertiesRegex()
    {
	return new String[0];
    }

    @Override public Set<CorePropertiesProvider.Flags> getPropertyFlags(String propName)
    {
	return EnumSet.of(CorePropertiesProvider.Flags.PUBLIC);
    }

    @Override public String getProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
return props.getProperty(propName);
    }

    @Override public File getFileProperty(String propName)
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

    @Override public boolean setFileProperty(String propName, File value)
    {
	NullCheck.notEmpty(propName, "propName");
	NullCheck.notNull(value, "value");
	return false;
    }

    @Override public void setListener(CorePropertiesProvider.Listener listener)
    {
	this.listener = listener;
    }

    private void readProps(File file)
    {
	NullCheck.notNull(file, "file");
	if (file.isDirectory() || !file.getName().endsWith(".properties"))
	    return;
	Log.debug(LOG_COMPONENT, "reading properties from " + file.getAbsolutePath());
	try {
	    final InputStream s = new FileInputStream(file);
	    try {
		props.load(new InputStreamReader(new BufferedInputStream(s), "UTF-8"));
	    }
	    finally {
		s.close();
	    }
	}
	catch(IOException e)
	{
	    Log.error(LOG_COMPONENT, "unable to read properties file " + file.getAbsolutePath() + ":" + e.getClass().getName() + ":" + e.getMessage());
	}
	}
}