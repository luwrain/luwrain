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

package org.luwrain.app.crash;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

public class App implements Application
{
    public enum Type {
	APP_EXCEPT,
	AREA_EXCEPT,
	NETWORK_SERVICE_INACCESSIBLE,
    };

    private Luwrain luwrain = null;
    private Strings strings = null;
    private SimpleArea area = null;

    private final Type type;
    private final Application app;
    private final Throwable exception;

    public App(Type type, Application app, Throwable exception)
    {
	NullCheck.notNull(type, "type");
	this.type = type;
	switch(type)
	{
	case APP_EXCEPT:
	NullCheck.notNull(app, "app");
	NullCheck.notNull(exception, "exception");
	this.app = app;
	this.exception = exception;
	break;
	case NETWORK_SERVICE_INACCESSIBLE:
	    NullCheck.notNull(app, "app");
	    this.app = app;
	    this.exception = exception;
	    break;
	default:
	    throw new IllegalArgumentException("Unsupported type: " + type.toString());
	}
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
	case NETWORK_SERVICE_INACCESSIBLE:
	    return "Сетевой сервис недоступен";
	default:
	return strings.appName();
	}
    }

    private void createArea()
    {
	area = new SimpleArea(new DefaultControlContext(luwrain), strings.appName()){
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    switch (event.getCode())
		    {
		    case CLOSE:
			closeApp();
			return true;
		    }
		    return false;
		}
	    };

	area.beginLinesTrans();
	switch(type)
	{
	case APP_EXCEPT:
	    {
	final String[] msg = strings.introMessage();
	area.addLine("");
	for(String s: msg)
	    area.addLine(s);
	area.addLine("");
	area.addLine(strings.app(app.getClass().getName()));
	area.addLine("");
	area.addLine(strings.stackTrace());
	final StringWriter sw = new StringWriter();
	final PrintWriter pw = new PrintWriter(sw);
	exception.printStackTrace(pw);
	pw.flush();
	sw.flush();
	final String[] trace = sw.toString().split("\n", -1);
	for(String s: trace)
	area.addLine(s);
	break;
	    }
	case NETWORK_SERVICE_INACCESSIBLE:
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
