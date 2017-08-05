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
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.util.*;

public class ListArea  implements Area, ClipboardTranslator.Provider
{
    public enum Flags {EMPTY_LINE_TOP, EMPTY_LINE_BOTTOM};

    static protected final Set<Appearance.Flags> NONE_APPEARANCE_FLAGS = EnumSet.noneOf(Appearance.Flags.class);
    static protected final Set<Appearance.Flags> BRIEF_ANNOUNCEMENT_ONLY = EnumSet.of(Appearance.Flags.BRIEF);

    public interface Model
    {
	int getItemCount();
	Object getItem(int index);
	//	boolean toggleMark(int index);
	void refresh();
    }

    public interface Appearance
    {
	public enum Flags {BRIEF, CLIPBOARD};

	void announceItem(Object item, Set<Flags> flags);
	String getScreenAppearance(Object item, Set<Flags> flags);
	int getObservableLeftBound(Object item);
	int getObservableRightBound(Object item);
    }

public interface ClipboardObjects
{
    Serializable getClipboardObject(ListArea listArea, Model model, Appearance appearance, int index);
}

    public interface Transition
    {
	public enum Type{SINGLE_DOWN, SINGLE_UP,
			 PAGE_DOWN, PAGE_UP,
			 HOME, END};

	static public class State
	{
	    public enum Type{EMPTY_LINE_TOP, EMPTY_LINE_BOTTOM, ITEM_INDEX, NO_TRANSITION};

	    public final Type type;
	    public final int itemIndex;

	    public State(Type type)
	    {
		NullCheck.notNull(type, "type");
		this.type = type;
		this.itemIndex = -1;
	    }

	    public State(int itemIndex)
	    {
		this.type = Type.ITEM_INDEX;
		this.itemIndex = itemIndex;
	    }
	}

	State transition(Type type, State fromState, int itemCount,
			 boolean hasEmptyLineTop, boolean hasEmptyLineBottom);
    }

    static public class Params
    {
	public ControlEnvironment context;
	public Model model;
	public Appearance appearance;
	public ListClickHandler clickHandler;
	public Transition transition = new ListUtils.DefaultTransition();
	public ClipboardObjects clipboardObjects = new ListUtils.DefaultClipboardObjects();
	public String name;
	public Set<Flags> flags = EnumSet.of(Flags.EMPTY_LINE_BOTTOM);
    }

    protected final ControlEnvironment context;
    protected final RegionPoint regionPoint = new RegionPoint();
    protected final ClipboardTranslator clipboardTranslator = new ClipboardTranslator(this, regionPoint, EnumSet.of(ClipboardTranslator.Flags.ALLOWED_EMPTY, ClipboardTranslator.Flags.ALLOWED_WITHOUT_REGION_POINT));
    protected String areaName = "";
    protected final Model listModel;
    protected final Appearance listAppearance;
    protected final Transition listTransition;
    protected final ClipboardObjects listClipboardObjects;
    protected final Set<Flags> listFlags;
    protected ListClickHandler listClickHandler;

    protected int hotPointX = 0;
    protected int hotPointY = 0;

    public ListArea(Params params)
    {
	NullCheck.notNull(params, "params");
	NullCheck.notNull(params.context, "params.context");
	NullCheck.notNull(params.model, "params.model");
	NullCheck.notNull(params.appearance, "params.appearance");
	NullCheck.notNull(params.transition, "params.transition");
	NullCheck.notNull(params.clipboardObjects, "params.clipboardObjects");
	NullCheck.notNull(params.name, "params.name");
	NullCheck.notNull(params.flags, "params.flags");
	this.context = params.context;
	this.listModel = params.model;
	this.listAppearance = params.appearance;
	this.listTransition = params.transition;
	this.listClipboardObjects = params.clipboardObjects;
	this.listClickHandler = params.clickHandler;
	this.areaName = params.name;
	this.listFlags = params.flags;
	resetHotPoint();
    }

