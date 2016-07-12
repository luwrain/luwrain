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

package org.luwrain.settings;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

class SysInfo extends SimpleArea implements SectionArea
{
    private ControlPanel controlPanel;

    SysInfo(ControlPanel controlPanel,
ControlEnvironment env, String name)
    {
	super(env, name);
	NullCheck.notNull(controlPanel, "controlPanel");
	this.controlPanel = controlPanel;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onKeyboardEvent(event))
	    return true;
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onEnvironmentEvent(event))
	    return true;
	return super.onEnvironmentEvent(event);
    }

    @Override public boolean saveSectionData()
    {
	return true;
    }


    static SysInfo create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	final Luwrain luwrain = controlPanel.getCoreInterface();
	final SysInfo sysInfo = new SysInfo(controlPanel, new DefaultControlEnvironment(luwrain), "Информация о системе");

	sysInfo.beginLinesTrans();
	sysInfo.addLine("Версия LUWRAIN: " + luwrain.getProperty("luwrain.version"));
	sysInfo.addLine("");

	sysInfo.addLine("Операционная система: " + System.getProperty("os.name"));
	sysInfo.addLine("Версия операционной системы: " + System.getProperty("os.version"));
	sysInfo.addLine("Архитектура: " + System.getProperty("os.arch"));
	sysInfo.addLine("");

	int i = 0;
	while(true)
	{
	    final String cpu = luwrain.getProperty("luwrain.hardware.cpu." + i);
	    if (cpu.trim().isEmpty())
		break;
	    sysInfo.addLine("Центральный процессор " + (i + 1) + ": " + cpu);
	    ++i;
	    if (i >= 1024)
		break;
	}
	sysInfo.addLine("Объём оперативной памяти (МБ): " + luwrain.getProperty("luwrain.hardware.ramsizemb"));
	sysInfo.addLine("");

	sysInfo.addLine("Версия Java: " + System.getProperty("java.version"));
	sysInfo.addLine("Поставщик виртуальной машины Java: " + System.getProperty("java.vm.vendor"));
	sysInfo.addLine("Разрядность виртуальной машины Java: " + System.getProperty("sun.arch.data.model") + " бита");
	sysInfo.addLine("Базовый каталог виртуальной машины Java: " + System.getProperty("java.home"));
	sysInfo.endLinesTrans();
	return sysInfo;
    }
}
