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

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;
import org.luwrain.popups.*;

public class MainMenu extends ListArea implements PopupClosingTranslator.Provider
{
    private final Luwrain luwrain;
    public final PopupClosingTranslator closing = new PopupClosingTranslator(this);
    private UniRefInfo result = null;

    private MainMenu(Luwrain luwrain, ListArea.Params params)
    {
	super(params);
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	setListClickHandler((area,index,obj)->closing.doOk());
    }

    @Override public boolean onInputEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (closing.onInputEvent(event))
	    return true;
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case PAGE_DOWN:
	    case ALTERNATIVE_PAGE_DOWN:
		if (selectedIndex() + 1 >= listModel.getItemCount())
		{
		    context.setEventResponse(DefaultEventResponse.hint(Hint.NO_ITEMS_BELOW));
		    return true;
		}
		for(int i = selectedIndex() + 1;i < listModel.getItemCount();++i)
		    if (listModel.getItem(i) instanceof Section)
		    {
			select(i, true);
			return true;
		    }
		context.setEventResponse(DefaultEventResponse.hint(Hint.NO_ITEMS_BELOW));
		return true;
	    case PAGE_UP:
	    case ALTERNATIVE_PAGE_UP:
		if (selectedIndex() < 1)
		{
		    context.setEventResponse(DefaultEventResponse.hint(Hint.NO_ITEMS_ABOVE));
		    return true;
		}
		for(int i = selectedIndex() - 1;i >= 0;--i)
		    if (listModel.getItem(i) instanceof Section)
		    {
			select(i, true);
			return true;
		    }
context.setEventResponse(DefaultEventResponse.hint(Hint.NO_ITEMS_ABOVE));
		return true;
	    }
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (closing.onSystemEvent(event))
	    return true;
	if (event.getType() != EnvironmentEvent.Type.REGULAR)
	    return super.onSystemEvent(event);
	switch(event.getCode())
	{
	case INTRODUCE:
	    luwrain.silence();
	    luwrain.playSound(Sounds.MAIN_MENU);
	    luwrain.speak(getAreaName());
	    return true;
	default:
	    return super.onSystemEvent(event);
	}
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	switch(query.getQueryCode())
	{
	case AreaQuery.UNIREF_HOT_POINT:
	    if (selected() == null || !(selected() instanceof UniRefInfo))
		return false;
	    ((UniRefHotPointQuery)query).answer(((UniRefInfo)selected()).getValue());
	    return true;
	case AreaQuery.BACKGROUND_SOUND:
	    ((BackgroundSoundQuery)query).answer(new BackgroundSoundQuery.Answer(BkgSounds.MAIN_MENU));
	    return true;
	default:
	    return super.onAreaQuery(query);
	}
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

    public UniRefInfo result()
    {
	return result;
    }

    static public MainMenu newMainMenu(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Registry registry = luwrain.getRegistry();
	final String[] dirs = registry.getDirectories(Settings.MAIN_MENU_SECTIONS_PATH);
	if (dirs == null || dirs.length < 1)
	{
	    Log.warning("core", "no main menu sections in the registry");
	    return null;
	}
	Arrays.sort(dirs);
	final LinkedList<Section> sects = new LinkedList<Section>();
	for(String s: dirs)
	{
	    final String path = Registry.join(Settings.MAIN_MENU_SECTIONS_PATH, s);
	    final Settings.MainMenuSection proxy = Settings.createMainMenuSection(registry, path);
	    final Section sect = loadSection(luwrain, proxy);
	    if (sect != null)
		sects.add(sect);
	}
	final LinkedList objs = new LinkedList();
	for(Section s: sects)
	{
	    objs.add(s);
	    for(UniRefInfo u: s.uniRefs)
		objs.add(u);
	}
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.model = new ListUtils.FixedModel(objs.toArray(new Object[objs.size()]));
	params.appearance = new Appearance(luwrain);
	params.transition = new Transition();
	params.flags = EnumSet.noneOf(ListArea.Flags.class);
	params.name = luwrain.i18n().getStaticStr("MainMenuName");
	final MainMenu mainMenu = new MainMenu(luwrain, params);
	return mainMenu;
    }

    static private Section loadSection(Luwrain luwrain, Settings.MainMenuSection proxy)
    {
	final String title = sectionName(proxy.getTitle(""));
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

    static private String sectionName(String name)
    {
	return name;
    }

    static private class Section
    {
	final String title;
	final UniRefInfo[] uniRefs;

	Section(String title, UniRefInfo[] uniRefs)
	{
	    NullCheck.notNull(title, "title");
	    NullCheck.notNullItems(uniRefs, "uniRefs");
	    this.title = title;
	    this.uniRefs = uniRefs;
	}

	@Override public String toString() 
	{
	    return title;
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
	    if (item instanceof Section)
	    {
		luwrain.silence();
		luwrain.playSound(Sounds.DOC_SECTION);
		luwrain.speak(item.toString());
		return;
	    }
	    luwrain.silence();
	    luwrain.playSound(Sounds.MAIN_MENU_ITEM);
	    luwrain.speak(item.toString());
	}

	@Override public String getScreenAppearance(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    if (item instanceof Section)
		return item.toString();
	    return "  " + item.toString();
	}

	@Override public int getObservableLeftBound(Object item)
	{
	    if (item == null)
		return 0;
	    if (item instanceof Section)
		return 0;
	    return 2;
	}

	@Override public int getObservableRightBound(Object item)
	{
	    return item != null?getScreenAppearance(item, EnumSet.noneOf(Flags.class)).length():0;
	}
    }

    static private class Transition extends ListUtils.DefaultTransition
    {
	@Override public State transition(Type type, State fromState, int itemCount,
					  boolean hasEmptyLineTop, boolean hasEmptyLineBottom)
	{
	    NullCheck.notNull(type, "type");
	    NullCheck.notNull(fromState, "fromState");
	    if (itemCount == 0)
		throw new IllegalArgumentException("itemCount must be greater than zero");
	    switch(type)
	    {
	    case SINGLE_DOWN:
		if (fromState.type != State.Type.ITEM_INDEX || fromState.itemIndex + 1 != itemCount)
		    return super.transition(type, fromState, itemCount, hasEmptyLineTop, hasEmptyLineBottom);
		return new State(0);
	    case SINGLE_UP:
		if (fromState.type != State.Type.ITEM_INDEX || fromState.itemIndex != 0)
		    return super.transition(type, fromState, itemCount, hasEmptyLineTop, hasEmptyLineBottom);
		return new State(itemCount - 1);
	    default:
		return super.transition(type, fromState, itemCount, hasEmptyLineTop, hasEmptyLineBottom);
	    }
	}
    }
}
