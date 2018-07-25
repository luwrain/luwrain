/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
import org.luwrain.cpanel.*;

final class SoundsList extends ListArea implements SectionArea, ListClickHandler
{
    static private final Sounds[] allSounds = new Sounds[]{
	Sounds.ANNOUNCEMENT,
	Sounds.ATTENTION,
	Sounds.BLOCKED,
	Sounds.CANCEL,
	Sounds.CHAT_MESSAGE,
	Sounds.CLICK,
	Sounds.COMMANDER_LOCATION,
	Sounds.COPIED,
	Sounds.CUT,
	Sounds.DELETED,
	Sounds.DESKTOP_ITEM,
	Sounds.DOC_SECTION,
	Sounds.DONE,
	Sounds.EMPTY_LINE,
	Sounds.END_OF_LINE,
	Sounds.ERROR,
	Sounds.EVENT_NOT_PROCESSED,
	Sounds.FATAL,
	Sounds.GENERAL_TIME,
	Sounds.INTRO_APP,
	Sounds.INTRO_POPUP,
	Sounds.INTRO_REGULAR,
	Sounds.LIST_ITEM,
	Sounds.MAIN_MENU,
	Sounds.MAIN_MENU_ITEM,
	Sounds.MESSAGE,
	Sounds.NO_APPLICATIONS,
	Sounds.NO_CONTENT,
	Sounds.NO_ITEMS_ABOVE,
	Sounds.NO_ITEMS_BELOW,
	Sounds.NO_LINES_ABOVE,
	Sounds.NO_LINES_BELOW,
	Sounds.OK,
	Sounds.PARAGRAPH,
	Sounds.PASTE,
	Sounds.PROTECTED_RESOURCE,
	Sounds.REGION_POINT,
	Sounds.SEARCH,
	Sounds.SELECTED,
	Sounds.SHUTDOWN,
	Sounds.STARTUP,
	Sounds.TABLE_CELL,
	Sounds.TERM_BELL,
	Sounds.UNSELECTED,
    };

    private final ControlPanel controlPanel;
    private final Luwrain luwrain;

    SoundsList(ControlPanel controlPanel, ListArea.Params params)
    {
	super(params);
	NullCheck.notNull(controlPanel, "controlPanel");
	NullCheck.notNull(params, "params");
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
	setListClickHandler(this);
    }

    @Override public boolean onListClick(ListArea area, int index, Object obj)
    {
	luwrain.message("aaa");
	return true;
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

    static private Item[] loadItems(Registry registry)
    {
	final List<Item> res = new LinkedList();
	for(Sounds s: allSounds)
	    res.add(new Item(s, s.toString(), new File("/tmp")));
	return res.toArray(new Item[res.size()]);
    }

    static SoundsList create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	final Luwrain luwrain = controlPanel.getCoreInterface();
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlEnvironment(luwrain);
	params.appearance = new ListUtils.DefaultAppearance(params.context, Suggestions.LIST_ITEM);
	params.name = "Звуки системных событий";
	params.model = new ListUtils.FixedModel(loadItems(luwrain.getRegistry()));
	return new SoundsList(controlPanel, params);
    }

    static private final class Item 
    {
	final Sounds sound;
	final String title;
	final File file;
	Item(Sounds sound, String title, File file)
	{
	    NullCheck.notNull(sound, "sound");
	    NullCheck.notNull(title, "title");
	    NullCheck.notNull(file, "file");
	    this.sound = sound;
	    this.title = title;
	    this.file = file;
	}
	@Override public String toString()
	{
	    return title + ": " + file.getAbsolutePath();
	}
    }
    
}
