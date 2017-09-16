
package org.luwrain.core;

import org.luwrain.core.events.*;

class DummyArea implements Area
{
    @Override public int getLineCount()
    {
	return 2;
    }

    @Override public String getLine(int index)
    {
	switch(index)
	{
	case 0:
	    return "abc";
	case 1:
	    return "123";
	default:
	    return "";
	}
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event) 
    {
	return false;
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	return false;
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	return false;
    }

    @Override public int getHotPointY()
    {
	return 0;
    }

    @Override public int getHotPointX()
    {
	return 0;
    }

    @Override public String getAreaName()
    {
	return "#name#";
    }

    @Override public Action[] getAreaActions()
    {
	return null;
    }
}
