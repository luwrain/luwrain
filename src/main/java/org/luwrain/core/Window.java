/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

class Window
{
    Application app;
    Area area;
    boolean popup = false;
Popup.Position popupPos;
    int x = 0;
    int y = 0;
    int width = 0;
    int height = 0;//With title bar;
    int scrolledVert = 0;
    int scrolledHoriz = 0;

    Window(Application app, Area area)
    {
	this.app = app;
	this.area = area;
	this.popup = false;
    }

    Window(Application app, Area area,
	   Popup.Position popupPos)
    {
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
