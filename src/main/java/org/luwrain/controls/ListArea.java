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

//FIXME:DESCRIBE;
//FIXME:ControlEnvironment interface support;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.util.*;

public class ListArea  implements Area, RegionProvider
{
    public enum Flags {EMPTY_LINE_TOP, EMPTY_LINE_BOTTOM};

    static protected final Set<Appearance.Flags> NONE_APPEARANCE_FLAGS = EnumSet.noneOf(Appearance.Flags.class);
    static protected final Set<Appearance.Flags> BRIEF_ANNOUNCEMENT_ONLY = EnumSet.of(Appearance.Flags.BRIEF);

    public interface Model
    {
	int getItemCount();
	Object getItem(int index);
	boolean toggleMark(int index);
	void refresh();
    }

    public interface Appearance
    {
	public enum Flags { BRIEF };

	void announceItem(Object item, Set<Flags> flags);
	String getScreenAppearance(Object item, Set<Flags> flags);
	int getObservableLeftBound(Object item);
	int getObservableRightBound(Object item);
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
	public ControlEnvironment environment;
	public Model model;
	public Appearance appearance;
	public Transition transition = new ListUtils.DefaultTransition();
	public ListClickHandler clickHandler;
	public String name;
	public Set<Flags> flags = EnumSet.of(Flags.EMPTY_LINE_BOTTOM);
    }

    protected final RegionTranslator region = new RegionTranslator(this);
    protected final ControlEnvironment environment;
    protected String areaName = "";
    protected final Model model;
    protected final Appearance appearance;
    protected final Transition transition;
    protected final Set<Flags> flags;
    protected ListClickHandler clickHandler;

    protected int hotPointX = 0;
    protected int hotPointY = 0;

    public ListArea(Params params)
    {
	NullCheck.notNull(params, "params");
	NullCheck.notNull(params.environment, "params.environment");
	NullCheck.notNull(params.model, "params.model");
	NullCheck.notNull(params.appearance, "params.appearance");
	NullCheck.notNull(params.transition, "params.transition");
	NullCheck.notNull(params.name, "params.name");
	NullCheck.notNull(params.flags, "params.flags");
	this.environment = params.environment;
	this.model = params.model;
	this.appearance = params.appearance;
	this.transition = params.transition;
	this.clickHandler = params.clickHandler;
	this.areaName = params.name;
	this.flags = params.flags;
	//	itemsLayout.setFlags(params.flags);
	resetHotPoint();
    }

    public void setListClickHandler(ListClickHandler clickHandler)
    {
	this.clickHandler = clickHandler;
    }

    public Model getListModel()
    {
	return model;
    }

