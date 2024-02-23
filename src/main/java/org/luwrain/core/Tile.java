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

import static org.luwrain.core.NullCheck.*;

final class Tile
{
    final Application app;
    final Area area;
    final boolean popup;
    final Popup.Position popupPos;
    int x = 0, y = 0, width = 0, height = 0;//With title bar;
    int scrolledVert = 0, scrolledHoriz = 0;

    Tile(Application app, Area area)
    {
	//app can be null
	notNull(area, "area");
	this.app = app;
	this.area = area;
	this.popup = false;
	this.popupPos = null;
    }

    Tile(Application app, Area area, Popup.Position popupPos)
    {
	//app can be null
	notNull(area, "area");
	notNull(popupPos, "popupPos");
	this.app = app;
	this.area = area;
	this.popup = true;
	this.popupPos = popupPos;
    }

    void markInvisible()
    {
	x = 0;
	y = 0;
	width = 0;
	height = 0;
    }

    boolean isVisible()
    {
	return x >= 0 && y >= 0 && width > 0 && height > 0;
    }

    boolean valid()
    {
	return isVisible();
    }
}
