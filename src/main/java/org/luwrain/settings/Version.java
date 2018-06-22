/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

class Version extends SimpleArea implements SectionArea
{
    private final ControlPanel controlPanel;
    private final Luwrain luwrain;

    Version(ControlPanel controlPanel)
    {
	super(new DefaultControlEnvironment(controlPanel.getCoreInterface()), "Версия системы");
	NullCheck.notNull(controlPanel, "controlPanel");
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
	fillData();
    }

    private void fillData()
    {
	beginLinesTrans();
	addLine("Версия LUWRAIN: " + luwrain.getProperty("luwrain.version"));
	addLine("Операционная система: " + System.getProperty("os.name"));
	addLine("Версия операционной системы: " + System.getProperty("os.version"));
	addLine("Архитектура: " + System.getProperty("os.arch"));
	addLine("Версия Java: " + System.getProperty("java.version"));
	addLine("Поставщик виртуальной машины: " + System.getProperty("java.vm.vendor"));
	addLine("Разрядность виртуальной машины: " + System.getProperty("sun.arch.data.model") + " бита");
	addLine("Базовый каталог виртуальной машины: " + System.getProperty("java.home"));
	addLine("");
	endLinesTrans();
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

    @Override public boolean saveSectionData()
    {
	return true;
    }

    static Version create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	return new Version(controlPanel);
    }
}
