/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.app.calendar;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.CalendarArea;

public class CalendarApp implements Application, Actions
{
    private Luwrain luwrain;
    private StringConstructor stringConstructor;
    private CalendarArea area;

    public CalendarApp()
    {
    }

    public boolean onLaunch(Luwrain luwrain)
    {
	Object o = Langs.requestStringConstructor("calendar");
	if (o == null)
	    return false;
	this.luwrain = luwrain;
	stringConstructor = (StringConstructor)o;
	createArea();
	return true;
    }

    private void createArea()
    {
	final Actions a = this;
	area = new CalendarArea(new DefaultControlEnvironment(luwrain),
				new GregorianCalendar()){
		private Actions actions = a;
		public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    switch(event.getCode())
		    {
		    case EnvironmentEvent.CLOSE:
			actions.close();
			return true;
		    case EnvironmentEvent.INTRODUCE:
			Speech.say(stringConstructor.introduction() + " " + getName());
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
	    };
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(area);
    }

    public void close()
    {
	luwrain.closeApp();
    }
}
