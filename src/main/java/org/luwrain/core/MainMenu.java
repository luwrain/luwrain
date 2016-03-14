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

package org.luwrain.core;

import java.util.*;

import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.util.*;

public class MainMenu extends ListArea implements PopupClosingRequest, ListClickHandler
{
    static private class Section
    {
	private String title;
	private UniRefInfo[] uniRefs;

	Section(String title, UniRefInfo[] uniRefs)
	{
	    this.title = title;
	    this.uniRefs = uniRefs;
	    NullCheck.notNull(title, "title");
	    NullCheck.notNullItems(uniRefs, "uniRefs");
	}

	String title(){return title;}
	UniRefInfo[] uniRefs(){return uniRefs;}
	@Override public String toString() {return title;}
    }

    static private class Appearance implements ListItemAppearance
    {
	private Luwrain luwrain;

	Appearance(Luwrain luwrain)
	{
	    this.luwrain = luwrain;
	    NullCheck.notNull(luwrain, "luwrain");
	}

	@Override public void introduceItem(Object item, int flags)
	{
	    if (item == null)
		return;
	    if (item instanceof Section)
	    {
		luwrain.silence();
		luwrain.playSound(Sounds.DOC_SECTION);
		luwrain.say(item.toString());
		return;
	    }
	    luwrain.silence();
	    luwrain.playSound(Sounds.NEW_LIST_ITEM);
	    luwrain.say(item.toString());
	}

	@Override public String getScreenAppearance(Object item, int flags)
	{
	    return item != null?item.toString():"";
	}

	@Override public int getObservableLeftBound(Object item)
	{
	    return 0;
	}

	@Override public int getObservableRightBound(Object item)
	{
	    return item != null?getScreenAppearance(item, 0).length():0;
	}
    }

    private Luwrain luwrain;
    final PopupClosing closing = new PopupClosing(this);
    private Strings strings;
    private UniRefInfo result = null;

    private MainMenu(Luwrain luwrain, ListParams params)
    {
	super(params);
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (closing.onKeyboardEvent(event))
	    return true;
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case PAGE_DOWN:
	    case ALTERNATIVE_PAGE_DOWN:
		if (selectedIndex() + 1 >= model.getItemCount())
		{
		    environment.hint(Hints.NO_ITEMS_BELOW);
		    return true;
		}
		for(int i = selectedIndex() + 1;i < model.getItemCount();++i)
		    if (model.getItem(i) instanceof Section)
		    {
			setSelectedByIndex(i, true);
			return true;
		    }
		environment.hint(Hints.NO_ITEMS_BELOW);
		return true;
	    case PAGE_UP:
	    case ALTERNATIVE_PAGE_UP:
		if (selectedIndex() < 1)
		{
		    environment.hint(Hints.NO_ITEMS_ABOVE);
		    return true;
		}
		for(int i = selectedIndex() - 1;i >= 0;--i)
		    if (model.getItem(i) instanceof Section)
		    {
			setSelectedByIndex(i, true);
			return true;
		    }
		environment.hint(Hints.NO_ITEMS_ABOVE);
		return true;
	    }
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (closing.onEnvironmentEvent(event))
	    return true;
	switch(event.getCode())
	{
	case INTRODUCE:
	    luwrain.silence();
	    luwrain.playSound(Sounds.MAIN_MENU);
	    luwrain.say(getAreaName());
	    return true;
	default:
	return super.onEnvironmentEvent(event);
	}
    }

    @Override public boolean onListClick(ListArea area, int index,
					    Object item)
    {
	return closing.doOk();
    }

    @Override public boolean onOk()
    {
	final Object o = selected();
	if (o == null || !(o instanceof UniRefInfo))
	    return false;
	result = (UniRefInfo)o;
	return true;
    }

    @Override public boolean onCancel()
    {
	return true;
    }

    UniRefInfo result()
    {
	return result;
    }

    static MainMenu newMainMenu(Luwrain luwrain)
    {
	final Registry registry = luwrain.getRegistry();
	final RegistryKeys keys = new RegistryKeys();
	final String[] dirs = registry.getDirectories(keys.mainMenuSections());
	if (dirs == null || dirs.length < 1)
	{
	    Log.warning("core", "no main menu sections in the registry");
	    return null;
	}
	Arrays.sort(dirs);
	final LinkedList<Section> sects = new LinkedList<Section>();
	for(String s: dirs)
	{
	    final String path = RegistryPath.join(keys.mainMenuSections(), s);
	    final Settings.MainMenuSection proxy = Settings.createMainMenuSection(registry, path);
	    final Section sect = loadSection(luwrain, proxy);
	    if (sect != null)
		sects.add(sect);
	}
	final LinkedList objs = new LinkedList();
	for(Section s: sects)
	{
	    objs.add(s);
	    for(UniRefInfo u: s.uniRefs())
		objs.add(u);
	}
	final ListParams params = new ListParams();
	params.environment = new DefaultControlEnvironment(luwrain);
	params.model = new FixedListModel(objs.toArray(new Object[objs.size()]));
	params.appearance = new Appearance(luwrain);
	params.name = "Главное меню";
	final MainMenu mainMenu = new MainMenu(luwrain, params);
mainMenu.setClickHandler(mainMenu);
return mainMenu;
    }

    static private Section loadSection(Luwrain luwrain, Settings.MainMenuSection proxy)
    {
	final String title = proxy.getTitle("");
	final String[] refs = proxy.getUniRefs("").split("\\\\:", -1);
	final LinkedList<UniRefInfo> uniRefs = new LinkedList<UniRefInfo>();
	for(String s: refs)
	{
	    if (s == null || s.trim().isEmpty())
		continue;
	    final UniRefInfo uniRef = luwrain.getUniRefInfo(s);
	    if (uniRef != null)
		uniRefs.add(uniRef);
	}
	return new Section(title, uniRefs.toArray(new UniRefInfo[uniRefs.size()]));
    }
}
