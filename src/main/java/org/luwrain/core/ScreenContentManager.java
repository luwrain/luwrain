/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

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
import org.luwrain.util.*;

final class ScreenContentManager
{
    static public final int NO_APPLICATIONS = 0;
    static public final int EVENT_NOT_PROCESSED = 1;
    static public final int EVENT_PROCESSED = 2;

    private AppManager apps;
    private boolean activePopup = false;

    public ScreenContentManager(AppManager apps)
    {
	this.apps = apps;
	NullCheck.notNull(apps, "apps");
    }

    public  Application isNonPopupDest()
    {
	    if (isPopupActive())
		return null;
	final Area activeArea = apps.getEffectiveActiveAreaOfActiveApp();
	return activeArea != null?apps.getActiveApp():null;
    }

    int onSystemEvent(SystemEvent event)
    {
	final Area activeArea = getActiveArea();
	if (activeArea == null)
	return NO_APPLICATIONS;
	if (isActiveAreaBlockedByPopup())
	    Log.warning("core", "area " + activeArea.getClass().getName() + " is accepting an environment event even being blocked");
	    return activeArea.onSystemEvent(event)?EVENT_PROCESSED:EVENT_NOT_PROCESSED;
    }

    public boolean setPopupActive()
    {
	if (!isPopupOpened())
	    return false;
	activePopup = true;
	return true;
    }

    public void updatePopupState()
    {
	if (activePopup)
	{
	    if (!isPopupOpened())
		activePopup = false;
	} else
	{
	    if (apps.noEffectiveActiveArea() && isPopupOpened())
		activePopup = true;
	}
    }

    public Area getActiveArea()
    {
	    if (isPopupActive())
		return apps.getEffectiveAreaOfLastPopup();
	final Area activeArea = apps.getEffectiveActiveAreaOfActiveApp();
	if (activeArea != null)
	    return activeArea;
	if (isPopupOpened())
	{
	    activePopup = true;
	    return apps.getEffectiveAreaOfLastPopup();
	}
	return null;
    }

    /**
     * Checks that the active area may accept events. Events accepting is
     * prohibited for non-popup areas of the application which has opened
     * popups. This method return false even if there is no active area at
     * all. Weak popups block areas as all others.
     *
     * @return False if the active area may accept events, true otherwise
     */
    public boolean isActiveAreaBlockedByPopup()
    {
	if (isPopupActive())
	    return false;
	final Application activeApp = apps.getActiveApp();
	if (activeApp == null)
	    return false;
	return apps.hasPopupOfApp(activeApp);
    }

    /**
     * Checks that there is an opened popup (probably, inactive). Opened
     * popup appears on screen in one of two cases: if the currently active
     * application has a popup or if the environment itself has it.  If the
     * application with a popup switches to the another one without a popup,
     * the popup hides.
     *
     * @return True if the environment has an opened popup (regardless active or inactive), false otherwise
     */
    public boolean isPopupOpened()
    {
	if (!apps.hasAnyPopup())
	    return false;
	final Application app = apps.getAppOfLastPopup();
	if (app == null)//it is an environment popup
	    return true;
	return apps.isAppActive(app);
    }

    /**
     * Checks that the environment has a proper popup, it opened and active.
     *
     * @return True if the popup opened and active, false otherwise
     */
    public boolean isPopupActive()
    {
	if (!activePopup)
	    return false;
	    if (isPopupOpened())
		return true;
	    activePopup = false;
	    return false;
    }

    public void activateNextArea()
    {
	Area activeArea = getActiveArea();
	if (activeArea == null)
	    return;
	Object[] objs = getWindows().getObjects();
	Window[] windows = new Window[objs.length];
	for(int i = 0;i < objs.length;++i)
	    windows[i] = (Window)objs[i];
	if (windows == null || windows.length <= 0)
	{
	    activePopup = isPopupOpened();
	    return;
	}
	int index;
	for(index = 0;index < windows.length;index++)
	    if (windows[index].area == activeArea)
		break;
	index++;
	if (index >= windows.length)
	    index = 0;
	activePopup = windows[index].popup;
	if (!activePopup)
	{
	    apps.setActiveAreaOfApp(windows[index].app, windows[index].area);
	    apps.setActiveApp(windows[index].app);
	}
    }

    TileManager getWindows()
    {
	TileManager windows;
	final Application activeApp = apps.getActiveApp();
	if (activeApp != null)
	    windows = constructWindowLayoutOfApp(activeApp); else
	    windows = new TileManager();
	if (isPopupOpened())
	{
	    Window popupWindow = new Window(apps.getAppOfLastPopup(), apps.getEffectiveAreaOfLastPopup(), apps.getPositionOfLastPopup());
	    switch(popupWindow.popupPos)
	    {
	    case BOTTOM:
		windows.addBottom(popupWindow);
		break;
	    case TOP:
		windows.addTop(popupWindow);
		break;
	    case LEFT:
		windows.addLeftSide(popupWindow);
		break;
	    case RIGHT:
		windows.addRightSide(popupWindow);
		break;
	    }
	}
	return windows;
    }

    private TileManager constructWindowLayoutOfApp(Application app)
    {
	NullCheck.notNull(app, "app");
	final AreaLayout layout = apps.getEffectiveAreaLayout(app);
	if (layout == null)
	{
	    Log.warning("core", "got null area layout for the application " + app.getClass().getName());
	    return null;
	}
	final TileManager tiles = new TileManager();
	switch(layout.getLayoutType())
	{
	case AreaLayout.SINGLE:
	    tiles.createSingle(new Window(app, layout.getArea1()));
	    break;
	case AreaLayout.LEFT_RIGHT:
	    tiles.createLeftRight(new Window(app, layout.getArea1()),
				  new Window(app, layout.getArea2()));
	    break;
	case AreaLayout.TOP_BOTTOM:
	    tiles.createTopBottom(new Window(app, layout.getArea1()),
				  new Window(app, layout.getArea2()));
	    break;
	case AreaLayout.LEFT_TOP_BOTTOM:
	    tiles.createLeftTopBottom(new Window(app, layout.getArea1()),
				      new Window(app, layout.getArea2()),
				      new Window(app, layout.getArea3()));
	    break;
	case AreaLayout.LEFT_RIGHT_BOTTOM:
	    tiles.createLeftRightBottom(new Window(app, layout.getArea1()),
					new Window(app, layout.getArea2()),
					new Window(app, layout.getArea3()));
	    break;
	}
	return tiles;
    }
}
