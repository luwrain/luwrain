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
    static public final String STRINGS_NAME = "luwrain.registry";

    private Luwrain luwrain;
    private Strings strings;
    private Base base = new Base();
    private TreeArea dirsArea;
    private ListArea valuesArea;

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	final Object o = luwrain.i18n().getStrings(STRINGS_NAME);
	if (o == null || !(o instanceof Strings))
	    return false;
	strings = (Strings)o;
	this.luwrain = luwrain;
	if (!base.init(luwrain, strings))
	    return false;
	createAreas();
	return true;
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

    @Override public void gotoDirs()
    {
	luwrain.setActiveArea(dirsArea);
    }

    @Override public void gotoValues()
    {
	luwrain.setActiveArea(valuesArea);
    }

    @Override public void refresh()
    {
	dirsArea.refresh();//FIXME:Comparator;
	valuesArea.refresh();
    }

    @Override public void openDir(Directory dir)
    {
	/*
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
	*/
    }

    @Override public boolean insertDir()
    {
			final Object obj = dirsArea.getObjectUnderHotPoint();
			if (obj == null || !(obj instanceof Directory))
			    return false;
			if (!base.insertDir((Directory)obj))
			    return true;
			dirsArea.refresh();
			return true;
    }

    private void createAreas()
    {
	final Actions a = this;

	final ListClickHandler valuesHandler = new ListClickHandler(){
		private Actions actions = a;
		@Override public boolean onListClick(ListArea area,
						     int index,
						     Object item)
		{
		    //		    if (index < 0 || item == null || !(item instanceof NewsGroupWrapper))
			return false;
		}
	    };

	dirsArea = new TreeArea(new DefaultControlEnvironment(luwrain),
				base.getDirsModel(),
				strings.dirsAreaName()) {
		private Actions actions = a;
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    if (event.isCommand() && !event.isModified())
		    switch(event.getCommand())
		    {
		    case KeyboardEvent.INSERT:
return actions.insertDir();
		    case KeyboardEvent.TAB:
			actions.gotoValues();
			return true;
		    default:
			return super.onKeyboardEvent(event);
		    }
			return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    switch (event.getCode())
		    {
		    case EnvironmentEvent.CLOSE:
			actions.closeApp();
			return true;
		    case EnvironmentEvent.REFRESH://To remove;
			actions.refresh();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
		@Override public void onClick(Object obj)
		{
		    if (obj == null || !(obj instanceof Directory))
			return;
		    actions.openDir((Directory)obj);
		}
	    };

	final ListParams params = new ListParams();
	params.environment = new DefaultControlEnvironment(luwrain);
	params.model = base.getValuesModel();
	params.appearance = base.getValuesAppearance();
	params.name = strings.valuesAreaName();
	params.clickHandler = valuesHandler;

	valuesArea = new ListArea(params)
	    {
		private Actions actions = a;
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    if (event.isCommand() && !event.isModified())
			switch(event.getCommand())
		    {
		    case KeyboardEvent.TAB:
			actions.gotoDirs();
			return true;
		    default:
			return super.onKeyboardEvent(event);
		    }
			return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    switch(event.getCode())
		    {
		    case EnvironmentEvent.CLOSE:
			actions.closeApp();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
	    };

    }

    @Override public AreaLayout getAreasToShow()
    {
	//	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, groupArea, summaryArea, messageArea);
	return new AreaLayout(AreaLayout.LEFT_RIGHT, dirsArea, valuesArea);
    }

    @Override public void closeApp()
    {
	luwrain.closeApp();
    }
}
