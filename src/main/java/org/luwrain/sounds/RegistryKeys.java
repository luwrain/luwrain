/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.sounds;

import java.util.Properties;
import java.net.URL;
import java.io.IOException;

import org.luwrain.core.Log;

class RegistryKeys
{
    private static final String REGISTRY_KEYS_RESOURCE = "org/luwrain/sounds/registry-keys.properties";

    private Properties properties;

    public RegistryKeys()
    {
	URL url = ClassLoader.getSystemResource(REGISTRY_KEYS_RESOURCE);
	if (url == null)
	{
	    Log.error("sounds", "no resource with name " + REGISTRY_KEYS_RESOURCE + " needed for registry keys loading");
	    return;
	}
	properties = new Properties();
	try {
	    properties.load(url.openStream());
	}
	catch (IOException e)
	{
	    Log.error("sounds", "error loading of properties with registry keys:" + e.getMessage());
	    e.printStackTrace();
	}
    }

    public String eventNotProcessed()
    {
	return getProperty("sounds.EventNotProcessed");
    }

    public String noApplications()
    {
	return getProperty("sounds.NoApplications");
    }

    public String startup()
    {
	return getProperty("sounds.Startup");
    }

    public String shutdown()
    {
	return getProperty("sounds.Shutdown");
    }

    public String mainMenu()
    {
	return getProperty("sounds.MainMenu");
    }

    public String mainMenuItem()
    {
	return getProperty("sounds.MainMenuItem");
    }

    public String mainMenuEmptyItem()
    {
	return getProperty("sounds.MainMenuEmptyItem");
    }

    public String generalError()
    {
	return getProperty("sounds.GeneralError");
    }

    public String generalOk()
    {
	return getProperty("sounds.GeneralOk");
    }

    public String introRegular()
    {
	return getProperty("sounds.IntroRegular");
    }

    public String introPopup()
    {
	return getProperty("sounds.IntroPopup");
    }

    public String introApp()
    {
	return getProperty("sounds.IntroApp");
    }

    public String noItemsAbove()
    {
	return getProperty("sounds.NoItemsAbove");
    }

    public String noItemsBelow()
    {
	return getProperty("sounds.NoItemsBelow");
    }

    public String noLinesAbove()
    {
	return getProperty("sounds.NoLinesAbove");
    }

    public String noLinesBelow()
    {
	return getProperty("sounds.NoLinesBelow");
    }

    public String commanderNewLocation()
    {
	return getProperty("sounds.CommanderNewLocation");
    }

    public String newListItem()
    {
	return getProperty("sounds.NewListItem");
    }

    public String generalTime()
    {
	return getProperty("sounds.GeneralTime");
    }

    public String termBell()
    {
	return getProperty("sounds.TermBell");
    }

    private String getProperty(String name)
    {
	if (name == null)
	    throw new NullPointerException("name may not be null");
	if (name.isEmpty())
	    throw new IllegalArgumentException("name may not be empty");
	if (properties == null)
	    throw new NullPointerException("properties object is null");
	final String value = properties.getProperty(name);
	if (value == null)
	    throw new NullPointerException("property value is null");
	return value;
    }
};
