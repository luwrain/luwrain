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
import org.luwrain.popups.*;
import org.luwrain.cpanel.*;
import org.luwrain.player.*;
import org.luwrain.util.*;

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
	Sounds.PLAYING,
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
	if (obj == null || !(obj instanceof Item))
	    return false;
	final Item item = (Item)obj;
	final File file = Popups.path(luwrain, luwrain.i18n().getStaticStr("CpSoundsList"), luwrain.i18n().getStaticStr("CpSoundsListChangePopupPrefix"), item.file.getAbsoluteFile());//FIXME:change to Popups.file()
	if (file == null || file.isDirectory())
	    return true;
	final String soundsDirPath = luwrain.getFileProperty("luwrain.dir.sounds").getAbsolutePath();
	final String path = file.getAbsolutePath();
	final String res;
	if (soundsDirPath.length() + 1 < path.length() && path.startsWith(soundsDirPath))
	    res = path.substring(soundsDirPath.length() + 1); else
	    res = path;
	luwrain.getRegistry().setString(getRegistryPath(item.sound), res);
	item.file = file;
	refresh();
	return true;
    }

    private boolean playSound()
    {
	final Object obj = selected();
	if (obj == null || !(obj instanceof Item))
	    return false;
	final Item item = (Item)obj;
	if (item.file == null || !item.file.exists() || item.file.isDirectory())
	    return false;
	if (luwrain.getPlayer() == null)
	    return false;
	luwrain.getPlayer().play(new Playlist(Urls.toUrl(item.file).toString()), 0, 0, Player.DEFAULT_FLAGS, null);
	return true;
    }

    @Override public boolean onInputEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onInputEvent(event))
	    return true;
	if (!event.isSpecial() && !event.isModified())
	    switch(event.getChar())
	    {
	    case ' ':
		return playSound();
			    }
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

    static private Item[] loadItems(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Registry registry = luwrain.getRegistry();
	final List<Item> res = new LinkedList();
	final File soundsDir = luwrain.getFileProperty("luwrain.dir.sounds");
	for(Sounds s: allSounds)
	{
	    final String path = getRegistryPath(s);
	    final String file;
	    if (registry.getTypeOf(path) == Registry.STRING)
		file = registry.getString(path); else
		file = "";
	    res.add(new Item(s, luwrain.i18n().getStaticStr(getI18nName(s)), new File(soundsDir, file)));
	}
	return res.toArray(new Item[res.size()]);
    }

    static SoundsList create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	final Luwrain luwrain = controlPanel.getCoreInterface();
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.appearance = new ListUtils.DefaultAppearance(params.context, Suggestions.LIST_ITEM);
	params.name = luwrain.i18n().getStaticStr("CpSoundsList");
	params.model = new ListUtils.FixedModel(loadItems(luwrain));
	return new SoundsList(controlPanel, params);
    }

    static private String getI18nName(Sounds sound)
    {
	NullCheck.notNull(sound, "sound");
	final StringBuilder b = new StringBuilder();
	final String str = sound.toString();
	boolean wasLetter = false;
	for(int i = 0;i < str.length();i++)
	{
	    final char c = str.charAt(i);
	    if (Character.isLetter(c) || Character.isDigit(c))
	    {
		b.append("" + (wasLetter?Character.toLowerCase(c):c));
		wasLetter = true;
		continue;
	    }
	    wasLetter = false;
	}
	return "CpSoundsList" + (new String(b));
    }

    static private String getRegistryPath(Sounds sound)
    {
	NullCheck.notNull(sound, "sound");
	final String str = sound.toString();
	return Registry.join(Settings.CURRENT_SOUND_SCHEME_PATH, str.toLowerCase().replaceAll("_", "-"));
    }

    static private final class Item 
    {
	final Sounds sound;
	final String title;
	File file;
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
