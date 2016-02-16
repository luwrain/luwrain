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

//import org.luwrain.core.*;
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

    private Luwrain luwrain;
    public final PopupClosing closing = new PopupClosing(this);
    private Strings strings;

    private MainMenu(ListParams params)
    {
	super(params);
    }

    @Override public boolean onListClick(ListArea area, int index,
					    Object item)
    {
	return false;
    }

    @Override public boolean onOk()
    {
	return false;
    }

    @Override public boolean onCancel()
    {
	return false;
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
	params.appearance = new DefaultListItemAppearance(params.environment);
	params.name = "Главное меню";
final MainMenu mainMenu = new MainMenu(params);
mainMenu.setClickHandler(mainMenu);
return mainMenu;
    }

    static private Section loadSection(Luwrain luwrain, Settings.MainMenuSection proxy)
    {
	final String title = proxy.getTitle("");
	final String[] refs = proxy.getUniRefs("").split(":", -1);
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
