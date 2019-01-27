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

package org.luwrain.core.script.api;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.concurrent.*;
import javax.script.*;
import jdk.nashorn.api.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

final class Prop extends AbstractJSObject
{
    private final Luwrain luwrain;
    private final String propName;

    Prop(Luwrain luwrain, String propName)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(propName, "propName");
	this.luwrain = luwrain;
	this.propName = propName;
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	if (name.isEmpty())
	    return null;
	if (propName.isEmpty())
	    return new Prop(luwrain, name);
	return new Prop(luwrain, propName + "." + name);
    }

    @Override public String toString()
    {
	final String res = luwrain.getProperty(propName);
	return res != null?res:"";
    }
}
