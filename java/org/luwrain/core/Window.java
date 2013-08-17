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

public class Window
{
    public Application app;
    public Area area;
    public boolean popup = false;
    public int popupPlace;
    public int x = 0;
    public int y = 0;
    public int width = 0;
    public int height = 0;//With title bar;
    public int scrolledVert = 0;
    public int scrolledHoriz = 0;

    public Window(Application app,
		  Area area)
    {
	this.app = app;
	this.area = area;
	this.popup = false;
    }

    public Window(Application app,
		  Area area,
		  int popupPlace)
    {
	this.app = app;
	this.area = area;
	this.popup = true;
	this.popupPlace = popupPlace;
    }

    public void markInvisible()
    {
	x = 0;
	y = 0;
	width = 0;
	height = 0;
    }

    public boolean isVisible()
    {
	return x >= 0 && y >= 0 && width > 0 && height > 0;
    }

    public boolean valid()
    {
	return isVisible();
    }
}
