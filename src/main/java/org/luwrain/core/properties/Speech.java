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
import org.luwrain.speech.*;

public final class Speech implements PropertiesProvider
{
    private final org.luwrain.core.Speech speech;
    private PropertiesProvider.Listener listener = null;

    public Speech(org.luwrain.core.Speech speech)
    {
	NullCheck.notNull(speech, "speech");
	this.speech = speech;
    }

    @Override public String getExtObjName()
    {
	return this.getClass().getName();
    }

    @Override public String[] getPropertiesRegex()
    {
	return new String[]{"^luwrain \\.speech\\."};
    }

    @Override public Set<PropertiesProvider.Flags> getPropertyFlags(String propName)
    {
	return EnumSet.of(PropertiesProvider.Flags.PUBLIC);
    }

    @Override public String getProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	if (!propName.startsWith("luwrain.speech.channel."))
	    return null;
	//FIXME:
	return null;
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
}
