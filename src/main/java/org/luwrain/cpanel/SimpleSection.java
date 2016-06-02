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

package org.luwrain.cpanel;

import java.util.*;

import org.luwrain.core.*;

public class SimpleSection implements Section
{
    public interface AreaFactory
    {
	SectionArea newSectionArea(ControlPanel controlPanel);
    }

    protected Element element;
    protected AreaFactory areaFactory = null;
    protected String name;
    protected Set<Section.Flags> flags = EnumSet.noneOf(Section.Flags.class);

    private SectionArea area = null;

    public SimpleSection(Element element, String name)
    {
	NullCheck.notNull(element, "element");
	NullCheck.notNull(name, "name");
	this.element = element;
	this.name = name;
    }

    public SimpleSection(Element element, String name,
			 AreaFactory areaFactory)
    {
	NullCheck.notNull(element, "element");
	NullCheck.notNull(name, "name");
	this.element = element;
	this.name = name;
	this.areaFactory = areaFactory;
    }

    public SimpleSection(Element element, String name,
			 AreaFactory areaFactory, Set<Section.Flags> flags)
    {
	NullCheck.notNull(element, "element");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(flags, "flags");
	this.element = element;
	this.name = name;
	this.areaFactory = areaFactory;
	this.flags = flags;
    }

    public void setAreaFactory(AreaFactory areaFactory)
    {
	this.areaFactory = areaFactory;
    }

    public void setSectionFlags(Set<Section.Flags> flags)
    {
	NullCheck.notNull(flags, "flags");
	this.flags = flags;
    }

    @Override public SectionArea getSectionArea(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	if (area != null)
	    return area;
	if (areaFactory == null)
	    return null;
	area = areaFactory.newSectionArea(controlPanel);
	return area;
    }

    @Override public Element getElement()
    {
	return element;
    }

    @Override public boolean canCloseSection(ControlPanel controlPanel)
    {
	return true;
    }

    @Override public boolean onTreeInsert(ControlPanel controlPanel)
    {
	return false;
    }

    @Override public boolean onTreeDelete(ControlPanel controlPanel)
    {
	return false;
    }

    @Override public boolean isSectionEnabled()
    {
	return true;
    }

    @Override public Set<Flags> getSectionFlags()
    {
	return flags;
    }

    @Override public String toString()
    {
	return name;
    }
}