    public void setListClickHandler(ListClickHandler clickHandler)
    {
	this.listClickHandler = clickHandler;
    }

    public Model getListModel()
    {
	return listModel;
    }

    public Appearance getListAppearance()
    {
	return listAppearance;
    }

    /**
     * Returns the object in the model corresponding to current hot point
     * position.  If the model is empty or hot point is on an empty line,
     * this method always returns {@code null}. 
     *
     * @return The object in the model associated with the currently selected line or {@code null} if there is no any
     */
    public final Object selected()
    {
	final int index = selectedIndex();
	return (index >= 0 && index < listModel.getItemCount())?listModel.getItem(index):null;
    }

    /**
     * The index of the item in the model which is under the hot point in
     * this list. This method returns the index in the model, not on the
     * screen. It means that the value returned by this method may be
     * different than the value returned by {@code getHotPointY()} (but may
     * be equal as well). If the list is empty or an empty line is selected,
     * this method returns -1. 
     *
     * @return The index of the selected line in the model or -1 if there is no any
     */
    public final int selectedIndex()
    {
	return getItemIndexOnLine(hotPointY);
    }

    /**
     * Searches for the item in the model and sets hot point on it. Given an
     * arbitrary object, this method looks through all items in the model and
     * does a couple of checks: literal pointers equality and a check with
     * {@code equals()} method. If at least one of these checks succeeds, the
     * item is considered equal to the given one, and hot points is set on
     * it.  
     *
     * @param obj The object to search for
     * @param announce Must be true if it is necessary to introduce the object, once it's found
     * @return True if the request object is found, false otherwise
     */
    public boolean select(Object obj, boolean announce)
    {
	NullCheck.notNull(obj, "obj");
	for(int i = 0;i < listModel.getItemCount();++i)
	{
	    final Object o = listModel.getItem(i);
	    if (o == null)
		continue;
		if (obj != o && !obj.equals(o))
	continue;
	    hotPointY = getLineIndexByItemIndex(i);
	hotPointX = listAppearance.getObservableLeftBound(o);
	context.onAreaNewHotPoint(this);
	if (announce)
	    listAppearance.announceItem(o, NONE_APPEARANCE_FLAGS);
	return true;
	}
	return false;
    }

    /**
     * Selects the item by its index. Given the non-negative integer value as
     * an index, this method sets the hot point on the item addressed with
     * this index, checking only that index is in appropriate bounds. Index must address
     * the object as a number in the model, ignoring any empty lines.
     *
     * @param index The item index to select
     * @param announce Must be true, if it is necessary to announce the item , once it has been selected
     * @return True if the index is valid and the item gets hot point on it
     */
    public boolean select(int index, boolean announce)
    {
	if (index < 0 || index >= listModel.getItemCount())
	    return false;
	final int emptyCountAbove = listFlags.contains(Flags.EMPTY_LINE_TOP)?1:0;
	hotPointY = index + emptyCountAbove;
	final Object item = listModel.getItem(index);
	if (item != null)
	{
	    hotPointX = listAppearance.getObservableLeftBound(item);
	    if (announce)
		listAppearance.announceItem(item, NONE_APPEARANCE_FLAGS);
	} else
	{
	    hotPointX = 0;
	    if (announce)
		context.hint(Hints.EMPTY_LINE);
	}
	context.onAreaNewHotPoint(this);
	return true;
    }

    public int getItemIndexOnLine(int index)
    {
	final int linesTop = listFlags.contains(Flags.EMPTY_LINE_TOP)?1:0;
	if (index < linesTop)
	    return -1;
	if (index - linesTop < listModel.getItemCount())
	    return index - linesTop;
	return -1;
    }

    public int getLineIndexByItemIndex(int index)
    {
	final int count = listModel.getItemCount();
	if (index < 0 || index >= count)
	    return -1;
	final int linesTop = listFlags.contains(Flags.EMPTY_LINE_TOP)?1:0;
	return index + linesTop;
    }

