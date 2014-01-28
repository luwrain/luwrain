/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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

public class Dispatcher
{
    static public void enqueueEvent(Event e)
    {
	Environment.enqueueEvent(e);
    }

    static public void launchApplication(Application app)
    {
	Environment.launchApplication(app);
    }

static public     void closeApplication(Object instance)
    {
	Environment.closeApplication(instance);
    }


    //Not for popup areas, only standard areas of applications;
    static public void setActiveArea(Object instance, Area area)
    {
	Environment.setActiveArea(instance, area);
    }

    //Never produces any speech output automatically;
    static public void onAreaNewHotPoint(Area area)
    {
	Environment.onAreaNewHotPoint(area);
    }

    //Never produces any speech output automatically;
    static public void onAreaNewContent(Area area)
    {
	Environment.onAreaNewContent(area);
    }

    //Never produces any speech output automatically;
    static public void onAreaNewName(Area area)
    {
	Environment.onAreaNewName(area);
    }

    //May return -1 if area is not shown on the screen;
    static public int getAreaVisibleHeight(Area area)
    {
	return Environment.getAreaVisibleHeight(area);
    }

    static public void quit()
    {
	Environment.quit();
    }

    static public void message(String text)
    {
	Environment.message(text);
    }

    static public void open(String[] fileNames)
    {
	Environment.openFileNames(fileNames);
    }

    static public org.luwrain.core.registry.Registry getRegistry()
    {
	//FIXME:
	return null;
    }
}
