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

import static org.luwrain.core.DefaultEventResponse.*;

public class DisksPopup extends ListPopupBase<DisksPopup.Disk>
{
    static private final String
	LOG_COMPONENT = Popups.LOG_COMPONENT,
	PROP_FACTORY_CLASS = "luwrain.class.diskspopupfactory";

    public enum Flags {READ_ONLY};

    public interface Disk
    {
	File activate(Set<Flags> flags);
	boolean isActivated();
	boolean deactivate(Set<Flags> flags);
    }

    public interface Disks { Disk[] getDisks(Set<Flags> flags); }
    public interface Factory { Disks newDisks(Luwrain luwrain); }

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
	    case DELETE:
		return deactivate();
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

    protected boolean deactivate()
    {
	final Disk disk = selected();
	if (disk == null)
	    return false;
	try {
	    if (!disk.deactivate(EnumSet.noneOf(Flags.class)))
		luwrain.message("Устройство не может быть отключено", Luwrain.MessageType.ERROR);
	}
	catch(Throwable e)
	{
	    Log.error(LOG_COMPONENT, "unable to deactivate a disk: " + e.getClass().getName() + ": " + e.getMessage());
	    luwrain.message("Устройство не может быть отключено", Luwrain.MessageType.ERROR);
	}
	return true;
    }

    @Override public boolean onOk()
    {
	final Disk disk = selected();
	if (disk == null)
	    return false;
	final File res;
	try {
	    res = disk.activate(EnumSet.noneOf(Flags.class));
	}
	catch(Throwable e)
	{
	    Log.error(LOG_COMPONENT, "unable to activate a disk: " + e.getClass().getName() + ": " + e.getMessage());
	    luwrain.message("Устройство не может быть подключено", Luwrain.MessageType.ERROR);//FIXME:
	    return false;
	}
	if (res == null)
	{
	    luwrain.message("Устройство не может быть подключено", Luwrain.MessageType.ERROR);//FIXME:
	    return false;
	}
	this.result = res;
	return true;
    }

    static private void announceDisk(Luwrain luwrain, DisksPopup.Disk disk, Set<ListArea.Appearance.Flags> flags)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(disk, "disk");
	NullCheck.notNull(flags, "flags");
	final String str = disk.toString().replaceAll(",", " ").replaceAll(",", " ").replaceAll("-", " ");
	if (str.equals("/"))
	{
	    luwrain.setEventResponse(listItem(luwrain.i18n().getStaticStr("DisksPopupItemRoot"), Suggestions.CLICKABLE_LIST_ITEM));
	    return;
	}
	if (str.equals("/home"))
	{
	    luwrain.setEventResponse(listItem(luwrain.i18n().getStaticStr("DisksPopupItemUserHome"), Suggestions.CLICKABLE_LIST_ITEM));
	    return;
	}
	luwrain.setEventResponse(listItem(disk.isActivated()?Sounds.ATTENTION:Sounds.LIST_ITEM, luwrain.getSpeakableText(str, Luwrain.SpeakableTextType.PROGRAMMING), Suggestions.CLICKABLE_LIST_ITEM));
    }

    static private Disk[] getDisks(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Factory factory = (Factory)luwrain.newExtObject(luwrain.getProperty(PROP_FACTORY_CLASS));
	if (factory  == null)
	{
	    Log.error(LOG_COMPONENT, "no disks popup factory");
	    return new Disk[0];
	}
	final Disks disks = factory.newDisks(luwrain);
	if (disks == null)
	{
	    Log.debug(LOG_COMPONENT, "the disks factory object gives a null pointer");
	    return new Disk[0];
	}
	return disks.getDisks(EnumSet.noneOf(Flags.class));
    }

    static protected ListArea.Params<Disk> createParams(Luwrain luwrain, String name)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	final ListArea.Params<Disk> params = new ListArea.Params<Disk>();
	params.context = new DefaultControlContext(luwrain);
	params.name = name;
	params.model = new ListUtils.FixedModel<Disk>(getDisks(luwrain)){
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
