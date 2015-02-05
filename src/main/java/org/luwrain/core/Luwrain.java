/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

//No input data checking here, everything in Environment class

import java.io.File;

public class Luwrain
{
    private Environment environment;

    public Luwrain(Environment environment)
    {
	this.environment = environment;
    }

    public void enqueueEvent(Event e)
    {
	environment.enqueueEvent(e);
    }

    public void launchApp(Application app)
    {
	environment.launchApp(app);
    }
    
public     void closeApp()
    {
	environment.closeApp(this);
    }

    //Not for popup areas, only standard areas of applications;
    //Introduces new area in contrast with onAreaNewContent, onAreaNewHotPoint and onAreaNewName  
    public void setActiveArea(Area area)
    {
	environment.setActiveArea(this, area);
    }

    //Never produces any speech output automatically;
    public void onAreaNewHotPoint(Area area)
    {
	environment.onAreaNewHotPoint(area);
    }

    //Never produces any speech output automatically;
    public void onAreaNewContent(Area area)
    {
	environment.onAreaNewContent(area);
    }

    //Never produces any speech output automatically;
public void onAreaNewName(Area area)
    {
	environment.onAreaNewName(area);
    }

    //May return -1 if area is not shown on the screen;
    public int getAreaVisibleHeight(Area area)
    {
	return environment.getAreaVisibleHeight(area);
    }

    public void quit()
    {
	environment.quit();
    }

    public void message(String text)
    {
	environment.message(text);
    }

    /**
     * @param name The desired popup name, can be null if default value is required
     * @param prefix The desired input prefix, can be null if default value is required
     * @param defaultValue The desired default value, can be null to use the user home directory path
     */
    public File openPopup(String name,
			  String prefix,
			  File defaultValue)
    {
	return environment.openPopup(this, name, prefix, defaultValue);
    }

    public void openFile(String fileName)
    {
	String[] s = new String[1];
	s[0] = fileName;
	environment.openFiles(s);
    }

    public void openFiles(String[] fileNames)
    {
	environment.openFiles(fileNames);
    }

    public void popup(Popup popup)
    {
	environment.popup(popup);
    }

    public org.luwrain.core.Registry getRegistry()
    {
	return environment.getRegistry();
    }

    public Object getPimManager()
    {
	return environment.getPimManager();
    }

    public void setClipboard(String[] value)
    {
	environment.setClipboard(value);
    }

    public String[] getClipboard()
    {
	return environment.getClipboard();
    }

    public void say(String text)
    {
	//FIXME:
    }

    public void sayLetter(char letter)
    {
    }

    public void hint(String text)
    {

    }

    public void hint(String text, int code)
    {
    }

    public void hint(int code)
    {
    }

    public void silence()
    {
    }
}
