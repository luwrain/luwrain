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

public class SimpleListSection extends EmptySection
{
public interface Loader
{
    //params.environment is always prepared
    void setListParams(Luwrain luwrain, ListParams params);
}

    static private class Area extends ListArea
    {
	private Environment environment;

	Area(Environment environment , ListParams params)
	{
	    super(params);
	    this.environment = environment;
	    NullCheck.notNull(environment, "environment");
	}

	@Override public boolean onKeyboardEvent(KeyboardEvent event)
	{
	    NullCheck.notNull(event, "event");
	    if (event.isCommand() && !event.isModified())
		switch(event.getCommand())
		{
		case KeyboardEvent.TAB:
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
	    case EnvironmentEvent.CLOSE:
		environment.close();
		return true;
	    default:
		return super.onEnvironmentEvent(event);
	    }
	}

    }

private Area area = null;
    private String name;
    private int desiredRoot;
    private Loader loader;
    private final ListParams params = new ListParams();

    public SimpleListSection(String name, int desiredRoot, Loader loader)
    {
	this.name = name;
	this.desiredRoot = desiredRoot;
	this.loader = loader;
	NullCheck.notNull(name, "name");
	NullCheck.notNull(params, "params");
	NullCheck.notNull(loader, "loader");
    this.params.name = name;
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
	params.environment = new DefaultControlEnvironment(environment.getLuwrain());
	loader.setListParams(environment.getLuwrain(), params);
	    area = new Area(environment, params);
    }

    @Override public String toString()
    {
	return name;
    }

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

    /*
    private Area  createArea(Environment environment)
    {
	final Area res = 
}
    */
}
