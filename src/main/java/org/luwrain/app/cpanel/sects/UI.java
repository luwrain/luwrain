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

package org.luwrain.app.cpanel.sects;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;
import org.luwrain.util.*;

class UI extends EmptySection
{
    static private class Area extends FormArea
    {
	private Environment environment;
	private final RegistryKeys registryKeys = new RegistryKeys();

	Area(Environment environment )
	{
	    super(new DefaultControlEnvironment(environment.getLuwrain()));
	    this.environment = environment;
	    NullCheck.notNull(environment, "environment");
	    final RegistryAutoCheck check = new RegistryAutoCheck(environment.getLuwrain().getRegistry());
	    addEdit("desktop-introduction-file", "Имя файла для отображения на рабочем столе:", check.stringAny(registryKeys.desktopIntroductionFile(), ""), null, true);
	    addEdit("launch-greeting", "Текст голосового приветствия при старте системы:", check.stringAny(registryKeys.launchGreeting(), ""), null, true);
	}

	@Override public boolean onKeyboardEvent(KeyboardEvent event)
	{
	    NullCheck.notNull(event, "event");
	    if (event.isCommand() && !event.isModified())
		switch(event.getCommand())
		{
		case TAB:
		    environment.gotoSectionsTree ();
	    return true;
		}
	return super.onKeyboardEvent(event);
	}

	@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
	{
	    NullCheck.notNull(event, "event");
	    switch(event.getCode())
	    {
	    case CLOSE:
		environment.close();
		return true;
	    default:
		return super.onEnvironmentEvent(event);
	    }
	}

	@Override public String getAreaName()
	{
	    return "Настройки интерфейса";
	}

	boolean save()
	{
	    final Luwrain luwrain = environment.getLuwrain();
	    final Registry registry = luwrain.getRegistry();
	    if (!registry.setString(registryKeys.desktopIntroductionFile(), getEnteredText("desktop-introduction-file")))
		return false;
	    if (!registry.setString(registryKeys.launchGreeting(), getEnteredText("launch-greeting")))
		return false;
	    return true;
	}
    }

    private Area area = null;

    @Override public Area getSectionArea(Environment environment)
    {
	if (area == null)
	    area = new Area(environment);
	return area;
    }

    @Override public String toString()
    {
	return "Интерфейс";
    }

    @Override public boolean canCloseSection(Environment environment)
    {
	if (area == null)
	    return true;
	if (!area.save())
	    environment.getLuwrain().message("Во время сохранения сделанных изменений произошла непредвиденная ошибка", Luwrain.MESSAGE_ERROR);
	return true;
    }
}
