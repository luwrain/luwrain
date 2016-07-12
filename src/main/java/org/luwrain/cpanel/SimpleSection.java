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
import org.luwrain.core.events.*;

public class SimpleSection implements Section
{
    public interface AreaFactory
    {
	SectionArea newSectionArea(ControlPanel controlPanel);
    }

public interface ActionHandler
{
    boolean onSectionActionEvent(Luwrain luwrain, Area area, EnvironmentEvent event);
}

    protected Element element;
    protected String name;
    protected AreaFactory areaFactory = null;
    protected ActionHandler actionHandler = null;
    protected Action[] actions = new Action[0];

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
			 AreaFactory areaFactory, Action[] actions, ActionHandler actionHandler)
    {
	NullCheck.notNull(element, "element");
	NullCheck.notNull(name, "name");
	NullCheck.notNullItems(actions, "actions");
	this.element = element;
	this.name = name;
	this.areaFactory = areaFactory;
	this.actions = actions;
	this.actionHandler = actionHandler;
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

    @Override public Action[] getSectionActions()
    {
	return actions;
    }

    @Override public boolean onSectionActionEvent(Luwrain luwrain, Area area, EnvironmentEvent event)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(area, "area");
	NullCheck.notNull(event, "event");
	if (actionHandler == null)
	    return false;
	return actionHandler.onSectionActionEvent(luwrain, area, event);
    }

    @Override public String toString()
    {
	return name;
    }
}
