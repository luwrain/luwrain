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
    public static final String STRINGS_NAME = "luwrain.cpanel";

    private Luwrain luwrain;
    private Strings strings;
    private EnvironmentImpl environment;
    private Section[] extensionsSections;
    private SectionsTreeModel sectionsModel;
    private TreeArea sectionsArea;
    private Section currentSection = null;
    private Area currentOptionsArea = null;

    public ControlPanelApp(Section[] extensionsSections)
    {
	this.extensionsSections = extensionsSections;
	if (extensionsSections == null)
	    throw new NullPointerException("extensionsSections may not be null");
    }

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	final Object str = luwrain.i18n().getStrings(STRINGS_NAME);
	if (str == null || !(str instanceof Strings))
	    return false;
	this.luwrain = luwrain;
	strings = (Strings)str;
	environment = new EnvironmentImpl(luwrain);
	sectionsModel = new SectionsTreeModel(luwrain, environment, extensionsSections);
	createAreas();
	return true;
    }

    @Override public String getAppName()
    {
	return "control";
    }

    @Override public void openSection(Section section)
    {
	final Area area = section.getSectionArea(environment);
	if (area == null)
	    return;
	currentSection = section;
	currentOptionsArea = area;
	luwrain.onNewAreaLayout();
	gotoOptions();
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
		    if (obj != null && (obj instanceof Section ))
			actions.openSection((Section)obj);
		}
	    };
    }

    @Override public AreaLayout getAreasToShow()
    {
	if (currentOptionsArea != null)
	    return new AreaLayout(AreaLayout.LEFT_RIGHT, sectionsArea, currentOptionsArea);
	return new AreaLayout(sectionsArea);
    }

    @Override public void gotoSections()
    {
	luwrain.setActiveArea(sectionsArea);
    }

    @Override public void gotoOptions()
    {
	if (currentOptionsArea == null)
	    return;
	luwrain.setActiveArea(currentOptionsArea);
    }
}
