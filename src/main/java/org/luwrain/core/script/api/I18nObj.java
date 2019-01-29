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

import jdk.nashorn.api.scripting.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

final class I18nObj extends AbstractJSObject
{
    private final Luwrain luwrain;

    I18nObj(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	switch(name)
	{
	case "static":
	    return new AbstractJSObject(){
		    @Override public Object getMember(String name)
		{
		    NullCheck.notNull(name, "name");
		    if (name.isEmpty())
			return super.getMember(name);
		    if (name.length() >= 2)
			return luwrain.i18n().getStaticStr(Character.toUpperCase(name.charAt(0)) + name.substring(1));
		    return luwrain.i18n().getStaticStr(name);
		}
	    };
	default:
	    return super.getMember(name);
	}
    }
}
