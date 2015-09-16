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

package org.luwrain.app.cpanel;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

public class ControlPanelApp implements Application, Actions
{
    static public final String STRINGS_NAME = "luwrain.cpanel";

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
	NullCheck.notNull(extensionsSections, "extensionsSections"); 
    }

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	final Object str = luwrain.i18n().getStrings(STRINGS_NAME);
	if (str == null || !(str instanceof Strings))
	    return false;
	strings = (Strings)str;
	this.luwrain = luwrain;
	environment = new EnvironmentImpl(luwrain);
	sectionsModel = new SectionsTreeModel(environment, strings, extensionsSections);
	createArea();
	return true;
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

    @Override public void openSection(Section section)
    {
luwrain.message("open");
/*
	final Area area = section.getSectionArea(environment);
	if (area == null)
	    return;
	currentSection = section;
	currentOptionsArea = area;
	luwrain.onNewAreaLayout();
	gotoOptions();
*/
    }

@Override public boolean onSectionsInsert()
{
luwrain.message("insert");
return true;
}

@Override public boolean onSectionsDelete()
{
luwrain.message("delete");
return true;
}

    void refreshGroups(Object preferableSelected)
    {
	//FIXME:
    }

    @Override public void closeApp()
    {
	//FIXME:
	luwrain.closeApp();
    }

    private void createArea()
    {
	final Actions a = this;
	sectionsArea = new TreeArea(new DefaultControlEnvironment(luwrain),
				    sectionsModel,
				    strings.sectionsAreaName()) {
		private final Actions actions = a;
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isCommand() && !event.isModified())
			switch (event.getCommand())
			{
			case KeyboardEvent.TAB:
			    return actions.gotoOptions();
			case KeyboardEvent.INSERT:
			    return actions.onSectionsInsert();
			case KeyboardEvent.DELETE:
			    return actions.onSectionsDelete();
			}
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    switch (event.getCode())
		    {
		    case EnvironmentEvent.CLOSE:
			actions.closeApp();
			return true;
		    }
		    return false;
		}
		@Override public void onClick(Object obj)
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

    @Override public boolean gotoOptions()
    {
	if (currentSection == null || currentOptionsArea == null)
	    return false;
	luwrain.setActiveArea(currentOptionsArea);
	return true;
    }
}
