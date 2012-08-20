/*
   Copyright 2012 Michael Pozhidaev <msp@altlinux.org>

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

package com.marigostra.luwrain.core;

public class DispatcherImpl implements Dispatcher
{
    public void launchApplication(Application app)
    {
	Environment.launchApplication(app);
    }

    public void setActiveArea(Object instance, Area area)
    {
	Environment.setActiveArea(instance, area);
    }

    public void onAreaNewHotPoint(Area area, int x, int y)
    {
	Environment.onAreaNewHotPoint(area, x, y);
    }

    public void onNewAreaContent(Area area)
    {
	Environment.onNewAreaContent(area);
    }

    public void quit()
    {
	Environment.quit();
    }
}
