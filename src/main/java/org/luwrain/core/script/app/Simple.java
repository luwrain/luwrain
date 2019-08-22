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

package org.luwrain.core.script.app;

import java.util.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public final class Simple implements Application
{
    private final String name;
    private final ScriptObjectMirror jsObj;

    private Luwrain luwrain = null;
    private NavigationArea area = null;
    private String[] lines = null;

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
	    this.lines = requestLines();
	    if (this.lines == null)
		this.lines = new String[0];
	this.area = new NavigationArea(new DefaultControlContext(luwrain)){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (Simple.this.handleInputEvent(event))
			return true;
		    		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case ESCAPE:
			    luwrain.closeApp();
			    return true;
			}
				    		    if (super.onInputEvent(event))
			return true;
						    return false;
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    		    if (Simple.this.handleSystemEvent(event))
			return true;
		    switch(event.getCode())
		    {
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onSystemEvent(event);
		    }
		}
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
	final int hotPointX = requestHotPointX();
	final int hotPointY = requestHotPointY();
	area.setHotPoint(hotPointX >= 0?hotPointX:0, hotPointY >= 0?hotPointY:0);
    }

        private boolean handleInputEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	final Object funcObj = org.luwrain.script.ScriptUtils.getMember(jsObj, "onInputEvent");
	if (funcObj == null || !(funcObj instanceof JSObject))
	    return false;
	final JSObject func = (JSObject)funcObj;
		if (!org.luwrain.script.ScriptUtils.isValid(func) || !func.isFunction())
	    return false;
	final Object arg = org.luwrain.script.ScriptUtils.createInputEvent(event);
	final Object res = func.call(jsObj, new Object[]{arg});
	if (res != null && (res instanceof java.lang.Boolean))
	    if (((java.lang.Boolean)res).booleanValue())
	{
	    updateLines();
	    updateHotPoint();
	    //	    updateBkgSound();
	    return true;
	}
	return false;
    }

            private boolean handleSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	final Object funcObj = org.luwrain.script.ScriptUtils.getMember(jsObj, "onSystemEvent");
	if (funcObj == null || !(funcObj instanceof JSObject))
	    return false;
	final JSObject func = (JSObject)funcObj;
		if (!org.luwrain.script.ScriptUtils.isValid(func) || !func.isFunction())
	    return false;
	final Object arg = org.luwrain.script.ScriptUtils.createSystemEvent(event);
	final Object res = func.call(jsObj, new Object[]{arg});
	if (res != null && (res instanceof java.lang.Boolean))
	    if (((java.lang.Boolean)res).booleanValue())
	{
	    updateLines();
	    updateHotPoint();
	    //	    updateBkgSound();
	    return true;
	}
	return false;
    }


    private String[] requestLines()
    {
	    if (jsObj.get("lines") == null || !(jsObj.get("lines") instanceof JSObject))
		return null;
	    final List<String> value = org.luwrain.core.script.Utils.getStringArray((JSObject)jsObj.get("lines"));
	    if (value == null)
		return null;
	    return value.toArray(new String[value.size()]);
    }

        private int requestHotPointX()
    {
			final Object obj = org.luwrain.script.ScriptUtils.getMember(jsObj, "hotPointX");
	final Integer value = org.luwrain.script.ScriptUtils.getIntegerValue(obj);
	if (value == 0)
	    return -1;
	return value.intValue() >= 0?value.intValue():-1;
    }

	        private int requestHotPointY()
    {
			final Object obj = org.luwrain.script.ScriptUtils.getMember(jsObj, "hotPointY");
	final Integer value = org.luwrain.script.ScriptUtils.getIntegerValue(obj);
	if (value == 0)
	    return -1;
	return value.intValue() >= 0?value.intValue():-1;
    }

    private void updateLines()
{
    final String[] newLines = requestLines();
    if (!theSameLines(newLines))
    {
	this.lines = newLines;
	luwrain.onAreaNewContent(area);
    }
}

    private void updateHotPoint()
    {
int newX = requestHotPointX();
int newY = requestHotPointY();
if (newX < 0)
    newX = area.getHotPointX();
if (newY < 0)
    newY = area.getHotPointY();
if (newX != area.getHotPointX() || newY != area.getHotPointY())
    area.setHotPoint(newX, newY);
    }

private boolean theSameLines(String[] value)
{
    NullCheck.notNullItems(value, "value");
    if (value.length != this.lines.length)
	return  false;
    for(int i = 0;i < value.length;++i)
	if (!value[i].equals(this.lines[i]))
	    return false;
    return true;
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
}
