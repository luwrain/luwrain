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

public class ControlPanelApp implements Application, Actions
{
    static public final String STRINGS_NAME = "luwrain.control-panel";

    private Luwrain luwrain;
    private final Base base = new Base();
    private Strings strings;
    private EnvironmentImpl environment;
    private Section[] extensionsSections;
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
	final Object o = luwrain.i18n().getStrings(STRINGS_NAME);
	if (o == null || !(o instanceof Strings))
	    return false;
	strings = (Strings)o;
	this.luwrain = luwrain;
	environment = new EnvironmentImpl(luwrain, this);
	//	sectionsModel = new SectionsTreeModel(environment, strings, extensionsSections);
	createArea();
	return true;
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

    @Override public void openSection(Section sect)
    {
	NullCheck.notNull(sect, "sect");
	sect.refreshArea(environment);
	final Area area = sect.getSectionArea(environment);
	if (area == null)
	    return;
	if (!mayCloseCurrentSection())
	    return;
	currentSection = sect;
	currentOptionsArea = area;
	luwrain.onNewAreaLayout();
	gotoOptions();
    }

    @Override public void refreshSectionsTree()
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

    @Override public boolean onSectionsInsert()
    {
	final Object o = sectionsArea.selected();
	if (o == null || !(o instanceof Section))
	    return false;
	final Section sect = (Section)o;
	return sect.onTreeInsert(environment);
    }

    @Override public boolean onSectionsDelete()
    {
	final Object o = sectionsArea.selected();
	if (o == null || !(o instanceof Section))
	    return false;
	final Section sect = (Section)o;
	return sect.onTreeDelete(environment);
    }

    private void createArea()
    {
	final Actions actions = this;

	final TreeArea.Params treeParams = new TreeArea.Params();
	treeParams.environment = new DefaultControlEnvironment(luwrain);
	treeParams.model = base.getTreeModel();
	treeParams.name = strings.sectionsAreaName();

	sectionsArea = new TreeArea(treeParams){
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch (event.getSpecial())
			{
			case TAB:
			    return actions.gotoOptions();
			case INSERT:
			    return actions.onSectionsInsert();
			case DELETE:
			    return actions.onSectionsDelete();
			}
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    switch (event.getCode())
		    {
		    case ACTION:
			if (ActionEvent.isAction(event, "insert"))
			    actions.onSectionsInsert();
			if (ActionEvent.isAction(event, "delete"))
			    actions.onSectionsDelete();
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
		    final int flags = sect.getSectionFlags();
		    final LinkedList<Action> res = new LinkedList<Action>();
		    if ((flags & Section.FLAG_HAS_INSERT) > 0)
			res.add(new Action("insert", "Добавить"));
		    if ((flags & Section.FLAG_HAS_DELETE) > 0)
		    res.add(new Action("delete", "Удалить"));
		    return res.toArray(new Action[res.size()]);
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
	if (currentSection != null && currentOptionsArea != null)
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

    private boolean mayCloseCurrentSection()
    {
	if (currentSection == null)
	    return true;
	return currentSection.canCloseSection(environment);
    }

    @Override public void closeApp()
    {
	if (!mayCloseCurrentSection())
	    return;
	luwrain.closeApp();
    }
}
