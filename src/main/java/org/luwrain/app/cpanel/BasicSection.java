/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.app.cpanel;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.cpanel.*;

public class BasicSection implements Section
{
    private String title;
    private LinkedList<Section> subsections = new LinkedList<Section>();

    public BasicSection(String title)
    {
	this.title = title;
	if (title == null)
	    throw new NullPointerException("title may not be null");
    }

    public void addSubsection(Section section)
    {
	if (section == null)
	    throw new NullPointerException("section may not be null");
	subsections.add(section);
    }

    @Override public int getDesiredRoot()
    {
	return BasicSections.ROOT;
    }

    @Override public Section[] getChildSections()
    {
	return subsections.toArray(new Section[subsections.size()]);
    }

    @Override public Area getSectionArea(Environment environment)
    {
	return null;
    }

    String getSectionName()
    {
	return "Section";
    }

    @Override public boolean canCloseSection(Environment environment)
    {
	return true;
    }

    @Override public boolean onTreeInsert(Environment environment)
    {
	return false;
    }

    @Override public boolean onTreeDelete(Environment environment)
    {
	return false;
    }



    @Override public String toString()
    {
	return title;
    }

    @Override public boolean isSectionEnabled()
    {
	return true;
    }
}
