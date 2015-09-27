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

public class BasicSection extends EmptySection
{
    private String title;
    private final LinkedList<Section> subsections = new LinkedList<Section>();

    public BasicSection(String title)
    {
	this.title = title;
	NullCheck.notNull(title, "title");
    }

    public void addSubsection(Section section)
    {
	NullCheck.notNull(section, "section");
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

    @Override public String toString()
    {
	return title;
    }

    @Override public boolean equals(Object obj)
    {
	return this == obj;
    }

    public void clear()
    {
	subsections.clear();
    }
}
