
package org.luwrain.popups;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.hardware.*;

public class DefaultPartitionsPopupControl implements PartitionsPopup.Control
{
    static private class PartWrapper implements PartitionsPopup.Partition
    {
	private org.luwrain.hardware.Partition part;
	private Strings strings;

	PartWrapper(Partition part, Strings strings)
	{
	    this.part = part;
	    this.strings = strings;
	}

	@Override public String getFullTitle()
	{
	    //	    System.out.println(part.name());
	    return strings.partitionTitle(part);
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
	    this.device = device;
	    NullCheck.notNull(device, "device");
	}

	@Override public String toString()
	{
	    return device.model + " (" + device.devName + ")";
	}

	StorageDevice device()
	{
	    return device;
	}
    }

    private Hardware hardware;
private Strings strings;

public DefaultPartitionsPopupControl(Luwrain luwrain, Hardware hardware)
{
    this.hardware = hardware;
    NullCheck.notNull(luwrain, "luwrain");
    NullCheck.notNull(hardware, "hardware");
    this.strings = (Strings)luwrain.i18n().getStrings("luwrain.environment");
}

@Override public PartitionsPopup.Partition[] getPartitions()
    {
	final Partition[] parts = hardware.getMountedPartitions();
	if (parts == null || parts.length < 1)
	    return new PartWrapper[0];
	final PartWrapper[] res = new PartWrapper[parts.length];
	for(int i = 0;i < parts.length;++i)
	    res[i] = new PartWrapper(parts[i], strings);
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