    public Appearance getListAppearance()
    {
	return appearance;
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
	return (index >= 0 && index < model.getItemCount())?model.getItem(index):null;
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
	for(int i = 0;i < model.getItemCount();++i)
	{
	    final Object o = model.getItem(i);
	    if (o == null)
		continue;
		if (obj != o && !obj.equals(o))
	continue;
	    hotPointY = getLineIndexByItemIndex(i);
	hotPointX = appearance.getObservableLeftBound(o);
	environment.onAreaNewHotPoint(this);
	if (announce)
	    appearance.announceItem(o, NONE_APPEARANCE_FLAGS);
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
	if (index < 0 || index >= model.getItemCount())
	    return false;
	final int emptyCountAbove = flags.contains(Flags.EMPTY_LINE_TOP)?1:0;
	hotPointY = index + emptyCountAbove;
	final Object item = model.getItem(index);
	if (item != null)
	{
	    hotPointX = appearance.getObservableLeftBound(item);
	    if (announce)
		appearance.announceItem(item, NONE_APPEARANCE_FLAGS);
	} else
	{
	    hotPointX = 0;
	    if (announce)
		environment.hint(Hints.EMPTY_LINE);
	}
	environment.onAreaNewHotPoint(this);
	return true;
    }

    public int getItemIndexOnLine(int index)
    {
	final int linesTop = flags.contains(Flags.EMPTY_LINE_TOP)?1:0;
	if (index < linesTop)
	    return -1;
	if (index - linesTop < model.getItemCount())
	    return index - linesTop;
	return -1;
    }

    public int getLineIndexByItemIndex(int index)
    {
	final int count = model.getItemCount();
	if (index < 0 || index >= count)
	    return -1;
	final int linesTop = flags.contains(Flags.EMPTY_LINE_TOP)?1:0;
	return index + linesTop;
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
	final int count = model.getItemCount();
	if (count < 1)
	{
	    hotPointX = 0;
	    environment.onAreaNewHotPoint(this);
	    return;
	}
	final Object item = model.getItem(0);
	if (item != null)
	{
	    hotPointX = item != null?appearance.getObservableLeftBound(item):0;
	    if (introduce)
		appearance.announceItem(item, NONE_APPEARANCE_FLAGS);
	} else
	{
	    hotPointX = 0;
	    environment.hint(Hints.EMPTY_LINE);
	}
	environment.onAreaNewHotPoint(this);
    }

    public void announceSelected()
    {
	final Object item = selected();
	if (item != null)
	    appearance.announceItem(item, NONE_APPEARANCE_FLAGS);
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
	model.refresh();
	environment.onAreaNewContent(this);
	final int count = model.getItemCount();
	if (count == 0)
	{
	    hotPointX = 0;
	    hotPointY = 0;
	    environment.onAreaNewHotPoint(this);
	    return;
	}
	if (previouslySelected != null && select(previouslySelected, false))
	    return;
	hotPointY = hotPointY < count?hotPointY :count - 1;
	final Object item = model.getItem(hotPointY);
	if (item != null)
	    hotPointX = appearance.getObservableLeftBound(item); else
	    hotPointX = 0;
	environment.onAreaNewHotPoint(this);
    }

    public boolean isEmpty()
    {
	return model.getItemCount() <= 0;
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
	case INSERT:
	    return onInsert(event);
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
	    return region.onEnvironmentEvent(event, hotPointX, hotPointY);
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
	    return region.onAreaQuery(query, hotPointX, hotPointY);
	}
    }

    @Override public Action[] getAreaActions()
    {
	return new Action[0];
    }

    @Override public int getLineCount()
    {
	final int emptyCountTop = flags.contains(Flags.EMPTY_LINE_TOP)?1:0;
	final int emptyCountBottom = flags.contains(Flags.EMPTY_LINE_BOTTOM)?1:0;

	final int res = model.getItemCount() + emptyCountTop + emptyCountBottom;
	return res>= 1?res:1;
    }

    @Override public String getLine(int index)
    {
	if (isEmpty())
	    return index == 0?noContentStr():"";
	final int itemIndex = getItemIndexOnLine(index);
	if (itemIndex < 0 || itemIndex >= model.getItemCount())
	    return "";
	final Object res = model.getItem(itemIndex);
	return res != null?appearance.getScreenAppearance(res, NONE_APPEARANCE_FLAGS):"";
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
	environment.onAreaNewName(this);
    }

    protected boolean onAnnounce()
    {
	environment.playSound(Sounds.INTRO_REGULAR);
	String item = "";
	if (selected() != null)
	    item = appearance.getScreenAppearance(selected(), EnumSet.noneOf(Appearance.Flags.class)).trim();
	if (!item.isEmpty())
	    item = " " + item;
	environment.say(getAreaName() + item);
	return true;
    }

