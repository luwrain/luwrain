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

//LWR_API 1.0

package org.luwrain.controls;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;

import static org.luwrain.core.DefaultEventResponse.*;

public class ListUtils
{
    static public abstract class AbstractAppearance<E> implements ListArea.Appearance<E>
    {
	@Override public String getScreenAppearance(E item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    return item.toString();
	}
	@Override public int getObservableLeftBound(E item)
	{
	    return 0;
	}
	@Override public int getObservableRightBound(E item)
	{
	    NullCheck.notNull(item, "item");
	    return getScreenAppearance(item, EnumSet.noneOf(Flags.class)).length();
	}
    }

    static public class DefaultAppearance<E> extends AbstractAppearance<E>
    {
	protected final ControlContext context;
	protected final Suggestions suggestion;
	public DefaultAppearance(ControlContext context, Suggestions suggestion)
	{
	    NullCheck.notNull(context, "context");
	    this.context = context;
	    this.suggestion = suggestion;
	}
	public DefaultAppearance(ControlContext context)
	{
	    NullCheck.notNull(context, "context");
	    this.context = context;
	    this.suggestion = Suggestions.LIST_ITEM;
	}
	@Override public void announceItem(E item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    context.setEventResponse(DefaultEventResponse.listItem(item.toString(), flags.contains(Flags.BRIEF)?null:suggestion));
	}
    }

