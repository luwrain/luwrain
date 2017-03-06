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
import java.util.concurrent.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.util.*;
import org.luwrain.base.*;

public class CommanderArea<E> extends ListArea
{
    static public final String PARENT_DIR = "..";
    public enum Flags {MARKING};
    public enum EntryType {REGULAR, DIR, PARENT, SYMLINK, SYMLINK_DIR, SPECIAL};

    public interface Model<E>
    {
	E[] getEntryChildren(E entry);
	E getEntryParent(E entry);
	EntryType getEntryType(E currentLocation, E entry);
    }

    public interface Appearance<E>
    {
	void announceLocation(E entry);
	void announceEntry(E entry, EntryType type, boolean marked);
	String getEntryTextAppearance(E entry, EntryType type, boolean marked);
	String getCommanderName(E entry);
    }

    public interface Filter<E>
    {
	boolean commanderEntrySuits(E entry);
    }

    public interface ClickHandler<E>
    {
	public enum Result {OPEN_DIR, OK, REJECTED};
	Result onCommanderClick(CommanderArea area, E entry, boolean dir);
    }

    public interface LoadingResultHandler<E>
    {
	void onLoadingResult(E location, Wrapper<E>[] wrappers, int selectedIndex, boolean announce);
    }

    static public class Params<E>
    {
	public ControlEnvironment environment;
	public CommanderArea.Model<E> model;
	public CommanderArea.Appearance<E> appearance;
	public CommanderArea.ClickHandler<E> clickHandler;
	public LoadingResultHandler<E> loadingResultHandler;
	public Filter<E> filter = null;
	public Comparator comparator = null;
	public Set<Flags> flags = EnumSet.noneOf(Flags.class);
    }

    protected final CommanderArea.Model<E> model;
    protected final CommanderArea.Appearance<E> appearance;
    protected CommanderArea.ClickHandler<E> clickHandler = null;
    protected Filter<E> filter = null;
    protected Comparator comparator = null;
    protected LoadingResultHandler<E> loadingResultHandler = null;
    protected E currentLocation = null;

    protected final ExecutorService executor = Executors.newSingleThreadExecutor();
    protected FutureTask task = null;

    protected boolean closed = false;

    public CommanderArea(Params<E> params)
    {
	super(prepareListParams(params));
	NullCheck.notNull(params.flags, "params.flags");
	this.model = params.model;
	this.appearance = params.appearance;
	this.filter = params.filter;
	this.comparator = params.comparator;
	this.clickHandler = params.clickHandler;
	this.loadingResultHandler = params.loadingResultHandler;
	super.setListClickHandler((area, index, obj)->clickImpl(index, (Wrapper<E>)obj));
	getListModel().marking = params.flags.contains(Flags.MARKING);
    }

    public CommanderArea.Model<E> getCommanderModel()
    {
	return model;
    }

    public void setCommanderFilter(Filter filter)
    {
	this.filter = filter;
    }

    public void setCommanderComparator(Comparator comparator)
    {
	NullCheck.notNull(comparator, "comparator");
	this.comparator = comparator;
    }

    public void setLoadingResultHandler(LoadingResultHandler<E> loadingResultHandler)
    {
	this.loadingResultHandler = loadingResultHandler;
    }

    public boolean findFileName(String fileName, boolean announce)
    {
	NullCheck.notNull(fileName, "fileName");
	if (isEmpty())
	    return false;
	final Wrapper<E>[] wrappers = getListModel().wrappers;
	int index = 0;
	while(index < wrappers.length && !appearance.getEntryTextAppearance(wrappers[index].obj, wrappers[index].type, wrappers[index].isMarked()).equals(fileName))
	    ++index;
	if (index >= wrappers.length)
	    return false;
	select(index, false);
	if (announce)
	    appearance.announceEntry(wrappers[index].obj, wrappers[index].type, wrappers[index].isMarked());
	return true;
    }

    public boolean isBusy()
    {
	return task != null && !task.isDone();
    }

    public boolean isEmpty()
    {
	return currentLocation == null || getListModel().wrappers == null || getListModel().wrappers.length < 1;
    }

    //never returns parent
    public E getSelectedEntry()
    {
	final Object res = selected();
	if (res == null)
	    return null;
	final Wrapper<E> w = (Wrapper<E>)res;
	return w.type != EntryType.PARENT?w.obj:null;
    }

    public E opened()
    {
	return currentLocation;
    }

    public Object[] getMarked()
    {
	if (getListModel().wrappers == null)
	    return new Object[0];
	final LinkedList res = new LinkedList();
	for(Wrapper w: getListModel().wrappers)
	    if (w.isMarked())
		res.add(w.obj);
	return res.toArray(new Object[res.size()]);
    }

    public void open(E entry)
    {
	NullCheck.notNull(entry, "entry");
	open(entry, null, true);
    }

