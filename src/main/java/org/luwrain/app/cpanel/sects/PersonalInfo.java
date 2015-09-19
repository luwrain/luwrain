/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

class PersonalInfo implements Section
{
    static private class Area extends FormArea
    {
	private Environment environment;
	private final RegistryKeys registryKeys = new RegistryKeys();

	Area(Environment environment )
	{
	    super(environment);
	    this.environment = environment;
	    NullCheck.notNull(environment, "environment");
	    final RegistryAutoCheck check = new RegistryAutoCheck(environment.getLuwrain().getRegistry());
	    addEdit("name", "Полное имя:", check.stringAny(registryKeys.personalFullName(), ""), null, true);
	    addEdit("address", "Основной адрес электронной почты:", check.stringAny(registryKeys.personalDefaultMailAddress(), ""), null, true);
	    activateMultilinedEdit("Текст подписи в сообщениях электронной почты:", new String[0], true);
	}

	@Override public boolean onKeyboardEvent(KeyboardEvent event)
	{
	    NullCheck.notNull(event, "event");
	    if (event.isCommand() && !event.isModified())
		switch(event.getCommand())
		{
		case KeyboardEvent.TAB:
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
	    case EnvironmentEvent.SAVE:
		save();
		return true;
	    case EnvironmentEvent.CLOSE:
		environment.close();
		return true;
	    default:
		return super.onEnvironmentEvent(event);
	    }
	}

	@Override public String getAreaName()
	{
	    return "Персональная информация";
	}

	private void save()
	{
	    final Luwrain luwrain = environment.getLuwrain();
	    final Registry registry = luwrain.getRegistry();
	    if (!registry.setString(registryKeys.personalFullName(), getEnteredText("name")))
	    {
		luwrain.message("Невозможно сохранить в реестре значение полного имени", Luwrain.MESSAGE_ERROR);
		return;
	    }
	    if (!registry.setString(registryKeys.personalDefaultMailAddress(), getEnteredText("address")))
	    {
		luwrain.message("Невозможно сохранить в реестре значение адреса электронной почты", Luwrain.MESSAGE_ERROR);
		return;
	    }
	    if (!registry.setString(registryKeys.personalSignature(), getMultilinedEditText()))
	    {
		luwrain.message("Невозможно сохранить в реестре значение подписи", Luwrain.MESSAGE_ERROR);
		return;
	    }
	    luwrain.message("Персональная информация сохранена", Luwrain.MESSAGE_OK);
	}
    }

    private Area area = null;

    @Override public int getDesiredRoot()
    {
	return BasicSections.NONE;
    }

    @Override public Section[] getChildSections()
    {
	return new Section[0];
    }

    @Override public Area getSectionArea(Environment environment)
    {
	if (area == null)
	    area = new Area(environment);
	return area;
    }

    @Override public boolean canCloseSection(Environment environment)
    {
	return true;
    }

    @Override public boolean onTreeInsert(Environment environment)
    {
	return false;
    }

    @Override public boolean onTreeDelete(Environment environment)
    {
	return false;
    }

    @Override public String toString()
    {
	return "Персональная информация";
    }

    @Override public boolean isSectionEnabled()
    {
	return true;
    }
}