    static abstract public class DoubleLevelAppearance<E> implements ListArea.Appearance<E>
    {
	protected final ControlContext context;
	public DoubleLevelAppearance(ControlContext context)
	{
	    NullCheck.notNull(context, "context");
	    this.context = context;
	}
	abstract public boolean isSectionItem(E item);
	public void announceNonSection(E item)
	{
	    NullCheck.notNull(item, "item");
	    context.setEventResponse(listItem(getNonSectionScreenAppearance(item)));
	}
	public String getNonSectionScreenAppearance(E item)
	{
	    NullCheck.notNull(item, "item");
	    return item.toString();
	}
	public void announceSection(E item)
	{
	    NullCheck.notNull(item, "item");
	    context.playSound(Sounds.DOC_SECTION);
	    context.say(getSectionScreenAppearance(item));
	}
	public String getSectionScreenAppearance(E item)
	{
	    NullCheck.notNull(item, "item");
	    return item.toString();
	}
	@Override public void announceItem(E item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    if (isSectionItem(item))
		announceSection(item); else
		announceNonSection(item);
	}
	@Override public String getScreenAppearance(E item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    if (isSectionItem(item))
		return getSectionScreenAppearance(item);
	    return "  " + getNonSectionScreenAppearance(item);
	}
	@Override public int getObservableLeftBound(E item)
	{
	    NullCheck.notNull(item, "item");
	    if (isSectionItem(item))
		return 0;
	    return 2;
	}
	@Override public int getObservableRightBound(E item)
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
		throw new IllegalArgumentException("Unknown transition type: " + type.toString());
	    }
	}
    }

    static abstract public class DoubleLevelTransition<E> extends DefaultTransition
    {
	protected final ListArea.Model<E> model;
	public DoubleLevelTransition(ListArea.Model<E> model)
	{
	    NullCheck.notNull(model, "model");
	    this.model = model;
	}
	abstract public boolean isSectionItem(E item);
	@Override public State transition(Type type, State fromState, int itemCount, boolean hasEmptyLineTop, boolean hasEmptyLineBottom)
	{
	    NullCheck.notNull(type, "type");
	    NullCheck.notNull(fromState, "fromState");
	    switch(type)
	    {
	    case PAGE_UP:
		if (fromState.type == State.Type.EMPTY_LINE_BOTTOM)
		{
		    for(int i = model.getItemCount() - 1;i >= 0;i--)
			if (isSectionItem(model.getItem(i)))
			    return new State(i);
		    return new State(State.Type.NO_TRANSITION);
		}
		if (fromState.type != State.Type.ITEM_INDEX)
		    return new State(State.Type.NO_TRANSITION);
		for(int i = fromState.itemIndex - 1;i >= 0;i--)
		    if (isSectionItem(model.getItem(i)))
			return new State(i);
		return new State(State.Type.NO_TRANSITION);
	    case PAGE_DOWN:
		if (fromState.type == State.Type.EMPTY_LINE_TOP)
		{
		    for(int i = 0;i < model.getItemCount();i++)
			if (isSectionItem(model.getItem(i)))
			    return new State(i);
		    return new State(State.Type.NO_TRANSITION);
		}
		if (fromState.type != State.Type.ITEM_INDEX)
		    return new State(State.Type.NO_TRANSITION);
		for(int i = fromState.itemIndex + 1;i < model.getItemCount();i++)
		    if (isSectionItem(model.getItem(i)))
			return new State(i);
		return new State(State.Type.NO_TRANSITION);
	    default:
		return super.transition(type, fromState, itemCount, hasEmptyLineTop, hasEmptyLineBottom);
	    }
	}
    }

    static public class FixedModel<E> extends ArrayList<E> implements ListArea.Model<E>
    {
	public FixedModel()
	{
	}
	public FixedModel(E[] items)
	{
	    NullCheck.notNullItems(items, "items");
	    addAll(Arrays.asList(items));
	}
	public void setItems(E[] items)
	{
	    NullCheck.notNullItems(items, "items");
	    clear();
	    addAll(Arrays.asList(items));
	}
	public Object[] getItems()
	{
	    return toArray(new Object[size()]);
	}
	@Override public int getItemCount()
	{
	    return size();
	}
	@Override public E getItem(int index)
	{
	    return get(index);
	}
	@Override public void refresh()
	{
	}
    }

    static public class ArrayModel <E>implements ListArea.Model<E>
    {
	public interface Source<E>
	{
	    E[] getItems();
	}
	protected final Source<E> source;
	public ArrayModel(Source<E> source)
	{
	    NullCheck.notNull(source, "source");
	    this.source = source;
	}
	@Override public int getItemCount()
	{
	    final E[] o = source.getItems();
	    return o != null?o.length:0;
	}
	@Override public E getItem(int index)
	{
	    final E[] o = source.getItems();
	    return o[index];
	}
	@Override public void refresh()
	{
	}
    }

        static public class ListModel<E> implements ListArea.Model<E>
    {
	protected final List<E> source;
	public ListModel(List<E> source)
	{
	    NullCheck.notNull(source, "source");
	    this.source = source;
	}
	@Override public int getItemCount()
	{
	    return source.size();
	    	}
	@Override public E getItem(int index)
	{
	    return source.get(index);
	}
	@Override public void refresh()
	{
	}
    }

    static public class DefaultEditableModel<E> extends ArrayList<E> implements EditableListArea.Model<E>
    {
	protected final Class<E> itemClass;
	public DefaultEditableModel(Class<E> itemClass)
	{
	    NullCheck.notNull(itemClass, "itemClass");
	    this.itemClass = itemClass;
	}
	public DefaultEditableModel(Class<E> itemClass, E[] items)
	{
	    this(itemClass);
	    NullCheck.notNullItems(items, "items");
	    setItems(items);
	}
	public DefaultEditableModel(Class<E> itemClass, List<E> items)
	{
	    this(itemClass);
	    addAll(items);
	}
	public void setItems(E[] items)
	{
	    NullCheck.notNullItems(items, "items");
	    clear();
	    addAll(Arrays.asList(items));
	}
	public Object[] getItems()
	{
	    return toArray(new Object[size()]);
	}
	@Override public boolean addToModel(int pos, java.util.function.Supplier<Object> supplier)
	{
	    NullCheck.notNull(supplier, "supplier");
	    if (pos < 0)
		throw new IllegalArgumentException("pos may not be negative (" + pos + ")");
	    final Object value = supplier.get();
	    if (value == null)
		return false;
	    final Object[] values;
	    if (value instanceof Object[])
		values = (Object[])value; else
		values = new Object[]{value};
	    final List<E> c = new ArrayList<>();
	    for(Object o: values)
		if (o != null)
		{
		    if (itemClass.isInstance(o))
			c.add(itemClass.cast(o)); else
		    {
			final E e = adjust(o);
			if (e != null)
			    c.add(e);
		    }
		}
	    if (c.isEmpty())
		return false;
	    addAll(pos, c);
	    return true;
	}
	@Override public boolean removeFromModel(int fromIndex, int toIndex)
	{
	    if (fromIndex < 0)
		throw new IllegalArgumentException("fromIndex can't be negative (" + String.valueOf(fromIndex) + ")");
	    if (toIndex < 0)
		throw new IllegalArgumentException("toIndex can't be negative (" + String.valueOf(toIndex) + ")");
	    if (fromIndex >= size() || toIndex > size())
		return false;
	    removeRange(fromIndex, toIndex);
	    return true;
	}
	@Override public int getItemCount()
	{
	    return size();
	}
	@Override public E getItem(int index)
	{
	    return get(index);
	}
	@Override public void refresh()
	{
	}
	protected E adjust(Object o)
	{
	    return null;
	}
    }

    static public class DefaultMarksInfo implements MarkableListArea.MarksInfo
    {
	protected final Set<Object> items = new HashSet<>();
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
	    final List<Object> res = new ArrayList<>();
	    for(Object o: items)
		res.add(o);
	    return res.toArray(new Object[res.size()]);
	}
    }

    public static class MarkableListAppearance implements ListArea.Appearance<Object>
    {
	protected final ControlContext context;
	protected final MarkableListArea.MarksInfo marksInfo;

	public MarkableListAppearance(ControlContext context, MarkableListArea.MarksInfo marksInfo)
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

    static public class DefaultClipboardSaver<E> implements ListArea.ClipboardSaver<E>
    {
	@Override public boolean saveToClipboard(ListArea<E> listArea, ListArea.Model<E> model, ListArea.Appearance<E> appearance,
						    int fromIndex, int toIndex, Clipboard clipboard)
	{
	    NullCheck.notNull(listArea, "listArea");
	    NullCheck.notNull(model, "model");
	    NullCheck.notNull(appearance, "appearance");
	    NullCheck.notNull(clipboard, "clipboard");
	    if (fromIndex < 0)
		throw new IllegalArgumentException("fromIndex may not be negative (" + fromIndex + ")");
	    if (toIndex < 0)
		throw new IllegalArgumentException("toIndex may not be negative (" + toIndex + ")");
	    if (fromIndex >= toIndex)
		return false;
final List<String> res = new ArrayList<>();
	    for(int i = fromIndex;i < toIndex;++i)
	    {
	    final E obj = model.getItem(i);
	    if (obj == null)
		return false;
	    res.add(appearance.getScreenAppearance(obj, EnumSet.of(ListArea.Appearance.Flags.CLIPBOARD)));
	}
	    clipboard.set(res.toArray(new String[res.size()]));
	    return true;
	}
    }
}
