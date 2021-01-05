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

package org.luwrain.popups;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.script.*;

public class DisksPopup extends ListPopupBase
{
    static private final String LOG_COMPONENT = Popups.LOG_COMPONENT;

    static public final String LIST_HOOK = "luwrain.popups.disks.list";
    static public final String CLICK_HOOK = "luwrain.popups.disks.click";

    protected File result = null;

    public DisksPopup(Luwrain luwrain, String name, Set<Popup.Flags> popupFlags)
    {
	super(luwrain, createParams(luwrain, name), popupFlags);
    }

    public File result()
    {
	return this.result;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() || !event.isModified())
	    switch(event.getSpecial())
	    {
	    case ENTER:
		if (selected() == null)
		    return false;
		closing.doOk();
		return true;
		/*
		  case INSERT:
		  case DELETE:
		*/
	    }
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() == SystemEvent.Type.BROADCAST)
	    switch(event.getCode())
	    {
	    case REFRESH:
		if (event.getBroadcastFilterUniRef().startsWith("disksvolumes:"))
		    refresh();
		return true;
	    default:
		return super.onSystemEvent(event);
	    }
	return super.onSystemEvent(event);
    }

    @Override public boolean onOk()
    {
	final Object sel = selected();
	if (sel == null)
	    return false;
	final Object obj;
	try {
	    obj = new org.luwrain.script.hooks.ProviderHook(luwrain).run(CLICK_HOOK, new Object[]{sel});
	}
	catch(RuntimeException e)
	{
	    Log.error(LOG_COMPONENT, "unable to run the " + CLICK_HOOK + " hook:" + e.getClass().getName() + ":" + e.getMessage());
	    luwrain.message(luwrain.i18n().getExceptionDescr(e), Luwrain.MessageType.ERROR);
	    return false;
	}
	if (obj == null || !(obj instanceof String))
	    return false;
	final String str = (String)obj;
	if (str.isEmpty())
	    return false;
	this.result = new File(str);
	return true;
    }

    static private Object[] prepareContent(Luwrain luwrain)
    {
	final Object obj;
	try {
	    obj = new org.luwrain.script.hooks.ProviderHook(luwrain).run(LIST_HOOK, new Object[0]);
	}
	catch(RuntimeException e)
	{
	    Log.error(LOG_COMPONENT, "unable to run " + LIST_HOOK + " hook:" + e.getClass().getName() + ":" + e.getMessage());
	    luwrain.message(luwrain.i18n().getExceptionDescr(e), Luwrain.MessageType.ERROR);
	    return new Object[0];
	}
	if (obj == null)
	    return new Object[0];
	final List items = ScriptUtils.getArray(obj);
	if (items == null)
	    return new Object[0];
	final List<Item> res = new LinkedList();
	for(Object o: items)
	{
	    if (o == null)
		continue;
	    final Object nameObj = ScriptUtils.getMember(o, "name");
	    if (nameObj == null)
		continue;
	    final String name = ScriptUtils.getStringValue(nameObj);
	    if (name == null || name.trim().isEmpty())
		continue;
	    res.add(new Item(name, o));
	}
	return res.toArray(new Item[res.size()]);
    }

    static protected ListArea.Params createParams(Luwrain luwrain, String name)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.name = name;
	params.model = new ListUtils.FixedModel(prepareContent(luwrain)){
		@Override public void refresh()
		{
		    setItems(prepareContent(luwrain));
		}};
	params.appearance = new ListUtils.DefaultAppearance(new DefaultControlContext(luwrain)){
		@Override public void announceItem(Object obj, Set<Flags> flags)
		{
		    NullCheck.notNull(obj, "obj");
		    luwrain.setEventResponse(DefaultEventResponse.listItem(luwrain.getSpeakableText(obj.toString(), Luwrain.SpeakableTextType.PROGRAMMING), Suggestions.CLICKABLE_LIST_ITEM));
		}
	    };
	params.flags = EnumSet.of(ListArea.Flags.EMPTY_LINE_TOP);
	return params;
    }

    static public final class Item
    {
	private final String name;
	private final Object obj;
	public Item(String name, Object obj)
	{
	    NullCheck.notEmpty(name, "name");
	    NullCheck.notNull(obj, "obj");
	    this.name = name;
	    this.obj = obj;
	}
	@Override public String toString()
	{
	    return name;
	}
	public String getName()
	{
	    return name;
	}
	public Object getObj()
	{
	    return obj;
	}
    }}
