/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

final class Braille extends FormArea implements SectionArea
{
    private ControlPanel controlPanel;
    private Luwrain luwrain;
    private Settings.Braille settings;

    Braille(ControlPanel controlPanel)
    {
	super(new DefaultControlContext(controlPanel.getCoreInterface()), "Брайль");
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
	this.settings = Settings.createBraille(luwrain.getRegistry());
	fillForm();
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onInputEvent(event))
	    return true;
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onSystemEvent(event))
	    return true;
	return super.onSystemEvent(event);
    }

    @Override public boolean saveSectionData()
    {
	System.out.println("saving " + getCheckboxState("enabled"));
	settings.setEnabled(getCheckboxState("enabled"));
	return true;
    }

    private void fillForm()
    {
	final boolean activated = luwrain.getProperty("luwrain.braille.active").equals("1");
	addCheckbox("enabled", "Включена поддержка брайля:", settings.getEnabled(false));
	addStatic("activated", "Активировано:" + (activated?"Да":"Нет"));
	if (activated)
	{
	    addStatic("driver", "Драйвер:" + luwrain.getProperty("luwrain.braille.driver"));
	    addStatic("display-width", "Ширина дисплея:" + luwrain.getProperty("luwrain.braille.displaywidth"));
	    addStatic("display-height", "Высота дисплея:" + luwrain.getProperty("luwrain.braille.displayheight"));
	} else
	{
	    final String error = luwrain.getProperty("luwrain.braille.error");
	    if (!error.isEmpty())
		addStatic("error", "Текст ошибки:" + error);
	}
    }

    static Braille create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	return new Braille(controlPanel);
    }
}
