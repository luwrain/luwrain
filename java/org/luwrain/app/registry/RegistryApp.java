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

package org.luwrain.app.registry;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class RegistryApp implements Application, RegistryActions
{
    private Object instance;
    private StringConstructor stringConstructor;
    private RegistryDirsModel dirsModel;
    private TreeArea dirsArea;

    public boolean onLaunch(Object instance)
    {
	if (instance == null)
	    return false;
	Object str = Langs.requestStringConstructor("registry");
	if (str == null)
	    return false;
	stringConstructor = (StringConstructor)str;
	this.instance = instance;
	dirsModel = new RegistryDirsModel(this, stringConstructor);
	createAreas();
	return true;
    }

    public AreaLayout getAreasToShow()
    {
	//	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, groupArea, summaryArea, messageArea);
	return new AreaLayout(dirsArea);
    }

    public void gotoDirs()
    {
    }

    public void gotoValues()
    {
    }

    public void openDir(RegistryDir dir)
    {
    }

    public void close()
    {
	Luwrain.closeApplication(instance);
    }

    private void createAreas()
    {
	final RegistryActions a = this;
	dirsArea = new TreeArea(dirsModel, stringConstructor.dirsAreaName()) {
		private RegistryActions actions = a;
		public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    if (super.onKeyboardEvent(event))
			return true;
		    //Insert;
		    if (event.isCommand() && event.getCommand() == KeyboardEvent.INSERT &&
			!event.isModified())
		    {
			//FIXME:
			return true;
		    }
		    //Tab;
		    if (event.isCommand() && event.getCommand() == KeyboardEvent.TAB &&
			!event.isModified())
		    {
			actions.gotoValues();
			return true;
		    }
		    return false;
		}
		public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    switch (event.getCode())
		    {
		    case EnvironmentEvent.CLOSE:
			actions.close();
			return true;
		    }
		    return false;
		}
		public void onClick(Object obj)
		{
		    //FIXME:
		}
	    };
    }
}
