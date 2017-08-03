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

package org.luwrain.controls;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;

public class ListUtils
{
    static public class DefaultAppearance implements ListArea.Appearance
    {
	protected final ControlEnvironment environment;
	protected final Suggestions suggestion;

	public DefaultAppearance(ControlEnvironment environment, Suggestions suggestion)
	{
	    NullCheck.notNull(environment, "environment");
	    this.environment = environment;
	    this.suggestion = suggestion;
	}

	public DefaultAppearance(ControlEnvironment environment)
	{
	    NullCheck.notNull(environment, "environment");
	    this.environment = environment;
	    this.suggestion = Suggestions.LIST_ITEM;
	}


	@Override public void announceItem(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    //	    environment.playSound(Sounds.LIST_ITEM);
	    //	    environment.say(item.toString());
	    environment.setEventResponse(DefaultEventResponse.listItem(item.toString(), flags.contains(Flags.BRIEF)?null:suggestion));
	}

	@Override public String getScreenAppearance(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    return item.toString();
	}

	@Override public int getObservableLeftBound(Object item)
	{
	    return 0;
	}

	@Override public int getObservableRightBound(Object item)
	{
	    return item != null?item.toString().length():0;
	}
    }

    static abstract public class DoubleLevelAppearance implements ListArea.Appearance
    {
	protected final ControlEnvironment environment;

	public DoubleLevelAppearance(ControlEnvironment environment)
	{
	    NullCheck.notNull(environment, "environment");
	    this.environment = environment;
	}

	abstract public boolean isSectionItem(Object item);

	@Override public void announceItem(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    environment.silence();
	    if (isSectionItem(item))
	    {
		environment.playSound(Sounds.DOC_SECTION);
		environment.say(item.toString());
	    } else
	    {
		environment.playSound(Sounds.LIST_ITEM);
		environment.say(item.toString());
	    }
	}

	@Override public String getScreenAppearance(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    if (isSectionItem(item))
		return item.toString();
	    return "  " + item.toString();
	}

	@Override public int getObservableLeftBound(Object item)
	{
	    NullCheck.notNull(item, "item");
	    if (isSectionItem(item))
		return 0;
	    return 2;
	}

	@Override public int getObservableRightBound(Object item)
	{
	    NullCheck.notNull(item, "item");
	    return getScreenAppearance(item, EnumSet.noneOf(Flags.class)).length();
	}
    }
    
    static public class DefaultTransition implements ListArea.Transition
    {
	static protected final int PAGE_SIZE = 20;

	@Override public State transition(Type type, State fromState, int itemCount,
					  boolean hasEmptyLineTop, boolean hasEmptyLineBottom)
	{
	    NullCheck.notNull(type, "type");
	    NullCheck.notNull(fromState, "fromState");
	    if (itemCount == 0)
		throw new IllegalArgumentException("itemCount must be positive and non-zero (itemCount=" + itemCount + ")");
	    switch(type)
	    {
	    case SINGLE_DOWN:
		if (fromState.type == State.Type.EMPTY_LINE_TOP)
		    return new State(0);
		if (fromState.type == State.Type.EMPTY_LINE_BOTTOM)
		    return new State(State.Type.NO_TRANSITION);
		if (fromState.type != State.Type.ITEM_INDEX)
		    return new State(State.Type.NO_TRANSITION);
		if (fromState.itemIndex + 1 < itemCount)
		    return new State(fromState.itemIndex + 1);
		return new State(hasEmptyLineBottom?State.Type.EMPTY_LINE_BOTTOM:State.Type.NO_TRANSITION);
	    case SINGLE_UP:
		if (fromState.type == State.Type.EMPTY_LINE_BOTTOM)
		    return new State(itemCount - 1);
		if (fromState.type == State.Type.EMPTY_LINE_TOP)
		    return new State(State.Type.NO_TRANSITION);
		if (fromState.type != State.Type.ITEM_INDEX)
		    return new State(State.Type.NO_TRANSITION);
		if (fromState.itemIndex > 0)
		    return new State(fromState.itemIndex - 1);
		return new State(hasEmptyLineTop?State.Type.EMPTY_LINE_TOP:State.Type.NO_TRANSITION);
	    case PAGE_DOWN:
		if (fromState.type == State.Type.EMPTY_LINE_TOP)
		    return new State(Math.min(PAGE_SIZE, itemCount - 1));
		if (fromState.type == State.Type.EMPTY_LINE_BOTTOM)
		    return new State(State.Type.NO_TRANSITION);
		if (fromState.type != State.Type.ITEM_INDEX)
		    return new State(State.Type.NO_TRANSITION);
		if (fromState.itemIndex + PAGE_SIZE < itemCount)
		    return new State(fromState.itemIndex + PAGE_SIZE);
		if (hasEmptyLineBottom)
		    return new State(State.Type.EMPTY_LINE_BOTTOM);
		if (fromState.itemIndex + 1>= itemCount)
		    return new State(State.Type.NO_TRANSITION);
		return new State(itemCount - 1);
	    case PAGE_UP:
		if (fromState.type == State.Type.EMPTY_LINE_BOTTOM)
		    return new State(itemCount > PAGE_SIZE?itemCount - PAGE_SIZE:0);
		if (fromState.type == State.Type.EMPTY_LINE_TOP)
		    return new State(State.Type.NO_TRANSITION);
		if (fromState.type != State.Type.ITEM_INDEX)
		    return new State(State.Type.NO_TRANSITION);
		if (fromState.itemIndex >= PAGE_SIZE)
		    return new State(fromState.itemIndex - PAGE_SIZE);
		if (hasEmptyLineTop)
		    return new State(State.Type.EMPTY_LINE_TOP);
		if (fromState.itemIndex == 0)
		    return new State(State.Type.NO_TRANSITION);
		return new State(0);
	    case HOME:
		return new State(0);
	    case END:
		if (hasEmptyLineBottom)
		    return new State(State.Type.EMPTY_LINE_BOTTOM);
		if (fromState.type != State.Type.ITEM_INDEX)
		    return new State(State.Type.NO_TRANSITION);
		return new State(fromState.itemIndex - 1);
	    default:
		return new State(State.Type.NO_TRANSITION);
	    }
	}
    }

