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

package org.luwrain.popups;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class PartitionsPopup extends ListPopupBase
{
    protected final Control control;
    protected Partition result = null;

    public PartitionsPopup(Luwrain luwrain, Control control,
			   String name, Set<Popup.Flags> popupFlags, Set<ListArea.Flags> listFlags)
    {
	super(luwrain, constructParams(luwrain, control, name, listFlags), popupFlags);
	NullCheck.notNull(control, "control");
	this.control = control;
    }

    public Partition result()
    {
	return result;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() || !event.isModified())
	    switch(event.getSpecial())
	    {
	    case ENTER:
		closing.doOk();
		return true;
	    case INSERT:
		return attach();
	    case DELETE:
		return detach();
	    }
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onOk()
    {
	final Object res = selected();
	if (res == null || !(res instanceof Partition))
	    return false;
	result = (Partition)res;
	return true;
    }

    private boolean attach()
    {
	final Object selected = selected();
	if (selected == null ||
	    selected instanceof Partition ||
	    selected instanceof String)
	    return false;
	final int res = control.attachStorageDevice(selected);
	if (res < 0)
	{
	    luwrain.message("Во время попытки подключения разделов на съёмном накопителе произошла ошибка", Luwrain.MESSAGE_ERROR);
	    return true;
	}
	luwrain.message("Подключено разделов: " + res, res > 0?Luwrain.MESSAGE_OK:Luwrain.MESSAGE_REGULAR);
	refresh();
	return true;
    }

    private boolean detach()
    {
	final Object selected = selected();
	if (selected == null ||
	    selected instanceof Partition ||
	    selected instanceof String)
	    return false;
	final int res = control.detachStorageDevice(selected);
	if (res < 0)
	{
	    luwrain.message("Во время попытки отключения разделов на съёмном накопителе произошла ошибка", Luwrain.MESSAGE_ERROR);
	    return true;
	}
	luwrain.message("Отключено разделов: " + res, res > 0?Luwrain.MESSAGE_OK:Luwrain.MESSAGE_REGULAR);
	refresh();
	return true;
    }

    static private ListArea.Params constructParams(Luwrain luwrain, Control control,
						   String name, Set<ListArea.Flags> listFlags)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(control, "control");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(listFlags, "listFlags");
	final ListArea.Params params = new ListArea.Params();
	params.environment = new DefaultControlEnvironment(luwrain);
	params.name = name;
	params.model = new Model(control);
	params.appearance = new Appearance(luwrain);
	params.flags = listFlags;
	return params;
    }

    public interface Partition 
    {
	String getFullTitle();
	String getBriefTitle();
	Object getObject();
    }

    public interface Control
    {
	Partition[] getPartitions();
	Object[] getStorageDevices();
	String[] getStorageDevicesIntroduction();
	int attachStorageDevice(Object device);
	int detachStorageDevice(Object dev);
    }

    static protected class Appearance implements ListArea.Appearance
    {
	protected final Luwrain luwrain;

	Appearance(Luwrain luwrain)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    this.luwrain = luwrain;
	}

	@Override public void announceItem(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flag ");
	    final String value;
	    if (item instanceof Partition)
	    {
		final Partition part = (Partition)item;
		if (flags.contains(Flags.BRIEF))
		    value = part.getBriefTitle(); else
		    value = part.getFullTitle();
	    } else
		value = item.toString();
	    if (!value.trim().isEmpty())
		luwrain.say(value); else
		luwrain.hint(Hints.EMPTY_LINE);
	}

	@Override public String getScreenAppearance(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
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
	    return getScreenAppearance(item, EnumSet.noneOf(Flags.class)).length();
	}
    }

    static protected class Model implements ListArea.Model
    {
	protected final Control control;
	protected Object[] items;

	Model(Control control)
	{
	    NullCheck.notNull(control, "control");
	    this.control = control;
	    refresh();
	}

	@Override public int getItemCount()
	{
	    return items != null?items.length:0;
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
}
