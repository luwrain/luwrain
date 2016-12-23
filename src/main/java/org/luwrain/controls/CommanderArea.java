/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

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

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.util.*;
import org.luwrain.hardware.*;
import org.luwrain.os.*;

/**
 * The area for browsing the directories.  This class behaves as a panel in
 * old-style file commander. The user can explore directory content and
 * move around it, traversing over near directories. Custom filters and
 * comparators are supported.
 */
public class CommanderArea extends ListArea
{
    static public final String PARENT_DIR = "..";
    public enum Flags {MARKING};

    static public class Entry
    {
	public enum Type {REGULAR, DIR, PARENT, SYMLINK, SYMLINK_DIR, SPECIAL};

	final Path path;
	final Type type;
	protected boolean marked;

	Entry(Path path) throws IOException
	{
	    NullCheck.notNull(path, "path");
	    this.path = path;
	    this.type = readType(path);
	    this.marked = false;
	}

	Entry(Path path, Type type)
	{
	    NullCheck.notNull(path, "path");
	    NullCheck.notNull(type, "type");
	    this.path = path;
	    this.type = type;
	    this.marked = false;
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

	public Path getPath() { return path; }
public Type getType() { return type; }
	public boolean marked() { return marked; }
	public String baseName() { return path.getFileName().toString(); }

	@Override public boolean equals(Object o)
	{
	    if (o == null || !(o instanceof Entry))
		return false;
	    final Entry e = (Entry)o;
	    return path.equals(e.path) && type == e.type;
	}
    }

static public class Params
{
    public ControlEnvironment environment;
    public CommanderAppearance appearance;
    public CommanderArea.ClickHandler clickHandler;
    public boolean selecting = false;
    public Filter filter = null;//FIXME:
    public Comparator comparator = new CommanderUtils.ByNameComparator();
}

    protected final CommanderAppearance commanderAppearance;
    protected final boolean selecting;
    protected CommanderArea.ClickHandler clickHandler = null;

    public CommanderArea(Params params, Path current)
    {
	super(constructListParams(params));
	this.commanderAppearance = params.appearance;
	this.clickHandler = params.clickHandler;
	this.selecting = params.selecting;
	super.setClickHandler((area, index, obj)->clickImpl(index, (Entry)obj));
	if (!Files.isDirectory(current))
	    throw new IllegalArgumentException("current must address a directory");
	getListModel().load(current);
    }

    public boolean findPath(Path path, boolean announce)
    {
	NullCheck.notNull(path, "path");
	if (isEmpty())
	    return false;
	final Entry[] entries = getListModel().entries;
	int index = 0;
	while(index < entries.length && !entries[index].path.equals(path))
	    ++index;
	if (index >= entries.length)
	    return false;
	select(index, false);
	if (announce)
	    commanderAppearance.announceEntry(entries[index], false);
	return true;
    }

    public boolean findFileName(String fileName, boolean announce)
    {
	NullCheck.notNull(fileName, "fileName");
	if (isEmpty())
	    return false;
	final Entry[] entries = getListModel().entries;
	int index = 0;
	while(index < entries.length && !entries[index].baseName().equals(fileName))
	    ++index;
	if (index >= entries.length)
	    return false;
	select(index, false);
	if (announce)
	    commanderAppearance.announceEntry(entries[index], false);
	return true;
    }

    public Path[] marked()
    {
	if (!selecting || isEmpty())
	    return new Path[0];
	final LinkedList<Path> paths = new LinkedList<Path>();
	for(Entry e: getListModel().entries)
	    if (e.marked() && e.type != Entry.Type.PARENT)
		paths.add(e.path);
	return paths.toArray(new Path[paths.size()]);
    }

    /**
     * Returns the location being currently observed.  In general, this
     * method may return null if the object isn't associated with any
     * particular location but in practice this should happen quite rarely.  
     *
     * @return The location being observed
     */
    public Path opened()
    {
	return getListModel().current;
    }

    public Path selectedPath()
    {
	if (isEmpty())
	    return null;
	final Object res = selected();
	if (res == null)
	    return null;
	return ((Entry)res).path;
    }

