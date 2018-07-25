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

//Reads  and saves

package org.luwrain.settings;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;
import org.luwrain.util.*;

class PersonalInfo extends FormArea implements SectionArea
{
    private final ControlPanel controlPanel;
    private final Luwrain luwrain;
    private final Registry registry;
    private final Settings.PersonalInfo sett;

    PersonalInfo(ControlPanel controlPanel)
    {
	super(new DefaultControlEnvironment(controlPanel.getCoreInterface()), controlPanel.getCoreInterface().i18n().getStaticStr("CpPersonalInfoSection"));
	NullCheck.notNull(controlPanel, "controlPanel");
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
	this.registry = luwrain.getRegistry();
this.sett = Settings.createPersonalInfo(luwrain.getRegistry());
fillForm();
    }

    private void fillForm()
    {
	addEdit("name", luwrain.i18n().getStaticStr("CpPersonalInfoFullName"), sett.getFullName(""), null, true);
	addEdit("address", luwrain.i18n().getStaticStr("CpPersonalInfoMailAddress"), sett.getDefaultMailAddress(""), null, true);
	activateMultilineEdit(luwrain.i18n().getStaticStr("CpPersonalInfoSignature"), sett.getSignature(""), true);
    }

    @Override public boolean saveSectionData()
    {
	final Luwrain luwrain = controlPanel.getCoreInterface();
	final Registry registry = luwrain.getRegistry();
	sett.setFullName(getEnteredText("name"));
	sett.setDefaultMailAddress(getEnteredText("address"));
	sett.setSignature(getMultilineEditText());
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
