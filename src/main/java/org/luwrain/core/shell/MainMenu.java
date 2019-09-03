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

package org.luwrain.core.shell;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;
import org.luwrain.popups.*;
import org.luwrain.util.*;

public final class MainMenu extends ListArea implements PopupClosingTranslator.Provider
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
		/*
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
		*/
		return true;
	    case PAGE_UP:
	    case ALTERNATIVE_PAGE_UP:
		/*
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
		*/
		return true;
	    }
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != EnvironmentEvent.Type.REGULAR)
	    return super.onSystemEvent(event);
	if (closing.onSystemEvent(event))
	    return true;
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
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.model = new ListUtils.FixedModel(RegistryUtils.getStringArray(registry, Settings.MAIN_MENU_UNIREFS_PATH));
	params.appearance = new Appearance(new DefaultControlContext(luwrain));
	params.transition = new Transition();
	//	params.flags = EnumSet.noneOf(ListArea.Flags.class);
	params.name = luwrain.i18n().getStaticStr("MainMenuName");
	final MainMenu mainMenu = new MainMenu(luwrain, params);
	return mainMenu;
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

    //Also used in the control panel section for editing
    static public final class Appearance extends ListUtils.DoubleLevelAppearance
    {
	static private final String STATIC_PREFIX = "static:";
	private final Map<String, UniRefInfo> uniRefCache = new HashMap();
	public Appearance(ControlContext context)
	{
	    super(context);
	}
	@Override public boolean isSectionItem(Object obj)
	{
	    NullCheck.notNull(obj, "obj");
	    final UniRefInfo info = getUniRefInfo(obj);
	    return info.getType().equals("section");
	}
	@Override public String getSectionScreenAppearance(Object item)
	{
	    NullCheck.notNull(item, "item");
	    final UniRefInfo info = getUniRefInfo(item);
	    final String title = info.getTitle();
	    if (!title.startsWith(STATIC_PREFIX))
		return title;
	    return context.getI18n().getStaticStr(title.substring(STATIC_PREFIX.length()));
	}
    	@Override public String getNonSectionScreenAppearance(Object item)
	{
	    NullCheck.notNull(item, "item");
	    final UniRefInfo info = getUniRefInfo(item);
	    final String title = info.getTitle();
	    if (!title.startsWith(STATIC_PREFIX))
		return title;
	    return context.getI18n().getStaticStr(title.substring(STATIC_PREFIX.length()));
	}
	private UniRefInfo getUniRefInfo(Object obj)
	{
	    NullCheck.notNull(obj, "obj");
	    if (obj instanceof UniRefInfo)
		return (UniRefInfo)obj;
	    if (uniRefCache.containsKey(obj.toString()))
		return uniRefCache.get(obj.toString());
	    final UniRefInfo info = context.getUniRefInfo(obj.toString());
	    if (info != null)
	    {
		uniRefCache.put(obj.toString(), info);
		return info;
	    }
	    final UniRefInfo info2 = new UniRefInfo(obj.toString());
	    uniRefCache.put(obj.toString(), info2);
	    return info2;
	}
    }
}
