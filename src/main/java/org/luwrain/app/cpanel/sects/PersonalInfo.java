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
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

class PersonalInfo extends FormArea  implements Section
{
    private Environment environment;

    PersonalInfo(Environment environment)
    {
	super(environment, "Персональная информация");
	this.environment = environment;
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
    }

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
	return this;
    }

    String getSectionName()
    {
	return "Персональная информация";
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
