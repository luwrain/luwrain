/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

import org.luwrain.core.*;
import org.luwrain.core.events.*;

import static org.luwrain.core.DefaultEventResponse.*;

public class TreeListArea<E> extends ListArea<E> implements ListArea.ClickHandler<E>
{
    public interface Collector<E>
    {
	void collect(List<E> items);
    }

public interface Model<E>
{
    E getRoot();
    boolean getItems(E root, Collector<E> collector);
    boolean isLeaf(E item);
}

    public interface LeafClickHandler<E>
    {
	boolean onLeafClick(TreeListArea<E> area, E item);
    }

    static public final class Params<E>
    {
	public ControlContext context;
	public String name;
	public TreeListArea.Model<E> model = null;
	public LeafClickHandler leafClickHandler = null;
    }

    protected final TreeListArea.Model<E> model;
    protected List<Frame<E>> history = new ArrayList<>();
    protected final List<E> content;
    protected LeafClickHandler leafClickHandler = null;

    public TreeListArea(TreeListArea.Params<E> params)
    {
	super(createListParams(params));
	NullCheck.notNull(params.model, "params.model");
	this.model = params.model;
	this.leafClickHandler = params.leafClickHandler;
	this.content = ((ModelImpl)getListModel()).getSource();
	setListClickHandler(this);
    }

    public void setLeafClickHandler(LeafClickHandler leafClickHandler)
    {
	NullCheck.notNull(leafClickHandler, "leafClickHandler");
	this.leafClickHandler = leafClickHandler;
    }

    public boolean requery()
    {
	if (history.isEmpty())
	    history.add(new Frame<E>(model.getRoot()));
	return fill(getLastFrame().parent);
    }

    public void clear()
    {
	history.clear();
	content.clear();
	reset(false);
	refresh();
    }

    public E opened()
    {
	if (history.isEmpty())
	    return null;
	return getLastFrame().parent;
    }

    protected boolean fill(E obj, E select)
    {
	return model.getItems(obj, (items)->{
		if (items == null)
		    throw new NullPointerException("items can't be null");
		this.content.clear();
		this.content.addAll(items);
		reset(false);
		refresh();
		if (select != null)
		    this.select(select, false);
	    });
    }

        protected boolean fill(E obj)
    {
	return fill(obj, null);
    }

    protected boolean fill(Frame<E> frame)
    {
	return fill(frame.parent, frame.selected);
    }

    @Override public boolean onListClick(ListArea<E> area, int index, E item)
    {
	if (!model.isLeaf(item))
	{
	getLastFrame().selected = selected();
	history.add(new Frame<E>(item));
	return fill(item);
	}
	if (leafClickHandler != null)
	    return leafClickHandler.onLeafClick(this, item);
	return false;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case BACKSPACE:
		return onBackspace(event);
	    }
	return super.onInputEvent(event);
    }

    protected boolean onBackspace(InputEvent event)
    {
	if (history.size() <= 1)
	    return false;
	history.remove(history.size() - 1);
	return fill(getLastFrame());
    }

    protected Frame<E> getLastFrame()
    {
	if (history.isEmpty())
	    throw new IllegalStateException("No frames in the history");
	return history.get(history.size() - 1);
    }

    static protected <E> ListArea.Params<E> createListParams(TreeListArea.Params params)
    {
	NullCheck.notNull(params, "params");
	NullCheck.notNull(params.model, "params.model");
final ListArea.Params<E> res = new ListArea.Params<>();
res.context = params.context;
res.name = params.name;
res.model = new ModelImpl<E>(new ArrayList<E>());
res.appearance = new Appearance<E>(res.context){
	@Override public boolean isLeaf(E e)
	{
	    return params.model.isLeaf(e);
	}
    };
return res;
    }

    static protected class ModelImpl<E> extends ListUtils.ListModel<E>
    {
	public ModelImpl(List<E> source) { super(source); }
	public List<E> getSource() { return source; }
    }

    static protected final class Frame<E>
    {
	public final E parent;
	E selected = null;
	public Frame(E parent)
	{
	    NullCheck.notNull(parent, "parent");
	    this.parent = parent;
	}
    }

static abstract    protected class Appearance<E> extends ListUtils.DefaultAppearance<E>
    {
	Appearance(ControlContext context) { super(context); }
	abstract public boolean isLeaf(E e);
	@Override public void announceItem(E e, Set<Flags> flags)
	{
	    if (!isLeaf(e))
		context.setEventResponse(listItem(Sounds.ATTENTION, e.toString(), Suggestions.CLICKABLE_LIST_ITEM)); else
		context.setEventResponse(listItem(e.toString(), Suggestions.LIST_ITEM));
	}
    }
}
