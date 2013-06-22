/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

import org.luwrain.core.events.*;

public class ScreenContentManager
{
    private ApplicationRegistry applications;
    private PopupRegistry popups;
    private Application systemApp;
    private boolean activePopup = false;

    public ScreenContentManager(ApplicationRegistry applications,
				PopupRegistry popups,
Application systemApp)
    {
	this.applications = applications;
	this.popups = popups;
	this.systemApp = systemApp;
    }

    public  boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (activePopup)
	{
	    if (hasProperPopup())
		return popups.getAreaOfLastPopup().onKeyboardEvent(event);
	    activePopup = false;
	}
	Area activeArea = applications.getActiveAreaOfActiveApp();
	if (activeArea != null)
	    return activeArea.onKeyboardEvent(event);
	return false;
    }

    public  boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (activePopup)
	{
	    if (hasProperPopup())
		return popups.getAreaOfLastPopup().onEnvironmentEvent(event);
	    activePopup = false;
	}
	Area activeArea = applications.getActiveAreaOfActiveApp();
	if (activeArea != null)
	    return activeArea.onEnvironmentEvent(event);
	return false;
    }

    public boolean setPopupAreaActive()
    {
	if (!hasProperPopup())
	    return false;
	activePopup = true;
	return true;
    }

    public boolean setPopupInactive()
    {
	if (applications.getActiveAreaOfActiveApp() == null)
	    return !hasProperPopup();
	activePopup = false;
	return false;
    }

    public boolean isPopupAreaActive()
    {
	if (!hasProperPopup())
	{
	    activePopup = false;
	    return false;
	}
	return activePopup;
    }

    public void updatePopupState()
    {
	if (!hasProperPopup())
	    activePopup = false;
    }

    public void introduceActiveArea()
    {
	//Popups only if not stopCondition;
	if (activePopup)
	{
	    if (hasProperPopup())
	    {
		Speech.say(popups.getAreaOfLastPopup().getName());
		return;
	    }
	    activePopup = false;
	}
	Area activeArea = applications.getActiveAreaOfActiveApp();
	if (activeArea != null)
	    Speech.say(activeArea.getName());
    }

    public void activateNextArea()
    {
	if (activePopup)
	{
	    if (applications.getActiveAreaOfActiveApp() != null)
		activePopup = false;
	    return;
	}
	Area activeArea = applications.getActiveAreaOfActiveApp();
	Window[] windows = (Window[])getWindows().getObjects();
	if (windows == null || windows.length <= 0)
	{
	    activePopup = hasProperPopup();
	    return;
	}
	int index;
	for(index = 0;index < windows.length;index++)
	    if (windows[index].area == activeArea)
		break;
	index++;
	if (index >= windows.length)
	    index = 0;
	if (windows[index].popup)
	{
	    activePopup = true;
	    return;
	}
	applications.setActiveAreaOfApp(windows[index].app, windows[index].area);
	applications.setActiveApp(windows[index].app);
    }

    TileManager getWindows()
    {
	TileManager windows = new TileManager();
	Application[] visibleApps = applications.getVisibleApps();
	windows.createHorizontally(visibleApps);
	for(int i = 0;i < visibleApps.length;i++)
	    windows.replace(visibleApps[i], constructWindowLayoutOfApp(visibleApps[i]));
	if (hasProperPopup())
	{
	    Window popupWindow = createPopupWindow();
	    switch(popups.getPositionOfLastPopup())
	    {
	    case PopupRegistry.BOTTOM:
		windows.addBottom(popupWindow);
		break;
	    case PopupRegistry.TOP:
		windows.addTop(popupWindow);
		break;
	    case PopupRegistry.LEFT:
		windows.addLeftSide(popupWindow);
		break;
	    case PopupRegistry.RIGHT:
		windows.addRightSide(popupWindow);
		break;
	    }
	}
	return windows;
    }

    private boolean hasProperPopup()
    {
	return popups.hasPopups() && (applications.isVisibleApp(popups.getAppOfLastPopup()) || popups.getAppOfLastPopup() == systemApp);
    }

    private Window createPopupWindow()
    {
	Window popupWindow = new Window(popups.getAppOfLastPopup(), popups.getAreaOfLastPopup(), true);
	return popupWindow;
    }

    private TileManager constructWindowLayoutOfApp(Application app)
    {
	if (app == null)
	    return null;
	AreaLayout layout = app.getAreasToShow();
	TileManager tiles = new TileManager();
	switch(layout.getType())
	{
	case AreaLayout.SINGLE:
	    tiles.createSingle(new Window(app, layout.getArea1(), false));
	    break;
	    //FIXME:LEFT_RIGHT;
	    //FIXME:TOP_BOTTOM;
	case AreaLayout.LEFT_TOP_BOTTOM:
	    tiles.createLeftTopBottom(new Window(app, layout.getArea1(), false),
				      new Window(app, layout.getArea2(), false),
				      new Window(app, layout.getArea3(), false));
	    break;
	case AreaLayout.LEFT_RIGHT_BOTTOM:
	    tiles.createLeftRightBottom(new Window(app, layout.getArea1(), false),
					new Window(app, layout.getArea2(), false),
					new Window(app, layout.getArea3(), false));
	    break;
	}
	return tiles;
    }
}