    public boolean open(E entry, String desiredSelected)
    {
	NullCheck.notNull(entry, "entry ");
	return open(entry, desiredSelected, true);
    }

    public boolean open(E entry, boolean announce)
    {
	NullCheck.notNull(entry, "entry ");
	return open(entry, null, announce);
    }

    public boolean open(E entry, String desiredSelected, boolean announce)
    {
	NullCheck.notNull(entry, "entry");
	NullCheck.notNull(loadingResultHandler, "loadingResultHandler");
	if (closed || isBusy())
	    return false;
	final E newCurrent = entry;
	task = new FutureTask(()->{
		try {
		    final Wrapper<E>[] wrappers;
		    final E[] res = model.getEntryChildren(newCurrent);
		    if (res != null)
		    {
			final Vector<E> filtered = new Vector<E>();
			for(E e: res)
			    if (filter == null || filter.commanderEntrySuits(e))
				filtered.add(e);
			wrappers = new Wrapper[filtered.size()];
			for(int i = 0;i < filtered.size();++i)
			    wrappers[i] = new Wrapper(filtered.get(i), model.getEntryType(newCurrent, filtered.get(i)));
			if (comparator != null)
			    Arrays.sort(wrappers, comparator);
		    } else
			wrappers = null;
		    int index = -1;
		    if (desiredSelected != null && !desiredSelected.isEmpty())
			for(int i = 0;i < wrappers.length;++i)
			    if (desiredSelected.equals(appearance.getEntryTextAppearance(wrappers[i].obj, wrappers[i].type, wrappers[i].isMarked())))
				index = i;
		    loadingResultHandler.onLoadingResult(newCurrent, wrappers, index, announce);
		}
		catch (Exception e)
		{
		    Log.error("core", "unexpected error on commander content reading:" + e.getClass().getName() + ":" + e.getMessage());
		}
	    }, null);
	executor.execute(task);
	return true;
    }

    public boolean reread(boolean announce)
    {
	return reread(null, announce);
    }

    public boolean reread(String desiredSelected, boolean announce)
    {
	if (currentLocation == null)
	    return false;
	return open(currentLocation, desiredSelected, announce);
    }

    public void acceptNewLocation(E location, Wrapper<E>[] wrappers, int selectedIndex, boolean announce)
    {
	NullCheck.notNull(location, "location");
	currentLocation = location;
	getListModel().wrappers = wrappers;
	super.refresh();
	if (wrappers != null && selectedIndex >= 0)
	    select(selectedIndex, false); else
	    reset(false);
	if (announce)
	    appearance.announceLocation(currentLocation);
    }

    @Override public ListModelAdapter<E> getListModel()
    {
	return (ListModelAdapter<E>)super.getListModel();
    }

    public void setClickHandler(CommanderArea.ClickHandler clickHandler)
    {
	this.clickHandler = clickHandler;
    }

