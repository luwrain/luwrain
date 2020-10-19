/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import java.util.function.*;

import com.google.gson.*;
import com.google.gson.reflect.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.popups.*;
import org.luwrain.cpanel.*;
import org.luwrain.util.*;
import org.luwrain.io.json.*;

final class MainMenu extends EditableListArea implements SectionArea
{
    //    static final Type ITEM_LIST_TYPE = new TypeToken<List<MainMenuItem>>(){}.getType();

    private final Gson gson = new Gson();
    private final ControlPanel controlPanel;
    private final Luwrain luwrain;
    private final Settings.UserInterface sett;

    MainMenu(ControlPanel controlPanel, EditableListArea.Params params)
    {
	super(params);
	NullCheck.notNull(controlPanel, "controlPanel");
	NullCheck.notNull(params, "params");
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
	this.sett = Settings.createUserInterface(luwrain.getRegistry());
    }

        @Override public boolean saveSectionData()
    {
	final List<UniRefInfo> model = (List)getListModel();
	final List<MainMenuItem> items = new LinkedList();
	for(UniRefInfo info: model)
	    items.add(new MainMenuItem(MainMenuItem.TYPE_UNIREF, info.getValue()));
	sett.setMainMenuContent(gson.toJson(items));
	return true;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onInputEvent(event))
	    return true;
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onSystemEvent(event))
	    return true;
	return super.onSystemEvent(event);
    }


    static MainMenu create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	final Luwrain luwrain = controlPanel.getCoreInterface();
	final Settings.UserInterface sett = Settings.createUserInterface(luwrain.getRegistry());
	final List<MainMenuItem> items = new Gson().fromJson(sett.getMainMenuContent(""), MainMenuItem.LIST_TYPE);
	final List<UniRefInfo> uniRefs = new LinkedList();
	if (items != null)
	    for(MainMenuItem item: items)
	    {
		final UniRefInfo info = UniRefUtils.make(luwrain, item.getValueNotNull());
		if (info != null)
		    uniRefs.add(info);
	    }
	final EditableListArea.Params params = new EditableListArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.appearance = new org.luwrain.core.shell.MainMenu.Appearance(params.context);
	params.name = luwrain.i18n().getStaticStr("CpMainMenu");
	params.model = new ListUtils.DefaultEditableModel(uniRefs.toArray(new UniRefInfo[uniRefs.size()])){
		@Override public boolean addToModel(int index, Supplier supplier)
		{
		    NullCheck.notNull(supplier, "supplier");
		    if (index < 0 || index > size())
			return false;
		    final Object o = supplier.get();
		    if (o == null)
			return false;
		    final Object[] objs;if (o instanceof Object[])
					    objs = (Object[])o; else
			objs = new Object[]{o};
		    final UniRefInfo[] uniRefs = UniRefUtils.make(luwrain, objs);
		    if (uniRefs.length == 0)
			return false;
		    addAll(index, Arrays.asList(uniRefs));
		    return true;
		}
	    };
	return new MainMenu(controlPanel, params);
    }
}
