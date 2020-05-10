/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

//LWR_API 1.0

package org.luwrain.script;

import java.util.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.core.*;

public class PropertiesHookObject extends EmptyHookObject
{
    private final Properties props;
    private final String propName;

    public PropertiesHookObject(Properties props, String propName)
    {
	NullCheck.notNull(props, "props");
	NullCheck.notNull(propName, "propName");
	this.props = props;
	this.propName = propName;
    }

    public PropertiesHookObject(Properties props)
    {
	this(props, "");
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	if (name.isEmpty())
	    return super.getMember(name);
	if (propName.isEmpty())
	    return new PropertiesHookObject(props, name);
	return new PropertiesHookObject(props, propName + "." + name);
    }

    @Override public void setMember(String name, Object value)
    {
	NullCheck.notNull(name, "name");
	if (name.isEmpty() || value == null)
	    return;
	final String text = value.toString();
	if (text == null)
	    return;
	if (!propName.isEmpty())
	props.setProperty(propName + "." + name, text); else
	    	props.setProperty(name, text);
    }

    @Override public Object getDefaultValue(Class hint)
    {
	final String res = props.getProperty(propName);
	return res != null?res:"";
    }

    @Override public String toString()
    {
	return getDefaultValue(String.class).toString();
    }

    public Properties getProperties()
    {
	return props;
    }
}
