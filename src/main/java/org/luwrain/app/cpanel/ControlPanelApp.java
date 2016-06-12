/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

public class ControlPanelApp implements Application, MonoApp, Actions
{
    static private final String STRINGS_NAME = "luwrain.control-panel";

    private Luwrain luwrain;
    private final Base base = new Base();
    private Strings strings;
    private ControlPanelImpl iface;
    private Factory[] factories;
    private Section currentSection = null;
    private TreeArea sectionsArea;
    private SectionArea currentOptionsArea = null;

    public ControlPanelApp(Factory[] factories)
    {
	NullCheck.notNullItems(factories, "factories");
	this.factories = factories;
    }

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	final Object o = luwrain.i18n().getStrings(STRINGS_NAME);
	if (o == null || !(o instanceof Strings))
	    return false;
	strings = (Strings)o;
	this.luwrain = luwrain;
	if (!base.init(luwrain, factories))
	    return false;
	iface = new ControlPanelImpl(luwrain, this);
	createArea();
	return true;
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
	if (!mayCloseCurrentSection())
	    return true;
	currentSection = sect;
	currentOptionsArea = area;
	luwrain.onNewAreaLayout();
	gotoOptions();
	return true;
    }

    void refreshSectionsTree()
    {
	//	sectionsModel.refresh();
    sectionsArea.refresh();
    if (currentSection == null || currentOptionsArea == null)
    {
	currentSection = null;
	currentOptionsArea = null;
	return;
    }
    if (currentSection.isSectionEnabled())
	return;
    currentSection = null;
    currentOptionsArea = null;
    luwrain.onNewAreaLayout();
    }

    private boolean onSectionsInsert()
    {
	final Object o = sectionsArea.selected();
	if (o == null || !(o instanceof Section))
	    return false;
	final Section sect = (Section)o;
	return sect.onTreeInsert(iface);
    }

    private boolean onSectionsDelete()
    {
	final Object o = sectionsArea.selected();
	if (o == null || !(o instanceof Section))
	    return false;
	final Section sect = (Section)o;
	return sect.onTreeDelete(iface);
    }

    private void createArea()
    {
	final Actions actions = this;

	final TreeArea.Params treeParams = new TreeArea.Params();
	treeParams.environment = new DefaultControlEnvironment(luwrain);
	treeParams.model = base.getTreeModel();
	treeParams.name = strings.sectionsAreaName();
	treeParams.clickHandler = (area, obj)->openSection(obj);

	sectionsArea = new TreeArea(treeParams){
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch (event.getSpecial())
			{
			case TAB:
			    return gotoOptions();
			}
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    switch (event.getCode())
		    {
		    case ACTION:
			return true;
		    case CLOSE:
			actions.closeApp();
			return true;
		    }
		    return false;
		}
		@Override public Action[] getAreaActions()
		{
		    final Object selected = selected();
		    if (selected == null || !(selected instanceof Section))
			return new Action[0];
		    final Section sect = (Section)selected;
		    final Set<Section.Flags> flags = sect.getSectionFlags();
		    final LinkedList<Action> res = new LinkedList<Action>();
		    if (flags.contains(Section.Flags.HAS_INSERT))
			res.add(new Action("insert", "Добавить"));
		    if (flags.contains(Section.Flags.HAS_DELETE))
		    res.add(new Action("delete", "Удалить"));
		    return res.toArray(new Action[res.size()]);
		}
	    };
    }

    @Override public AreaLayout getAreasToShow()
    {
	if (currentSection != null && currentOptionsArea != null)
	    return new AreaLayout(AreaLayout.LEFT_RIGHT, sectionsArea, currentOptionsArea);
	return new AreaLayout(sectionsArea);
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

    private boolean mayCloseCurrentSection()
    {
	if (currentSection == null)
	    return true;
	return currentSection.canCloseSection(iface);
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
	if (!mayCloseCurrentSection())
	    return;
	luwrain.closeApp();
    }
}
