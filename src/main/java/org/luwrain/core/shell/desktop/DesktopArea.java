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

package org.luwrain.core.shell.desktop;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.core.shell.*;
import org.luwrain.io.json.*;

final class DesktopArea extends EditableListArea implements EditableListArea.ClickHandler
{
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
	params.confirmation = (area,model,fromIndex,toIndex)->{
	    if (fromIndex + 1== toIndex)
		return conv.deleteItem(params.appearance.getScreenAppearance(model.getItem(fromIndex), EnumSet.noneOf(EditableListArea.Appearance.Flags.class)));
	    return conv.deleteItems(toIndex - fromIndex);
	};
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

    static private final class Model implements EditableListArea.Model
    {
	private final Luwrain luwrain;
	private final Settings.UserInterface sett;
	private ArrayList<DesktopItem> items = new ArrayList();
	Model(Luwrain luwrain)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    this.luwrain = luwrain;
	    this.sett = Settings.createUserInterface(luwrain.getRegistry());
	    this.items.addAll(Arrays.asList(DesktopItem.fromJson(sett.getDesktopContent(""))));
	}
	@Override public boolean clearModel()
	{
	    this.items.clear();
	    save();
	    return true;
	}
	@Override public boolean removeFromModel(int index)
	{
	    if (index < 0)
		throw new IllegalArgumentException("index may not be negative");
	    if (index >= this.items.size())
		return false;
	    this.items.remove(index);
	    save();
	    return true;
	}
	@Override public boolean addToModel(int index, java.util.function.Supplier supplier)
	{
	    NullCheck.notNull(supplier, "supplier");
	    if (index < 0)
		throw new IllegalArgumentException("index may not be negative");
	    final Object supplied = supplier.get();
	    if (supplied == null)
		return false;
	    final Object[] objs;
	    if (supplied instanceof Object[])
		objs = (Object[])supplied; else
		objs = new Object[]{supplied};
	    if (objs.length == 0)
		return false;
	    final List<DesktopItem> newItems = new ArrayList();
	    for(Object o: objs)
	    {
		final UniRefInfo info = UniRefUtils.make(luwrain, o);
		if (info == null)
		    return false;
		    newItems.add(new DesktopItem(info));
	    }
	    items.addAll(index, newItems);
	    save();
	    return true;
	}
	@Override public int getItemCount()
	{
	    return items.size();
	}
	@Override public Object getItem(int index)
	{
	    return items.get(index);
	}
	@Override public void refresh()
	{
	}
	private void save()
	{
	    sett.setDesktopContent(DesktopItem.toJson(this.items.toArray(new DesktopItem[this.items.size()])));
	}
    }
}
