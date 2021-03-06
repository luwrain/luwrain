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

package org.luwrain.core.shell;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.controls.*;
import org.luwrain.popups.*;
//import org.luwrain.util.*;
import org.luwrain.io.json.*;

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

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (closing.onInputEvent(event))
	    return true;
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != SystemEvent.Type.REGULAR)
	    return super.onSystemEvent(event);
	if (closing.onSystemEvent(event))
	    return true;
	switch(event.getCode())
	{
	case OK:
	    return closing.doOk();
	case INTRODUCE:
	    luwrain.silence();
	    luwrain.speak(getAreaName(), Sounds.MAIN_MENU);
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
	if (o == null)
	    return false;
	final Appearance appearance = (Appearance)getListAppearance();
	final UniRefInfo uniRefInfo = appearance.getUniRefInfo(o);
	if (uniRefInfo == null)
	    return false;
	if (uniRefInfo.getAddr().isEmpty())
	    return false;
	result = uniRefInfo;
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
	final Settings.UserInterface ui = Settings.createUserInterface(luwrain.getRegistry());
	final MainMenuItem[] items = MainMenuItem.fromJson(ui.getMainMenuContent(""));
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.model = new ListUtils.FixedModel(items);
	params.appearance = new Appearance(new DefaultControlContext(luwrain));
	params.transition = new Transition(params.model, (Appearance)params.appearance);
	params.name = luwrain.i18n().getStaticStr("MainMenuName");
	final MainMenu mainMenu = new MainMenu(luwrain, params);
	return mainMenu;
    }

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
	    return info.getType().equals(UniRefProcs.TYPE_SECTION);
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
	@Override public void announceNonSection(Object item)
	{
	    NullCheck.notNull(item, "item");
	    context.setEventResponse(DefaultEventResponse.text(Sounds.DESKTOP_ITEM, context.getSpeakableText(getNonSectionScreenAppearance(item), Luwrain.SpeakableTextType.NATURAL)));
	}
	public void announceSection(Object item)
	{
	    NullCheck.notNull(item, "item");
	    context.setEventResponse(DefaultEventResponse.text(Sounds.DOC_SECTION, context.getSpeakableText(getNonSectionScreenAppearance(item), Luwrain.SpeakableTextType.NATURAL)));//FIXME:DefaultEventResponse.listItem()
	}
	UniRefInfo getUniRefInfo(Object obj)
	{
	    NullCheck.notNull(obj, "obj");
	    if (obj instanceof UniRefInfo)
		return (UniRefInfo)obj;
	    final String value;
	    if (obj instanceof MainMenuItem)
		value = ((MainMenuItem)obj).getValueNotNull(); else
		value = obj.toString();
	    if (uniRefCache.containsKey(value))
		return uniRefCache.get(value);
	    final UniRefInfo info = context.getUniRefInfo(value);
	    uniRefCache.put(obj.toString(), info);
	    return info;
	}
    }

    static private class Transition extends ListUtils.DoubleLevelTransition
    {
	private final Appearance appearance;
	Transition(ListArea.Model model, Appearance appearance)
	{
	    super(model);
	    NullCheck.notNull(appearance, "appearance");
	    this.appearance = appearance;
	}
	@Override public boolean isSectionItem(Object item)
	{
	    NullCheck.notNull(item, "item");
	    return appearance.isSectionItem(item);
	}
	@Override public State transition(Type type, State fromState, int itemCount, boolean hasEmptyLineTop, boolean hasEmptyLineBottom)
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
