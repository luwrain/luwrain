/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core;

import java.util.Vector;

public class AreaLayoutSwitch
{
    private Luwrain luwrain;
    private final Vector<AreaLayout> layouts = new Vector<AreaLayout>();
    private int currentIndex = 0;

    public AreaLayoutSwitch(Luwrain luwrain)
    {
	this.luwrain = luwrain;
	NullCheck.notNull(luwrain, "luwrain");
    }

    public int getCurrentIndex()
    {
	return currentIndex;
    }

    public void add(AreaLayout layout)
    {
	NullCheck.notNull(layout, "layout");
	layouts.add(layout);
    }

    public boolean show(int index)
    {
	if (index < 0 || index >= layouts.size())
	    return false;
	currentIndex = index;
	luwrain.onNewAreaLayout();
	return true;
    }

    public AreaLayout getCurrentLayout()
    {
	if (currentIndex >= layouts.size())
	    return null;
	return layouts.get(currentIndex);
    }
}
