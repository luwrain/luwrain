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

package org.luwrain.app.registry;

import org.luwrain.core.*;
import org.luwrain.controls.*;

class ValuesListAppearance implements ListItemAppearance
{
    private Luwrain luwrain;
    private Strings strings;

    public ValuesListAppearance(Luwrain luwrain, Strings strings)
    {
	this.luwrain = luwrain;
	this.strings = strings;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
    }

    @Override public void introduceItem(Object item, int flags)
    {
	if (item == null || !(item instanceof Value))
	    return;
	final Value value = (Value)item;
	luwrain.say(value.name);
    }

    @Override public String getScreenAppearance(Object item, int flags)
    {
	if (item == null || !(item instanceof Value))
	    return "";
	final Value value = (Value)item;
	return value.name;
    }

    @Override public int getObservableLeftBound(Object item)
    {
	return 0;
	//FIXME:
    }

    @Override public int getObservableRightBound(Object item)
    {
	return getScreenAppearance(item, 0).length();
    }
}
