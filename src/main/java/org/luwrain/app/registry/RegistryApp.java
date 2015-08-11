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

//FIXME:Delete directory;
//FIXME:Rename directory;
//FIXME:Refresh on inserting;
//FIXME:Saving values on values inserting;

package org.luwrain.app.registry;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.popups.*;

public class RegistryApp implements Application, Actions
{
    private Luwrain luwrain;
    private Strings strings;
    private DirectoriesTreeModel dirsModel;
    private TreeArea dirsArea;
    private ValuesArea valuesArea;
    private Registry registry;

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	Object str = luwrain.i18n().getStrings("luwrain.registry");
	if (str == null)
	    return false;
	strings = (Strings)str;
	this.luwrain = luwrain;
	this.registry = luwrain.getRegistry();
	dirsModel = new DirectoriesTreeModel(luwrain, this, strings);
	createAreas();
	return true;
    }

    @Override public String getAppName()
    {
	return "registry";
    }

    @Override public AreaLayout getAreasToShow()
    {
	//	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, groupArea, summaryArea, messageArea);
	return new AreaLayout(AreaLayout.LEFT_RIGHT, dirsArea, valuesArea);
    }

    public void gotoDirs()
    {
	luwrain.setActiveArea(dirsArea);
    }

    public void gotoValues()
    {
	luwrain.setActiveArea(valuesArea);
    }

    public void refresh()
    {
	dirsArea.refresh();//FIXME:Comparator;
	valuesArea.refresh();
    }

    public void openDir(Directory dir)
    {
	if (dir == null || dir.equals(valuesArea.getOpenedDir()))
	    return;
	if (valuesArea.hasModified())
	{
	    YesNoPopup popup = new YesNoPopup(luwrain, "Saving values", "Are you want to loose changes?", true);//FIXME:
	    luwrain.popup(popup);
	    if (popup.closing.cancelled() || !popup.result())
	    {
		gotoValues();
		return;
	    }
	}
	    valuesArea.open(dir);
	    luwrain.setActiveArea(valuesArea);
    }

    public void insertDir(Directory parent)
    {
	SimpleEditPopup popup = new SimpleEditPopup(luwrain, strings.newDirectoryTitle(), strings.newDirectoryPrefix(parent.toString()), "");//FIXME:Validator if not empty;
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return;
	if (popup.text().trim().isEmpty())
	{
	    luwrain.message(strings.directoryNameMayNotBeEmpty());
	    return;
	}
	if (popup.text().indexOf("/") >= 0)
	{
	    luwrain.message(strings.directoryInsertionRejected(parent.toString(), popup.text()));
	    return;
	}
	if (!registry.addDirectory(parent.getPath() + "/" + popup.text()))
	{
	    luwrain.message(strings.directoryInsertionRejected(parent.toString(), popup.text()));
	    return;
	}
	    dirsArea.refresh();

    }

    public void close()
    {
	luwrain.closeApp();
    }

    private void createAreas()
    {
	final Actions a = this;
	dirsArea = new TreeArea(new DefaultControlEnvironment(luwrain),
				dirsModel,
				strings.dirsAreaName()) {
		private Actions actions = a;
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
			Directory dir;
			try {
			    dir = (Directory)obj;
			}
			catch (ClassCastException e)
			{
			    Log.warning("registry-app", "tree returned the object of type different than expected (Directory):" + e.getMessage());
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
		    Directory dir;
		    try {
			dir = (Directory)obj;
		    }
		    catch(ClassCastException e)
		    {
			Log.warning("registry-app", "tree returned the object of type different than expected (Directory):" + e.getMessage());
			e.printStackTrace();
			return;
		    }
		    actions.openDir(dir);
		}
	    };
	valuesArea = new ValuesArea(luwrain, registry, this, strings);
    }
}
