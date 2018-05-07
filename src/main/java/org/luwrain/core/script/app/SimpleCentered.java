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
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public final class SimpleCentered implements Application
{
    private final String name;
    private final ScriptObjectMirror jsObj;

    private Luwrain luwrain = null;
    private CenteredArea area = null;

    public SimpleCentered(String name, ScriptObjectMirror jsObj)
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
	this.area = new CenteredArea(new DefaultControlEnvironment(luwrain), name){
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (super.onKeyboardEvent(event))
			return true;
		    return SimpleCentered.this.handleKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onEnvironmentEvent(event);
		    switch(event.getCode())
		    {
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
	    };
	final String[] lines = requestLines();
if (lines != null)
    this.area.setLines(lines);
	final int hotPointX = requestHotPointX();
	final int hotPointY = requestHotPointY();
	area.setLocalHotPointX(hotPointX >= 0?hotPointX:0);
		area.setLocalHotPointY(hotPointY >= 0?hotPointY:0);
    }

    private boolean handleKeyboardEvent(KeyboardEvent event)
    {
	if (jsObj.get("onInputEvent") == null || !(jsObj.get("onInputEvent") instanceof JSObject))
	    return false;
	final JSObject func = (JSObject)jsObj.get("onInputEvent");
	final Object res = func.call(jsObj, new Object[]{makeInputEventName(event)});
	if (res != null && (res instanceof java.lang.Boolean))
	    if (((java.lang.Boolean)res).booleanValue())
	{
	    updateLines();
	    updateHotPoint();
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
	    	    if (jsObj.get("hotPointX") == null || !(jsObj.get("hotPointX") instanceof java.lang.Number))
		return -1;
		    final java.lang.Number value = (java.lang.Number)jsObj.get("hotPointX");
	    if (value.intValue() < 0)
		return -1;
	    return value.intValue();
    }

	        private int requestHotPointY()
    {
	    if (jsObj.get("hotPointY") == null || !(jsObj.get("hotPointY") instanceof java.lang.Number))
		return -1;
	    final java.lang.Number value = (java.lang.Number)jsObj.get("hotPointY");
	    if (value.intValue() < 0)
		return -1;
	    return value.intValue();
    }

    private void updateLines()
{
    final String[] newLines = requestLines();
    if (!theSameLines(newLines))
	area.setLines(newLines);
}

    private void updateHotPoint()
    {
int newX = requestHotPointX();
int newY = requestHotPointY();
if (newX < 0)
    newX = area.getLocalHotPointX();
if (newY < 0)
    newY = area.getLocalHotPointY();
if (newX != area.getLocalHotPointX() || newY != area.getLocalHotPointY())
{
    area.setLocalHotPointX(newX);
    area.setLocalHotPointY(newY);
    luwrain.message("" + area.getHotPointY());
}
    }

private boolean theSameLines(String[] value)
{
    NullCheck.notNullItems(value, "value");
    final String[] lines = area.getLines();
    if (value.length != lines.length)
	return  false;
    for(int i = 0;i < value.length;++i)
	if (!value[i].equals(lines[i]))
	    return false;
    return true;
}

    private String makeInputEventName(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial())
	    return event.getSpecial().toString();
	return "" + event.getChar();
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
