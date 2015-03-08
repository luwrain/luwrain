
package org .luwrain.core;

public class DummyApp implements Application
{
    public Area area = new DummyArea();
    public Luwrain luwrain;

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	this.luwrain = luwrain;
	return true;
    }

    @Override public String getAppName()
    {
	return "#DummyApp#";
    }

    @Override public AreaLayout getAreasToShow()
    {
	return new AreaLayout(area);
    }
}
