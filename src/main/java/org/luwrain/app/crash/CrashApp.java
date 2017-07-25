/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

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

public class CrashApp implements Application, Actions
{
    static public final String STRINGS_NAME = "luwrain.crash";

    private Luwrain luwrain;
    private Strings strings;
    private SimpleArea area;
    private Application app;
    private Exception exception;

    public CrashApp(Application app, Exception exception)
    {
	NullCheck.notNull(app, "app");
	NullCheck.notNull(exception, "exception");
	this.app = app;
	this.exception = exception;
    }

    @Override public InitResult onLaunch(Luwrain luwrain)
    {
	final Object o = luwrain.i18n().getStrings(STRINGS_NAME);
	if (o == null || !(o instanceof Strings))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, STRINGS_NAME);
	strings = (Strings)o;
	this.luwrain = luwrain;
	createArea();
	return new InitResult();
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

    private void createArea()
    {
	final Actions actions = this;

	area = new SimpleArea(new DefaultControlEnvironment(luwrain), strings.appName()){
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    switch (event.getCode())
		    {
		    case CLOSE:
			actions.closeApp();
			return true;
		    }
		    return false;
		}
	    };

	area.beginLinesTrans();
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
	area.endLinesTrans();
    }

    @Override public AreaLayout getAreasToShow()
    {
	return new AreaLayout(area);
    }

    @Override public void closeApp()
    {
	luwrain.closeApp();
    }
}
