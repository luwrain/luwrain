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

package org.luwrain.popups;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.hardware.Partition;
import org.luwrain.os.OperatingSystem;

class ImportantLocationsListModel implements ListModel
{
    private Luwrain luwrain;
    private OperatingSystem os;
    private Partition[] partitions;

    public ImportantLocationsListModel(Luwrain luwrain)
    {
	this.luwrain = luwrain;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	this.os = luwrain.os();
	partitions = os.getHardware().getMountedPartitions();
    }

    @Override public int getItemCount()
    {
	return partitions != null?partitions.length + 1:1;
    }

    @Override public Object getItem(int index)
    {
	if (index < 0)
	    return null;
	if (index == 00)
	    return new Partition(Partition.USER_HOME, luwrain.launchContext().userHomeDirAsFile(), luwrain.launchContext().userHomeDir(), true);
	if (partitions == null || index > partitions.length)
	    return null;
	return partitions[index - 1];
    }

    @Override public boolean toggleMark(int index)
    {
	return false;
    }

    @Override public void refresh()
    {
	partitions = os.getHardware().getMountedPartitions();
    }
}
