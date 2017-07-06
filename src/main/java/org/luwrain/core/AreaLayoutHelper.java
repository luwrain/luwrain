
package org.luwrain.core;

public class AreaLayoutHelper
{
    public enum Position {LEFT, RIGHT, TOP, BOTTOM};

    protected final Luwrain luwrain;
    protected Area basicArea = null;
    protected AreaLayout basicLayout = null;

    protected Area additionalArea = null;
    protected Position additionalAreaPos = null;
    protected Area tempArea = null;

    public AreaLayoutHelper(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    public AreaLayoutHelper(Luwrain luwrain, Area basicArea)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(basicArea, "basicArea");
	this.luwrain = luwrain;
	this.basicArea = basicArea;
    }

    public AreaLayoutHelper(Luwrain luwrain, AreaLayout basicLayout)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(basicLayout, "basicLayout");
	this.luwrain = luwrain;
	this.basicLayout = basicLayout;
    }

    public void setBasicArea(Area area)
    {
	NullCheck.notNull(area, "area");
	this.basicArea = basicArea;
	this.basicLayout = null;
    }

    public void setBasicLayout(AreaLayout layout)
    {
	NullCheck.notNull(layout, "layout");
	this.basicLayout = layout;
	this.basicArea = null;
    }

    public void clear()
    {
	this.basicArea = null;
	this.basicLayout = null;
    }

    public boolean openAdditionalArea(Area area, Position pos)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(pos, "pos");
	if (basicLayout != null)
	    return false;
	additionalArea = area;
	additionalAreaPos = pos;
	luwrain.onNewAreaLayout();
	return true;
    }

    public void closeAdditionalArea()
    {
	if (additionalArea == null)
	    return;
	additionalArea = null;
	additionalAreaPos = null;
	luwrain.onNewAreaLayout();
    }

    public Area getAdditionalArea()
    {
	return additionalArea;
    }

    public void openTempArea(Area area)
    {
	NullCheck.notNull(area, "area");
	tempArea = area;
	luwrain.onNewAreaLayout();
    }

    public void closeTempArea()
    {
	if (tempArea == null)
	    return;
	tempArea = null;
	luwrain.onNewAreaLayout();
    }

    public Area getTempArea()
    {
	return tempArea;
    }

    public AreaLayout getLayout()
    {
	if (tempArea != null)
	    return new AreaLayout(tempArea);
	if (basicLayout != null)
	    return basicLayout;
	if (basicArea == null)
	return null;
	return null;//FIXME:
    }


}
