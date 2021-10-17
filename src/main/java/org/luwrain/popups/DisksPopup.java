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

package org.luwrain.popups;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.util.*;

public class DisksPopup extends ListPopupBase<DisksPopup.Disk>
{
    static private final String LOG_COMPONENT = Popups.LOG_COMPONENT;

    public interface Disk
    {
	File activate();
    }

    public interface Disks
    {
	Disk[] getDisks();
    }

    public interface Factory
    {
	Disks newDisks(Luwrain luwrain);
    }

    protected File result = null;

    public DisksPopup(Luwrain luwrain, String name, Set<Popup.Flags> popupFlags)
    {
	super(luwrain, createParams(luwrain, name), popupFlags);
    }

    public File result()
    {
	return this.result;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() || !event.isModified())
	    switch(event.getSpecial())
	    {
	    case ENTER:
		if (selected() == null)
		    return false;
		closing.doOk();
		return true;
	    }
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() == SystemEvent.Type.BROADCAST)
	    switch(event.getCode())
	    {
	    case REFRESH:
		if (event.getBroadcastFilterUniRef().startsWith("disksvolumes:"))
		    refresh();
		return true;
	    default:
		return super.onSystemEvent(event);
	    }
	return super.onSystemEvent(event);
    }

    @Override public boolean onOk()
    {
	final Object sel = selected();
	if (sel == null || !(sel instanceof Disk))
	    return false;
	final Disk disk = (Disk)sel;
	final File res = disk.activate();
	if (res == null)
	    return false;
	this.result = res;
	return true;
    }

    static private void announceDisk(Luwrain luwrain, Object obj, Set<ListArea.Appearance.Flags> flags)
    {
	NullCheck.notNull(obj, "obj");
	NullCheck.notNull(flags, "flags");
	final String str = obj.toString().replaceAll(",", " ").replaceAll(",", " ").replaceAll("-", " ");
	if (str.equals("/"))
	{
	        luwrain.setEventResponse(DefaultEventResponse.listItem(luwrain.i18n().getStaticStr("DisksPopupItemRoot"), Suggestions.CLICKABLE_LIST_ITEM));
		return;
	}

		if (str.equals("/home"))
	{
	        luwrain.setEventResponse(DefaultEventResponse.listItem(luwrain.i18n().getStaticStr("DisksPopupItemUserHome"), Suggestions.CLICKABLE_LIST_ITEM));
		return;
	}

		
	luwrain.setEventResponse(DefaultEventResponse.listItem(luwrain.getSpeakableText(str, Luwrain.SpeakableTextType.NATURAL), Suggestions.CLICKABLE_LIST_ITEM));
    }

    static private Disk[] getDisks(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Factory factory = (Factory)ClassUtils.newInstanceOf(luwrain.getClass().getClassLoader(), "org.luwrain.linux.disks.Factory", Factory.class);
	if (factory  == null)
	    return new Disk[0];
	final Disks disks = factory.newDisks(luwrain);
	if (disks == null)
	{
	    Log.debug(LOG_COMPONENT, "the disks factory object gives a null pointer");
	    return new Disk[0];
	}
	return disks.getDisks();
    }

    static protected ListArea.Params<Disk> createParams(Luwrain luwrain, String name)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	final ListArea.Params<Disk> params = new ListArea.Params<Disk>();
	params.context = new DefaultControlContext(luwrain);
	params.name = name;
	params.model = new ListUtils.FixedModel(getDisks(luwrain)){
		@Override public void refresh()
		{
		    setItems(getDisks(luwrain));
		}};
	params.appearance = new ListUtils.DefaultAppearance<Disk>(new DefaultControlContext(luwrain)){
		@Override public void announceItem(Disk disk, Set<Flags> flags) { announceDisk(luwrain, disk, flags); }
	    };
	params.flags = EnumSet.of(ListArea.Flags.EMPTY_LINE_TOP);
	return params;
    }
}
