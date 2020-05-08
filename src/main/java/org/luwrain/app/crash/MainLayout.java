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

package org.luwrain.app.crash;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.template.*;

final class MainLayout extends LayoutBase
{
    private final App app;
    private final SimpleArea simpleArea;

    MainLayout(App app)
    {
	NullCheck.notNull(app, "app");
	this.app = app;
	this.simpleArea = new SimpleArea(new DefaultControlContext(app.getLuwrain()), app.getStrings().appName()){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onInputEvent(this, event))
			return true;
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onSystemEvent(this, event))
			return true;
		    return super.onSystemEvent(event);
		}
		@Override public boolean onAreaQuery(AreaQuery query)
		{
		    NullCheck.notNull(query, "query");
		    if (app.onAreaQuery(this, query))
			return true;
		    return super.onAreaQuery(query);
		}
		@Override public void announceLine(int index, String line)
		{
		    NullCheck.notNull(line, "line");
		    defaultLineAnnouncement(context, index, app.getLuwrain().getSpeakableText(line, Luwrain.SpeakableTextType.PROGRAMMING));
		}
	    };
	fillText();
    }

    private void fillText()
    {
	simpleArea.beginLinesTrans();
	switch(app.type)
	{
	case EXCEPTION:
	    {
		final String[] msg = app.getStrings().introMessage();
		simpleArea.addLine("");
		for(String s: msg)
		    simpleArea.addLine(s);
		simpleArea.addLine("");
		if (app.srcApp != null)
		    simpleArea.addLine(app.getStrings().app(app.srcApp.getClass().getName()));
		if (app.srcArea != null)
		    simpleArea.addLine(app.getStrings().area(app.srcArea.getClass().getName()));
		if (app.srcApp != null || app.srcArea != null)
		    simpleArea.addLine("");
		simpleArea.addLine(app.getStrings().stackTrace());
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw);
		app.ex.printStackTrace(pw);
		pw.flush();
		sw.flush();
		final String[] trace = sw.toString().split("\n", -1);
		for(String s: trace)
		    simpleArea.addLine(s);
		break;
	    }
	case INACCESSIBLE_NETWORK_SERVICE:
	    {
		simpleArea.addLine("Сетевой сервис недоступен");
		break;
	    }
	}
	simpleArea.endLinesTrans();
    }

    AreaLayout getLayout()
    {
    	return new AreaLayout(simpleArea);
    }
}
