
package org.luwrain.popups;

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
	return new String[]{
	    "",
"Подключенные съёмные накопители:"
	};
    }

	@Override public Object[] getStorageDevices()
    {
	return new Object[0];
    }

	@Override public void attachStorageDevice(Object device)
    {
	return;
    }

	@Override public void detachStorageDevice(Object dev)

    {
	return;
    }
}
