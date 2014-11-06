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

package org.luwrain.app.diary;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.registry.Registry;
import org.luwrain.controls.*;
import org.luwrain.pim.*;

public class DiaryApp implements Application, Actions
{
    private Luwrain luwrain;
    private StringConstructor stringConstructor;

    private ListModel timedAreaModel, nonTimedAreaModel;
    private ListArea nonTimedArea, timedArea;
    private CalendarArea calendarArea;

    public DiaryApp()
    {
    }

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	Object o = Langs.requestStringConstructor("diary");
	if (o == null || !(o instanceof StringConstructor))
	    return false;
	stringConstructor = (StringConstructor)o;
	return false;
    }

    private void createAreas()
    {
	final Actions a = this;
	final StringConstructor s = stringConstructor;

	nonTimedArea = new ListArea(new DefaultControlEnvironment(luwrain),
				    nonTimedAreaModel,
				    stringConstructor.nonTimedAreaName(),
				    null,
				    null,
				    1) {
		private StringConstructor stringConstructor = s;
		private Actions actions = a;
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    if (event.isCommand() && !event.isModified())
			switch(event.getCommand())
			{
			case KeyboardEvent.TAB:
			    actions.gotoTimedArea();
			    return true;
			case KeyboardEvent.DELETE:
			    return actions.deleteDiaryEntry(getSelectedObject());
			}
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    switch(event.getCode())
		    {
		    case EnvironmentEvent.CLOSE:
			actions.close();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
		@Override public boolean onClick(ListModel model, int index, Object item)
		{
		    return actions.editDiaryEntry(item);
		}
	    };

	timedArea = new ListArea(new DefaultControlEnvironment(luwrain),
				 timedAreaModel,
				 stringConstructor.timedAreaName(),
				 null,
				 null, 1) {
		private StringConstructor stringConstructor = s;
		private Actions actions = a;
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    if (event.isCommand() && !event.isModified())
			switch(event.getCommand())
			{
			case KeyboardEvent.TAB:
			    actions.gotoCalendar();
			    return true;
			case KeyboardEvent.DELETE:
			    return actions.deleteDiaryEntry(getSelectedObject());
			}
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    switch(event.getCode())
		    {
		    case EnvironmentEvent.CLOSE:
			actions.close();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
		@Override public boolean onClick(ListModel model, int index, Object item)
		{
		    return actions.editDiaryEntry(item);
		}
	    };
    }

    @Override public AreaLayout getAreasToShow()
    {
	//FIXME:	return new AreaLayout(area);
	return null;
    }

    @Override public void close()
    {
	luwrain.closeApp();
    }

    public boolean editDiaryEntry(Object entry)
    {
	return false;
    }

    public boolean deleteDiaryEntry(Object entry)
    {
	//FIXME:
	return false;
    }

    public void gotoTimedArea()
    {
	//FIXME:
    }

    public void gotoNonTimedArea()
    {
	//FIXME:
    }

    public void gotoCalendar()
    {
	//FIXME:
    }
}
