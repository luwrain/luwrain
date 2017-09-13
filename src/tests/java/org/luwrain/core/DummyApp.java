
package org .luwrain.core;

public class DummyApp implements Application
{
    public Area area = new DummyArea();
    public Luwrain luwrain;

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	this.luwrain = luwrain;
	return new InitResult();
    }

    @Override public String getAppName()
    {
	return "#DummyApp#";
    }

    @Override public AreaLayout getAreaLayout()
    {
	return new AreaLayout(area);
    }

    @Override public void closeApp()
    {
    }
}