    public Object getItemOnLine(int lineIndex)
    {
	if (lineIndex < 0)
	    throw new IllegalArgumentException("lineIndex may not be negative (" + lineIndex + ")");
	final int index = getItemIndexOnLine(lineIndex);
	if (index < 0 || index >= listModel.getItemCount())
	    return null;
	return listModel.getItem(index);
    }

    public void reset(boolean announce)
    {
	EnvironmentEvent.resetRegionPoint(this);
	resetHotPoint(announce);
    }

    public void resetHotPoint()
    {
	resetHotPoint(false);
    }

    public void resetHotPoint(boolean introduce)
    {
	hotPointY = 0;
	final int count = listModel.getItemCount();
	if (count < 1)
	{
	    hotPointX = 0;
	    context.onAreaNewHotPoint(this);
	    return;
	}
	final Object item = listModel.getItem(0);
	if (item != null)
	{
	    hotPointX = item != null?listAppearance.getObservableLeftBound(item):0;
	    if (introduce)
		listAppearance.announceItem(item, NONE_APPEARANCE_FLAGS);
	} else
	{
	    hotPointX = 0;
	    context.hint(Hints.EMPTY_LINE);
	}
	context.onAreaNewHotPoint(this);
    }

    public void announceSelected()
    {
	final Object item = selected();
	if (item != null)
	    listAppearance.announceItem(item, NONE_APPEARANCE_FLAGS);
    }

    /**
     * Refreshes the content of the list. This method calls {@code refresh()}
     * method of the model and displays new items. It does not produce any
     * speech announcement of the change. HotPointY is preserved if it is
     * possible (meaning, the new number of lines not less than old value of
     * hotPointY), but hotPointX is moved to the beginning of the line.
     */
    public void refresh()
    {
	final Object previouslySelected = selected();
	listModel.refresh();
	context.onAreaNewContent(this);
	final int count = listModel.getItemCount();
	if (count == 0)
	{
	    hotPointX = 0;
	    hotPointY = 0;
	    context.onAreaNewHotPoint(this);
	    return;
	}
	if (previouslySelected != null && select(previouslySelected, false))
	    return;
	hotPointY = hotPointY < count?hotPointY :count - 1;
	final Object item = listModel.getItem(hotPointY);
	if (item != null)
	    hotPointX = listAppearance.getObservableLeftBound(item); else
	    hotPointX = 0;
	context.onAreaNewHotPoint(this);
    }

