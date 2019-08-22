/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

import jdk.nashorn.api.scripting.*;
import java.util.function.*;

import org.luwrain.base.*;

import org.luwrain.core.*;

final class SpokenTextObj extends AbstractJSObject
{
    private final Luwrain luwrain;

    SpokenTextObj(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	switch(name)
	{
	case "natural":
	    return (Function)this::natural;
	case "programming":
	    return (Function)this::programming;
	default:
	    return super.getMember(name);
	}
    }

    private Object natural(Object arg)
    {
	final String text = org.luwrain.script.ScriptUtils.getStringValue(arg);
	if (text == null)
	    return null;
	return luwrain.getSpeakableText(text, Luwrain.SpeakableTextType.NATURAL);
    }

    private Object programming(Object arg)
    {
	final String text = org.luwrain.script.ScriptUtils.getStringValue(arg);
	if (text == null)
	    return null;
	return luwrain.getSpeakableText(text, Luwrain.SpeakableTextType.PROGRAMMING);
    }
    }
