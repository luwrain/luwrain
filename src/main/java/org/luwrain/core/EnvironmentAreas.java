/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

import org.luwrain.core.events.*;

abstract class EnvironmentAreas extends EnvironmentBase
{
    protected ScreenContentManager screenContentManager;
    protected WindowManager windowManager;
    protected final InterfaceManager interfaces = new InterfaceManager();
    protected AppManager apps;

    protected void onNewScreenLayout()
    {
	screenContentManager.updatePopupState();
	windowManager.redraw();
    }

    //Returns an effective area for the specified one
    //Returns null if specified area not known in applications and areas managers 
    //Instance is not mandatory but can increase speed of search
    protected Area getEffectiveAreaFor(Luwrain instance, Area area)
    {
	Area effectiveArea = null;
	if (instance != null)
	{
	    final Application app = interfaces.findApp(instance);
	    if (app != null && apps.isAppLaunched(app))
		effectiveArea = apps.getCorrespondingEffectiveArea(app, area);
	}
	//No provided instance or it didn't help
	if (effectiveArea == null)
	    effectiveArea = apps.getCorrespondingEffectiveArea(area);
	return effectiveArea;
    }

    //This method may not return an unwrapped area, there should be at least ta security wrapper
    protected Area getActiveArea()
    {
	//FIXME:Ensure that there is a security wrapper
	final Area area = screenContentManager.getActiveArea();
	if (!(area instanceof AreaWrapper))
	    Log.warning("core", "area " + area.getClass().getName() + " goes through Environment.getActiveArea() not being wrapped by any instance of core.AreaWrapper");
	return area;
    }

    protected boolean isActiveAreaBlockedByPopup()
    {
	return screenContentManager.isActiveAreaBlockedByPopup();
    }

    protected boolean isAreaBlockedBySecurity(Area area)
    {
	return false;
    }

    void sendBroadcastMessage(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	apps.sendBroadcastEvent(event);
    }

    Luwrain getObjForEnvironment()
    {
	return interfaces.getObjForEnvironment();
    }
}
