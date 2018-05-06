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

package org.luwrain.core.script.app;

import java.util.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;

public final class Simple implements Application
{
	private final String name;
	private final ScriptObjectMirror jsObj;

    private Luwrain luwrain = null;
    private NavigationArea area = null;
    private String[] lines = new String[0];

public Simple(String name, ScriptObjectMirror jsObj)
	{
	    NullCheck.notEmpty(name, "name");
	    NullCheck.notNull(jsObj, "jsObj");
	    this.name = name;
	    this.jsObj = jsObj;
	}

        @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	createArea();
	return new InitResult();
    }

    private void createArea()
    {
	this.area = new NavigationArea(new DefaultControlEnvironment(luwrain)){
		@Override public String getAreaName()
		{
		    return name;
		}
		@Override public int getLineCount()
		{
		    return lines.length > 0?lines.length:1;
		}
		@Override public String getLine(int index)
		{
		    if (index < 0)
			throw new IllegalArgumentException("index (" + index + ") may not be negative");
		    return index < lines.length?lines[index]:"";
		}
	    };
    }

@Override public AreaLayout getAreaLayout()
{
    return new AreaLayout(area);
    }

        @Override public String getAppName()
    {
	return this.name;
    }

            @Override public void closeApp()
    {
	luwrain.closeApp();
    }

private void updateLines()
{
    if (jsObj.get("lines") == null && (jsObj.get("lines") instanceof JSObject))
    {
	final List<String> value = org.luwrain.core.script.Utils.getStringArray((JSObject)jsObj.get("lines"));
	if (value != null)
	{
	    this.lines = value.toArray(new String[value.size()]);
	}
    }
}

private boolean theSameLines(String[] value)
{
    NullCheck.notNullItems(value, "value");
    if (value.length != lines.length)
	return  false;
    for(int i = 0;i < value.length;++i)
	if (!value[i].equals(lines[i]))
	    return false;
    return true;
}
}
