/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.app.preview;

//TODO:Refresh;

import java.io.File;
import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class PreviewArea extends NavigateArea
{
    private StringConstructor stringConstructor;
    private PreviewActions actions;
    private Filter filter;

    public PreviewArea(StringConstructor stringConstructor, PreviewActions actions)
    {
	this.stringConstructor = stringConstructor;
	this.actions = actions;
    }

    public void setFilter(Filter filter)
    {
	if (filter == null)
	    return;
	this.filter = filter;
	setHotPoint(0, 0);
	Dispatcher.onAreaNewContent(this);
	Dispatcher.onAreaNewName(this);
    }

    public int getLineCount()
    {
	if (filter == null)
	    return 1;
	final int count = filter.getLineCount();
	return count >= 1?count:0;
    }

    public String getLine(int index)
    {
	if (filter == null)
	    return "";
	final String value = filter.getLine(index);
	return value != null?value:"";
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	switch(event.getCode())
	{
	case EnvironmentEvent.CLOSE:
	    actions.closePreview();
	    return true;
	case EnvironmentEvent.INTRODUCE:
	    if (filter != null)
		Speech.say(stringConstructor.appName() + " " + getFileName()); else
		Speech.say(stringConstructor.appName());
	    return true;
	default:
	    return false;
	}
    }

    public String getName()
    {
	if (filter == null)
	    return stringConstructor.appName();
	return getFileName();
    }

    private String getFileName()
    {
	if (filter == null)
	    return "";
	File f = new File(filter.getFileName());
	return f.getName();
    }
}
