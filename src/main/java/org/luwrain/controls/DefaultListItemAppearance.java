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

package org.luwrain.controls;

import org.luwrain.core.Sounds;

public class DefaultListItemAppearance implements ListItemAppearance
{
    private ControlEnvironment environment;

    public DefaultListItemAppearance(ControlEnvironment environment)
    {
	this.environment = environment;
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
    }

    @Override public void introduceItem(Object item, int flags)
    {
	if (item == null)
	    return;
	environment.playSound(Sounds.NEW_LIST_ITEM);
	environment.say(item.toString());
    }

    @Override public String getScreenAppearance(Object item, int flags)
    {
	if (item == null)
	    return null;
	return item.toString();
    }

    @Override public int getObservableLeftBound(Object item)
    {
	return 0;
    }

    @Override public int getObservableRightBound(Object item)
    {
	return item != null?item.toString().length():0;
    }
}