    public boolean isEmpty()
    {
	return listModel.getItemCount() <= 0;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (!event.isSpecial() && (!event.isModified() || event.withShiftOnly()))
	    return onChar(event);
	if (!event.isSpecial() || event.isModified())
	    return false;
	switch(event.getSpecial())
	{
	case ARROW_DOWN:
	    return onArrowDown(event, false);
	case ARROW_UP:
	    return onArrowUp(event, false);
	case ARROW_RIGHT:
	    return onArrowRight(event);
	case ARROW_LEFT:
	    return onArrowLeft(event);
	case ALTERNATIVE_ARROW_DOWN:
	    return onArrowDown(event, true);
	case ALTERNATIVE_ARROW_UP:
	    return onArrowUp(event, true);
	case ALTERNATIVE_ARROW_RIGHT:
	    return onAltRight(event);
	case ALTERNATIVE_ARROW_LEFT:
	    return onAltLeft(event);
	case HOME:
	    return onHome(event);
	case END:
	    return onEnd(event);
	case ALTERNATIVE_HOME:
	    return onAltHome(event);
	case ALTERNATIVE_END:
	    return onAltEnd(event);
	case PAGE_DOWN:
	    return onPageDown(event, false);
	case PAGE_UP:
	    return onPageUp(event, false);
	case  ALTERNATIVE_PAGE_DOWN:
	    return onPageDown(event, true);
	case ALTERNATIVE_PAGE_UP:
	    return onPageUp(event, true);
	case ENTER:
	    return onEnter(event);
	default:
	    return false;
	}
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != EnvironmentEvent.Type.REGULAR)
	    return false;
	switch (event.getCode())
	{
	case REFRESH:
	    refresh();
	    return true;
	case INTRODUCE:
	    return onAnnounce();
	case ANNOUNCE_LINE:
	    return onAnnounceLine();
	case OK:
	    return onOk(event);
	case LISTENING_FINISHED:
	    if (event instanceof ListeningFinishedEvent)
		return onListeningFinishedEvent((ListeningFinishedEvent)event);
	    return false;
	case MOVE_HOT_POINT:
	    if (event instanceof MoveHotPointEvent)
		return onMoveHotPoint((MoveHotPointEvent)event);
	    return false;
	default:
	    return clipboardTranslator.onEnvironmentEvent(event, hotPointX, hotPointY);
	}
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	switch(query.getQueryCode())
	{
	case AreaQuery.BEGIN_LISTENING:
	    if (query instanceof BeginListeningQuery)
		return onBeginListeningQuery((BeginListeningQuery)query);
		return false;
	default:
	    return false;
	}
    }

    @Override public Action[] getAreaActions()
    {
	return new Action[0];
    }

    @Override public int getLineCount()
    {
	final int emptyCountTop = listFlags.contains(Flags.EMPTY_LINE_TOP)?1:0;
	final int emptyCountBottom = listFlags.contains(Flags.EMPTY_LINE_BOTTOM)?1:0;

	final int res = listModel.getItemCount() + emptyCountTop + emptyCountBottom;
	return res>= 1?res:1;
    }

    @Override public String getLine(int index)
    {
	if (isEmpty())
	    return index == 0?noContentStr():"";
	final int itemIndex = getItemIndexOnLine(index);
	if (itemIndex < 0 || itemIndex >= listModel.getItemCount())
	    return "";
	final Object res = listModel.getItem(itemIndex);
	return res != null?listAppearance.getScreenAppearance(res, NONE_APPEARANCE_FLAGS):"";
    }

    @Override public int getHotPointX()
    {
	return hotPointX >= 0?hotPointX:0;
    }

    @Override public int getHotPointY()
    {
	return hotPointY >= 0?hotPointY:0;
    }

    @Override public String getAreaName()
    {
	NullCheck.notNull(areaName, "areaName");
	return areaName;
    }

    public void setAreaName(String areaName)
    {
	NullCheck.notNull(areaName, "areaName");
	this.areaName = areaName;
	context.onAreaNewName(this);
    }

    protected boolean onAnnounce()
    {
	context.playSound(Sounds.INTRO_REGULAR);
	String item = "";
	if (selected() != null)
	    item = listAppearance.getScreenAppearance(selected(), EnumSet.noneOf(Appearance.Flags.class)).trim();
	if (!item.isEmpty())
	    item = " " + item;
	context.say(getAreaName() + item);
	return true;
    }

    protected boolean onAnnounceLine()
    {
	if (isEmpty())
	    return false;
	final Object item = selected();
	if (item == null)
	{
	    context.hint(Hints.EMPTY_LINE);
	    return true;
	}
	listAppearance.announceItem(item, NONE_APPEARANCE_FLAGS);
	return true;
    }

    protected boolean onMoveHotPoint(MoveHotPointEvent event)
    {
	NullCheck.notNull(event, "event");
	final int x = event.getNewHotPointX();
	final int y = event.getNewHotPointY();
	final int newY;
	if (y >= getLineCount())
	{
	    if (event.precisely())
		return false;
newY = getLineCount() - 1;
	} else
newY = y;
	    if (getItemIndexOnLine(newY) >= 0)
	    {
		//Line with item, not empty
		final Object item = listModel.getItem(getItemIndexOnLine(newY));
		final int leftBound = listAppearance.getObservableLeftBound(item);
final int rightBound = listAppearance.getObservableRightBound(item);
		if (event.precisely() &&
		    (x < leftBound || x > rightBound))
		    return false;
		hotPointY = newY;
		hotPointX = x;
		if (hotPointX < leftBound)
		    hotPointX = leftBound;
		if (hotPointX > rightBound)
		    hotPointX = rightBound;
		context.onAreaNewHotPoint(this);
		return true;
	    }
	    //On empty line
	    hotPointY = newY;
	    hotPointX = 0;
	    context.onAreaNewHotPoint(this);
	    return true;
    }

    protected boolean onBeginListeningQuery(BeginListeningQuery query)
    {
	NullCheck.notNull(query, "query");
	final int index = selectedIndex();
	if (index < 0)
	    return false;
	final int count = listModel.getItemCount();
	if (index >= count)
	    return false;
	final Object current = listModel.getItem(index);
	final String text = listAppearance.getScreenAppearance(current, NONE_APPEARANCE_FLAGS).substring(hotPointX, listAppearance.getObservableRightBound(current));
	if (text.isEmpty() && index + 1 >= count)
	    return false;
	if (index + 1 < count)
	{
	    final Object next = listModel.getItem(index + 1);
	    query.answer(new BeginListeningQuery.Answer(text, new ListeningInfo(index + 1, listAppearance.getObservableLeftBound(next))));
	} else
	    query.answer(new BeginListeningQuery.Answer(text, new ListeningInfo(index, listAppearance.getObservableRightBound(current))));
	return true;
    }

    protected boolean onListeningFinishedEvent(ListeningFinishedEvent event)
    {
	NullCheck.notNull(event, "event");
	if (!(event.getExtraInfo() instanceof ListeningInfo))
	    return false;
	final ListeningInfo info = (ListeningInfo)event.getExtraInfo();
	final int count = listModel.getItemCount();
	if (info.itemIndex >= count)
	    return false;
	final Object item = listModel.getItem(info.itemIndex);
	final int leftBound = listAppearance.getObservableLeftBound(item);
	final int rightBound = listAppearance.getObservableRightBound(item);
	if (info.pos < leftBound || info.pos > rightBound)
	    return false;
	hotPointY = getLineIndexByItemIndex(info.itemIndex);
	hotPointX = info.pos;
	context.onAreaNewHotPoint(this);
	return true;
    }

    protected boolean onChar(KeyboardEvent event)
    {
	if (noContent())
	    return true;
	final int count = listModel.getItemCount();
	final char c = Character.toLowerCase(event.getChar());
	final String beginning;
	if (selected() != null)
	{
	    if (hotPointX >= listAppearance.getObservableRightBound(selected()))
		return false;
	    final String name = getObservableSubstr(selected()).toLowerCase();
	    final int pos = Math.min(hotPointX - listAppearance.getObservableLeftBound(selected()), name.length());
	    if (pos < 0)
		return false;
	    beginning = name.substring(0, pos);
	} else
	    beginning = "";
	final String mustBegin = beginning + c;
	for(int i = 0;i < count;++i)
	{
	    final String name = getObservableSubstr(listModel.getItem(i)).toLowerCase();
	    if (!name.startsWith(mustBegin))
		continue;
	    hotPointY = getLineIndexByItemIndex(i);
	    ++hotPointX;
	    listAppearance.announceItem(listModel.getItem(hotPointY), NONE_APPEARANCE_FLAGS);
	    context.onAreaNewHotPoint(this);
	    return true;
	}
	return false;
    }

    protected boolean onArrowDown(KeyboardEvent event, boolean briefAnnouncement)
    {
	return onTransition(Transition.Type.SINGLE_DOWN, Hints.NO_ITEMS_BELOW, briefAnnouncement);
    }

    protected boolean onArrowUp(KeyboardEvent event, boolean briefAnnouncement)
    {
	return onTransition(Transition.Type.SINGLE_UP, Hints.NO_ITEMS_ABOVE, briefAnnouncement);
    }

    protected boolean onPageDown(KeyboardEvent event, boolean briefAnnouncement)
    {
	return onTransition(Transition.Type.PAGE_DOWN, Hints.NO_ITEMS_BELOW, briefAnnouncement);
    }

    protected boolean onPageUp(KeyboardEvent event, boolean briefAnnouncement)
    {
	return onTransition(Transition.Type.PAGE_UP, Hints.NO_ITEMS_ABOVE, briefAnnouncement);
    }

    protected boolean onEnd(KeyboardEvent event)
    {
	return onTransition(Transition.Type.END, Hints.NO_ITEMS_BELOW, false);
    }

    protected boolean onHome(KeyboardEvent event)
    {
	return onTransition(Transition.Type.HOME, Hints.NO_ITEMS_ABOVE, false);
    }

    protected boolean onTransition(Transition.Type type, int hint, boolean briefAnnouncement)
    {
	NullCheck.notNull(type, "type");
	//	NullCheck.notNull(hint, "hint");
	if (noContent())
	    return true;
	final int index = selectedIndex();
	final int count = listModel.getItemCount();
	final int emptyCountTop = listFlags.contains(Flags.EMPTY_LINE_TOP)?1:0;
	final Transition.State current;
	if (index >= 0)
current = new Transition.State(index); else
	if (listFlags.contains(Flags.EMPTY_LINE_TOP) && hotPointY == 0)
	    current = new Transition.State(Transition.State.Type.EMPTY_LINE_TOP); else
	if (listFlags.contains(Flags.EMPTY_LINE_BOTTOM) && hotPointY == count + emptyCountTop)
current = new Transition.State(Transition.State.Type.EMPTY_LINE_BOTTOM); else
	    return false;
	final Transition.State newState = listTransition.transition(type, current, count,
								listFlags.contains(Flags.EMPTY_LINE_TOP), listFlags.contains(Flags.EMPTY_LINE_BOTTOM));
	NullCheck.notNull(newState, "newState");
	switch(newState.type)
	{
	case NO_TRANSITION:
	    context.hint(hint);
	    return true;
	case EMPTY_LINE_TOP:
	    if (!listFlags.contains(Flags.EMPTY_LINE_TOP))
		return false;
	    hotPointY = 0;
	    break;
	case EMPTY_LINE_BOTTOM:
	    if (!listFlags.contains(Flags.EMPTY_LINE_BOTTOM))
		return false;
	    hotPointY = count + emptyCountTop;
	    break;
	case ITEM_INDEX:
	    if (newState.itemIndex < 0 || newState.itemIndex >= count)
		return false;
	    hotPointY = newState.itemIndex + emptyCountTop;
	    break;
	default:
	    return false;
	}
	onNewHotPointY(briefAnnouncement);
	return true;
    }

