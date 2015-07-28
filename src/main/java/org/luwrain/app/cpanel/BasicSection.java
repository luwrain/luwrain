/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.app.cpanel;

import org.luwrain.core.*;
import org.luwrain.cpanel.*;

class BasicSection implements Section
{
    private String title;
    private Section[] subsections;

    public BasicSection(String title, Section[] subsections)
    {
	this.title = title;
	this.subsections = subsections;
	if (title == null)
	    throw new NullPointerException("title may not be null");
	if (subsections == null)
	    throw new NullPointerException("subsections may not be null");
    }

    @Override public int getControlPanelSRoot()
    {
	return BasicSections.ROOT;
    }

    @Override public Section[] getControlPanelSubsections()
    {
	return subsections;
    }

    @Override public Area getControlPanelSectionArea()
    {
return null;
    }

    @Override public String getControlPanelSectionName()
    {
	return "Section";
    }

    @Override public boolean canCloseControlPanelSection()
    {
	return true;
    }

    @Override public String toString()
    {
	return title;
    }
}
