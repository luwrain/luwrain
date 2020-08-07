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
import org.luwrain.app.base.*;

final class MainLayout extends LayoutBase
{
    private final App app;
    private final SimpleArea simpleArea;

    MainLayout(App app)
    {
	NullCheck.notNull(app, "app");
	this.app = app;
	this.simpleArea = new SimpleArea(new DefaultControlContext(app.getLuwrain()), app.getStrings().appName()){
		@Override public boolean onInputEvent(InputEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onInputEvent(this, event))
			return true;
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(SystemEvent event)
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
	if (app.ex instanceof InitResultException)
	{
	    final InitResultException ex = (InitResultException)app.ex;
	    if (ex.getInitResult().getType() == InitResult.Type.EXCEPTION)
	    {
		fillException(ex.getInitResult().getException());
		return;
	    }
	}
	if (app.ex instanceof CustomMessageException)
	{
	    final CustomMessageException c = (CustomMessageException)app.ex;
	    simpleArea.beginLinesTrans();
	    final String[] message = c.getCustomMessage();
	    for(String s: message)
		simpleArea.addLine(s);
	    simpleArea.addLine("");
	    simpleArea.endLinesTrans();
	    return;
	}
	fillException(app.ex);
    }

    private void fillException(Throwable t)
    {
			simpleArea.beginLinesTrans();
	NullCheck.notNull(t, "t");
	if (t instanceof java.io.FileNotFoundException && t.getMessage() != null)
	{
	    simpleArea.beginLinesTrans();
	    simpleArea.addLine("");
	    simpleArea.addLine(app.getStrings().fileNotFound() + ": " + t.getMessage());
	}

	final String[] msg = app.getStrings().intro().split("\\n");
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
	t.printStackTrace(pw);
	pw.flush();
	sw.flush();
	final String[] trace = sw.toString().split("\n", -1);
	for(String s: trace)
	    simpleArea.addLine(s);
	simpleArea.endLinesTrans();
    }

    AreaLayout getLayout()
    {
    	return new AreaLayout(simpleArea);
    }
}
