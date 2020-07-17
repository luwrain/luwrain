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

package org.luwrain.settings;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

final class Version extends SimpleArea implements SectionArea
{
    private final ControlPanel controlPanel;
    private final Luwrain luwrain;

    Version(ControlPanel controlPanel)
    {
	super(new DefaultControlContext(controlPanel.getCoreInterface()), controlPanel.getCoreInterface().i18n().getStaticStr("CpVersion"));
	NullCheck.notNull(controlPanel, "controlPanel");
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
	fillData();
    }

    private void fillData()
    {
	Runtime.getRuntime().gc();
	final long memUsedBytes = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	final String luwrainVersion = luwrain.i18n().getStaticStr("CpVersionLuwrain") + " " + luwrain.getProperty("luwrain.version") + " (" + System.getProperty("sun.arch.data.model") + " " + luwrain.i18n().getStaticStr("CpVersionLuwrainBits") + ")";
	final String osVersion = luwrain.i18n().getStaticStr("CpVersionOs") + " " + System.getProperty("os.name") + " " + System.getProperty("os.version");
	final String javaVersion = luwrain.i18n().getStaticStr("CpVersionJava") + " " + System.getProperty("java.version") + " (" + System.getProperty("java.vm.vendor") + ")";
	final String memUsed = luwrain.i18n().getStaticStr("CpVersionMemUsed") + " " + (memUsedBytes / 1048576) + "M";
	beginLinesTrans();
	addLine("");
	addLine(luwrainVersion);
	addLine(osVersion);
	addLine(javaVersion);
	addLine(memUsed);
	endLinesTrans();
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
	return true;
    }

    static Version create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	return new Version(controlPanel);
    }
}