    static public class FixedModel extends Vector implements ListArea.Model
    {
	public FixedModel()
	{
	}

	public FixedModel(Object[] items)
	{
	    NullCheck.notNullItems(items, "items");
	    setItems(items);
	}

	public void setItems(Object[] items)
	{
	    NullCheck.notNullItems(items, "items");
	    setSize(items.length);
	    for(int i = 0;i < items.length;++i)
		set(i, items[i]);
	}

	public Object[] getItems()
	{
	    return toArray(new Object[size()]);
	}

	@Override public int getItemCount()
	{
	    return size();
	}

	@Override public Object getItem(int index)
	{
	    return get(index);
	}

	@Override public void refresh()
	{
	}

    }

    static public class DefaultMarksInfo implements MarkableListArea.MarksInfo
    {
	protected final Set items = new HashSet();

	@Override public boolean marked(Object o)
	{
	    NullCheck.notNull(o, "o");
	    return items.contains(o);
	}

	@Override public void mark(Object o)
	{
	    NullCheck.notNull(o, "o");
	    items.add(o);
	}

	@Override public void unmark(Object o)
	{
	    NullCheck.notNull(o, "o");
	    items.remove(o);
	}

	@Override public boolean toggleMark(Object o)
	{
	    NullCheck.notNull(o, "o");
	    if (marked(o))
	    {
unmark(o);
		return false;
	    }
mark(o);
	    return true;
	}

	@Override public void markOnly(Object[] o)
	{
	    NullCheck.notNullItems(o, "o");
	    items.clear();
	    for(Object oo: o)
		items.add(oo);
	}

	@Override public void clearMarks()
	{
	    items.clear();
	}

	@Override public Object[] getAllMarked()
	{
	    final List res = new LinkedList();
	    for(Object o: items)
		res.add(o);
	    return res.toArray(new Object[res.size()]);
	}
    }

    public static class MarkableListAppearance implements ListArea.Appearance
    {
	protected final ControlEnvironment context;
	protected final MarkableListArea.MarksInfo marksInfo;

	public MarkableListAppearance(ControlEnvironment context, MarkableListArea.MarksInfo marksInfo)
	{
	    NullCheck.notNull(context, "context");
	    NullCheck.notNull(marksInfo, "marksInfo");
	    this.context = context;
	    this.marksInfo = marksInfo;
	}

	@Override public void announceItem(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    context.playSound(Sounds.LIST_ITEM);
	    context.silence();
	    if (flags.contains(Flags.BRIEF))
	    {
		context.say(item.toString());
		return;
	    }
	    if (marksInfo.marked(item))
		context.say("Отмечено " + item.toString()); else// //FIXME:
		context.say(item.toString());
	}

	@Override public String getScreenAppearance(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    if (marksInfo.marked(item))
		return "* " + item.toString();
	    return "  " + item.toString();
	}

	@Override public int getObservableLeftBound(Object item)
	{
	    if (item == null)
		return 0;
	    return 2;
	}

	@Override public int getObservableRightBound(Object item)
	{
	    if (item == null)
		return 0;
	    return getScreenAppearance(item, EnumSet.noneOf(Flags.class)).length();
	}
    }

    static public class DefaultClipboardObjects implements ListArea.ClipboardObjects
    {
	@Override public Serializable getClipboardObject(ListArea listArea, ListArea.Model model, ListArea.Appearance appearance, int index)
	{
	    NullCheck.notNull(model, "model");
	    NullCheck.notNull(appearance, "appearance");
	    final Object obj = model.getItem(index);
	    if (obj == null)
		return null;
	    return appearance.getScreenAppearance(obj, EnumSet.of(ListArea.Appearance.Flags.CLIPBOARD));
	}
    }
}
