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

package org.luwrain.app.cpanel;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

public class ControlPanelApp implements Application, MonoApp, Actions
{
    private Luwrain luwrain;
    private Base base = null;
    private Strings strings;
    private ControlPanelImpl iface;

    private final Factory[] factories;

    private Section currentSection = null;
    private TreeArea sectionsArea;
    private SectionArea currentOptionsArea = null;

    public ControlPanelApp(Factory[] factories)
    {
	NullCheck.notNullItems(factories, "factories");
	this.factories = factories;
    }

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, Strings.NAME);
	strings = (Strings)o;
	this.luwrain = luwrain;
	base = new Base(luwrain, factories);
	iface = new ControlPanelImpl(luwrain, this);
	createArea();
	return new InitResult();
    }

    private void createArea()
    {
	final TreeArea.Params treeParams = new TreeArea.Params();
	treeParams.context = new DefaultControlContext(luwrain);
	treeParams.model = base.getTreeModel();
	treeParams.name = strings.sectionsAreaName();
	treeParams.clickHandler = (area, obj)->openSection(obj);

	sectionsArea = new TreeArea(treeParams){

		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch (event.getSpecial())
			{
			case ESCAPE:
			    closeApp();
			    return true;
			case TAB:
			    return gotoOptions();
			}
		    return super.onInputEvent(event);
		}

		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch (event.getCode())
		    {
		    case ACTION:
			return onTreeAction(event);
		    case CLOSE:
			closeApp();
			return true;
		    }
		    return false;
		}
		@Override public Action[] getAreaActions()
		{
		    return getTreeActions();
		}
	    };
    }

    void refreshSectionsTree()
    {
	base.refreshTreeItems();
    sectionsArea.refresh();
    }

    private Action[] getTreeActions()
    {
		    final Object selected = sectionsArea.selected();
		    if (selected == null || !(selected instanceof Section))
			return new Action[0];
		    final Section sect = (Section)selected;
		    final Action[] res = sect.getSectionActions();
		    return res != null?res:new Action[0];
    }

    private boolean onTreeAction(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
		    final Object selected = sectionsArea.selected();
		    if (selected == null || !(selected instanceof Section))
			return false;
		    final Section sect = (Section)selected;
		    return sect.onSectionActionEvent(iface, (ActionEvent)event);
    }

    private  boolean openSection(Object obj)
    {
	NullCheck.notNull(obj, "obj");
	if (!(obj instanceof Section))
	    return false;
	final Section sect = (Section)obj;
	final SectionArea area = sect.getSectionArea(iface);
	if (area == null)
	    return false;
	if (currentOptionsArea != null)
	{
	    if (!currentOptionsArea.saveSectionData())
		return true;
	    currentOptionsArea = null;
	    currentSection = null;
	}
	currentSection = sect;
	currentOptionsArea = area;
	luwrain.onNewAreaLayout();
	gotoOptions();
	return true;
    }

    void gotoSections()
    {
	luwrain.setActiveArea(sectionsArea);
    }

    private boolean gotoOptions()
    {
	if (currentSection == null || currentOptionsArea == null)
	    return false;
	luwrain.setActiveArea(currentOptionsArea);
	return true;
    }

    @Override public AreaLayout getAreaLayout()
    {
	if (currentSection != null && currentOptionsArea != null)
	    return new AreaLayout(AreaLayout.LEFT_RIGHT, sectionsArea, currentOptionsArea);
	return new AreaLayout(sectionsArea);
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

    @Override public MonoApp.Result onMonoAppSecondInstance(Application app)
    {
	NullCheck.notNull(app, "app");
	return MonoApp.Result.BRING_FOREGROUND;
    }

    @Override public void closeApp()
    {
	if (currentOptionsArea != null && !currentOptionsArea.saveSectionData())
	    return;
	luwrain.closeApp();
    }
}
