/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.shell;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.popups.Popups;

public class Desktop implements Application
{
    private Luwrain luwrain = null;
    private Storing storing = null;
    private EditableListArea area = null;
    private Conversations conversations = null;

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	createArea();
	return new InitResult();
    }

    //Runs by the core when language extensions loaded 
    public void ready()
    {
	storing.load();
	luwrain.onAreaNewName(area);
    }

    public void setConversations(Conversations conversations)
    {
	this.conversations = conversations;
    }

    private void createArea()
    {
	this.storing = new Storing(luwrain);
	final EditableListArea.Params params = new EditableListArea.Params();
	params.context = new DefaultControlEnvironment(luwrain);
	params.model = new Model(storing);
	params.appearance = new Appearance(luwrain);
	params.name = luwrain.i18n().getStaticStr("Desktop");
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
	params.confirmation = (area,model,fromIndex,toIndex)->conversations.deleteDesktopItemsConfirmation(toIndex - fromIndex);
	area = new EditableListArea(params) {
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{ 
			case ESCAPE:
			    if (luwrain == null)
				return false;
			    luwrain.quit();
			    return true;
			}
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    switch(event.getCode())
		    {
		    case CLOSE:
			luwrain.silence();
			luwrain.message(luwrain.i18n().getStaticStr("DesktopNoApplication"), Sounds.NO_APPLICATIONS);
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
		@Override protected String noContentStr()
		{
		    return "Рабочий стол пуст";//FIXME:
		}
		@Override public String getAreaName()
		{
		    return luwrain.i18n().getStaticStr("Desktop");
		}
	    };
	area.setListClickHandler((area, index, obj)->{
		if (!onClick(index, obj))
		    return false;
		area.refresh();
		return true;
	    });
    }

    private boolean onClick(int index, Object obj)
    {
	if (obj == null)
	    return false;
	if (obj instanceof UniRefInfo)
	{
	    final UniRefInfo uniRefInfo = (UniRefInfo)obj;
	    return luwrain.openUniRef(uniRefInfo.getValue());
	}
	return false;
    }

    @Override public String getAppName()
    {
	return luwrain.i18n().getStaticStr("Desktop");
    }

    @Override public AreaLayout getAreaLayout()
    {
	return new AreaLayout(area);
    }

    @Override public void closeApp()
    {
	//Never called
    }

    static private class Appearance implements ListArea.Appearance
    {
	private final Luwrain luwrain;
	Appearance(Luwrain luwrain)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    this.luwrain = luwrain;
	}
	@Override public void announceItem(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    if (item instanceof UniRefInfo)
	    {
		final UniRefInfo info = (UniRefInfo)item;
		announceUniRefInfo(info, flags.contains(Flags.BRIEF));
		return;
	    }
	    luwrain.setEventResponse(DefaultEventResponse.text(item.toString()));
	}
	@Override public String getScreenAppearance(Object item, Set<Flags> flags)
	{
	    if (item == null)
		return "";
	    if (item instanceof String)
		return (String)item;
	    if (item instanceof UniRefInfo)
	    {
		final UniRefInfo i = (UniRefInfo)item;
		return i.toString();
	    }
	    return "";
	}
	@Override public int getObservableLeftBound(Object item)
	{
	    return 0;
	}
	@Override public int getObservableRightBound(Object item)
	{
	    return getScreenAppearance(item, EnumSet.noneOf(Flags.class)).length();
	}
	private void announceUniRefInfo(UniRefInfo uniRefInfo, boolean brief)
    {
	NullCheck.notNull(uniRefInfo, "uniRefInfo");
			if (!uniRefInfo.isAvailable())
		{
		    		    		    luwrain.setEventResponse(DefaultEventResponse.text(uniRefInfo.getValue()));
				    return;				    
		}
					if (brief)
		{
		    		    luwrain.setEventResponse(DefaultEventResponse.listItem(uniRefInfo.getTitle(), null));
				    return;				    
		}
		final String type = uniRefInfo.getValue().substring(0, uniRefInfo.getValue().indexOf(":")).toLowerCase();
		switch(type)
		{
		case "static":
		    		    		    luwrain.setEventResponse(DefaultEventResponse.text(uniRefInfo.getTitle()));
						    break;
		case "empty":
		    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
		    break;
						    		case "section":
		    		    luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.DOC_SECTION, uniRefInfo.getTitle(), null));
						    break;

						    

	
		default:
		    		    luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.MAIN_MENU_ITEM, uniRefInfo.getTitle(), Suggestions.CLICKABLE_LIST_ITEM));
		}
    }
    }

