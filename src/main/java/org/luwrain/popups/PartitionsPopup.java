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

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class PartitionsPopup extends ListPopupBase
{
    public interface Partition 
    {
	String getFullTitle();
	String getBriefTitle();
	Object getObject();
    }

    public interface Control
    {
	Partition[] getPartitions();
	String[] getStorageDevicesIntroduction();
	Object[] getStorageDevices();
	void attachStorageDevice(Object device);
	void detachStorageDevice(Object dev);
    }

    static private class Appearance implements ListItemAppearance
    {
	private Luwrain luwrain;

	Appearance(Luwrain luwrain)
	{
	    this.luwrain = luwrain;
	    NullCheck.notNull(luwrain, "luwrain");
	}

	@Override public void introduceItem(Object item, int flags)
	{
	    if (item == null)
		return;
	    if (item instanceof Partition)
	    {
		//		System.out.println("instance");
		final Partition part = (Partition)item;
		if ((flags & BRIEF) != 0)
		    luwrain.say(part.getBriefTitle()); else
		    luwrain.say(part.getFullTitle());
				return;
	    }
	    luwrain.say(item.toString());
	}

	@Override public String getScreenAppearance(Object item, int flags)
	{
	    if (item == null)
		return "";
	    if (item instanceof Partition)
	    {
		final Partition part = (Partition)item;
		return part.getFullTitle();
	    }
	    return item.toString();
	}

	@Override public int getObservableLeftBound(Object item)
	{
	    return 0;
	}

	@Override public int getObservableRightBound(Object item)
	{
	    return getScreenAppearance(item, 0).length();
	}
    }

    static private class Model implements ListModel
    {
	private Control control;
	private Object[] items;

	Model(Control control)
	{
	    this.control = control;
	    NullCheck.notNull(control, "control");
	    refresh();
	}

	@Override public int getItemCount()
	{
	    return items != null?items.length + 1:0;
	}

	@Override public Object getItem(int index)
	{
	    if (items == null ||
		index < 0 || index >= items.length)
		return null;
	    return items[index];
	}

	@Override public boolean toggleMark(int index)
	{
	    return false;
	}

	@Override public void refresh()
    {
	final LinkedList res = new LinkedList();
	for(Object o: control.getPartitions())
	    res.add(o);

	for(Object o: control.getStorageDevicesIntroduction())
	    res.add(o);
	for(Object o: control.getStorageDevices())
	    res.add(o);
	items = res.toArray(new Object[res.size()]);
    }
    }

    private Control control;
    private Partition result = null;

    public PartitionsPopup(Luwrain luwrain, Control control,
			   String name, int popupFlags)
    {
	super(luwrain, name,
	      new Model(control), new Appearance(luwrain),
	      popupFlags);
	this.control = control;
	NullCheck.notNull(control, "control");
    }

    public Partition result()
    {
	return result;
    }

    @Override public boolean onOk()
    {
	final Object res = selected();
	if (res == null || !(res instanceof Partition))
	    return false;
	result = (Partition)res;
	return true;
    }
}
