/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of LUWRAIN.

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
import org.luwrain.popups.Popups;

public class App implements Application
{
    private Luwrain luwrain;
    private Base base = null;
    private ListArea area;

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	base = new Base(luwrain);
	createArea();
	return true;
    }

    //Runs by the core when language extensions loaded 
    public void ready()
    {
	base.load();
	luwrain.onAreaNewName(area);
    }

    private void createArea()
    {
	final ListArea.Params params = new ListArea.Params();
	params.environment = new DefaultControlEnvironment(luwrain);
	params.model = base.model;
	params.appearance = base.appearance;
	params.name = luwrain.i18n().getStaticStr("Desktop");

	area = new ListArea(params) {

		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{ 
			case DELETE:
			    /*
			    if (!Popups.confirmDefaultNo(luwrain, luwrain.i18n().getStaticStr("DesktopDeleteConfirmPopupName"), luwrain.i18n().getStaticStr("DesktopDeleteConfirmPopupText")))
				return true;
			    */
			    if (base.delete(getHotPointX(), getHotPointY()))
				refresh();
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
			luwrain.silence();
			luwrain.message(luwrain.i18n().getStaticStr("DesktopNoApplication"), Sounds.NO_APPLICATIONS);
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}

		@Override public boolean insertRegion(int x, int y, RegionContent data)
		{
		    if (!base.insert(x, y, data))
			return false;
		    refresh();
		    return true;
		}

		@Override public String getAreaName()
		{
		    return luwrain.i18n().getStaticStr("Desktop");
		}
	    };

	area.setListClickHandler((area, index, obj)->{
		if (!base.onClick(index, obj))
		    return false;
		area.refresh();
		return true;
	    });
    }

    @Override public String getAppName()
    {
	return luwrain.i18n().getStaticStr("Desktop");
    }

    @Override public AreaLayout getAreasToShow()
    {
	return new AreaLayout(area);
    }
}
