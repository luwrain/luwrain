/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.settings;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

class DateTime extends FormArea implements SectionArea
{
    private final ControlPanel controlPanel;
    private final Luwrain luwrain;
    private final Registry registry;
    private final Settings.DateTime sett;

    DateTime(ControlPanel controlPanel)
    {
	super(new DefaultControlContext(controlPanel.getCoreInterface()), "Дата и время");
	NullCheck.notNull(controlPanel, "controlPanel");
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
	this.registry = luwrain.getRegistry();
this.sett = Settings.createDateTime(luwrain.getRegistry());
fillForm();
    }

    private void fillForm()
    {
	addEdit("time-zone", "Часовой пояс:", sett.getTimeZone(""));
    }

    @Override public boolean saveSectionData()
    {
	final String value = getEnteredText("time-zone").trim();
	if (value.isEmpty())
	{
	    sett.setTimeZone("");
	    return true;
	}
	final TimeZone timeZone = TimeZone.getTimeZone(value); 
	if (timeZone == null)//Looks like never happens
	{
	    luwrain.message("Неизвестный часовой пояс: " + value.trim());
	    return false;
	}
	TimeZone.setDefault(timeZone);
	sett.setTimeZone(value.trim());
	return true;
    }

    @Override public boolean onInputEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onInputEvent(event))
	    return true;
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onSystemEvent(event))
	    return true;
	return super.onSystemEvent(event);
    }
}
