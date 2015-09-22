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

package org.luwrain.desktop;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class App implements Application, Actions
{
    static public final String STRINGS_NAME = "luwrain.desktop";

    private Luwrain luwrain;
    private final TempStrings strings = new TempStrings();
    private final Base base = new Base();
    private ListArea area;

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	this.luwrain = luwrain;
	if (!base.init(luwrain, strings))
	    return false;
	createArea();
	return true;
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

    @Override public boolean onInsert(int x, int y, HeldData data)
    {
	if (!base.insert(x, y, data))
	    return false;
	area.refresh();
	return true;
    }

    public void ready(String lang, Object o)
    {
	if (o != null && (o instanceof Strings))
	    this.strings.setStrings((Strings)o);
	base.setReady(lang);
    }

    private void createArea()
    {
	final Luwrain l = luwrain;
	final Actions a = this;
	final Strings s = strings;

	final ListClickHandler handler = new ListClickHandler(){
		public Actions actions = a;
		@Override public boolean onListClick(ListArea area,
						     int index,
						     Object item)
		{
		    //FIXME:
		    return false;
		}
	    };

	final ListParams params = new ListParams();
	params.environment = new DefaultControlEnvironment(luwrain);
	params.model = base.getModel();
	params.appearance = base.getAppearance();
	params.clickHandler = handler;
	params.name = strings.appName();

	area = new ListArea(params) {
		      @Override public boolean onKeyboardEvent(KeyboardEvent event)
		      {
			  NullCheck.notNull(event, "event");
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    switch(event.getCode())
		    {
		    case EnvironmentEvent.CLOSE:
			l.silence();
			l.playSound(Sounds.NO_APPLICATIONS);
			l.message(s.noApplications());
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
    @Override public boolean insertRegion(int x, int y, HeldData data)
		{
		    return a.onInsert(x, y, data);
		}
		@Override public String getAreaName()
		{
		    return strings.appName();
		}
	    };
    }

    @Override public AreaLayout getAreasToShow()
    {
	return new AreaLayout(area);
    }
}