    @Override public void setListClickHandler(ListClickHandler clickHandler)
    {
	throw new UnsupportedOperationException("Changing list click handler for commander areas not allowed, use setClickHandler(CommanderArea.ClickHandler)instead");
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case BACKSPACE:
		return onBackspace(event);
	    }
	return super.onKeyboardEvent(event);
    }

    @Override public String getAreaName()
    {
	if (currentLocation == null)
	    return "";
	return appearance.getCommanderName(currentLocation);
    }

    protected boolean onBackspace(KeyboardEvent event)
    {
	//noContent() isn't applicable here, we should be able to leave the directory, even if it doesn't have any content
	if (currentLocation == null)
	    return false;
	final E parent = model.getEntryParent(currentLocation);
	if (parent == null)
	    return false;
	open(parent, appearance.getEntryTextAppearance(currentLocation, null, false));
	return true;
    }

    protected boolean clickImpl(int index, Wrapper<E> wrapper)
    {
	NullCheck.notNull(wrapper, "wrapper");
	if (closed || isBusy())
	    return false;
	if (clickHandler == null || currentLocation == null)
	    return false;
	if (wrapper.type == EntryType.PARENT)
	{
	    final E parent = model.getEntryParent(currentLocation);
	    if (parent == null)
		return false;
	    open(parent, appearance.getEntryTextAppearance(currentLocation, null, false));
	    return true;
	}
	if (wrapper.type == EntryType.DIR || wrapper.type == EntryType.SYMLINK_DIR)
	{
	    final ClickHandler.Result res = this.clickHandler.onCommanderClick(this, wrapper.obj, true);
	    NullCheck.notNull(res, "res");
	    switch(res)
	    {
	    case OPEN_DIR:
		open(wrapper.obj, null);
		return true;
	    case OK:
		return true;
	    case REJECTED:
		return false;
	    }
	    return false;
	} //directory
	final ClickHandler.Result res = this.clickHandler.onCommanderClick(this, wrapper.obj, false);
	NullCheck.notNull(res, "res");
	return res == ClickHandler.Result.OK?true:false;
    }

    @Override protected String noContentStr()
    {
	return environment.getStaticStr("CommanderNoContent");
    }

    static protected ListArea.Params prepareListParams(CommanderArea.Params params)
    {
	NullCheck.notNull(params, "params");
	NullCheck.notNull(params.environment, "params.environment");
	NullCheck.notNull(params.model, "params.model");
	NullCheck.notNull(params.appearance, "params.appearance");
	NullCheck.notNull(params.comparator, "params.comparator");
	final ListArea.Params listParams = new ListArea.Params();
	listParams.environment = params.environment;
	listParams.model = new ListModelAdapter(params.model, params.filter, params.comparator);
	listParams.appearance = new ListAppearanceImpl(params.appearance);
	listParams.name = "";//Never used, getAreaName() overrides
	return listParams;
    }

    static public class Wrapper<E>
    {
	public final E obj;
	public final EntryType type;
	protected boolean marked;

	public Wrapper(E obj, EntryType type)
	{
	    NullCheck.notNull(obj, "obj");
	    NullCheck.notNull(type, "type");
	    this.obj = obj;
	    this.type = type;
	    this.marked = false;
	}

	public boolean isDirectory()
	{
	    return type == EntryType.DIR || type == EntryType.SYMLINK_DIR;
	}

	public void mark()
	{
	    marked = true;
	}

	public void unmark()
	{
	    marked = false;
	}

	public void toggleMark()
	{
	    marked = !marked;
	}

	public boolean isMarked() 
	{
	    return marked; 
	}

	@Override public boolean equals(Object o)
	{
	    if (o == null || !(o instanceof Wrapper))
		return false;
	    final Wrapper<E> w = (Wrapper<E>)o;
	    return obj.equals(w.obj) && type == w.type;
	}
    }

    static public class ListAppearanceImpl<E> implements ListArea.Appearance
    {
	protected final CommanderArea.Appearance appearance;

	public ListAppearanceImpl(CommanderArea.Appearance<E> appearance)
	{
	    NullCheck.notNull(appearance, "appearance");
	    this.appearance = appearance;
	}

	@Override public void announceItem(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    final Wrapper<E> wrapper = (Wrapper<E>)item;
	    appearance.announceEntry(wrapper.obj, wrapper.type, wrapper.isMarked());
	}

	@Override public String getScreenAppearance(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    final Wrapper<E> wrapper = (Wrapper<E>)item;
	    final boolean marked = wrapper.isMarked();
	    final EntryType type = wrapper.type;
	    final String name = appearance.getEntryTextAppearance(wrapper.obj, wrapper.type, wrapper.isMarked());
	    final StringBuilder b = new StringBuilder();
	    b.append(marked?"*":" ");
	    switch(type)
	    {
	    case DIR:
		b.append("[");
		break;
	    case SPECIAL:
		b.append("!");
		break;
	    case SYMLINK:
	    case SYMLINK_DIR:
		b.append("{");
		break;
	    default:
		b.append(" ");
	    }
	    b.append(name);
	    switch(type)
	    {
	    case DIR:
		b.append("]");
		break;
	    case SYMLINK:
	    case SYMLINK_DIR:
		b.append("}");
		break;
	    }
	    return new String(b);
	}

	@Override public int getObservableLeftBound(Object item)
	{
	    return 2;
	}

	@Override public int getObservableRightBound(Object item)
	{
	    NullCheck.notNull(item, "item");
	    final Wrapper<E> wrapper = (Wrapper)item;
	    return appearance.getEntryTextAppearance(wrapper.obj, wrapper.type, wrapper.isMarked()).length() + 2;
	}
    }

    static public class ListModelAdapter<E> implements ListArea.Model
    {
	protected final CommanderArea.Model<E> model;
	boolean marking = true;
	Wrapper<E>[] wrappers;//null means the content is inaccessible

	public ListModelAdapter(CommanderArea.Model<E> model, Filter<E> filter, Comparator comparator)
	{
	    NullCheck.notNull(model, "model");
	    this.model = model;
	}

	@Override public int getItemCount()
	{
	    return wrappers != null?wrappers.length:0;
	}

	@Override public Object getItem(int index)
	{
	    return (wrappers != null && index < wrappers.length)?wrappers[index]:null;
	}

	@Override public boolean toggleMark(int index)
	{
	    if (!marking)
		return false;
	    if (wrappers == null ||
		index < 0 || index >= wrappers.length)
		return false;
	    if (wrappers[index].type == EntryType.PARENT)
		return false;
	    wrappers[index].toggleMark();
	    return true;
	}

	@Override public void refresh()
	{
	}
    }
}
