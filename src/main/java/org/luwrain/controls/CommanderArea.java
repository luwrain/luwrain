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
import java.util.concurrent.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.util.*;

public class CommanderArea<E> extends ListArea<CommanderArea.Wrapper<E>>
{
    static public final String PARENT_DIR = "..";
    public enum Flags {MARKING};
    public enum EntryType {REGULAR, DIR, PARENT, SYMLINK, SYMLINK_DIR, ARCHIVE, SPECIAL};

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
	String getEntryText(E entry, EntryType type, boolean marked);
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

    protected interface NativeItem<E>//FIXME:to delete
    {
	E getNativeObj();
	EntryType getEntryType();
	String getBaseName();
	boolean isDirectory();
    }

    static public class Params<E>
    {
	public ControlContext context = null;
	public CommanderArea.Model<E> model = null;
	public CommanderArea.Appearance<E> appearance = null;
	public CommanderArea.ClickHandler<E> clickHandler = null;
	public ClipboardSaver<Wrapper<E>> clipboardSaver = new ListUtils.DefaultClipboardSaver<Wrapper<E>>();
	public Filter<E> filter = null;
	public Comparator<NativeItem<E>> comparator = new CommanderUtils.ByNameComparator<NativeItem<E>>();
	public Set<Flags> flags = EnumSet.noneOf(Flags.class);
    }

    protected final CommanderArea.Model<E> model;
    protected final CommanderArea.Appearance<E> appearance;
    protected final Set<Flags> flags;
    protected CommanderArea.ClickHandler<E> clickHandler = null;
    protected Filter<E> filter = null;
    protected Comparator<NativeItem<E>> comparator = null;

    protected E currentLocation = null;
    protected FutureTask task = null;
    protected boolean closed = false;

    public CommanderArea(Params<E> params)
    {
	super(createListParams(params));
	NullCheck.notNull(params.flags, "params.flags");
	this.model = params.model;
	this.appearance = params.appearance;
	this.flags = params.flags;
	this.filter = params.filter;
	this.comparator = params.comparator;
	this.clickHandler = params.clickHandler;
	super.setListClickHandler((area, index, obj)->clickImpl(index, (Wrapper<E>)obj));
	getListModel().marking = params.flags.contains(Flags.MARKING);
    }

    public CommanderArea.Model<E> getCommanderModel()
    {
	return this.model;
    }

    public void setCommanderFilter(Filter<E> filter)
    {
	this.filter = filter;
    }

    public void setCommanderComparator(Comparator<NativeItem<E>> comparator)
    {
	NullCheck.notNull(comparator, "comparator");
	this.comparator = comparator;
    }

    public boolean findFileName(String fileName, boolean announce)
    {
	NullCheck.notNull(fileName, "fileName");
	if (isEmpty())
	    return false;
	final List<Wrapper<E>> wrappers = getListModel().wrappers;
	int index = 0;
	while(index < wrappers.size() && !getBaseName(wrappers.get(index)).equals(fileName))
	    ++index;
	if (index >= wrappers.size())
	    return false;
	select(index, false);
	if (announce)
	{
	    final Wrapper<E> w = wrappers.get(index);
	    appearance.announceEntry(w.obj, w.type, w.marked);
	}
	return true;
    }

    public boolean isBusy()
    {
	return task != null && !task.isDone();
    }

    public boolean isEmpty()
    {
	return currentLocation == null || getListModel().wrappers == null || getListModel().wrappers.isEmpty();
    }

    //FIXME:select() prohibiting calling and suggesting to call getSelectedEntry() instead

protected Wrapper<E> getSelectedWrapper()
    {
	return super.selected();
    }

    //never returns parent
    public E getSelectedEntry()
    {
	final Wrapper<E> w = getSelectedWrapper();
	if (w == null)
	    return null;
	return w.type != EntryType.PARENT?w.obj:null;
    }

    //Never returns parent 
    public String getSelectedEntryText()
    {
	final Wrapper<E> wrapper = getSelectedWrapper();
	if (wrapper == null || wrapper.type == EntryType.PARENT)
	    return null;
	return getBaseName(wrapper);
    }

    public E opened()
    {
	return currentLocation;
    }