private class Model implements EditableListArea.EditableModel
    {
	private final Storing storing;
	Model(Storing storing)
	{
	    NullCheck.notNull(storing, "storing");
	    this.storing = storing;
	}
	@Override public boolean clearList()
	{
	    storing.clear();
	    storing.save();
	    return true;
	}
	@Override public boolean removeFromList(int index)
	{
	    if (index < 0)
		throw new IllegalArgumentException("index may not be negative");
	    if (index >= storing.size())
		return false;
	    storing.remove(index);
	    storing.save();
	    return true;
	}
	@Override public boolean addToList(int index, Clipboard clipboard)
	{
	    NullCheck.notNull(clipboard, "clipboard");
	    if (index < 0)
		throw new IllegalArgumentException("index may not be negative");
	    final Object[] objs = clipboard.get();
	    if (objs == null || objs.length == 0)
		return false;
	    final List<UniRefInfo> items = new LinkedList<UniRefInfo>();
	    for(Object o: objs)
	    {
		if (o instanceof java.io.File)
		{
		    final java.io.File file = (java.io.File)o;
		    items.add(luwrain.getUniRefInfo("file:" + file.getAbsolutePath()));
		    continue;
		}
		if (o instanceof String)
		{
		    final  String str = (String)o;
		    final UniRefInfo info = luwrain.getUniRefInfo(str);
		    if (info.isAvailable())
			items.add(info); else
			items.add(luwrain.getUniRefInfo("static:" + str));
		    continue;
		}
		//FIXME:url
	    }
	    storing.addAll(index, items);
	    storing.save();
	    return true;
	}
	@Override public int getItemCount()
	{
	    return storing.size();
	}
	@Override public Object getItem(int index)
	{
	    return storing.get(index);
	}
	@Override public void refresh()
	{
	}
    }

    static class Storing extends Vector<UniRefInfo>
    {
	private final Luwrain luwrain;
	private final Registry registry;
	Storing(Luwrain luwrain)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    this.luwrain = luwrain;
	    this.registry = luwrain.getRegistry();
	}
	UniRefInfo[] getAll()
	{
	    return toArray(new UniRefInfo[size()]);
	}
	void load()
	{
	    final String[] values = registry.getValues(Settings.DESKTOP_UNIREFS_PATH);
	    for(String v: values)
	    {
		final String path = Registry.join(Settings.DESKTOP_UNIREFS_PATH, v);
		if (registry.getTypeOf(path) != Registry.STRING)
		    continue;
		final String s = registry.getString(path);
		if (s.isEmpty())
		    continue;
		final UniRefInfo uniRef = luwrain.getUniRefInfo(s);
		if (uniRef != null)
		    add(uniRef);
	    }
	}
	void save()
	{
	    for(String v: registry.getValues(Settings.DESKTOP_UNIREFS_PATH))
		registry.deleteValue(Registry.join(Settings.DESKTOP_UNIREFS_PATH, v));
	    final UniRefInfo[] uniRefs = getAll();
	    for(int i = 0;i < uniRefs.length;++i)
	    {
		String name = "" + (i + 1);
		while (name.length() < 6)
		    name = "0" + name;
		registry.setString(Registry.join(Settings.DESKTOP_UNIREFS_PATH, name), uniRefs[i].getValue());
	    }
	}
    }
}
