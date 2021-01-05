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

package org.luwrain.core.script.app;

import java.util.*;
import java.io.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;
import org.luwrain.util.*;

public final class SimpleCentered implements Application
{
    private final String name;
    private final File dataDir;
    private final ScriptObjectMirror jsObj;

    private Luwrain luwrain = null;
    private CenteredArea area = null;
    private String bkgSound = "";

    public SimpleCentered(String name, File dataDir, ScriptObjectMirror jsObj)
    {
	NullCheck.notEmpty(name, "name");
	NullCheck.notNull(dataDir, "dataDir");
	NullCheck.notNull(jsObj, "jsObj");
	this.name = name;
	this.dataDir = dataDir;
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
	this.area = new CenteredArea(new DefaultControlContext(luwrain), name){
		@Override public boolean onInputEvent(InputEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (SimpleCentered.this.handleInputEvent(event))
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
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != SystemEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    		    if (SimpleCentered.this.handleSystemEvent(event))
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
		@Override public boolean onAreaQuery(AreaQuery query)
		{
		    NullCheck.notNull(query, "query");
		    switch(query.getQueryCode())
		    {
		    case AreaQuery.BACKGROUND_SOUND:
			if (bkgSound.isEmpty())
			    return false;
			{
			    final File f = new File(dataDir, bkgSound);
			    ((BackgroundSoundQuery)query).answer(new BackgroundSoundQuery.Answer(Urls.toUrl(f).toString()));
			    return true;
			}
			//return false;
		    default:
			return super.onAreaQuery(query);
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
bkgSound = requestBkgSound();
    }

    private boolean handleInputEvent(InputEvent event)
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
	    updateBkgSound();
	    return true;
	}
	return false;
    }

        private boolean handleSystemEvent(SystemEvent event)
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
	    updateBkgSound();
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
	if (value == null)
	    return -1;
	return value.intValue() >= 0?value.intValue():-1;
    }

	        private int requestHotPointY()
    {
		final Object obj = org.luwrain.script.ScriptUtils.getMember(jsObj, "hotPointY");
	final Integer value = org.luwrain.script.ScriptUtils.getIntegerValue(obj);
	if (value == null)
	    return -1;
	return value.intValue() >= 0?value.intValue():-1;
    }

        private String requestBkgSound()
    {
	    	    if (jsObj.get("bkgSound") == null || !(jsObj.get("bkgSound") instanceof java.lang.String))
		return "";
		    final String value = (String)jsObj.get("bkgSound");
		    return value != null?value:"";
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
}
    }

        private void updateBkgSound()
    {
	final String value = requestBkgSound();
	if (!bkgSound.equals(value))
{
    bkgSound = value;
    luwrain.onAreaNewBackgroundSound(area);
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
