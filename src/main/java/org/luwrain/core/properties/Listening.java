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

package org.luwrain.core.properties;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.player.*;

public final class Listening implements PropertiesProvider
{
    static private final String PROP_NAME = "luwrain.area.listening";

    private PropertiesProvider.Listener listener = null;
    private boolean status = false;

    @Override public String getExtObjName()
    {
	return this.getClass().getName();
    }

    @Override public String[] getPropertiesRegex()
    {
	return new String[0];
    }

    @Override public Set<PropertiesProvider.Flags> getPropertyFlags(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	    if (propName.equals(PROP_NAME))
	    return EnumSet.of(PropertiesProvider.Flags.PUBLIC);
	return null;
    }

    @Override public String getProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	switch(propName)
	{
	case PROP_NAME:
	PROP_TRACK_INDEX:
	    return status?"true":"false";
	    	default:
	    	    return null;
	}
    }

    @Override public boolean setProperty(String propName, String value)
    {
	NullCheck.notEmpty(propName, "propName");
	NullCheck.notNull(value, "value");
	return false;
    }

    @Override public void setListener(PropertiesProvider.Listener listener)
    {
	this.listener = listener;
    }

    public void setStatus(boolean status)
    {
	if (this.status == status)
	    return;
	this.status = status;
	if (this.listener != null)
	    this.listener.onNewPropertyValue(PROP_NAME, getProperty(PROP_NAME));
		    Log.debug("proba", "status " + status);
    }

    public boolean getStatus()
    {
	return this.status;
    }
}
