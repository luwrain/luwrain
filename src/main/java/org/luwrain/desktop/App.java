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

package org.luwrain.desktop;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class App implements Application
{
    static public final String STRINGS_NAME = "luwrain.desktop";

    private Luwrain luwrain;
    private final Base base = new Base();
    private ListArea area;

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	this.luwrain = luwrain;
	if (!base.init(luwrain))
	    return false;
	createArea();
	return true;
    }

    @Override public String getAppName()
    {
	return luwrain.i18n().getStaticStr("Desktop");
    }

    private boolean onInsertRegion(int x, int y, RegionContent data)
    {
	if (!base.insert(x, y, data))
	    return false;
	area.refresh();
	return true;
    }

    private boolean onDeleteRegion(int x, int y)
    {
	if (!base.delete(x, y))
	    return false;
	area.refresh();
	return true;
    }

    private boolean onClick(int index, Object obj)
    {
	if (!base.onClick(index, obj))
	    return false;
	area.refresh();
	return  true;
    }

    private void createArea()
    {
	final Luwrain l = luwrain;
	final ListClickHandler handler = new ListClickHandler(){
		@Override public boolean onListClick(ListArea area, int index,
						     Object obj)
		{
		    return onClick(index, obj);
		}
	    };

	final ListArea.Params params = new ListArea.Params();
	params.environment = new DefaultControlEnvironment(luwrain);
	params.model = base.getModel();
	params.appearance = base.getAppearance();
	params.clickHandler = handler;
	params.name = luwrain.i18n().getStaticStr("Desktop");

	area = new ListArea(params) {
		      @Override public boolean onKeyboardEvent(KeyboardEvent event)
		      {
			  NullCheck.notNull(event, "event");
			  if (event.isSpecial() && !event.isModified())
			      switch(event.getSpecial())
			      {
			      case DELETE:
				  return onDeleteRegion(getHotPointX(), getHotPointY());
			      }
		    return super.onKeyboardEvent(event);
		}

		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    switch(event.getCode())
		    {
		    case CLOSE:
			l.silence();
			l.playSound(Sounds.NO_APPLICATIONS);
			luwrain.message(luwrain.i18n().getStaticStr("DesktopNoApplication"));
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}

    @Override public boolean insertRegion(int x, int y, RegionContent data)
		{
		    return onInsertRegion(x, y, data);
		}

		@Override public String getAreaName()
		{
		    return luwrain.i18n().getStaticStr("Desktop");
		}
	    };
    }

    @Override public AreaLayout getAreasToShow()
    {
	return new AreaLayout(area);
    }
}
