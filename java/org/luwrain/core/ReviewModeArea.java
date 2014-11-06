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

package org.luwrain.core;

import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class ReviewModeArea extends NavigateArea
{
    private ControlEnvironment environment;
    private Area hiddenArea = null;

    public ReviewModeArea(ControlEnvironment environment)
    {
	super(environment);
    }

    public int getLineCount()
    {
	if (hiddenArea == null)
	    return 1;
	return hiddenArea.getLineCount();
    }

    public String getLine(int index)
    {
	if (hiddenArea == null)
	    return new String();
	return hiddenArea.getLine(index);
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	//FIXME:
	return false;
    }

    public String getName()
    {
	if (hiddenArea == null)
	    return "FIXME";
	return hiddenArea.getName();///FIXME:Additional info;
    }

    public boolean onHiddenAreaNewContent(Area area)
    {
	return false;
    }

    public Area getHiddenArea()
    {
	return null;
    }
}
