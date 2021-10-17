/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

import static org.luwrain.core.DefaultEventResponse.*;

final class MainMenu extends EditableListArea implements SectionArea
{
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
	final List<MainMenuItem> items = new ArrayList<>();
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
	final List<UniRefInfo> uniRefs = new ArrayList<>();
	if (items != null)
	    for(MainMenuItem item: items)
	    {
		final UniRefInfo info = UniRefUtils.make(luwrain, item.getValueNotNull());
		if (info != null)
		    uniRefs.add(info);
	    }
	final EditableListArea.Params<UniRefInfo> params = new EditableListArea.Params<>();
	params.context = new DefaultControlContext(luwrain);
	params.name = luwrain.i18n().getStaticStr("CpMainMenu");
	params.appearance = new Appearance(luwrain);
	params.model = new ListUtils.DefaultEditableModel<UniRefInfo>(UniRefInfo.class, uniRefs){
		@Override public UniRefInfo adjust(Object o)
		{
		    NullCheck.notNull(o, "o");
		    return UniRefUtils.make(o.toString());
		}
	    };
	params.clipboardSaver = (area, model, appearance, fromIndex, toIndex, clipboard)->{
	    final List<UniRefInfo> u = new ArrayList<>();
	    final List<String> s = new ArrayList<String>();
	    for(int i = fromIndex;i < toIndex;++i)
	    {
		final Object obj = model.getItem(i);
		if (!(obj instanceof UniRefInfo))
		    continue;
		final UniRefInfo uniRefInfo = (UniRefInfo)obj;
		u.add(uniRefInfo);
		s.add(uniRefInfo.getTitle());
	    }
	    clipboard.set(u.toArray(new UniRefInfo[u.size()]), s.toArray(new String[s.size()]));
	    return true;
	};
	return new MainMenu(controlPanel, params);
    }


static private final class Appearance extends ListUtils.DoubleLevelAppearance<UniRefInfo>
    {
	static private final String STATIC_PREFIX = "static:";
	Appearance(Luwrain luwrain) { super(new DefaultControlContext(luwrain)); }
	@Override public boolean isSectionItem(UniRefInfo info)
	{
	    NullCheck.notNull(info, "info");
	    return info.getType().equals(UniRefProcs.TYPE_SECTION);
	}
	@Override public String getSectionScreenAppearance(UniRefInfo info)
	{
	    NullCheck.notNull(info, "info");
	    final String title = info.getTitle();
	    if (!title.startsWith(STATIC_PREFIX))
		return title;
	    return context.getI18n().getStaticStr(title.substring(STATIC_PREFIX.length()));
	}
    	@Override public String getNonSectionScreenAppearance(UniRefInfo info)
	{
	    NullCheck.notNull(info, "info");
	    final String title = info.getTitle();
	    if (!title.startsWith(STATIC_PREFIX))
		return title;
	    return context.getI18n().getStaticStr(title.substring(STATIC_PREFIX.length()));
	}
	@Override public void announceNonSection(UniRefInfo info)
	{
	    NullCheck.notNull(info, "info");
	    context.setEventResponse(text(Sounds.DESKTOP_ITEM, context.getSpeakableText(getNonSectionScreenAppearance(info), Luwrain.SpeakableTextType.NATURAL)));
	}
	public void announceSection(UniRefInfo info)
	{
	    NullCheck.notNull(info, "info");
	    context.setEventResponse(text(Sounds.DOC_SECTION, context.getSpeakableText(getNonSectionScreenAppearance(info), Luwrain.SpeakableTextType.NATURAL)));//FIXME:DefaultEventResponse.listItem()
	}
	/*
	UniRefInfo getUniRefInfo(Object obj)
	{
	    	    NullCheck.notNull(obj, "obj");
	    if (obj instanceof UniRefInfo)
		return (UniRefInfo)obj;
	    final String value;
	    if (obj instanceof MainMenuItem)
		value = ((MainMenuItem)obj).getValueNotNull(); else
		value = obj.toString();
	    if (uniRefCache.containsKey(value))
		return uniRefCache.get(value);
	    final UniRefInfo info = context.getUniRefInfo(value);
	    uniRefCache.put(obj.toString(), info);
	    return info;
	}
	*/
    }

    
}
