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
import java.nio.file.*;
import java.nio.charset.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.popups.Popups;

public class Desktop implements Application
{
    private Luwrain luwrain;
    private UniRefList uniRefList = null;
    private ListArea area;
    private Model model = null;

    private final Environment core;
    private Conversations conversations = null;

    public Desktop(Environment core)
    {
	NullCheck.notNull(core, "core");
	this.core = core;
    }

    private String clickHereLine = "#click here#";

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
	load();
	luwrain.onAreaNewName(area);
    }

    public void setConversations(Conversations conversations)
    {
	this.conversations = conversations;
    }

    private void createArea()
    {
	this.uniRefList = new UniRefList(luwrain);
	this.model = new Model(luwrain, uniRefList);

	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlEnvironment(luwrain);
	params.model = this.model;
	params.appearance = new Appearance(luwrain);
	params.name = luwrain.i18n().getStaticStr("Desktop");

	area = new ListArea(params) {

		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{ 
			case ESCAPE:
core.quit();
			    return true;
			case DELETE:
			    return onDeleteImpl(getHotPointX(), getHotPointY());
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
		    return "Рабочий стол пуст";
		}

		/*
		public boolean insertRegion(int x, int y, RegionContent data)//FIXME:
		{
		    if (!onInsertImpl(x, y, data))
			return false;
		    refresh();
		    return true;
		}
		*/

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

    boolean onClick(int index, Object obj)
    {
	if (obj == null)
	    return false;
	if (obj.equals(clickHereLine))
	{
	    model.setIntroduction(null);
	    model.refresh();
	    final Settings.Desktop sett = Settings.createDesktop(luwrain.getRegistry());
	    sett.setIntroductionFile("");
	    return true;
	}
	if (obj instanceof UniRefInfo)
	{
	    final UniRefInfo uniRefInfo = (UniRefInfo)obj;
	    luwrain.openUniRef(uniRefInfo.value());
	    return true;
	}
	return false;
    }

    boolean onInsertImpl(int x, int y, String[] data)
    {
	NullCheck.notNullItems(data, "data");
	uniRefList.add(y - model.getFirstUniRefPos(), data);
	uniRefList.save();
	return true;
    }

    boolean onDeleteImpl(int x, int y)
    {
	if (area.selected() == null)
	    return false;
	final int index = y - model.getFirstUniRefPos();
	if (index < 0)
	    return false;
	if (conversations != null && !conversations.deleteDesktopItemConfirmation())
	    return true;
	if (!uniRefList.delete(index))
	    return false;
	uniRefList.save();
	area.refresh();
	return true;
    }

    void load()
    {
	this.clickHereLine = luwrain.i18n().getStaticStr("DesktopClickHereToCancelIntroduction");
	uniRefList.load();
	final Settings.Desktop sett = Settings.createDesktop(luwrain.getRegistry());
	final String introductionFile = sett.getIntroductionFile("");
	if (!introductionFile.trim().isEmpty())
	{
	    final String[] introduction = new File(introductionFile).isAbsolute()?
	    readIntroduction(Paths.get(introductionFile)):
	    readIntroduction(luwrain.getFileProperty("luwrain.dir.data").toPath().resolve(introductionFile));
	    model.setIntroduction(introduction);
	    model.setClickHereLine(clickHereLine);
	}
	model.refresh();
    }

    @Override public String getAppName()
    {
	return luwrain.i18n().getStaticStr("Desktop");
    }

    @Override public AreaLayout getAreaLayout()
    {
	return new AreaLayout(area);
    }

    static String[] readIntroduction(Path path)
    {
	NullCheck.notNull(path, "path");
	try {
	    final LinkedList<String> a = new LinkedList<String>();
	    try (Scanner scanner =  new Scanner(path, StandardCharsets.UTF_8.name()))
		{
		    while (scanner.hasNextLine())
			a.add(scanner.nextLine());
		}
	    return 	    a.toArray(new String[a.size()]);
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	    return new String[0];
	}
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
	    if (item instanceof String)
	    {
		final String s = (String)item;
		if (s.trim().isEmpty())
		    luwrain.hint(Hints.EMPTY_LINE); else
		    luwrain.say(s);
		return;
	    }
	    if (item instanceof UniRefInfo)
	    {
		final UniRefInfo i = (UniRefInfo)item;
if (!flags.contains(Flags.BRIEF))
		luwrain.setEventResponse(DefaultEventResponse.listItem(i.title(), Suggestions.CLICKABLE_LIST_ITEM)); else
    luwrain.setEventResponse(DefaultEventResponse.listItem(i.title(), null));
		return;
	    }
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
    }

    static private class Model implements ListArea.Model
    {
	private final Luwrain luwrain;
	private final UniRefList uniRefList;
	private Object[] items;

	private String clickHereLine = null;
	private int firstUniRefPos = 0;

	private String[] introduction;

	Model(Luwrain luwrain, UniRefList uniRefList)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(uniRefList, "uniRefList");
	    this.luwrain = luwrain;
	    this.uniRefList = uniRefList;
	}

	@Override public int getItemCount()
	{
	    return items != null?items.length:0;
	}

	@Override public Object getItem(int index)
	{
	    if (items == null)
		return null;
	    return index < items.length?items[index]:null;
	}

	@Override public void refresh()
	{
	    final LinkedList res = new LinkedList();
	    if (introduction != null && introduction.length > 0)
	    {
		for(String s: introduction)
		    res.add(s);
		if (clickHereLine != null)
		{
		    res.add("");
		    res.add(clickHereLine);
		    res.add("");
		}
	    }
	    firstUniRefPos = res.size();
	    final UniRefInfo[] uniRefs = uniRefList.get();
	    for(UniRefInfo u: uniRefs)
		res.add(u);
	    items = res.toArray(new Object[res.size()]);
	}

	int getFirstUniRefPos()
	{
	    return firstUniRefPos;
	}

	void setIntroduction(String[] text)
	{
	    introduction =text;
	}

	void setClickHereLine(String line)
	{
	    clickHereLine = line;
	}
    }

    class UniRefList
    {
	private final Luwrain luwrain;
	private final Registry registry;
	private UniRefInfo[] uniRefs = new UniRefInfo[0];

	UniRefList(Luwrain luwrain)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    this.luwrain = luwrain;
	    this.registry = luwrain.getRegistry();
	}

	UniRefInfo[] get()
	{
	    return uniRefs;
	}

	void load()
	{
	    final String[] values = registry.getValues(Settings.DESKTOP_UNIREFS_PATH);
	    final LinkedList<UniRefInfo> res = new LinkedList<UniRefInfo>();
	    for(String v: values)
	    {
		final String path = Registry.join(Settings.DESKTOP_UNIREFS_PATH, v);
		if (registry.getTypeOf(path) != Registry.STRING)
		    continue;
		final String s = registry.getString(path);
		if (s.isEmpty())
		    continue;
		final UniRefInfo uniRef = luwrain.getUniRefInfo(s);
		if (uniRef != null && !res.contains(uniRef))
		    res	   .add(uniRef);
	    }
	    uniRefs = res.toArray(new UniRefInfo[res.size()]);
	}

	void save()
	{
	    final String[] values = registry.getValues(Settings.DESKTOP_UNIREFS_PATH);
	    if (values != null)
		for(String v: values)
		    registry.deleteValue(Registry.join(Settings.DESKTOP_UNIREFS_PATH, v));
	    for(int i = 0;i < uniRefs.length;++i)
	    {
		String name = "" + (i + 1);
		while (name.length() < 6)
		    name = "0" + name;
		registry.setString(Registry.join(Settings.DESKTOP_UNIREFS_PATH, name), uniRefs[i].value());
	    }
	}

	void add(int pos, String[] values)
	{
	    NullCheck.notNullItems(values, "values");
	    final LinkedList<UniRefInfo> toAdd = new LinkedList<UniRefInfo>();
	    for(String v: values)
	    {
		final UniRefInfo uniRef = luwrain.getUniRefInfo(v);
		if (uniRef != null)
		    toAdd.add(uniRef);
	    }
	    if (toAdd.isEmpty())
		return;
	    final UniRefInfo[] newItems = toAdd.toArray(new UniRefInfo[toAdd.size()]);
	    final int newPos = (pos >= 0 && pos <= uniRefs.length)?pos:0;
	    final LinkedList<UniRefInfo> res = new LinkedList<UniRefInfo>();
	    for(int i = 0;i < newPos;++i)
		res.add(uniRefs[i]);
	    for(UniRefInfo u: newItems)
		res.add(u);
	    for(int i = newPos;i < uniRefs.length;++i)
		res.add(uniRefs[i]);
	    uniRefs = res.toArray(new UniRefInfo[res.size()]);
	}

	boolean delete(int pos)
	{
	    if (uniRefs == null || uniRefs.length < 1 ||
		pos < 0 || pos >= uniRefs.length)
		return false;
	    final UniRefInfo[] n = new UniRefInfo[uniRefs.length - 1];
	    for(int i = 0;i < pos;++i)
		n[i] = uniRefs[i];
	    for(int i = pos + 1;i < uniRefs.length;++i)
		n[i - 1] = uniRefs[i];
	    uniRefs = n;
	    return true;
	}
    }

    @Override public void closeApp()
    {
    }
}
