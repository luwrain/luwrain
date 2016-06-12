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
import org.luwrain.util.RegistryPath;

class SpeechCurrent extends SimpleArea implements SectionArea
{
    private ControlPanel controlPanel;

    SpeechCurrent(ControlPanel controlPanel, String name)
    {
	super(new DefaultControlEnvironment(controlPanel.getCoreInterface()), name);
	this.controlPanel = controlPanel;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (controlPanel.onKeyboardEvent(event))
	    return true;
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (controlPanel.onEnvironmentEvent(event))
	    return true;
	return super.onEnvironmentEvent(event);
    }

    static SpeechCurrent create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	final Luwrain luwrain = controlPanel.getCoreInterface();
	final SpeechCurrent info = new SpeechCurrent(controlPanel, "Текущие речевые каналы");

	info.beginLinesTrans();

	int n = 0;
	while(true)
	{
	    final String prefix = "luwrain.speech.channel." + n + ".";
	    final String name = luwrain.getProperty(prefix + "name");
	    if (name.isEmpty())
		break;

	    info.addLine("Информация о канале \'" + name + "\':");
	    info.addLine("По умолчанию: " + (luwrain.getProperty(prefix + "default").equals("1")?"Да":"Нет"));
	    info.addLine("Реализация: " + luwrain.getProperty(prefix + "class"));

	    info.addLine("Поддерживает вывод звука напрямую на устройство: " + (luwrain.getProperty(prefix + "cansynthtospeakers").equals("1")?"Да":"Нет"));
	    info.addLine("Поддерживает уведомление об окончании воспроизведения: " + (luwrain.getProperty(prefix + "cannotifywhenfinished").equals("1")?"Да":"Нет"));
	    info.addLine("Поддерживает сохранения данных в поток: " + (luwrain.getProperty(prefix + "cansynthtostream").equals("1")?"Да":"Нет"));

	    ++n;
	    info.addLine("");
	}

	info.endLinesTrans();
	return info;
    }
}
