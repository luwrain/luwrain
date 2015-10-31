
package org.luwrain.controls;

class TestingMultilineEditLowLevelModel extends MutableLinesImpl implements MultilineEditLowLevelModel
{
    private int hotPointX = 0;
    private int hotPointY = 0;

    @Override public int getHotPointX()
    {
	return hotPointX;
    }

    @Override public int getHotPointY()
    {
	return hotPointY;
    }

    @Override public void setHotPointX(int value)
    {
	hotPointX = value;
    }

    @Override public void setHotPointY(int value)
    {
	hotPointY = value;
    }

    @Override public void beginEditTrans()
    {
    }

    @Override public void endEditTrans()
    {
    }


    @Override public String getTabSeq()
    {
	return "\t";
    }
}