    protected boolean onAnnounceLine()
    {
	if (isEmpty())
	    return false;
	final Object item = selected();
	if (item == null)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	appearance.announceItem(item, NONE_APPEARANCE_FLAGS);
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
		final Object item = model.getItem(getItemIndexOnLine(newY));
		final int leftBound = appearance.getObservableLeftBound(item);
final int rightBound = appearance.getObservableRightBound(item);
		if (event.precisely() &&
		    (x < leftBound || x > rightBound))
		    return false;
		hotPointY = newY;
		hotPointX = x;
		if (hotPointX < leftBound)
		    hotPointX = leftBound;
		if (hotPointX > rightBound)
		    hotPointX = rightBound;
		environment.onAreaNewHotPoint(this);
		return true;
	    }
	    //On empty line
	    hotPointY = newY;
	    hotPointX = 0;
	    environment.onAreaNewHotPoint(this);
	    return true;
    }

    protected boolean onBeginListeningQuery(BeginListeningQuery query)
    {
	NullCheck.notNull(query, "query");
	final int index = selectedIndex();
	if (index < 0)
	    return false;
	final int count = model.getItemCount();
	if (index >= count)
	    return false;
	final Object current = model.getItem(index);
	final String text = appearance.getScreenAppearance(current, NONE_APPEARANCE_FLAGS).substring(hotPointX, appearance.getObservableRightBound(current));
	if (text.isEmpty() && index + 1 >= count)
	    return false;
	if (index + 1 < count)
	{
	    final Object next = model.getItem(index + 1);
	    query.answer(new BeginListeningQuery.Answer(text, new ListeningInfo(index + 1, appearance.getObservableLeftBound(next))));
	} else
	    query.answer(new BeginListeningQuery.Answer(text, new ListeningInfo(index, appearance.getObservableRightBound(current))));
	return true;
    }

    protected boolean onListeningFinishedEvent(ListeningFinishedEvent event)
    {
	NullCheck.notNull(event, "event");
	if (!(event.getExtraInfo() instanceof ListeningInfo))
	    return false;
	final ListeningInfo info = (ListeningInfo)event.getExtraInfo();
	final int count = model.getItemCount();
	if (info.itemIndex >= count)
	    return false;
	final Object item = model.getItem(info.itemIndex);
	final int leftBound = appearance.getObservableLeftBound(item);
	final int rightBound = appearance.getObservableRightBound(item);
	if (info.pos < leftBound || info.pos > rightBound)
	    return false;
	hotPointY = getLineIndexByItemIndex(info.itemIndex);
	hotPointX = info.pos;
	environment.onAreaNewHotPoint(this);
	return true;
    }

    protected boolean onChar(KeyboardEvent event)
    {
	if (noContent())
	    return true;
	final int count = model.getItemCount();
	final char c = Character.toLowerCase(event.getChar());
	final String beginning;
	if (selected() != null)
	{
	    if (hotPointX >= appearance.getObservableRightBound(selected()))
		return false;
	    final String name = getObservableSubstr(selected()).toLowerCase();
	    final int pos = Math.min(hotPointX - appearance.getObservableLeftBound(selected()), name.length());
	    if (pos < 0)
		return false;
	    beginning = name.substring(0, pos);
	} else
	    beginning = "";
	final String mustBegin = beginning + c;
	for(int i = 0;i < count;++i)
	{
	    final String name = getObservableSubstr(model.getItem(i)).toLowerCase();
	    if (!name.startsWith(mustBegin))
		continue;
	    hotPointY = getLineIndexByItemIndex(i);
	    ++hotPointX;
	    appearance.announceItem(model.getItem(hotPointY), NONE_APPEARANCE_FLAGS);
	    environment.onAreaNewHotPoint(this);
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
	final int count = model.getItemCount();
	final int emptyCountTop = flags.contains(Flags.EMPTY_LINE_TOP)?1:0;
	final Transition.State current;
	if (index >= 0)
current = new Transition.State(index); else
	if (flags.contains(Flags.EMPTY_LINE_TOP) && hotPointY == 0)
	    current = new Transition.State(Transition.State.Type.EMPTY_LINE_TOP); else
	if (flags.contains(Flags.EMPTY_LINE_BOTTOM) && hotPointY == count + emptyCountTop)
current = new Transition.State(Transition.State.Type.EMPTY_LINE_BOTTOM); else
	    return false;
	final Transition.State newState = transition.transition(type, current, count,
								flags.contains(Flags.EMPTY_LINE_TOP), flags.contains(Flags.EMPTY_LINE_BOTTOM));
	NullCheck.notNull(newState, "newState");
	switch(newState.type)
	{
	case NO_TRANSITION:
	    environment.hint(hint);
	    return true;
	case EMPTY_LINE_TOP:
	    if (!flags.contains(Flags.EMPTY_LINE_TOP))
		return false;
	    hotPointY = 0;
	    break;
	case EMPTY_LINE_BOTTOM:
	    if (!flags.contains(Flags.EMPTY_LINE_BOTTOM))
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
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = appearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
	NullCheck.notNull(line, "line");
	if (line.isEmpty())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
final int rightBound = appearance.getObservableRightBound(item);
	if (hotPointX >= rightBound)
	{
	    environment.hint(Hints.END_OF_LINE);
	    return true;
	}
	++hotPointX;
	announceChar(line, hotPointX, rightBound);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    protected boolean onArrowLeft(KeyboardEvent event)
    {
	if (noContent())
	    return true;
	final Object item = selected();
	if (item == null)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = appearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
	NullCheck.notNull(line, "line");
	if (line.isEmpty())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final int leftBound = appearance.getObservableLeftBound(item);
	final int rightBound = appearance.getObservableRightBound(item);
	if (hotPointX <= leftBound)
	{
	    environment.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	--hotPointX;
	announceChar(line, hotPointX, rightBound);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    protected boolean onAltRight(KeyboardEvent event)
    {
	if (noContent())
	    return true;
	final Object item = selected();
	if (item == null)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = appearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
	NullCheck.notNull(line, "line");
	if (line.isEmpty())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
		final int leftBound = appearance.getObservableLeftBound(item);
final int rightBound = appearance.getObservableRightBound(item);
	if (hotPointX >= rightBound)
	{
	    environment.hint(Hints.END_OF_LINE);
	    return true;
	}
	final String subline = line.substring(leftBound, rightBound);
	final WordIterator it = new WordIterator(subline, hotPointX - leftBound);
	if (!it.stepForward())
	{
	    environment.hint(Hints.END_OF_LINE);
	    return true;
	}
	hotPointX = it.pos() + leftBound;
	if (it.announce().length() > 0)
	    environment.say(it.announce()); else
	    environment.hint(Hints.END_OF_LINE);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    protected boolean onAltLeft(KeyboardEvent event)
    {
	if (noContent())
	    return true;
	final Object item = selected();
	if (item == null)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = appearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
	NullCheck.notNull(line, "line");
	if (line.isEmpty())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final int leftBound = appearance.getObservableLeftBound(item);
	final int rightBound = appearance.getObservableRightBound(item);
	if (hotPointX <= leftBound)
	{
	    environment.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	final String subline = line.substring(leftBound, rightBound);
	final WordIterator it = new WordIterator(subline, hotPointX - leftBound);
	if (!it.stepBackward())
	{
	    environment.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	hotPointX = it.pos() + leftBound;
	    environment.say(it.announce());
	environment.onAreaNewHotPoint(this);
	return true;
    }

protected boolean onAltEnd(KeyboardEvent event)
    {
	if (noContent())
	    return true;
	final Object item = selected();
	if (item == null)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = appearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
	NullCheck.notNull(line, "line");
	hotPointX = appearance.getObservableRightBound(item);
	environment.hint(Hints.END_OF_LINE);
	environment.onAreaNewHotPoint(this);
	return true;
    }

protected boolean onAltHome(KeyboardEvent event)
    {
	if (noContent())
	    return true;
	final Object item = selected();
	NullCheck.notNull(item, "item");
	final String line = appearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
	NullCheck.notNull(line, "line");
	hotPointX = appearance.getObservableLeftBound(item);
	announceChar(line, hotPointX, appearance.getObservableRightBound(item));
	environment.onAreaNewHotPoint(this);
	return true;
    }

    protected boolean onInsert(KeyboardEvent event)
    {
	final int index = selectedIndex();
	if (index < 0)
	    return false;
	if (!model.toggleMark(index))
	    return false;
	environment.onAreaNewContent(this);
	if (hotPointY + 1 < getLineCount())
	{
	    ++hotPointY;
	    onNewHotPointY(false); 
	}
	return true;
    }

    protected boolean onEnter(KeyboardEvent event)
    {
	if (isEmpty() || clickHandler == null)
	    return false;
	if (selected() == null || selectedIndex() < 0)
	    return false;
	return clickHandler.onListClick(this, selectedIndex(), selected());
    }

    protected boolean onOk(EnvironmentEvent event)
    {
	if (clickHandler == null)
	    return false;
	final int index = selectedIndex();
	final Object item = selected();
	if (index < 0 || item == null)
	    return false;
	    return clickHandler.onListClick(this, index, item);
    }

    @Override public RegionContent getWholeRegion()
    {
	if (model == null || model.getItemCount() < 0)
	    return null;
	final LinkedList<String> res = new LinkedList<String>();
	final int count = model.getItemCount();
	for(int i = 0;i < count;++i)
	{
	    final String line = appearance.getScreenAppearance(model.getItem(i), NONE_APPEARANCE_FLAGS);
	    res.add(line != null?line:"");
	}
	res.add("");
	return new RegionContent(res.toArray(new String[res.size()]));
    }

    @Override public RegionContent getRegion(int fromX, int fromY, int toX, int toY)
    {
	if (model == null || model.getItemCount() < 0)
	    return null;
	if (fromY >= model.getItemCount() || toY > model.getItemCount())
	    return null;
	if (fromY == toY)
	{
	    final String line = appearance.getScreenAppearance(model.getItem(fromY), NONE_APPEARANCE_FLAGS);
	    if (line == null || line.isEmpty())
		return null;
	    final int fromPos = fromX < line.length()?fromX:line.length();
	    final int toPos = toX < line.length()?toX:line.length();
	    if (fromPos >= toPos)
		return null;
	    return new RegionContent(new String[]{line.substring(fromPos, toPos)});
	}
	final LinkedList<String> res = new LinkedList<String>();
	for(int i = fromY;i < toY;++i)
	{
	    final String line = appearance.getScreenAppearance(model.getItem(i), NONE_APPEARANCE_FLAGS);
	    res.add(line != null?line:"");
	}
	res.add("");
	return new RegionContent(res.toArray(new String[res.size()]));
    }

    @Override public boolean deleteWholeRegion()
    {
	return false;
    }

    @Override public boolean deleteRegion(int fromX, int fromY, int toX, int toY)
    {
	return false;
    }

    @Override public boolean insertRegion(int x, int y, RegionContent data)
    {
	return false;
    }

    protected void onNewHotPointY(boolean briefAnnouncement)
    {
	final int index = selectedIndex();
	if (index < 0)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    hotPointX = 0;
	    environment.onAreaNewHotPoint(this);
	    return;
	}
	final Object item = model.getItem(index);
	if (item == null)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    hotPointX = 0;
	    environment.onAreaNewHotPoint(this);
	    return;
	}
	appearance.announceItem(item, briefAnnouncement?BRIEF_ANNOUNCEMENT_ONLY:NONE_APPEARANCE_FLAGS);
	hotPointX = appearance.getObservableLeftBound(item);
	environment.onAreaNewHotPoint(this);
    }

    protected String getObservableSubstr(Object item)
    {
	NullCheck.notNull(item, "item");
final String line = 	    appearance.getScreenAppearance(item, NONE_APPEARANCE_FLAGS);
NullCheck.notNull(line, "line");
if (line.isEmpty())
return "";
final int leftBound = Math.min(appearance.getObservableLeftBound(item), line.length());
final int rightBound = Math.min(appearance.getObservableRightBound(item), line.length());
if (leftBound >= rightBound)
    return "";
return line.substring(leftBound, rightBound);
    }

    protected String noContentStr()
    {
	return environment.getStaticStr("ListNoContent");
    }

    protected void announceChar(String  line, int pos, int rightBound)
    {
	NullCheck.notNull(line, "line");
	if (pos < rightBound)
	    environment.sayLetter(line.charAt(pos)); else
	    environment.hint(Hints.END_OF_LINE);
    }

	protected boolean noContent()
    {
	if (model == null || model.getItemCount() < 1)
	{
	    environment.hint(noContentStr(), Hints.NO_CONTENT);
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
