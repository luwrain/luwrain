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
import org.luwrain.mmedia.*;

public class ScreenContentManager
{
    public static final int NO_APPLICATIONS = 0;
    public static final int EVENT_NOT_PROCESSED = 1;
    public static final int EVENT_PROCESSED = 2;

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

    public  int onKeyboardEvent(KeyboardEvent event)
    {
	if (activePopup)
	{
	    if (hasProperPopup())
		return popups.getAreaOfLastPopup().onKeyboardEvent(event)?EVENT_PROCESSED:EVENT_NOT_PROCESSED;
	    activePopup = false;
	}
	Area activeArea = applications.getActiveAreaOfActiveApp();
	if (activeArea != null)
	    return activeArea.onKeyboardEvent(event)?EVENT_PROCESSED:EVENT_NOT_PROCESSED;
	return NO_APPLICATIONS;
    }

    public  int onEnvironmentEvent(EnvironmentEvent event)
    {
	if (activePopup)
	{
	    if (hasProperPopup())
		return popups.getAreaOfLastPopup().onEnvironmentEvent(event)?EVENT_PROCESSED:EVENT_NOT_PROCESSED;
	    activePopup = false;
	}
	Area activeArea = applications.getActiveAreaOfActiveApp();
	if (activeArea != null)
	    return activeArea.onEnvironmentEvent(event)?EVENT_PROCESSED:EVENT_NOT_PROCESSED;
	return NO_APPLICATIONS;
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
	//FIXME:Popups only if not stopCondition;
	//FIXME:Introduce area environment event;

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
	    Speech.say(activeArea.getName()); else
	{
	    EnvironmentSounds.play(EnvironmentSounds.NO_APPLICATIONS);
	    Speech.say("Запущенных приложений нет");//FIXME:
	}
    }

    public Area getActiveArea()
    {
	if (activePopup)
	{
	    if (hasProperPopup())
		return popups.getAreaOfLastPopup();
	    activePopup = false;
	}
return applications.getActiveAreaOfActiveApp();
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
	    Window popupWindow = new Window(popups.getAppOfLastPopup(), popups.getAreaOfLastPopup(), popups.getPositionOfLastPopup());
	    switch(popupWindow.popupPlace)
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

    private TileManager constructWindowLayoutOfApp(Application app)
    {
	if (app == null)
	    return null;
	AreaLayout layout = app.getAreasToShow();
	TileManager tiles = new TileManager();
	switch(layout.getType())
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
