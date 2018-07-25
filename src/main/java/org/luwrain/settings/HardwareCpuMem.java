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

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

class HardwareCpuMem extends SimpleArea implements SectionArea
{
    private final ControlPanel controlPanel;
    private final Luwrain luwrain;

    HardwareCpuMem(ControlPanel controlPanel)
    {
	super(new DefaultControlEnvironment(controlPanel.getCoreInterface()), "Процессор и память");
	NullCheck.notNull(controlPanel, "controlPanel");
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
	fillData();
    }

    private void fillData()
    {
	beginLinesTrans();
	int i = 0;
	while(true)
	{
	    final String cpu = luwrain.getProperty("luwrain.hardware.cpu." + i);
	    if (cpu.trim().isEmpty())
		break;
	    addLine("Центральный процессор " + (i + 1) + ": " + cpu);
	    ++i;
	    if (i >= 1024)
		break;
	}
	addLine("Объём оперативной памяти (МБ): " + luwrain.getProperty("luwrain.hardware.ramsizemb"));
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

    static HardwareCpuMem create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	return new HardwareCpuMem(controlPanel);
    }
}
