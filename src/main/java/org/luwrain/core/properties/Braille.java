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

public final class Braille implements PropertiesProvider
{
    private final org.luwrain.core.Braille braille;
    private PropertiesProvider.Listener listener = null;

    public Braille(org.luwrain.core.Braille braille)
    {
	NullCheck.notNull(braille, "braille");
	this.braille = braille;
    }

    @Override public String getExtObjName()
    {
	return this.getClass().getName();
    }

    @Override public String[] getPropertiesRegex()
    {
	return new String[]{"^luwrain \\.braille\\."};
    }

    @Override public Set<PropertiesProvider.Flags> getPropertyFlags(String propName)
    {
	return EnumSet.of(PropertiesProvider.Flags.PUBLIC);
    }

    @Override public String getProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	switch(propName)
	{
	case "luwrain.braille.active":
	    return braille.isActive()?"1":"0";
	case "luwrain.braille.driver":
	    return braille.getDriver();
	case "luwrain.braille.error":
	    return braille.getErrorMessage();
	case "luwrain.braille.displaywidth":
	    return "" + braille.getDisplayWidth();
	case "luwrain.braille.displayheight":
	    return "" + braille.getDisplayHeight();
	default:
	    return null;
	}
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

    @Override public void setListener(PropertiesProvider.Listener listener)
    {
	this.listener = listener;
    }
}