    public Object[] getMarked()
    {
	if (getListModel().wrappers == null && !flags.contains(Flags.MARKING))
	    return new Object[0];
	final List<Object> res = new ArrayList<>();
	for(Wrapper w: getListModel().wrappers)
	    if (w.marked)
		res.add(w.obj);
	return res.toArray(new Object[res.size()]);
    }

        public String[] getMarkedNames()
    {
	if (getListModel().wrappers == null || !flags.contains(Flags.MARKING))
	    return new String[0];
	final List<String> res = new ArrayList<>();
	for(Wrapper w: getListModel().wrappers)
	    if (w.marked)
		res.add(w.baseName);
	return res.toArray(new String[res.size()]);
    }

    @SuppressWarnings("unchecked")
    protected Wrapper<E>[] getMarkedWrappers()
    {
	if (getListModel().wrappers == null || !flags.contains(Flags.MARKING))
	    return new Wrapper[0];
	final List<Wrapper<E>> res = new ArrayList<>();
	for(Wrapper<E> w: getListModel().wrappers)
	    if (w.marked)
		res.add(w);
	return res.toArray(new Wrapper[res.size()]);
    }

    public boolean open(E entry)
    {
	NullCheck.notNull(entry, "entry");
	return open(entry, null, true);
    }

    //If no desiredSelected found, area tries to leave selection unchanged
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

        //If no desiredSelected found, area tries to leave selection unchanged
    public boolean open(E entry, String desiredSelected, boolean announce)
    {
	NullCheck.notNull(entry, "entry");
	return open(entry, desiredSelected, null, announce);
    }

    //If no desiredSelected found, area tries to leave selection unchanged
    public boolean open(E entry, String desiredSelected, String [] desiredMarked, boolean announce)
    {
	NullCheck.notNull(entry, "entry");
	if (closed || isBusy())
	    return false;
	final E newCurrent = entry;
	final String previouslySelectedText = getSelectedEntryText();
	task = new FutureTask<>(()->{
		try {
		    final ArrayList<Wrapper<E>> wrappers;
		    final E[] res = model.getEntryChildren(newCurrent);
		    if (res != null)
		    {
			wrappers = new ArrayList<>();
			final ArrayList<E> filtered = new ArrayList<>();
			filtered.ensureCapacity(res.length);
			for(E e: res)
			    if (filter == null || filter.commanderEntrySuits(e))
				filtered.add(e);
			wrappers.ensureCapacity(filtered.size());
			for(int i = 0;i < filtered.size();++i)
			{
			    final EntryType entryType = model.getEntryType(newCurrent, filtered.get(i));
			    wrappers.add(new Wrapper<E>(filtered.get(i), entryType, appearance.getEntryText(filtered.get(i), entryType, false)));
			}
			if (comparator != null)
			    Collections.sort(wrappers, comparator);
		    } else
			wrappers = null;
		    //Trying to find what to select after opening
		    int index = -1;
		    if (desiredSelected != null && !desiredSelected.isEmpty())
		    {
			for(int i = 0;i < wrappers.size();++i)
			{
			    final Wrapper<E> w = wrappers.get(i);
			    if (desiredSelected.equals(appearance.getEntryText(w.obj, w.type, w.marked)))
				index = i;
			}
			//If there is still no found selection, we must try to save selection without changes
			if (index < 0 && previouslySelectedText != null && !previouslySelectedText.isEmpty())
			    for(int i = 0;i < wrappers.size();++i)
			    {
				final Wrapper<E> w = wrappers.get(i);
			    if (previouslySelectedText.equals(appearance.getEntryText(w.obj, w.type, w.marked)))
				index = i;
			    }
		    }
		    //Setting marks
		    if (flags.contains(Flags.MARKING) && desiredMarked != null)
			for(String toMark: desiredMarked)
			{
			    int k = 0;
			    for(k = 0;k < wrappers.size();++k)
				if (wrappers.get(k).getBaseName().equals(toMark))
				    break;
			    if (k < wrappers.size())
				wrappers.get(k).marked = true;
			}
		    //		    loadingResultHandler.onLoadingResult(newCurrent, wrappers, index, announce);
		    final int finalIndex = index;
		    context.runUiSafely(()->acceptNewLocation(newCurrent, wrappers, finalIndex, announce));
		}
		catch (Throwable e)
		{
		    throw new RuntimeException(e);
		}
	    }, null);
	context.executeBkg(task);
	return true;
    }