    public Entry selectedEntry()
    {
	return !isEmpty() && hotPointY >= 0 && hotPointY < getListModel().entries.length?getListModel().entries[hotPointY]:null;
    }

    public void setFilter(Filter filter)
    {
	NullCheck.notNull(filter, "filter");
	//	this.filter = filter;
    }

    public boolean isEmpty()
    {
	return getListModel().entries == null || getListModel().entries.length < 1;
    }

    //Doesn't produce any speech announcement
    public void open(Path path, String desiredSelected)
    {
	NullCheck.notNull(path, "path");
	if (!Files.isDirectory(path))
	    throw new IllegalArgumentException("path must address a directory");
	getListModel().load(path);
	hotPointX = 0;
	hotPointY = 0;
	if (isEmpty())
	{
	    notifyNewContent();
	    return;
	}
	if (desiredSelected != null && !desiredSelected.isEmpty())
	    for(hotPointY = 0;hotPointY < getListModel().entries.length;++hotPointY)
		if (getListModel().entries[hotPointY].baseName().equals(desiredSelected))
		    break;
	if (hotPointY >= getListModel().entries.length)
	    hotPointY = 0;
	notifyNewContent();
    }

    public void open(Path path)
    {
	open(path, null);
    }

    @Override public ModelImpl getListModel()
    {
	return (ModelImpl)model;
    }

    public void setClickHandler(CommanderArea.ClickHandler clickHandler)
    {
	this.clickHandler = clickHandler;
    }

    @Override public void setClickHandler(ListClickHandler clickHandler)
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
    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	if (query.getQueryCode() == AreaQuery.CURRENT_DIR)
	{
	    final CurrentDirQuery currentDirQuery = (CurrentDirQuery)query;
	    currentDirQuery.answer(getListModel().current.toString());
	    return true;
	}
	return super.onAreaQuery(query);
    }

    @Override public String getAreaName()
    {
	if (getListModel().current == null)
	    return "-";
	return commanderAppearance.getCommanderName(getListModel().current);
    }

    protected boolean onBackspace(KeyboardEvent event)
    {
	//noContent() isn't applicable here, we should be able to leave the directory, even if it doesn't have any content
	if (getListModel().current == null)
	    return false;
	final Path parent = getListModel().current.getParent();
	if (parent == null)
	    return false;
	open(parent, getListModel().current.getFileName().toString());
	commanderAppearance.announceLocation(getListModel().current);
	return true;
    }

    protected boolean clickImpl(int index, Entry entry)
    {
	NullCheck.notNull(entry, "entry");
	final Path parent = getListModel().current.getParent();
	if (entry.type == Entry.Type.PARENT && parent != null)
	{
	    open(parent, getListModel().current.getFileName().toString());
	    commanderAppearance.announceLocation(getListModel().current);
									return true;
	}
	if (entry.type == Entry.Type.DIR || entry.type == Entry.Type.SYMLINK_DIR)
	{
	    ClickHandler.Result res = ClickHandler.Result.OPEN_DIR;
	    if (this.clickHandler != null)
		res = this.clickHandler.onCommanderClick(this, entry.path, true);
	    switch(res)
	    {
	    case OPEN_DIR:
		open(entry.path, null);
		commanderAppearance.announceLocation(getListModel().current);
		return true;
	    case OK:
		return true;
	    case REJECTED:
		return false;
	    }
	    return false;
	} //directory
	ClickHandler.Result res = ClickHandler.Result.REJECTED;
	if (this.clickHandler != null)
	    res = this.clickHandler.onCommanderClick(this, entry.path, false);
	return res == ClickHandler.Result.OK?true:false;
    }

