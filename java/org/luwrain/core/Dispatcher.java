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
    static private Environment environment;

    public static boolean setEnvironmentObject(Environment e)
    {
	if (e == null)
	    return false;
	if (environment != null)
	{
	    Log.error("environment", "environment object for the Luwrain class is already assigned, somebody tries to set another one and that is suspicious");
	    return false;
	}
	environment = e;
	return true;
    }

    static public void enqueueEvent(Event e)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.enqueueEvent(e);
    }

    static public void launchApplication(Application app)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.launchApplication(app);
    }

static public     void closeApplication(Object instance)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.closeApplication(instance);
    }

    //Not for popup areas, only standard areas of applications;
    static public void setActiveArea(Object instance, Area area)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.setActiveArea(instance, area);
    }

    //Never produces any speech output automatically;
    static public void onAreaNewHotPoint(Area area)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.onAreaNewHotPoint(area);
    }

    //Never produces any speech output automatically;
    static public void onAreaNewContent(Area area)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.onAreaNewContent(area);
    }

    //Never produces any speech output automatically;
    static public void onAreaNewName(Area area)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.onAreaNewName(area);
    }

    //May return -1 if area is not shown on the screen;
    static public int getAreaVisibleHeight(Area area)
    {
	if (!checkEnvironmentInstance())
	    return -1;
	return environment.getAreaVisibleHeight(area);
    }

    static public void quit()
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.quit();
    }

    static public void message(String text)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.message(text);
    }

    static public void open(String[] fileNames)
    {
	if (!checkEnvironmentInstance())
	    return;
	environment.openFileNames(fileNames);
    }

    static public org.luwrain.core.registry.Registry getRegistry()
    {
	if (!checkEnvironmentInstance())
	    return null;
	return environment.getRegistry();
    }

static private boolean checkEnvironmentInstance()
    {
	if (environment != null)
	    return true;
	Log.error("environment", "no default environment object, probably Luwrain was not properly initialized");
	return false;
    }
}
