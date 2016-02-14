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
import org.luwrain.controls.*;
import org.luwrain.util.*;

public class SimpleNavigateSection extends EmptySection
{
    static abstract public class Area extends NavigateArea
    {
	protected Environment environment;
	protected String name;

	protected Area(Environment environment , String name)
	{
	    super(new DefaultControlEnvironment(environment.getLuwrain()));
	    this.environment = environment;
	    this.name = name;
	    NullCheck.notNull(environment, "environment");
	    NullCheck.notNull(name, "name");
	}

	@Override public boolean onKeyboardEvent(KeyboardEvent event)
	{
	    NullCheck.notNull(event, "event");
	    if (event.isCommand() && !event.isModified())
		switch(event.getCommand())
		{
		case TAB:
		    environment.gotoSectionsTree ();
	    return true;
		}
	return super.onKeyboardEvent(event);
	}

	@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
	{
	    NullCheck.notNull(event, "event");
	    switch(event.getCode())
	    {
	    case CLOSE:
		environment.close();
		return true;
	    default:
		return super.onEnvironmentEvent(event);
	    }
	}

	@Override public String getAreaName()
	{
	    return name;
	}
    }

public interface Loader
{
    org.luwrain.cpanel.SimpleNavigateSection.Area createArea(Environment environment);
}

private Area area = null;
    private String name;
    private int desiredRoot;
    private Loader loader;

    public SimpleNavigateSection(String name, int desiredRoot, Loader loader)
    {
	this.name = name;
	this.desiredRoot = desiredRoot;
	this.loader = loader;
	NullCheck.notNull(name, "name");
	NullCheck.notNull(loader, "loader");
}

    @Override public int getDesiredRoot()
    {
	return desiredRoot;
    }

    @Override public Area getSectionArea(Environment environment)
    {
	if (area == null)
	    refreshArea(environment);
	return area;
    }

    @Override public void refreshArea(Environment environment)
    {
	area = loader.createArea(environment);
    }

    @Override public String toString()
    {
	return name;
    }

    /*
    @Override public boolean canCloseSection(Environment environment)
    {
	if (area == null)
	    return true;
	if (!save())
	    environment.getLuwrain().message("Во время сохранения сделанных изменений произошла непредвиденная ошибка", Luwrain.MESSAGE_ERROR);//FIXME:
	return true;
    }

    private boolean save()
    {
	return true;
    }
*/
}
