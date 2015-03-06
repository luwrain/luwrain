/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.os.Location;
import org.luwrain.os.OperatingSystem;

class ImportantLocationsListModel implements ListModel
{
    private Luwrain luwrain;
    private OperatingSystem os;
    private Location[] locations;

    public ImportantLocationsListModel(Luwrain luwrain)
    {
	this.luwrain = luwrain;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	this.os = luwrain.os();
	locations = os.getImportantLocations();
    }

    @Override public int getItemCount()
    {
	return locations != null?locations.length + 1:1;
    }

    @Override public Object getItem(int index)
    {
	if (index < 0)
	    return null;
	if (index == 00)
	    return new Location(Location.USER_HOME, luwrain.launchContext().userHomeDirAsFile(), luwrain.launchContext().userHomeDir());
	if (locations == null || index > locations.length)
	    return null;
	return locations[index - 1];
    }

    @Override public boolean toggleMark(int index)
    {
	return false;
    }

    @Override public void refresh()
    {
	locations = os.getImportantLocations();
    }
}
