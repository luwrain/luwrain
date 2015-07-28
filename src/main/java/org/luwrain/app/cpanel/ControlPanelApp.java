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

package org.luwrain.app.cpanel;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

public class ControlPanelApp implements Application, Actions
{
    public static final String STRINGS_NAME = "luwrain.strings";

    private Luwrain luwrain;
    private Strings strings;
    private SectionsTreeModel sectionsModel;
    private TreeArea sectionsArea;
    private Section currentSection;
    private Area currentArea;

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	System.out.println("Starting cpanel");
	Object str = luwrain.i18n().getStrings(STRINGS_NAME);
	if (str == null || !(str instanceof Strings))
	    return false;
	System.out.println("Have strings");
	this.luwrain = luwrain;
	strings = (Strings)str;
	sectionsModel = new SectionsTreeModel();
	createAreas();
	return true;
    }

    @Override public String getAppName()
    {
	return "control";
    }

    @Override public AreaLayout getAreasToShow()
    {
	//	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, groupArea, summaryArea, messageArea);
	return new AreaLayout(sectionsArea);
    }

    @Override public void gotoSections()
    {
    }

    @Override public void gotoOptions()
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

    @Override public void closeApp()
    {
	luwrain.closeApp();
    }

    private void createAreas()
    {
	final Actions a = this;
	sectionsArea = new TreeArea(new DefaultControlEnvironment(luwrain),
				  sectionsModel,
				  strings.groupsAreaName()) {
		private Actions actions = a;
		public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    if (super.onKeyboardEvent(event))
			return true;
		    //Insert;
		    if (event.isCommand() && event.getCommand() == KeyboardEvent.INSERT &&
			!event.isModified())
		    {
			SectionsTreeModel model = (SectionsTreeModel)getModel();
			//			model.insertItem();//FIXME:what selected
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
			actions.closeApp();
			return true;
		    }
		    return false;
		}
		public void onClick(Object obj)
		{
		    //		    if (obj != null)
			//			actions.openGroup(obj);
		}
	    };
    }
}
