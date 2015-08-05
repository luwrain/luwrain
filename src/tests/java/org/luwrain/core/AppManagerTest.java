
package org.luwrain.core;

import org.junit.*;

public class AppManagerTest extends Assert
{
    @Test public void basicSwitching()
    {
	DummyApp defaultApp = new DummyApp();
	DummyApp app1 = new DummyApp();
	DummyApp app2 = new DummyApp();
	DummyApp app3 = new DummyApp();
	DummyApp app4 = new DummyApp();

	AppManager apps = new AppManager(defaultApp);
	apps.registerAppSingleVisible(app1, app1.area);
	apps.registerAppSingleVisible(app2, app2.area);
	apps.registerAppSingleVisible(app3, app3.area);
	apps.registerAppSingleVisible(app4, app4.area);

	assertTrue(apps.getActiveApp() == app4);
	assertTrue(apps.getActiveAreaOfActiveApp() == app4.area);
	apps.switchNextInvisible();
	assertTrue(apps.getActiveApp() == app1);
	assertTrue(apps.getActiveAreaOfActiveApp() == app1.area);
	apps.switchNextInvisible();
	assertTrue(apps.getActiveApp() == app2);
	assertTrue(apps.getActiveAreaOfActiveApp() == app2.area);
	apps.switchNextInvisible();
	assertTrue(apps.getActiveApp() == app3);
	assertTrue(apps.getActiveAreaOfActiveApp() == app3.area);
	apps.switchNextInvisible();
	assertTrue(apps.getActiveApp() == app4);
	assertTrue(apps.getActiveAreaOfActiveApp() == app4.area);

	apps.releaseApp(app4);
	assertTrue(apps.getActiveApp() == app3);
	assertTrue(apps.getActiveAreaOfActiveApp() == app3.area);

	assertTrue(apps.getActiveAreaOfApp(app1) == app1.area);
	assertTrue(apps.getActiveAreaOfApp(app2) == app2.area);
	assertTrue(apps.getActiveAreaOfApp(app3) == app3.area);
    }

    @Test public void complexSwitching()
    {
	DummyApp defaultApp = new DummyApp();
	DummyApp app1 = new DummyApp();
	DummyApp app2 = new DummyApp();
	DummyApp app3 = new DummyApp();
	DummyApp app4 = new DummyApp();

	AppManager apps = new AppManager(defaultApp);
	apps.registerAppSingleVisible(app1, app1.area);
	apps.registerAppSingleVisible(app2, app2.area);
	apps.registerAppSingleVisible(app3, app3.area);

	assertTrue(apps.getActiveApp() == app3);
	assertTrue(apps.getActiveAreaOfActiveApp() == app3.area);
	apps.switchNextInvisible();
	assertTrue(apps.getActiveApp() == app1);
	assertTrue(apps.getActiveAreaOfActiveApp() == app1.area);
	apps.switchNextInvisible();
	assertTrue(apps.getActiveApp() == app2);
	assertTrue(apps.getActiveAreaOfActiveApp() == app2.area);

	apps.registerAppSingleVisible(app4, app4.area);
	assertTrue(apps.getActiveApp() == app4);
	assertTrue(apps.getActiveAreaOfActiveApp() == app4.area);
	apps.releaseApp(app4);
	//Here should be the same app as before app4 launch;
	assertTrue(apps.getActiveApp() == app2);
	assertTrue(apps.getActiveAreaOfActiveApp() == app2.area);
    }
}
