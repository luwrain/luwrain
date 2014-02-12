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

package org.luwrain.app.control;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class ControlApp implements Application, ControlActions
{
    private Object instance;
    private StringConstructor stringConstructor;
    private ControlGroupsModel groupsModel;
    private TreeArea groupsArea;

    public boolean onLaunch(Object instance)
    {
	if (instance == null)
	    return false;
	Object str = Langs.requestStringConstructor("control");
	if (str == null)
	    return false;
	stringConstructor = (StringConstructor)str;
	this.instance = instance;
	groupsModel = new ControlGroupsModel(this, stringConstructor);
	createAreas();
	return true;
    }

    public AreaLayout getAreasToShow()
    {
	//	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, groupArea, summaryArea, messageArea);
	return new AreaLayout(groupsArea);
    }

    public void gotoGroups()
    {
    }

    public void gotoOptions()
    {
    }

    public void openGroup(Object obj)
    {
	if (obj != null)//FIXME:
	Luwrain.message(obj.toString());
    }

    public void refreshGroups(Object preferableSelected)
    {
	//FIXME:
    }

    public void close()
    {
	Luwrain.closeApp(instance);
    }

    private void createAreas()
    {
	final ControlActions a = this;
	groupsArea = new TreeArea(groupsModel, stringConstructor.groupsAreaName()) {
		private ControlActions actions = a;
		public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    if (super.onKeyboardEvent(event))
			return true;
		    //Insert;
		    if (event.isCommand() && event.getCommand() == KeyboardEvent.INSERT &&
			!event.isModified())
		    {
			ControlGroupsModel model = (ControlGroupsModel)getModel();
			model.insertItem();//FIXME:what selected
			return true;
		    }

		    //Tab;
		    if (event.isCommand() && event.getCommand() == KeyboardEvent.TAB &&
			!event.isModified())
		    {
			actions.gotoOptions();
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
		    if (obj != null)
			actions.openGroup(obj);
		}
	    };
    }
}
