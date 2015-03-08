
package org.luwrain.tests; 

import org.junit.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class ClipboardTest extends Assert
{
    public ClipboardTest()
    {
    }

    @Test public void navigateMulti()
    {
	TestingControlEnvironment environment = new TestingControlEnvironment();
	NavigateArea area = new TestingNavigateArea(environment);
	area.setHotPoint(5, 1);
	area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY_CUT_POINT));
	area.setHotPoint(3, 3);
	area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY));
	assertTrue(environment.clipboard.length == 3);
	assertTrue(environment.clipboard[0].equals("yuiop"));
	assertTrue(environment.clipboard[1].equals("asdfghjkl"));
	assertTrue(environment.clipboard[2].equals("zxc"));
    }

    @Test public void navigateMultiReversed()
    {
	TestingControlEnvironment environment = new TestingControlEnvironment();
	NavigateArea area = new TestingNavigateArea(environment);
	area.setHotPoint(3, 3);
	area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY_CUT_POINT));
	area.setHotPoint(5, 1);
	area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY));
	assertTrue(environment.clipboard.length == 3);
	assertTrue(environment.clipboard[0].equals("yuiop"));
	assertTrue(environment.clipboard[1].equals("asdfghjkl"));
	assertTrue(environment.clipboard[2].equals("zxc"));
    }

    @Test public void navigateSingle()
    {
	TestingControlEnvironment environment = new TestingControlEnvironment();
	NavigateArea area = new TestingNavigateArea(environment);
	area.setHotPoint(3, 1);
	area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY_CUT_POINT));
	area.setHotPoint(7, 1);
	area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY));
	assertTrue(environment.clipboard.length == 1);
	assertTrue(environment.clipboard[0].equals("rtyu"));
    }

    @Test public void navigateSingleReversed()
    {
	TestingControlEnvironment environment = new TestingControlEnvironment();
	NavigateArea area = new TestingNavigateArea(environment);
	area.setHotPoint(7, 1);
	area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY_CUT_POINT));
	area.setHotPoint(3, 1);
	area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.COPY));
	assertTrue(environment.clipboard.length == 1);
	assertTrue(environment.clipboard[0].equals("rtyu"));
    }
}