    public boolean reread(boolean announce)
    {
	return reread(getSelectedEntryText(), announce);
    }

    public boolean reread(String desiredSelected, boolean announce)
    {
	if (currentLocation == null)
	    return false;
	return open(currentLocation, desiredSelected, getMarkedNames(), announce);
    }

    protected void acceptNewLocation(E location, List<Wrapper<E>> data, int selectedIndex, boolean announce)
    {
	NullCheck.notNull(location, "location");
	currentLocation = location;
	getListModel().wrappers = data;
	super.redraw();
	if (data != null && selectedIndex >= 0)
	    select(selectedIndex, false); else
	    reset(false);
	if (announce)
	    appearance.announceLocation(currentLocation);
    }

        @SuppressWarnings("unchecked") @Override public ListModelAdapter<E> getListModel()
    {
	return (ListModelAdapter<E>)super.getListModel();
    }

    public void setClickHandler(CommanderArea.ClickHandler<E> clickHandler)
    {
	this.clickHandler = clickHandler;
    }

    @Override public void setListClickHandler(ListArea.ClickHandler clickHandler)
    {
	throw new UnsupportedOperationException("Changing list click handler for commander areas not allowed, use setClickHandler(CommanderArea.ClickHandler)instead");
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case BACKSPACE:
		return onBackspace(event);
	    case INSERT:
		return onMarking(event);
	    }
	return super.onInputEvent(event);
    }

    @Override public String getAreaName()
    {
	if (currentLocation == null)
	    return "";
	return appearance.getCommanderName(currentLocation);
    }

    @Override public boolean onClipboardCopy(int fromX, int fromY, int toX, int toY, boolean withDeleting)
    {
	if (isEmpty() || withDeleting)
	    return false;
	if (fromX < 0 || toX < 0 ||
	    (fromX == toX && fromY == toY))
	{
	    final List<Wrapper<E>> wrappers = new ArrayList<>();
	    if (flags.contains(Flags.MARKING))
		wrappers.addAll(Arrays.asList(getMarkedWrappers())); else
	    {
		wrappers.add(getSelectedWrapper());
		if (wrappers.get(0).type == EntryType.PARENT)
		    return false;
	    }
	    if (wrappers.isEmpty())
		return super.onClipboardCopy(fromX, fromY, toX, toY, withDeleting);
	    return listClipboardSaver.saveToClipboard(this, new ListUtils.ListModel<>(wrappers), listAppearance, 0, wrappers.size(), context.getClipboard());
	}
	return super.onClipboardCopy(fromX, fromY, toX, toY, withDeleting);
    }

    protected boolean onBackspace(InputEvent event)
    {
	//noContent() isn't applicable here, we should be able to leave the directory, even if it doesn't have any content
	if (currentLocation == null)
	    return false;
	final E parent = model.getEntryParent(currentLocation);
	if (parent == null)
	    return false;
	open(parent, appearance.getEntryText(currentLocation, EntryType.DIR, false));
	return true;
    }

    protected boolean onMarking(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (!flags.contains(Flags.MARKING))
	    return false;
	final Wrapper<E> wrapper = getSelectedWrapper();
	if (wrapper == null || wrapper.type == EntryType.PARENT)
	    return false;
	wrapper.toggleMark(); 
	if (wrapper.marked)
	    context.setEventResponse(DefaultEventResponse.text(Sounds.SELECTED, context.getStaticStr("CommanderSelected") + getBaseName(wrapper))); else
	    context.say("не выделено" + getBaseName(wrapper), Sounds.UNSELECTED); //FIXME:
	final int index = selectedIndex();
	if (index >= 0 && index + 1 < getListItemCount())
	    select(index + 1, false);
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
	    open(parent, appearance.getEntryText(currentLocation, EntryType.DIR, false));
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
	return context.getStaticStr("CommanderNoContent");
    }

    protected String getBaseName(Wrapper wrapper)
    {
	NullCheck.notNull(wrapper, "wrapper");
	return wrapper.getBaseName();
    }

    static protected <E> ListArea.Params<Wrapper<E>> createListParams(CommanderArea.Params<E> params)
    {
	NullCheck.notNull(params, "params");
	NullCheck.notNull(params.context, "params.context");
	NullCheck.notNull(params.model, "params.model");
	NullCheck.notNull(params.appearance, "params.appearance");
	NullCheck.notNull(params.comparator, "params.comparator");
	NullCheck.notNull(params.clipboardSaver, "params.clipboardSaver");
	final ListArea.Params<Wrapper<E>> listParams = new ListArea.Params<>();
	listParams.context = params.context;
	listParams.model = new ListModelAdapter<>(params.model, params.filter, params.comparator);
	listParams.appearance = new ListAppearanceImpl<>(params.appearance);
	listParams.name = "";//Never used, getAreaName() overrides
	listParams.clipboardSaver = params.clipboardSaver;
	return listParams;
    }

    static public class Wrapper<E> implements NativeItem<E>
    {
	final E obj;
final EntryType type;
	final String baseName;
	boolean marked = false;
	Wrapper(E obj, EntryType type, String baseName)
	{
	    NullCheck.notNull(obj, "obj");
	    NullCheck.notNull(type, "type");
	    NullCheck.notNull(baseName, "baseName");
	    this.obj = obj;
	    this.type = type;
	    this.baseName = baseName;
	}
	@Override public E getNativeObj()
	{
	    return obj;
	}
	@Override public boolean isDirectory()
	{
	    return type == EntryType.DIR || type == EntryType.SYMLINK_DIR;
	}
	@Override public EntryType getEntryType()
	{
	    return type;
	}
	@Override public String getBaseName()
	{
	    return baseName;
	}
	void toggleMark()
	{
	    marked = !marked;
	}
	@SuppressWarnings("unchecked") @Override public boolean equals(Object o)
	{
	    if (o == null || !(o instanceof Wrapper))
		return false;
	    final Wrapper<E> w = (Wrapper<E>)o;
	    return obj.equals(w.obj) && type == w.type;
	}
    }

    static public class ListAppearanceImpl<E> implements ListArea.Appearance<Wrapper<E>>
    {
	protected final CommanderArea.Appearance<E> appearance;
	public ListAppearanceImpl(CommanderArea.Appearance<E> appearance)
	{
	    NullCheck.notNull(appearance, "appearance");
	    this.appearance = appearance;
	}
	@Override public void announceItem(Wrapper<E> wrapper, Set<Flags> flags)
	{
	    NullCheck.notNull(wrapper, "wrapper");
	    NullCheck.notNull(flags, "flags");
	    appearance.announceEntry(wrapper.obj, wrapper.type, wrapper.marked);
	}
	@Override public String getScreenAppearance(Wrapper<E> wrapper, Set<Flags> flags)
	{
	    NullCheck.notNull(wrapper, "wrapper");
	    NullCheck.notNull(flags, "flags");
	    final boolean marked = wrapper.marked;
	    final EntryType type = wrapper.type;
	    final String name = appearance.getEntryText(wrapper.obj, wrapper.type, wrapper.marked);
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
	@Override public int getObservableLeftBound(Wrapper<E> wrapper)
	{
	    return 2;
	}
	@Override public int getObservableRightBound(Wrapper<E> wrapper)
	{
	    NullCheck.notNull(wrapper, "wrapper");
	    return wrapper.getBaseName().length() + 2;
	}
    }

    static protected class ListModelAdapter<E> implements ListArea.Model<Wrapper<E>>
    {
	protected final CommanderArea.Model<E> model;
	boolean marking = true;
	List<Wrapper<E>> wrappers = null;//null means the content is inaccessible
	public ListModelAdapter(CommanderArea.Model<E> model, Filter<E> filter, Comparator<NativeItem<E>> comparator)
	{
	    NullCheck.notNull(model, "model");
	    this.model = model;
	}
	@Override public int getItemCount()
	{
	    return wrappers != null?wrappers.size():0;
	}
	@Override public Wrapper<E> getItem(int index)
	{
	    return wrappers != null?wrappers.get(index):null;
	}
	@Override public void refresh()
	{
	}
    }
}
