/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.util.*;

import org.luwrain.popups.*;
import org.luwrain.hardware.*;

class PartitionsPopupControl implements PartitionsPopup.Control
{
    static private class PartWrapper implements PartitionsPopup.Partition
    {
	private Luwrain luwrain;
	private org.luwrain.hardware.Partition part;

	PartWrapper(Luwrain luwrain, Partition part)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(part, "part");
	    this.luwrain = luwrain;
	    this.part = part;
	}

	@Override public String getFullTitle()
	{

	switch(part.type())
	{
	case Partition.ROOT:
	    return luwrain.i18n().getStaticStr("PartitionsPopupItemRoot");
	case Partition.USER_HOME:
	    return luwrain.i18n().getStaticStr("PartitionsPopupItemUserHome");
	case Partition.REGULAR:
	    return luwrain.i18n().getStaticStr("PartitionsPopupItemRegular") + " " + part.name();
	case Partition.REMOTE:
	    return luwrain.i18n().getStaticStr("PartitionsPopupItemRemote") + " " + part.name();
	case Partition.REMOVABLE:
	    return luwrain.i18n().getStaticStr("PartitionsPopupItemRemovable") + " " + part.name();
	default:
	    return part.name();
	}
    }

	@Override public String getBriefTitle()
	{
	    return part.name();
	}

	@Override public Object getObject()
	{
	    return part;
	}
    }

    static private class DeviceWrapper
    {
	private StorageDevice device;

	DeviceWrapper(StorageDevice device)
	{
	    NullCheck.notNull(device, "device");
	    this.device = device;
	}

	@Override public String toString()
	{
	    return device.model + " (" + device.devName + ")";
	}

	StorageDevice device() { return device; }
    }

    private Luwrain luwrain;
    private Hardware hardware;

    PartitionsPopupControl(Luwrain luwrain, Hardware hardware)
{
    NullCheck.notNull(luwrain, "luwrain");
    NullCheck.notNull(hardware, "hardware");
    this.luwrain = luwrain;
    this.hardware = hardware;
}

    @Override public PartitionsPopup.Partition[] getPartitions()
    {
	final Partition[] parts = hardware.getMountedPartitions();
	if (parts == null || parts.length < 1)
	    return new PartWrapper[0];
	final PartWrapper[] res = new PartWrapper[parts.length];
	for(int i = 0;i < parts.length;++i)
	    res[i] = new PartWrapper(luwrain, parts[i]);
	return res;
    }

    @Override public String[] getStorageDevicesIntroduction()
    {
	final Object[] devices = getStorageDevices();
	if (devices == null || devices.length < 1)
	    return new String[]{"", "Нет подключённых съёмных накопителей"};
	return new String[]{
	    "",
	    "Подключенные съёмные накопители:"
	};
    }

    @Override public Object[] getStorageDevices()
    {
	final StorageDevice[] devices = hardware.getStorageDevices();
	if (devices == null || devices.length < 1)
	    return new Object[0];
	final LinkedList<DeviceWrapper> res = new LinkedList<DeviceWrapper>();
	for(StorageDevice d: devices)
	    if (d.removable)
		res.add(new DeviceWrapper(d));
	return res.toArray(new DeviceWrapper[res.size()]);
    }

    @Override public int attachStorageDevice(Object device)
    {
	if (device == null || !(device instanceof DeviceWrapper))
	    return -1;
	final DeviceWrapper wrapper = (DeviceWrapper)device;
	return hardware.mountAllPartitions(wrapper.device());
    }

    @Override public int detachStorageDevice(Object device)
    {
	if (device == null || !(device instanceof DeviceWrapper))
	    return -1;
	final DeviceWrapper wrapper = (DeviceWrapper)device;
	return hardware.umountAllPartitions(wrapper.device());
    }
}