    static protected Entry[] loadEntries(Path path,
				       Filter filter, Comparator comparator)
    {
	NullCheck.notNull(path, "path");
	NullCheck.notNull(filter, "filter");
	NullCheck.notNull(comparator, "comparator");
	try {
	    final LinkedList<Path> paths = new LinkedList<Path>();
	    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
		    for (Path p : directoryStream)
			paths.add(p);
		}
	    final LinkedList<Entry> res = new LinkedList<Entry>();
	    if (path.getParent() != null)
		res.add(new Entry(path.resolve(PARENT_DIR), Entry.Type.PARENT));
	    for(Path p: paths)
	    {
		final Entry e = new Entry(p);
		if (filter == null || filter.commanderEntrySuits(e))
		    res.add(e);
	    }
	    final Entry[] toSort = res.toArray(new Entry[res.size()]);
	    Arrays.sort(toSort, comparator);
	    return toSort;
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	    return null;
	}
    }

    @Override protected String noContentStr()
    {
	return environment.getStaticStr("CommanderNoContent");
    }

    protected void notifyNewContent()
    {
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
	environment.onAreaNewName(this);
    }

    static protected Entry.Type readType(Path path) throws IOException
    {
	NullCheck.notNull(path, "path");
	final BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
	if (attr.isDirectory())
	    return Entry.Type.DIR;
	    if (attr.isSymbolicLink())
		if (Files.isDirectory(path))
		    return Entry.Type.SYMLINK_DIR; else
		    return Entry.Type.SYMLINK;
	    if (attr.isRegularFile())
		return Entry.Type.REGULAR;
	    return Entry.Type.SPECIAL;
	}

    static protected ListArea.Params constructListParams(CommanderArea.Params params)
    {
	NullCheck.notNull(params, "params");
	NullCheck.notNull(params.environment, "params.environment");
	NullCheck.notNull(params.comparator, "params.comparator");
	final ListArea.Params listParams = new ListArea.Params();
	listParams.environment = params.environment;
	listParams.model = new ModelImpl(params.filter, params.comparator);
	listParams.appearance = new AppearanceImpl(params.appearance);
	listParams.name = "#CommanderArea#";//Never used, getAreaName() overridden
	return listParams;
    }

    public interface Filter
{
    boolean commanderEntrySuits(Entry entry);
}

    public interface CommanderAppearance 
    {
	String getCommanderName(Path path);
	String getScreenLine(Entry entry);
	void announceLocation(Path path);
	void announceEntry(Entry entry, boolean brief);
    }

    public interface ClickHandler
    {
	public enum Result {OPEN_DIR, OK, REJECTED};
	Result onCommanderClick(CommanderArea area, Path path, boolean dir);
    }

    public static class AppearanceImpl implements ListArea.Appearance
    {
	protected final CommanderAppearance commanderAppearance;

	public AppearanceImpl(CommanderAppearance commanderAppearance)
	{
	    NullCheck.notNull(commanderAppearance, "commanderAppearance");
	    this.commanderAppearance = commanderAppearance;
	}

	@Override public void announceItem(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    commanderAppearance.announceEntry((Entry)item, flags.contains(Flags.BRIEF));
	}

	@Override public String getScreenAppearance(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    final Entry entry = (Entry)item;
	    final boolean marked = entry.marked();
	    final CommanderArea.Entry.Type type = entry.type;
	    final String name = commanderAppearance.getScreenLine(entry);
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
	    return commanderAppearance.getScreenLine((Entry)item).length() + 2;
	}
    }

    static public class ModelImpl implements ListArea.Model
    {
	boolean marking = true;
Filter filter = null;
	Comparator comparator = null;

	Path current;
	Entry[] entries;//null means the content is inaccessible

	ModelImpl(Filter filter, Comparator comparator)
	{
	    //filter may be null
	    NullCheck.notNull(comparator, "comparator");
	    this.filter = filter;
	    this.comparator = comparator;
	}

	void load(Path path)
	{
	    NullCheck.notNull(path, "path");
	    current = path;
	    entries = loadEntries(path, filter, comparator);
	}

	@Override public int getItemCount()
	{
	    return entries != null?entries.length:0;
	}

	@Override public Object getItem(int index)
	{
	    return (entries != null && index < entries.length)?entries[index]:null;
	}

	@Override public boolean toggleMark(int index)
	{
	    if (!marking)
		return false;
	    if (entries == null ||
		index < 0 || index >= entries.length)
		return false;
	    if (entries[index].type == Entry.Type.PARENT)
		return false;
	    entries[index].toggleMark();
	    return true;
	}

	@Override public void refresh()
	{
	    entries = loadEntries(current, filter, comparator);
	}
    }


}
