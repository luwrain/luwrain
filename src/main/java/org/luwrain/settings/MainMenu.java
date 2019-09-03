/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.settings;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.popups.*;
import org.luwrain.cpanel.*;
import org.luwrain.util.*;

final class MainMenu extends EditableListArea implements SectionArea
{
    private final ControlPanel controlPanel;
    private final Luwrain luwrain;

    MainMenu(ControlPanel controlPanel, EditableListArea.Params params)
    {
	super(params);
	NullCheck.notNull(controlPanel, "controlPanel");
	NullCheck.notNull(params, "params");
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
    }

    @Override public boolean onInputEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onInputEvent(event))
	    return true;
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onSystemEvent(event))
	    return true;
	return super.onSystemEvent(event);
    }

    @Override public boolean saveSectionData()
    {
	return true;
    }

    static MainMenu create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	final Luwrain luwrain = controlPanel.getCoreInterface();
	luwrain.getRegistry().addDirectory(Settings.MAIN_MENU_UNIREFS_PATH);
	final EditableListArea.Params params = new EditableListArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.appearance = new Appearance(params.context);
	params.name = luwrain.i18n().getStaticStr("CpSoundsList");
	params.model = new ListUtils.DefaultEditableModel(RegistryUtils.getStringArray(luwrain.getRegistry(), Settings.MAIN_MENU_UNIREFS_PATH));
	return new MainMenu(controlPanel, params);
    }


static private final class Appearance extends ListUtils.DoubleLevelAppearance
{
    private final Map<String, UniRefInfo> uniRefCache = new HashMap();
					       Appearance(ControlContext context)
					       {
						   super(context);
					       }
					       @Override public boolean isSectionItem(Object obj)
					       {
						   NullCheck.notNull(obj, "obj");
						   final UniRefInfo info = getUniRefInfo(obj);
						   return info.getType().equals("section");
					       }
	@Override public String getSectionScreenAppearance(Object item)
    {
	NullCheck.notNull(item, "item");
	final UniRefInfo info = getUniRefInfo(item);
	return info.getTitle();
    }
    	@Override public String getNonSectionScreenAppearance(Object item)
    {
	NullCheck.notNull(item, "item");
	final UniRefInfo info = getUniRefInfo(item);
	return info.getTitle();
    }
    private UniRefInfo getUniRefInfo(Object obj)
    {
	NullCheck.notNull(obj, "obj");
	if (obj instanceof UniRefInfo)
	    return (UniRefInfo)obj;
	if (uniRefCache.containsKey(obj.toString()))
	    return uniRefCache.get(obj.toString());
	final UniRefInfo info = context.getUniRefInfo(obj.toString());
	if (info != null)
	{
	    uniRefCache.put(obj.toString(), info);
	    return info;
	}
final UniRefInfo info2 = new UniRefInfo(obj.toString());
uniRefCache.put(obj.toString(), info2);
return info2;
    }
}
}
