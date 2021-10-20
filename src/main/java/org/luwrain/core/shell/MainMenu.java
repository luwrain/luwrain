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

public final class MainMenu extends ListArea<MainMenuItem> implements PopupClosingTranslator.Provider
{
    private final Luwrain luwrain;
    public final PopupClosingTranslator closing = new PopupClosingTranslator(this);
    private UniRefInfo result = null;

    private MainMenu(Luwrain luwrain, ListArea.Params<MainMenuItem> params)
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
	    /*
	case AreaQuery.UNIREF_HOT_POINT:
	    if (selected() == null || !(selected() instanceof UniRefInfo))
		return false;
	    ((UniRefHotPointQuery)query).answer(((UniRefInfo)selected()).getValue());
	    return true;
	case AreaQuery.BACKGROUND_SOUND:
	    ((BackgroundSoundQuery)query).answer(new BackgroundSoundQuery.Answer(BkgSounds.MAIN_MENU));
	    return true;
	    */
	default:
	    return super.onAreaQuery(query);
	}
    }

    @Override public boolean onOk()
    {
	final MainMenuItem o = selected();
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
	final ListArea.Params<MainMenuItem> params = new ListArea.Params<>();
	params.context = new DefaultControlContext(luwrain);
	final Appearance appearance = new Appearance(params.context);
	params.model = new ListUtils.FixedModel<>(items);
	params.appearance = appearance;
	params.transition = new Transition(params.model, (Appearance)params.appearance);
	params.name = luwrain.i18n().getStaticStr("MainMenuName");
	params.clipboardSaver = new ListUtils.DefaultClipboardSaver<MainMenuItem>(){
		@Override protected Object getClipboardObj(ListArea.Appearance<MainMenuItem> a, MainMenuItem item)
		{
		    NullCheck.notNull(a, "a");
		    NullCheck.notNull(item, "item");
		    return appearance.getUniRefInfo(item);
		}
		@Override protected String getClipboardString(ListArea.Appearance<MainMenuItem> a, MainMenuItem item)
		{
		    NullCheck.notNull(a, "a");
		    NullCheck.notNull(item, "item");
		    return appearance.getUniRefInfo(item).getTitle();
		}
	    };

	return new MainMenu(luwrain, params);
    }

    static final class Appearance extends ListUtils.DoubleLevelAppearance<MainMenuItem>
    {
	static private final String STATIC_PREFIX = "static:";
	private final Map<String, UniRefInfo> uniRefCache = new HashMap<>();
	Appearance(ControlContext context) { super(context); }
	@Override public boolean isSectionItem(MainMenuItem item)
	{
	    NullCheck.notNull(item, "item");
	    final UniRefInfo info = getUniRefInfo(item);
	    return info.getType().equals(UniRefProcs.TYPE_SECTION);
	}
	@Override public String getSectionScreenAppearance(MainMenuItem item)
	{
	    NullCheck.notNull(item, "item");
	    final UniRefInfo info = getUniRefInfo(item);
	    final String title = info.getTitle();
	    if (!title.startsWith(STATIC_PREFIX))
		return title;
	    return context.getI18n().getStaticStr(title.substring(STATIC_PREFIX.length()));
	}
    	@Override public String getNonSectionScreenAppearance(MainMenuItem item)
	{
	    NullCheck.notNull(item, "item");
	    final UniRefInfo info = getUniRefInfo(item);
	    final String title = info.getTitle();
	    if (!title.startsWith(STATIC_PREFIX))
		return title;
	    return context.getI18n().getStaticStr(title.substring(STATIC_PREFIX.length()));
	}
	@Override public void announceNonSection(MainMenuItem item)
	{
	    NullCheck.notNull(item, "item");
	    context.setEventResponse(DefaultEventResponse.text(Sounds.DESKTOP_ITEM, context.getSpeakableText(getNonSectionScreenAppearance(item), Luwrain.SpeakableTextType.NATURAL)));
	}
	public void announceSection(MainMenuItem item)
	{
	    NullCheck.notNull(item, "item");
	    context.setEventResponse(DefaultEventResponse.text(Sounds.DOC_SECTION, context.getSpeakableText(getNonSectionScreenAppearance(item), Luwrain.SpeakableTextType.NATURAL)));//FIXME:DefaultEventResponse.listItem()
	}
	UniRefInfo getUniRefInfo(MainMenuItem item)
	{
	    NullCheck.notNull(item, "item");
		final String value = item.getValueNotNull();
	    if (uniRefCache.containsKey(value))
		return uniRefCache.get(value);
	    final UniRefInfo info = context.getUniRefInfo(value);
	    uniRefCache.put(value, info);
	    return info;
	}
    }

    static private class Transition extends ListUtils.DoubleLevelTransition<MainMenuItem>
    {
	private final Appearance appearance;
	Transition(ListArea.Model<MainMenuItem> model, Appearance appearance)
	{
	    super(model);
	    NullCheck.notNull(appearance, "appearance");
	    this.appearance = appearance;
	}
	@Override public boolean isSectionItem(MainMenuItem item)
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