protected boolean onArrowRight(KeyboardEvent event)
    {
	if (noContent())
	    return true;
	final Object item = selected();
	if (item == null)
	{
	    context.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = listAppearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
	NullCheck.notNull(line, "line");
	if (line.isEmpty())
	{
	    context.hint(Hints.EMPTY_LINE);
	    return true;
	}
final int rightBound = listAppearance.getObservableRightBound(item);
	if (hotPointX >= rightBound)
	{
	    context.hint(Hints.END_OF_LINE);
	    return true;
	}
	++hotPointX;
	announceChar(line, hotPointX, rightBound);
	context.onAreaNewHotPoint(this);
	return true;
    }

    protected boolean onArrowLeft(KeyboardEvent event)
    {
	if (noContent())
	    return true;
	final Object item = selected();
	if (item == null)
	{
	    context.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = listAppearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
	NullCheck.notNull(line, "line");
	if (line.isEmpty())
	{
	    context.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final int leftBound = listAppearance.getObservableLeftBound(item);
	final int rightBound = listAppearance.getObservableRightBound(item);
	if (hotPointX <= leftBound)
	{
	    context.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	--hotPointX;
	announceChar(line, hotPointX, rightBound);
	context.onAreaNewHotPoint(this);
	return true;
    }

    protected boolean onAltRight(KeyboardEvent event)
    {
	if (noContent())
	    return true;
	final Object item = selected();
	if (item == null)
	{
	    context.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = listAppearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
	NullCheck.notNull(line, "line");
	if (line.isEmpty())
	{
	    context.hint(Hints.EMPTY_LINE);
	    return true;
	}
		final int leftBound = listAppearance.getObservableLeftBound(item);
final int rightBound = listAppearance.getObservableRightBound(item);
	if (hotPointX >= rightBound)
	{
	    context.hint(Hints.END_OF_LINE);
	    return true;
	}
	final String subline = line.substring(leftBound, rightBound);
	final WordIterator it = new WordIterator(subline, hotPointX - leftBound);
	if (!it.stepForward())
	{
	    context.hint(Hints.END_OF_LINE);
	    return true;
	}
	hotPointX = it.pos() + leftBound;
	if (it.announce().length() > 0)
	    context.say(it.announce()); else
	    context.hint(Hints.END_OF_LINE);
	context.onAreaNewHotPoint(this);
	return true;
    }

    protected boolean onAltLeft(KeyboardEvent event)
    {
	if (noContent())
	    return true;
	final Object item = selected();
	if (item == null)
	{
	    context.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = listAppearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
	NullCheck.notNull(line, "line");
	if (line.isEmpty())
	{
	    context.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final int leftBound = listAppearance.getObservableLeftBound(item);
	final int rightBound = listAppearance.getObservableRightBound(item);
	if (hotPointX <= leftBound)
	{
	    context.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	final String subline = line.substring(leftBound, rightBound);
	final WordIterator it = new WordIterator(subline, hotPointX - leftBound);
	if (!it.stepBackward())
	{
	    context.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	hotPointX = it.pos() + leftBound;
	context.say(it.announce());
	context.onAreaNewHotPoint(this);
	return true;
    }

protected boolean onAltEnd(KeyboardEvent event)
    {
	if (noContent())
	    return true;
	final Object item = selected();
	if (item == null)
	{
	    context.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = listAppearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
	NullCheck.notNull(line, "line");
	hotPointX = listAppearance.getObservableRightBound(item);
	context.hint(Hints.END_OF_LINE);
	context.onAreaNewHotPoint(this);
	return true;
    }

protected boolean onAltHome(KeyboardEvent event)
    {
	if (noContent())
	    return true;
	final Object item = selected();
	NullCheck.notNull(item, "item");
	final String line = listAppearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
	NullCheck.notNull(line, "line");
	hotPointX = listAppearance.getObservableLeftBound(item);
	announceChar(line, hotPointX, listAppearance.getObservableRightBound(item));
	context.onAreaNewHotPoint(this);
	return true;
    }

    protected boolean onEnter(KeyboardEvent event)
    {
	if (isEmpty() || listClickHandler == null)
	    return false;
	if (selected() == null || selectedIndex() < 0)
	    return false;
	return listClickHandler.onListClick(this, selectedIndex(), selected());
    }

    protected boolean onOk(EnvironmentEvent event)
    {
	if (listClickHandler == null)
	    return false;
	final int index = selectedIndex();
	final Object item = selected();
	if (index < 0 || item == null)
	    return false;
	    return listClickHandler.onListClick(this, index, item);
    }

    @Override public boolean onClipboardCopyAll()
    {
	if (listModel.getItemCount() < 0)
	    return false;
	final List<Serializable> res = new LinkedList<Serializable>();
	final int count = listModel.getItemCount();
	for(int i = 0;i < count;++i)
	{
	    final Serializable obj = listClipboardObjects.getClipboardObject(this, listModel, listAppearance, i);
	    if (obj == null)
		return false;
	    res.add(obj);
	}
	context.getClipboard().set(res.toArray(new Serializable[res.size()]));
	return true;
    }

@Override public boolean onClipboardCopy(int fromX, int fromY, int toX, int toY, boolean withDeleting)
    {
	if (withDeleting)
	    return false;
	if (listModel.getItemCount() < 0)
	    return false;
	final int modelFromY = getItemIndexOnLine(fromY);
	final int modelToY = getItemIndexOnLine(toY);
	if (modelFromY >= listModel.getItemCount() || modelToY > listModel.getItemCount())
	    return false;
	if (modelFromY == modelToY)
	{
	    final String line = listAppearance.getScreenAppearance(listModel.getItem(modelFromY), EnumSet.of(Appearance.Flags.CLIPBOARD));
	    if (line == null || line.isEmpty())
		return false;
	    final int fromPos = Math.min(fromX, line.length());
	    final int toPos = Math.min(toX, line.length());
	    if (fromPos >= toPos)
		return false;
	    context.getClipboard().set(line.substring(fromPos, toPos));
	    return true;
	}
	final List<String> res = new LinkedList<String>();
	for(int i = modelFromY;i < modelToY;++i)
	{
	    final String line = listAppearance.getScreenAppearance(listModel.getItem(i), NONE_APPEARANCE_FLAGS);
	    res.add(line != null?line:"");
	}
	res.add("");
	return true;
    }

    @Override public boolean onDeleteRegion(int fromX, int fromY, int toX, int toY)
    {
	return false;
    }

    protected void onNewHotPointY(boolean briefAnnouncement)
    {
	final int index = selectedIndex();
	if (index < 0)
	{
	    context.hint(Hints.EMPTY_LINE);
	    hotPointX = 0;
	    context.onAreaNewHotPoint(this);
	    return;
	}
	final Object item = listModel.getItem(index);
	if (item == null)
	{
	    context.hint(Hints.EMPTY_LINE);
	    hotPointX = 0;
	    context.onAreaNewHotPoint(this);
	    return;
	}
	listAppearance.announceItem(item, briefAnnouncement?BRIEF_ANNOUNCEMENT_ONLY:NONE_APPEARANCE_FLAGS);
	hotPointX = listAppearance.getObservableLeftBound(item);
	context.onAreaNewHotPoint(this);
    }

    protected String getObservableSubstr(Object item)
    {
	NullCheck.notNull(item, "item");
final String line = listAppearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
NullCheck.notNull(line, "line");
if (line.isEmpty())
return "";
final int leftBound = Math.min(listAppearance.getObservableLeftBound(item), line.length());
final int rightBound = Math.min(listAppearance.getObservableRightBound(item), line.length());
if (leftBound >= rightBound)
    return "";
return line.substring(leftBound, rightBound);
    }

    protected String noContentStr()
    {
	return context.getStaticStr("ListNoContent");
    }

    protected void announceChar(String  line, int pos, int rightBound)
    {
	NullCheck.notNull(line, "line");
	if (pos < rightBound)
	    context.sayLetter(line.charAt(pos)); else
	    context.hint(Hints.END_OF_LINE);
    }

	protected boolean noContent()
    {
	if (listModel == null || listModel.getItemCount() < 1)
	{
	    context.hint(noContentStr(), Hints.NO_CONTENT);
	    return true;
	}
	return false;
    }


static protected class ListeningInfo
{
    final int itemIndex;
    final int pos;

    ListeningInfo(int itemIndex, int pos)
    {
	this.itemIndex = itemIndex;
	this.pos = pos;
    }
}
}
