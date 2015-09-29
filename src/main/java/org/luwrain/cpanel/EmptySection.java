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

package org.luwrain.cpanel;

import org.luwrain.core.*;

public class EmptySection implements Section
{
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
	return null;
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

    @Override public boolean isSectionEnabled()
    {
	return true;
    }

    @Override public void refreshChildSubsections()
    {
    }

    @Override public boolean equals(Object obj)
    {
	return this == obj;
    }

    @Override public int getSectionFlags()
    {
	return 0;
    }
}
