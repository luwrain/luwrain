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

package org.luwrain.app.control;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class ControlApp implements Application, ControlActions
{
    private Luwrain luwrain;
    private StringConstructor stringConstructor;
    private ControlGroupsModel groupsModel;
    private TreeArea groupsArea;

    public boolean onLaunch(Luwrain luwrain)
    {
	Object str = luwrain.i18n().getStrings("luwrain.control");
	if (str == null)
	    return false;
	this.luwrain = luwrain;
	stringConstructor = (StringConstructor)str;
	groupsModel = new ControlGroupsModel(luwrain, this, stringConstructor);
	createAreas();
	return true;
    }

    @Override public String getAppName()
    {
	return "control";
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
	luwrain.message(obj.toString());
    }

    public void refreshGroups(Object preferableSelected)
    {
	//FIXME:
    }

    public void close()
    {
	luwrain.closeApp();
    }

    private void createAreas()
    {
	final ControlActions a = this;
	groupsArea = new TreeArea(new DefaultControlEnvironment(luwrain),
				  groupsModel,
				  stringConstructor.groupsAreaName()) {
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
