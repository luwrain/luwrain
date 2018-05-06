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

import jdk.nashorn.api.scripting.*;


import org.luwrain.core.*;


public final class SimpleCentered implements Application
{
	private final String name;
	private final ScriptObjectMirror jsObj;

    private Luwrain luwrain = null;

public SimpleCentered(String name, ScriptObjectMirror jsObj)
	{
	    NullCheck.notEmpty(name, "name");
	    NullCheck.notNull(jsObj, "jsObj");
	    this.name = name;
	    this.jsObj = jsObj;
	}

        @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	return new InitResult();
    }



    @Override public AreaLayout getAreaLayout()
    {
	return null;//FIXME:
    }

        @Override public String getAppName()
    {
	return this.name;
    }
    

            @Override public void closeApp()
    {
	luwrain.closeApp();
    }
    


    /*
	private void readUpdatedState()
	{
	    if (jsObj.get("state") == null && (jsObj.get("state") instanceof JSObject))
	    {
		final String value = jsObj.get("state").toString();
		if (value != null && !state.equals(value))
		{
		    this.state = value;
		    listener.onSingleLineStateChange(this);
		}
	    }
	    if (jsObj.get("multilineState") != null && (jsObj.get("multilineState") instanceof JSObject))
	    {
		final List<String> value = getStringArray((JSObject)jsObj.get("multilineState"));
		if (value != null)
		{
		    final String[] v = value.toArray(new String[value.size()]);
		    if (!theSameMultilineState(v))
		    {
			this.multilineState = v;
					    listener.onMultilineStateChange(this);
		    }
		}
	    }
	}

	private boolean theSameMultilineState(String[] value)
	{
	    NullCheck.notNullItems(value, "value");
	    if (value.length != multilineState.length)
		return  false;
	    for(int i = 0;i < value.length;++i)
		if (!value[i].equals(multilineState[i]))
		    return false;
	    return true;
	}
    */
}
