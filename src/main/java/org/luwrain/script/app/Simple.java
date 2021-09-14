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

package org.luwrain.script.app;

import java.util.*;
import java.io.*;

import org.graalvm.polyglot.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;

import static org.luwrain.util.Urls.toUrl;
import static org.luwrain.script2.ScriptUtils.*;

public final class Simple implements Application
{
    private final String name;
    private final File dataDir;
    private final Value jsApp;
    private final Object syncObj;

    private Luwrain luwrain = null;
    private CenteredArea area = null;
    private String bkgSound = "";

    public Simple(String name, File dataDir, Value jsApp, Object syncObj)
    {
	NullCheck.notEmpty(name, "name");
	NullCheck.notNull(dataDir, "dataDir");
	NullCheck.notNull(jsApp, "jsApp");
	NullCheck.notNull(syncObj, "syncObj");
	this.name = name;
	this.dataDir = dataDir;
	    this.jsApp = jsApp;
	    this.syncObj = syncObj;
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
		    if (handleInputEvent(event))
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
		    		    if (handleSystemEvent(event))
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
			    ((BackgroundSoundQuery)query).answer(new BackgroundSoundQuery.Answer(toUrl(f).toString()));
			    return true;
			}
		    default:
			return super.onAreaQuery(query);
		    }
		}
	    };
	synchronized(syncObj){
	    final String[] lines = requestLines();
	    if (lines != null)
		this.area.setLines(lines);
	    final int hotPointX = requestHotPointX();
	    final int hotPointY = requestHotPointY();
	    area.setLocalHotPointX(hotPointX >= 0?hotPointX:0);
	    area.setLocalHotPointY(hotPointY >= 0?hotPointY:0);
	    bkgSound = requestBkgSound();
	}
    }

    private boolean handleInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	synchronized(syncObj) {
	    final Object funcObj = getMember(jsApp, "onInputEvent");
	    if (funcObj == null || !(funcObj instanceof Value))
		return false;
	    final Value func = (Value)funcObj;
	    if (func.isNull() || !func.canExecute())
		return false;
	    final Object arg = createInputEvent(event);
	    final Object res = func.execute(new Object[]{arg});
	    if (res != null && (res instanceof Value))
	    {
		final Value resValue = (Value)res;
		if (!resValue.isNull() && resValue.isBoolean() && resValue.asBoolean())
		{
		    updateLines();
		    updateHotPoint();
		    updateBkgSound();
		    return true;
		}
	    }
	    return false;
	}
    }

    private boolean handleSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	synchronized(syncObj) {
	    final Object funcObj = getMember(jsApp, "onSystemEvent");
	    if (funcObj == null || !(funcObj instanceof Value))
		return false;
	    final Value func = (Value)funcObj;
	    if (func.isNull() || !func.canExecute())
		return false;
	    final Object arg = createSystemEvent(event);
	    final Object res = func.execute(new Object[]{arg});
	    if (res != null && (res instanceof Value))
	    {
		final Value resValue = (Value)res;
		if (!resValue.isNull() && resValue.isBoolean() && resValue.asBoolean())
		{
		    updateLines();
		    updateHotPoint();
		    updateBkgSound();
		    return true;
		}
	    }
	    return false;
	}
    }

    private String[] requestLines()
    {
final String[] res = asStringArray(getMember(jsApp, "lines"));
return res != null?res:new String[0];
	    }

    private int requestHotPointX()
    {
	return Math.max(0, asInt(getMember(jsApp, "hotPointX")));
    }

	        private int requestHotPointY() 
    {
	return Math.max(0, asInt(getMember(jsApp, "hotPointY")));
    }

        private String requestBkgSound()
    {
final String res = asString(getMember(jsApp, "bkgSound"));
return res != null?res:"";
    }

    private void updateLines()
{
    final String[] newLines = requestLines();
    if (!theSameLines(newLines))
	area.setLines(newLines);
}

    private void updateHotPoint()
    {
final int newX = requestHotPointX();
final int newY = requestHotPointY();
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
