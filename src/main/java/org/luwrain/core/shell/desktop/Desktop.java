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

package org.luwrain.core.shell.desktop;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.popups.Popups;
import org.luwrain.shell.*;

public class Desktop implements org.luwrain.core.Desktop
{
    private Luwrain luwrain = null;
    private String name = "";
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
    @Override public void ready()
    {
	this.storing.load();
	this.name = luwrain.i18n().getStaticStr("Desktop");
	luwrain.onAreaNewName(area);
    }

    @Override public void setConversations(Conversations conversations)
    {
	this.conversations = conversations;
    }

    private void createArea()
    {
	this.storing = new Storing(luwrain);
	final EditableListArea.Params params = new EditableListArea.Params();
	params.context = new DefaultControlContext(luwrain);
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
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{ 
			case ESCAPE:
			    if (luwrain == null)
				return false;
			    if (luwrain.xRunHooks("luwrain.desktop.escape", new Object[0], Luwrain.HookStrategy.CHAIN_OF_RESPONSIBILITY))
				return true;
			    {
				final Settings.UserInterface sett = Settings.createUserInterface(luwrain.getRegistry());
				final String cmdName = sett.getDesktopEscapeCommand("");
				if (cmdName.trim().isEmpty())
				    return false;
				return luwrain.runCommand(cmdName.trim());
			    }
			}
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
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
		@Override public String getAreaName()
		{
		    return name;
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
	return name;
    }

    @Override public AreaLayout getAreaLayout()
    {
	return new AreaLayout(area);
    }

    @Override public void closeApp()
    {
	//Never called
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

}
