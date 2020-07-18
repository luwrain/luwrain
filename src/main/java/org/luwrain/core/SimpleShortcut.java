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

package org.luwrain.core;

public class SimpleShortcut implements Shortcut
{
    static private final String LOG_COMPONENT = Core.LOG_COMPONENT;
    
    protected final String shortcutName;
    protected final Class appClass;

    public SimpleShortcut(String shortcutName, Class appClass)
    {
	NullCheck.notEmpty(shortcutName, "shortcutName");
	NullCheck.notNull(appClass, "appClass");
	this.shortcutName = shortcutName;
	this.appClass = appClass;
    }

        @Override public String getExtObjName()
    {
	return shortcutName;
    }

    @Override public Application[] prepareApp(String[] args)
    {
	NullCheck.notNullItems(args, "args");
	try {
	    final Object o = appClass.newInstance();
	    if (o == null || !(o instanceof Application))
	    {Log.error(LOG_COMPONENT, "unable to create new instance of the class " + appClass.getName() + " for the shortcut '" + shortcutName + "': the result is null or is not an instance of org.luwrain.core.Application");
		return new Application[0];
	    }
	    return new Application[]{(Application)o};
	}
	catch(InstantiationException | IllegalAccessException e)
	{
	    Log.error(LOG_COMPONENT, "unable to create new instance of the class " + appClass.getName() + " for the shortcut '" + shortcutName + "':" + e.getClass().getName() + ":" + e.getMessage());
	    return new Application[0];
	}
    }
}
