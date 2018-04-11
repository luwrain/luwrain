
package org.luwrain.app.cmdtool;

import java.util.*;
import java.io.*;

import org.luwrain.base.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class App implements Application
{
    private Luwrain luwrain = null;
    private Strings strings = null;
    private SimpleArea area = null;
    private final CommandLineTool.Instance tool;

    public App(CommandLineTool.Instance tool)
    {
	NullCheck.notNull(tool, "tool");
	this.tool = tool;
    }

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, Strings.NAME);
	strings = (Strings)o;
	this.luwrain = luwrain;
	createArea();
	return new InitResult();
    }

    private void createArea()
    {
	area = new SimpleArea(new DefaultControlEnvironment(luwrain), strings.appName()){
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    switch (event.getCode())
		    {
		    case CLOSE:
			closeApp();
			return true;
		    }
		    return false;
		}
	    };
    }

    @Override public AreaLayout getAreaLayout()
    {
	return new AreaLayout(area);
    }

    @Override public void closeApp()
    {
	luwrain.closeApp();
    }

        @Override public String getAppName()
    {
	return strings.appName();
    }
}
