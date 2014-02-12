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
import org.luwrain.core.registry.Registry;
import org.luwrain.controls.*;
import org.luwrain.popups.SimpleLinePopup;

public class RegistryApp implements Application, RegistryActions
{
    private Object instance;
    private StringConstructor stringConstructor;
    private RegistryDirsModel dirsModel;
    private TreeArea dirsArea;
    private ValuesArea valuesArea;
    private Registry registry = Luwrain.getRegistry();

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
	return new AreaLayout(AreaLayout.LEFT_RIGHT, dirsArea, valuesArea);
    }

    public void gotoDirs()
    {
	Luwrain.setActiveArea(instance, dirsArea);
    }

    public void gotoValues()
    {
	Luwrain.setActiveArea(instance, valuesArea);
    }

    public void refresh()
    {
	dirsArea.refresh();//FIXME:Comparator;
	valuesArea.refresh();
    }

    public void openDir(RegistryDir dir)
    {
	if (dir == null)
	    return;
	    valuesArea.open(dir);
	    Luwrain.setActiveArea(instance, valuesArea);
    }

    public void insertDir(RegistryDir parent)
    {
	SimpleLinePopup popup = new SimpleLinePopup(instance, stringConstructor.newDirectoryTitle(), stringConstructor.newDirectoryPrefix(parent.toString()), "");//FIXME:Validator if not empty;
	Luwrain.popup(instance, popup, popup.closing);
	if (popup.closing.cancelled())
	    return;
	if (popup.getText().trim().isEmpty())
	{
	    Luwrain.message(stringConstructor.directoryNameMayNotBeEmpty());
	    return;
	}
	if (popup.getText().indexOf("/") >= 0)
	{
	    Luwrain.message(stringConstructor.directoryInsertionRejected(parent.toString(), popup.getText()));
	    return;
	}
	if (!registry.addDirectory(parent.getPath() + "/" + popup.getText()))
	{
	    Luwrain.message(stringConstructor.directoryInsertionRejected(parent.toString(), popup.getText()));
	    return;
	}
    }

    public void close()
    {
	Luwrain.closeApp(instance);
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
		    if (event.isCommand() && !event.isModified() &&
			event.getCommand() == KeyboardEvent.INSERT)
		    {
			Object obj = getObjectUnderHotPoint();
			if (obj == null)
			    return false;
			RegistryDir dir;
			try {
			    dir = (RegistryDir)obj;
			}
			catch (ClassCastException e)
			{
			    Log.warning("registry-app", "tree returned the object of type different than expected (RegistryDir):" + e.getMessage());
			    return true;
			}
			actions.insertDir(dir);
			return true;
		    }
		    //Tab;
		    if (event.isCommand() && !event.isModified() &&
			event.getCommand() == KeyboardEvent.TAB)
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
		    case EnvironmentEvent.REFRESH:
			actions.refresh();
			return true;
		    }
		    return false;
		}
		public void onClick(Object obj)
		{
		    if (obj == null)
			return;
		    RegistryDir dir;
		    try {
			dir = (RegistryDir)obj;
		    }
		    catch(ClassCastException e)
		    {
			Log.warning("registry-app", "tree returned the object of type different than expected (RegistryDir):" + e.getMessage());
			e.printStackTrace();
			return;
		    }
		    actions.openDir(dir);
		}
	    };
	valuesArea = new ValuesArea(instance, registry, this, stringConstructor);
    }
}
