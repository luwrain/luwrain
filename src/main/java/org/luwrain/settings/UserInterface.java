/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

//LWR_API 1.0

package org.luwrain.settings;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

final class UserInterface extends FormArea implements SectionArea
{
    private final ControlPanel controlPanel;
    private final Luwrain luwrain;
    private final Settings.UserInterface sett;

    UserInterface(ControlPanel controlPanel)
    {
	super(new DefaultControlContext(controlPanel.getCoreInterface()),
	      controlPanel.getCoreInterface().i18n().getStaticStr("CpUiGeneral"));
	NullCheck.notNull(controlPanel, "controlPanel");
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
	this.sett = Settings.createUserInterface(luwrain.getRegistry());
	fillForm();
    }

    private void fillForm()
    {
	addEdit("desktop-title", luwrain.i18n().getStaticStr("CpUiDesktopTitle"), sett.getDesktopTitle(""));
	addEdit("window-title", luwrain.i18n().getStaticStr("CpUiWindowTitle"), sett.getWindowTitle(""));
	addEdit("desktop-escape-command", luwrain.i18n().getStaticStr("CpUiDesktopEscapeCommand"), sett.getDesktopEscapeCommand(""));
    }

    @Override public boolean saveSectionData()
    {
	sett.setDesktopTitle(getEnteredText("desktop-title"));
	sett.setWindowTitle(getEnteredText("window-title"));
	sett.setDesktopEscapeCommand(getEnteredText("desktop-escape-command"));
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

    static UserInterface create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	return new UserInterface(controlPanel);
    }
}
