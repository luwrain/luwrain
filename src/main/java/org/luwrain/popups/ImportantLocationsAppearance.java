/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.popups;

import org.luwrain.os.Location;
import org.luwrain.core.*;
import org.luwrain.controls.*;

class ImportantLocationsAppearance implements ListItemAppearance
{
    private Luwrain luwrain;
    private Strings strings;

    public ImportantLocationsAppearance(Luwrain luwrain)
    {
	this.luwrain = luwrain;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
final Object o = luwrain.i18n().getStrings("luwrain.environment");
if (o == null)
    throw new NullPointerException("strings object may not be null");
if (!(o instanceof Strings))
    throw new IllegalArgumentException("strings object is not an instance of org.luwrain.core.Strings");
strings = (Strings)o;
    }

    @Override public void introduceItem(Object item, int flags)
    {
	if (item == null || !(item instanceof Location))
	    return;
	final Location location = (Location)item;
	if ((flags & BRIEF) != 0)
	    luwrain.say(location.name()); else
	    luwrain.say(strings.locationTitle(location));
    }

    @Override public String getScreenAppearance(Object item, int flags)
    {
	if (item == null || !(item instanceof Location))
	    return "";
	final Location location = (Location)item;
	return strings.locationTitle(location);
    }

    @Override public int getObservableLeftBound(Object item)
    {
	return 0;
    }

    @Override public int getObservableRightBound(Object item)
    {
	if (item == null || !(item instanceof Location))
	    return 0;
	final Location location = (Location)item;
	return strings.locationTitle(location).length();
    }
}
