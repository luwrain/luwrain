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

package org.luwrain.core.shell.desktop;

import java.util.*;
import java.lang.reflect.*;

import com.google.gson.*;
import com.google.gson.reflect.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.core.shell.*;

final class DesktopArea extends EditableListArea implements EditableListArea.ClickHandler
{
    static final Type DESKTOP_ITEM_LIST_TYPE = new TypeToken<List<DesktopItem>>(){}.getType();

    private final Luwrain luwrain;

    DesktopArea(Luwrain luwrain, String areaName, Conversations conv)
    {
	super(createParams(luwrain, areaName, conv));
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	setListClickHandler(this);
    }

    static private EditableListArea.Params createParams(Luwrain luwrain, String areaName, Conversations conv)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(areaName, "areaName");
	NullCheck.notNull(conv, "conv");
	final EditableListArea.Params params = new EditableListArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.model = new Model(luwrain);
	params.appearance = new org.luwrain.core.shell.desktop.Appearance(luwrain);
	params.name = areaName;
	params.clipboardSaver = (area, model, appearance, fromIndex, toIndex, clipboard)->{
	    final List<String> res = new LinkedList<String>();
	    for(int i = fromIndex;i < toIndex;++i)
	    {
		final Object obj = model.getItem(i);
		NullCheck.notNull(obj, "obj");
		if (obj instanceof UniRefInfo)
		    res.add(((UniRefInfo)obj).getValue()); else
		    res.add(obj.toString());
	    }
	    clipboard.set(res.toArray(new String[res.size()]));
	    return true;
	};
	params.confirmation = (area,model,fromIndex,toIndex)->{return true;/*conv.deleteDesktopItemsConfirmation(toIndex - fromIndex);*/};
	return params;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    { 
	    case ESCAPE:
		return onEscape();
	    }
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != SystemEvent.Type.REGULAR)
	    return super.onSystemEvent(event);
	switch(event.getCode())
	{
	case HELP:
	    return luwrain.openHelp("luwrain.default");
	case CLOSE:
	    luwrain.silence();
	    luwrain.message(luwrain.i18n().getStaticStr("DesktopNoApplication"), Sounds.NO_APPLICATIONS);
	    return true;
	default:
	    return super.onSystemEvent(event);
	}
    }

    @Override protected String noContentStr()
    {
	return "Рабочий стол пуст";//FIXME:
    }

    @Override public boolean onListClick(ListArea area, int index, Object obj)
    {
	if (obj == null)
	    return false;
	if (obj instanceof DesktopItem)
	{
	    final DesktopItem item = (DesktopItem)obj;
	    final UniRefInfo uniRefInfo = item.getUniRefInfo(luwrain);
	    return luwrain.openUniRef(uniRefInfo.getValue());
	}
	return false;
    }

    private boolean onEscape()
    {
	if (luwrain == null)
	    return false;
	final Settings.UserInterface sett = Settings.createUserInterface(luwrain.getRegistry());
	final String cmdName = sett.getDesktopEscapeCommand("");
	if (cmdName.trim().isEmpty())
	    return false;
	return luwrain.runCommand(cmdName.trim());
    }

    static private final class Model implements EditableListArea.EditableModel
    {
	private final Luwrain luwrain;
	private final Settings.UserInterface sett;
	private ArrayList<DesktopItem> items = null;
	private final Gson gson = new Gson();
	Model(Luwrain luwrain)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    this.luwrain = luwrain;
	    this.sett = Settings.createUserInterface(luwrain.getRegistry());
	}
	@Override public boolean clearList()
	{
	    load();
	    this.items.clear();
	    save();
	    return true;
	}
	@Override public boolean removeFromList(int index)
	{
	    if (index < 0)
		throw new IllegalArgumentException("index may not be negative");
	    load();
	    if (index >= this.items.size())
		return false;
	    this.items.remove(index);
	    save();
	    return true;
	}
	@Override public boolean addToList(int index, Clipboard clipboard)
	{
	    NullCheck.notNull(clipboard, "clipboard");
	    if (index < 0)
		throw new IllegalArgumentException("index may not be negative");
	    load();
	    final Object[] objs = clipboard.get();
	    if (objs == null || objs.length == 0)
		return false;
	    for(Object o: objs)
	    {
		if (o instanceof java.io.File)
		{
		    final java.io.File file = (java.io.File)o;
		    items.add(new DesktopItem(luwrain, file));
		    continue;
		}
		if (o instanceof java.net.URL)
		{
		    final java.net.URL url = (java.net.URL)o;
		    items.add(new DesktopItem(luwrain, url));
		    continue;
		}
		if (o instanceof UniRefInfo)
		{
		    final UniRefInfo info = (UniRefInfo)o;
		    items.add(new DesktopItem(info));
		    continue;
		}
		if (o instanceof String)
		{
		    final  String str = (String)o;
		    final UniRefInfo info = luwrain.getUniRefInfo(str);
		    if (info.isAvailable())
			items.add(new DesktopItem(info)); else
			items.add(new DesktopItem(luwrain, str));
		    continue;
		}
	    }
	    save();
	    return true;
	}
	@Override public int getItemCount()
	{
	    load();
	    return items.size();
	}
	@Override public Object getItem(int index)
	{
	    load();
	    return items.get(index);
	}
	@Override public void refresh()
	{
	}
	private void load()
	{
	    if (items != null)
		return;
	    final List<DesktopItem> res = gson.fromJson(sett.getDesktopContent(""), DESKTOP_ITEM_LIST_TYPE);
	    this.items = new ArrayList();
	    if (res != null)
		items.addAll(res);
	}
	private void save()
	{
	    if (this.items == null)
		return;
	    sett.setDesktopContent(gson.toJson(this.items));
	}
    }
}
