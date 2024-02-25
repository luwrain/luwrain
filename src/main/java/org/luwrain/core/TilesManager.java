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

import static org.luwrain.core.Base.*;
import static org.luwrain.core.NullCheck.*;

final class TilesManager
{
    final AppManager apps;
    private boolean activePopup = false;

    TilesManager(AppManager apps)
    {
	notNull(apps, "apps");
	this.apps = apps;
    }

    boolean setPopupActive()
    {
	if (!isPopupOpened())
	    return false;
	activePopup = true;
	return true;
    }

    void updatePopupState()
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

    Area getActiveArea()
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

boolean isPopupActive()
    {
	if (!activePopup)
	    return false;
	    if (isPopupOpened())
		return true;
	    activePopup = false;
	    return false;
    }

    void activateNextArea()
    {
	Area activeArea = getActiveArea();
	if (activeArea == null)
	    return;
	Object[] objs = getWindows().getObjects();
	final Tile[] tiles = new Tile[objs.length];
	for(int i = 0;i < objs.length;++i)
	    tiles[i] = (Tile)objs[i];
	if (tiles == null || tiles.length <= 0)
	{
	    activePopup = isPopupOpened();
	    return;
	}
	int index;
	for(index = 0;index < tiles.length;index++)
	    if (tiles[index].area == activeArea)
		break;
	index++;
	if (index >= tiles.length)
	    index = 0;
	activePopup = tiles[index].popup;
	if (!activePopup)
	{
	    apps.setActiveAreaOfApp(tiles[index].app, tiles[index].area);
	    apps.setActiveApp(tiles[index].app);
	}
    }

    Tiles getWindows()
    {
	final Application activeApp = apps.getActiveApp();
			final Tiles windows;
	if (activeApp != null)
windows = constructLayoutOfApp(activeApp); else
	    windows = new Tiles();
	if (isPopupOpened())
	{
	    final Tile popupWindow = new Tile(apps.getAppOfLastPopup(), apps.getEffectiveAreaOfLastPopup(), apps.getPositionOfLastPopup());
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

       private  boolean isPopupOpened()
    {
	if (!apps.hasAnyPopup())
	    return false;
	final Application app = apps.getAppOfLastPopup();
	if (app == null)//it is an environment popup
	    return true;
	return apps.isAppActive(app);
    }

    private Tiles constructLayoutOfApp(Application app)
    {
	notNull(app, "app");
	final AreaLayout layout = apps.getFrontAreaLayout(app);
	if (layout == null)
	{
	    warn("got null area layout for the application " + app.getClass().getName());
	    return null;
	}
	final Tiles tiles = new Tiles();
	switch(layout.getLayoutType())
	{
	case AreaLayout.SINGLE:
	    tiles.createSingle(new Tile(app, layout.getArea1()));
	    break;
	case AreaLayout.LEFT_RIGHT:
	    tiles.createLeftRight(new Tile(app, layout.getArea1()),
				  new Tile(app, layout.getArea2()));
	    break;
	case AreaLayout.TOP_BOTTOM:
	    tiles.createTopBottom(new Tile(app, layout.getArea1()),
				  new Tile(app, layout.getArea2()));
	    break;
	case AreaLayout.LEFT_TOP_BOTTOM:
	    tiles.createLeftTopBottom(new Tile(app, layout.getArea1()),
				      new Tile(app, layout.getArea2()),
				      new Tile(app, layout.getArea3()));
	    break;
	case AreaLayout.LEFT_RIGHT_BOTTOM:
	    tiles.createLeftRightBottom(new Tile(app, layout.getArea1()),
					new Tile(app, layout.getArea2()),
					new Tile(app, layout.getArea3()));
	    break;
	}
	return tiles;
    }
}
