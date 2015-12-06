
package org.luwrain.controls;

import org.luwrain.core.NullCheck;

public class HotPointShift implements HotPointControl
{
    private HotPointControl control;
    private int offsetX;
    private int offsetY;

    public HotPointShift(HotPointControl control,
			 int offsetX, int offsetY)
    {
	this.control = control;
	this.offsetX = offsetX;
	this.offsetY = offsetY;
	NullCheck.notNull(control, "control");
    }

    @Override public void beginHotPointTrans()
    {
	control.beginHotPointTrans();
    }

    @Override public void endHotPointTrans()
    {
	control.endHotPointTrans();
    }

    @Override public int getHotPointX()
    {
	final int value = control.getHotPointX();
	return value >= offsetX?value - offsetX:0;
    }

    @Override public void setHotPointX(int value)
    {
	control.setHotPointX(value + offsetX);
    }

    @Override public int getHotPointY()
    {
	final int value = control.getHotPointY();
	return value >= offsetY?value - offsetY:0;
    }

    @Override public void setHotPointY(int value)
    {
	control.setHotPointY(value + offsetY);
    }

    public int offsetX()
    {
	return offsetX;
    }

    public void setOffsetX(int value)
    {
	offsetX = value;
    }

    public int offsetY()
    {
	return offsetY;
    }

    public void setOffsetY(int value)
    {
	offsetY = value;
    }
}
