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

package org.luwrain.cpanel;

import java.util.*;

import org.luwrain.core.*;

public class EmptySection implements Section
{
    @Override public SectionArea getSectionArea(ControlPanel controlPanel)
    {
	return null;
    }

    @Override public Element getElement()
    {
	return null;
    }

    @Override public boolean canCloseSection(ControlPanel controlPanel)
    {
	return true;
    }

    @Override public boolean onTreeInsert(ControlPanel controlPanel)
    {
	return false;
    }

    @Override public boolean onTreeDelete(ControlPanel controlPanel)
    {
	return false;
    }

    @Override public boolean isSectionEnabled()
    {
	return true;
    }

    @Override public boolean equals(Object obj)
    {
	return this == obj;
    }

    @Override public Set<Flags> getSectionFlags()
    {
	return EnumSet.noneOf(Flags.class);
    }
}
