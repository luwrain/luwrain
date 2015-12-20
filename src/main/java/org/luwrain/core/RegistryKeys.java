/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

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

import java.util.Properties;
import java.net.URL;
import java.io.IOException;

public class RegistryKeys
{
    private static final String REGISTRY_KEYS_RESOURCE = "org/luwrain/core/registry-keys.properties";

    private Properties properties;

    public RegistryKeys()
    {
	URL url = ClassLoader.getSystemResource(REGISTRY_KEYS_RESOURCE);
	if (url == null)
	{
	    Log.error("core", "no resource with name " + REGISTRY_KEYS_RESOURCE + " needed for registry keys loading");
	    return;
	}
	properties = new Properties();
	try {
	    properties.load(url.openStream());
	}
	catch (IOException e)
	{
	    Log.error("core", "error loading of properties with registry keys:" + e.getMessage());
	    e.printStackTrace();
	}
    }

    public String launchGreeting()
    {
	return "/org/luwrain/launch-greeting";//FIXME:
    }

    public String desktopIntroductionFile()
    {
	return "/org/luwrain/desktop/introduction-file";//FIXME:
    }

    public String mainMenuContent()
    {
	return getProperty("mainmenu.Content");
    }

    public String soundsDir()
    {
	return getProperty("environment.Sounds");
    }

    public String globalKeysDir()
    {
	return getProperty("environment.GlobalKeys");
    }

    public String personalFullName()
    {
	return getProperty("personal.FullName");
    }

    public String personalDefaultMailAddress()
    {
	return getProperty("personal.DefaultMailAddress");
    }

    public String personalSignature()
    {
	return getProperty("personal.Signature");
    }

    public String interactionWndLeft()
    {
	return getProperty("interaction.WindowLeft");
    }

    public String interactionWndTop()
    {
	return getProperty("interaction.WindowTop");
    }

    public String interactionWndWidth()
    {
	return getProperty("interaction.WindowWidth");
    }

    public String interactionWndHeight()
    {
	return getProperty("interaction.WindowHeight");
    }

    public String interactionMarginLeft()
    {
	return getProperty("interaction.MarginLeft");
    }

    public String interactionMarginTop()
    {
	return getProperty("interaction.MarginTop");
    }

    public String interactionMarginRight()
    {
	return getProperty("interaction.MarginRight");
    }

    public String interactionMarginBottom()
    {
	return getProperty("interaction.MarginBottom");
    }

    public String interactionFontColorRed()
    {
	return getProperty("interaction.FontColorRed");
    }

    public String interactionFontColorGreen()
    {
	return getProperty("interaction.FontColorGreenn");
    }

    public String interactionFontColorBlue()
    {
	return getProperty("interaction.FontColorBlue");
    }

    public String interactionBkgColorRed()
    {
	return getProperty("interaction.BkgColorRed");
    }

    public String interactionBkgColorGreen()
    {
	return getProperty("interaction.BkgColorGreen");
    }

    public String interactionBkgColorBlue()
    {
	return getProperty("interaction.BkgColorBlue");
    }

    public String interactionSplitterColorRed()
    {
	return getProperty("interaction.SplitterColorRed");
    }

    public String interactionSplitterColorGreen()
    {
	return getProperty("interaction.SplitterColorGreen");
    }

    public String interactionSplitterColorBlue()
    {
	return getProperty("interaction.SplitterColorBlue");
    }

	public String interactionInitialFontSize()
    {
	return getProperty("interaction.InitialFontSize");
    }

    public String interactionFontName()
    {
	return getProperty("interaction.FontName");
    }

    public String speechCharsToSkip()
    {
	return getProperty("speech.CharsToSkip");
    }

    public String commandsOs()
    {
	return getProperty("commands.OS");
    }

    public String fileTypes()
    {
	return getProperty("environment.FileTypes");
    }

    public String ui()
    {
	return "/org/luwrain/ui";//FIXME:
    }

    private String getProperty(String name)
    {
	if (name == null)
	    throw new NullPointerException("name may not be null");
	if (name.isEmpty())
	    throw new IllegalArgumentException("name may not be empty");
	//	Log.debug("param", name);
	if (properties == null)
	    throw new NullPointerException("properties object is null");
	final String value = properties.getProperty(name);
	if (value == null)
	    throw new NullPointerException("property value is null");
	return value;
    }
};
