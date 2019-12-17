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

package org.luwrain.app.crash;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

public final class App implements Application
{
    public enum Type {
	EXCEPTION,
	INIT_RESULT,
	INACCESSIBLE_NETWORK_SERVICE,
    };

    private Luwrain luwrain = null;
    private Strings strings = null;
    private SimpleArea area = null;

    private final Type type;
    private final Application srcApp;
    private final Area srcArea;
    private final Throwable ex;
    private final InitResult initRes;

    public App(Throwable ex, Application srcApp, Area srcArea)
    {
	NullCheck.notNull(ex, "ex");
	this.type = Type.EXCEPTION;
	this.ex = ex;
	this.srcApp = srcApp;
	this.srcArea = srcArea;
	this.initRes = null;
    }

    public App(InitResult initRes)
    {
	NullCheck.notNull(initRes, "initRes");
	this.type = Type.INIT_RESULT;
	this.initRes = null;
	this.srcApp = null;
	this.srcArea = null;
	this.ex = null;
    }

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, Strings.NAME);
	this.strings = (Strings)o;
	this.luwrain = luwrain;
	createArea();
	return new InitResult();
    }

    @Override public String getAppName()
    {
	switch(type)
	{
	case INACCESSIBLE_NETWORK_SERVICE:
	    return "Сетевой сервис недоступен";//FIXME:
	default:
	return strings.appName();
	}
    }

    private void createArea()
    {
	this.area = new SimpleArea(new DefaultControlContext(luwrain), strings.appName()){
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch (event.getCode())
		    {
		    case CLOSE:
			closeApp();
			return true;
		    }
		    return super.onSystemEvent(event);
		}
		@Override public void announceLine(int index, String line)
		{
		    NullCheck.notNull(line, "line");
		    defaultLineAnnouncement(context, index, luwrain.getSpeakableText(line, Luwrain.SpeakableTextType.PROGRAMMING));
		}
	    };

	area.beginLinesTrans();
	switch(type)
	{
	case EXCEPTION:
	    {
	final String[] msg = strings.introMessage();
	area.addLine("");
	for(String s: msg)
	    area.addLine(s);
	area.addLine("");
	if (srcApp != null)
	    area.addLine(strings.app(srcApp.getClass().getName()));
	if (srcArea != null)
	    	    area.addLine(strings.area(srcArea.getClass().getName()));
	if (srcApp != null || srcArea != null)
	area.addLine("");
	area.addLine(strings.stackTrace());
	final StringWriter sw = new StringWriter();
	final PrintWriter pw = new PrintWriter(sw);
	ex.printStackTrace(pw);
	pw.flush();
	sw.flush();
	final String[] trace = sw.toString().split("\n", -1);
	for(String s: trace)
	area.addLine(s);
	break;
	    }
	case INACCESSIBLE_NETWORK_SERVICE:
	    {
		area.addLine("Сетевой сервис недоступен");
		break;
	    }
	}
	area.endLinesTrans();
    }

    @Override public AreaLayout getAreaLayout()
    {
	return new AreaLayout(area);
    }

    @Override public void closeApp()
    {
	luwrain.closeApp();
    }
}
