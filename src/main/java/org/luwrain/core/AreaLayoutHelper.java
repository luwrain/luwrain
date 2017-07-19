
package org.luwrain.core;

public class AreaLayoutHelper
{
    public enum Position {LEFT, RIGHT, TOP, BOTTOM};

public interface UpdateNotification
{
    void onLayoutUpdate();
}

    protected final UpdateNotification notification;
    protected Area basicArea = null;
    protected AreaLayout basicLayout = null;

    protected Area additionalArea = null;
    protected Position additionalAreaPos = null;
    protected Area tempArea = null;

    public AreaLayoutHelper(UpdateNotification notification)
    {
	NullCheck.notNull(notification, "notification");
	this.notification = notification;
    }

    public AreaLayoutHelper(UpdateNotification notification, Area basicArea)
    {
	NullCheck.notNull(notification, "notification");
	NullCheck.notNull(basicArea, "basicArea");
	this.notification = notification;
	this.basicArea = basicArea;
    }

    public AreaLayoutHelper(UpdateNotification notification, AreaLayout basicLayout)
    {
	NullCheck.notNull(notification, "notification");
	NullCheck.notNull(basicLayout, "basicLayout");
	this.notification = notification;
	this.basicLayout = basicLayout;
    }

    public void setBasicArea(Area area)
    {
	NullCheck.notNull(area, "area");
	this.basicArea = basicArea;
	this.basicLayout = null;
	notification.onLayoutUpdate();
    }

    public void setBasicLayout(AreaLayout layout)
    {
	NullCheck.notNull(layout, "layout");
	this.basicLayout = layout;
	this.basicArea = null;
	notification.onLayoutUpdate();
    }

    public void clear()
    {
	this.basicArea = null;
	this.basicLayout = null;
	notification.onLayoutUpdate();
    }

    public boolean openAdditionalArea(Area area, Position pos)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(pos, "pos");
	if (basicLayout != null)
	    return false;
	additionalArea = area;
	additionalAreaPos = pos;
	notification.onLayoutUpdate();
	return true;
    }

    public void closeAdditionalArea()
    {
	if (additionalArea == null)
	    return;
	additionalArea = null;
	additionalAreaPos = null;
	notification.onLayoutUpdate();
    }

    public Area getAdditionalArea()
    {
	return additionalArea;
    }

    public boolean hasAdditionalArea()
    {
	return additionalArea != null && additionalAreaPos != null;
    }

    public void openTempArea(Area area)
    {
	NullCheck.notNull(area, "area");
	tempArea = area;
	notification.onLayoutUpdate();
    }

    public void closeTempArea()
    {
	if (tempArea == null)
	    return;
	tempArea = null;
	notification.onLayoutUpdate();
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
	if (additionalArea != null && additionalAreaPos != null)
	    switch(additionalAreaPos)
	    {
	    case RIGHT:
		return new AreaLayout(AreaLayout.LEFT_RIGHT, basicArea, additionalArea);
	    case LEFT:
		return new AreaLayout(AreaLayout.LEFT_RIGHT, additionalArea, basicArea);
	    case TOP:
		return new AreaLayout(AreaLayout.TOP_BOTTOM, additionalArea, basicArea);
	    case BOTTOM:
		return new AreaLayout(AreaLayout.TOP_BOTTOM, basicArea, additionalArea);
	    default:
		return null;
	    }
	return new AreaLayout(basicArea);
    }
}
