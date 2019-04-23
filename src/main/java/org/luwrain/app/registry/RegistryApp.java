/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of LUWRAIN.

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

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Object o = luwrain.i18n().getStrings(STRINGS_NAME);
	if (o == null || !(o instanceof Strings))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, STRINGS_NAME);
	strings = (Strings)o;
	this.luwrain = luwrain;
	if (!base.init(luwrain, strings))
	    return new InitResult(InitResult.Type.FAILURE);
	createAreas();
	return new InitResult();
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
			final Object obj = dirsArea.selected();
			if (obj == null || !(obj instanceof Directory))
			    return false;
			if (!base.insertDir((Directory)obj))
			    return true;
			dirsArea.refresh();
			return true;
    }

    private void createAreas()
    {
	final Actions actions = this;

	final ListClickHandler valuesHandler = (area, index, item)->{
		return false;
	    };

	    final TreeArea.Params treeParams = new TreeArea.Params();
	    treeParams.context = new DefaultControlContext(luwrain);
	    treeParams.model = base.getDirsModel();
	    treeParams.name = strings.dirsAreaName();

	    dirsArea = new TreeArea(treeParams){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    if (event.isSpecial() && !event.isModified())
		    switch(event.getSpecial())
		    {
		    case INSERT:
return actions.insertDir();
		    case TAB:
			actions.gotoValues();
			return true;
		    default:
			return super.onInputEvent(event);
		    }
			return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    switch (event.getCode())
		    {
		    case CLOSE:
			actions.closeApp();
			return true;
		    case REFRESH://To remove;
			actions.refresh();
			return true;
		    default:
			return super.onSystemEvent(event);
		    }
		}
		@Override public void onClick(Object obj)
		{
		    if (obj == null || !(obj instanceof Directory))
			return;
		    actions.openDir((Directory)obj);
		}
	    };

	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.model = base.getValuesModel();
	params.appearance = base.getValuesAppearance();
	params.name = strings.valuesAreaName();
	params.clickHandler = valuesHandler;

	valuesArea = new ListArea(params)
	    {
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
		    {
		    case TAB:
			actions.gotoDirs();
			return true;
		    default:
			return super.onInputEvent(event);
		    }
			return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    if (event == null)
			throw new NullPointerException("event may not be null");
		    switch(event.getCode())
		    {
		    case CLOSE:
			actions.closeApp();
			return true;
		    default:
			return super.onSystemEvent(event);
		    }
		}
	    };

    }

    @Override public AreaLayout getAreaLayout()
    {
	//	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, groupArea, summaryArea, messageArea);
	return new AreaLayout(AreaLayout.LEFT_RIGHT, dirsArea, valuesArea);
    }

    @Override public void closeApp()
    {
	luwrain.closeApp();
    }
}
