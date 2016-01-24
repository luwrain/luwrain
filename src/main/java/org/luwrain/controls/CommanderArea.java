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
 * The area for browsing of directories.  This class behaves as a panel in
 * old-style file commander. The user can explore directory content and
 * move around it, traversing over near directories. Custom filters and
 * comparators are supported.
 */
public class CommanderArea implements Area, RegionProvider
{
    public static final String PARENT_DIR = "..";

    static public class Entry
    {
	enum Type {REGULAR, DIR, SYMLINK, SPECIAL, UNKNOWN,
		   PIPE, SOCKET, BLOCK_DEVICE, CHAR_DEVICE};

	private Path path;
	private Type type;
	private boolean selected;
	private boolean parent;

	Entry(Path path) throws IOException
	{
	    NullCheck.notNull(path, "path");
	    this.path = path;
	    this.type = readType(path);
	    this.selected = false;
	    this.parent = false;
	}

	Entry(Path path, Type type,
	      boolean selected, boolean parent)
	{
	    //	    System.out.println(path.toString());
	    this.path = path;
	    this.type = type;
	    this.selected = selected;
	    this.parent = parent;
	    NullCheck.notNull(path, "path");
	}

	public Path path()
	{
	    return path;
	}

public Type type()
	{
	    return type;
	}

	public boolean selected()
	{
	    return selected;
	}

	public boolean parent()
	{
	    return parent;
	}

	public String baseName()
	{
	    return path.getFileName().toString();
	}

	static private Type readType(Path path) throws IOException
	{
	    NullCheck.notNull(path, "path");
	    final BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
	    if (attr.isDirectory())
		return Type.DIR;
	    if (attr.isSymbolicLink())
		return Type.SYMLINK;
	    if (attr.isRegularFile())
		return Type.REGULAR;
	    if (attr.isOther())
		return Type.SPECIAL;
	return Type.UNKNOWN;
	}

    }

    public interface ClickHandler
    {
    boolean onCommanderClick(Path cursorAt, Path[] selected);
    }

    public interface Filter
{
    boolean commanderEntrySuits(Entry entry);
}

public interface Appearance 
{
    void introduceEntry(Entry entry, boolean brief);
    void introduceLocation(Path path);
    String getScreenLine(Entry entry);
    String getCommanderName(Path path);
}

static public class Params
{
    public ControlEnvironment environment;
    public Appearance appearance;
    public boolean selecting = false;
    public ClickHandler clickHandler = null;
    public Filter filter = new CommanderFilters.NoHidden();
    public Comparator comparator = new ByNameCommanderComparator();
}

    private final Region region = new Region(this);
    protected ControlEnvironment environment;
    private Appearance appearance;
    private ClickHandler clickHandler;
    protected Filter filter = new CommanderFilters.NoHidden();
    protected Comparator comparator = new ByNameCommanderComparator();
    protected boolean selecting = false;
    protected int visualShift = 2;

    protected Path current;
    protected Vector<Entry> entries;//null means the content is inaccessible
    protected int hotPointX = 0;
    protected int hotPointY = 0;

    public CommanderArea(ControlEnvironment environment)
    {
	this.environment = environment;
	NullCheck.notNull(environment, "environment");
	current = environment.launchContext().userHomeDirAsPath();
	refresh();
    }

    public CommanderArea(ControlEnvironment environment, Path current)
    {
	this.environment = environment;
	this.current = current;
	NullCheck.notNull(environment, "environment");
	NullCheck.notNull(current, "current");
	if (!Files.isDirectory(current))
	    throw new IllegalArgumentException("current must address a directory");
	refresh();
    }

    public CommanderArea(Params params, Path current)
    {
	NullCheck.notNull(params, "params");
	this.environment = params.environment;
	this.appearance = params.appearance;
	this.clickHandler = params.clickHandler;
	this.filter = params.filter;
	this.comparator = params.comparator;
	this.selecting = params.selecting;
	this.current = current;
	NullCheck.notNull(environment, "environment");
	NullCheck.notNull(appearance, "appearance");
	//	NullCheck.notNull(clickHandler, "clickHandler");
	NullCheck.notNull(filter, "filter");
	NullCheck.notNull(comparator, "comparator");
	NullCheck.notNull(current, "current");
	if (!Files.isDirectory(current))
	    throw new IllegalArgumentException("current must address a directory");
	refresh();
    }

    public void setClickHandler(ClickHandler clickHandler)
    {
	this.clickHandler = clickHandler;
    }

    public boolean find(String fileName, boolean announce)
    {
	NullCheck.notNull(fileName, "fileName");
	if (fileName.isEmpty())
	    throw new IllegalArgumentException("fileName may not be null");
	if (isEmpty())
	    return false;
	int index = 0;
	while(index < entries.size() && !entries.get(index).baseName().equals(fileName))
	    ++index;
	if (index >= entries.size())
	    return false;
	hotPointY = index;
	hotPointX = 0;
	environment.onAreaNewHotPoint(this);
	if (announce)
	    appearance.introduceEntry(entries.get(hotPointY), false);
	return true;
    }

    /*
     * Returns the list of currently selected files. If user marked some
     * files or directories, this method returns list of them, regardless what
     * entry is under the cursor. Otherwise, this method returns exactly the
     * entry under the current cursor position or an empty array if the cursor is at
     * the empty string in the bottom of the area. The parent directory entry
     * is always ignored.
     *
     * @return The list of currently selected entries 
     */
    public Path[] selected()
    {
	if (isEmpty())
	    return new Path[0];
	if (selecting)
	{
	    final LinkedList<Path> paths = new LinkedList<Path>();
	    for(Entry e: entries)
		if (e.selected() && !e.parent())
		    paths.add(e.path());
	    if (!paths.isEmpty())
		return paths.toArray(new Path[paths.size()]);
	}
	final Entry e = cursorAtEntry();
	if (e == null || e.parent())
	    return new Path[0];
	return new Path[]{e.path()};
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
	return current;
    }

    /**
     * Returns the entry exactly under the cursor. This method returns the
     * entry without taking into account where the user marks are. If the cursor is at
     * the empty line in the bottom of the area this method returns null. The parent directory entry is returned
     * as usual.
     *
     * @return The entry under the cursor
     */
    public Path cursorAt()
    {
	return !isEmpty() && hotPointY >= 0 && hotPointY < entries.size()?entries.get(hotPointY).path():null;
    }

    public Entry cursorAtEntry()
    {
	return !isEmpty() && hotPointY >= 0 && hotPointY < entries.size()?entries.get(hotPointY):null;
    }


    /**
     * Updates the content of the current location. This method just rereads 
     * list of files from the disk.
     */
    public void refresh()
    {
	if (current == null)//What is very strange
	{
	    entries = null;
	    notifyNewContent();
	    return;
	}
	final Entry e = cursorAtEntry();
	open(current, e != null?e.baseName():null);
    }

    public void setFilter(Filter filter)
    {
	NullCheck.notNull(filter, "filter");
	this.filter = filter;
    }

    public boolean isEmpty()
    {
	return entries == null || entries.isEmpty();
    }

    @Override public int getLineCount()
    {
	return !isEmpty()?entries.size() + 1:1;
    }

    @Override public String getLine(int index)
    {
	if (isEmpty())
	    return index == 0?noContentStr():"";
	return index < entries.size()?appearance.getScreenLine(entries.get(index)):"";
    }

    @Override public int getHotPointX()
    {
	if (isEmpty())
	    return 0;
	if (hotPointY > entries.size())
	    return 0;
	return hotPointX >= 0?hotPointX + visualShift:visualShift;
    }

    @Override public int getHotPointY()
    {
	if (isEmpty())
	    return 0;
	return hotPointY >= 0?hotPointY:0;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (!event.isCommand())
	{
	    switch(event.getCharacter())
	    {
	    case '~':
		open(environment.launchContext().userHomeDirAsPath(), null);
		if (current != null)
		    appearance.introduceLocation(current);
		return true;
	    case '/':
		if (current == null)
		    return false;
		open(current.getRoot(), null);
		if (current != null)
		    appearance.introduceLocation(current);
		return true;
	    default:
		return onChar(event);
	    }
	}
	if (event.isModified())
	    return false;
	switch(event.getCommand())
	{
	case KeyboardEvent.BACKSPACE:
	    return onBackspace(event);
	case KeyboardEvent.ENTER:
	    return onEnter(event);
	case KeyboardEvent.ARROW_DOWN:
	    return onArrowDown(event, false);
	case KeyboardEvent.ARROW_UP:
	    return onArrowUp(event, false);
	case KeyboardEvent.ALTERNATIVE_ARROW_DOWN:
	    return onArrowDown(event, true);
	case KeyboardEvent.ALTERNATIVE_ARROW_UP:
	    return onArrowUp(event, true);
	case KeyboardEvent.ARROW_LEFT:
	    return onArrowLeft(event);
	case KeyboardEvent.ARROW_RIGHT:
	    return onArrowRight(event);
	case KeyboardEvent.ALTERNATIVE_ARROW_LEFT:
	    return onAltLeft(event);
	case KeyboardEvent.ALTERNATIVE_ARROW_RIGHT:
	    return onAltRight(event);
	case KeyboardEvent.PAGE_DOWN:
	    return onPageDown(event, false);
	case KeyboardEvent.PAGE_UP:
	    return onPageUp(event, false);
	case KeyboardEvent.ALTERNATIVE_PAGE_DOWN:
	    return onPageDown(event, true);
	case KeyboardEvent.ALTERNATIVE_PAGE_UP:
	    return onPageUp(event, true);
	case KeyboardEvent.HOME:
	    return onHome(event);
	case KeyboardEvent.END:
	    return onEnd(event);
	case KeyboardEvent.ALTERNATIVE_HOME:
	    return onAltHome(event);
	case KeyboardEvent.ALTERNATIVE_END:
	    return onAltEnd(event);
	case KeyboardEvent.INSERT:
	    return onInsert(event);
	default:
	    return false;
	}
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	switch(event.getCode())
	{
	case EnvironmentEvent.REFRESH:
	    refresh();
	    return true;
	case EnvironmentEvent.OK:
	    return onOk(event);
	default:
	    return region.onEnvironmentEvent(event, hotPointX, hotPointY);
	}
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	if (query.getQueryCode() == AreaQuery.CURRENT_DIR)
	{
	    final CurrentDirQuery currentDirQuery = (CurrentDirQuery)query;
	    currentDirQuery.setCurrentDir(current.toString());
	    return true;
	}
	return region.onAreaQuery(query, hotPointX, hotPointY);
    }

    @Override public Action[] getAreaActions()
    {
	return new Action[0];
    }

    @Override public String getAreaName()
    {
	if (current == null)
	    return "-";
	return appearance.getCommanderName(current);
    }

    @Override public HeldData getWholeRegion()
    {
	if (entries == null || entries.isEmpty())
	    return null;
	final LinkedList<String> res = new LinkedList<String>();
	for(Entry e: entries)
	{
	    final String line = appearance.getScreenLine(e);
	    res.add(line != null?line:"");
	}
	res.add("");
	return new HeldData(res.toArray(new String[res.size()]));
    }

    @Override public HeldData getRegion(int fromX, int fromY, int toX, int toY)
    {
	if (entries == null || entries.isEmpty())
	    return null;
	if (fromY >= entries.size() || toY > entries.size())
	    return null;
	if (fromY == toY)
	{
	    String line = appearance.getScreenLine(entries.get(fromY));
	    if (line == null || line.length() < 3)
		return null;
	    line = line.substring(2);
	    final int fromPos = fromX < line.length()?fromX:line.length();
	    final int toPos = toX < line.length()?toX:line.length();
	    if (fromPos >= toPos)
		return null;
	    return new HeldData(new String[]{line.substring(fromPos, toPos)});
	}
	final LinkedList<String> res = new LinkedList<String>();
	for(int i = fromY;i < toY;++i)
	{
	    final String line = appearance.getScreenLine(entries.get(i));
	    res.add(line != null?line:"");
	}
	res.add("");
	return new HeldData(res.toArray(new String[res.size()]));
    }

    @Override public boolean deleteWholeRegion()
    {
	return false;
    }

    @Override public boolean deleteRegion(int fromX, int fromY, int toX, int toY)
    {
	return false;
    }

    @Override public boolean insertRegion(int x, int y, HeldData data)
    {
	return false;
    }

    private boolean onEnter(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY >= entries.size())
	{
	    if (clickHandler == null)
		return false;
	    final Path[] selected = selected();
	    if (selected == null || selected.length < 1)
		return false;
	    return clickHandler.onCommanderClick(null, selected);
	}
	final Entry entry = entries.get(hotPointY);
	System.out.println("1");
	if (Files.isDirectory(entry.path()))//Explicit check because it could be a symlink to directory
	{
	    final Path parent = current.getParent();
	    if (entry.parent() && parent != null)
		open(parent, current.getFileName().toString()); else
		open(entry.path(), null);
	    appearance.introduceLocation(current);
	    return true;
	} //directory
	System.out.println("1a");
	if (clickHandler == null)
	    return false;
	System.out.println("2");
	return clickHandler.onCommanderClick(entry.path(), selected());
    }

    private boolean onBackspace(KeyboardEvent event)
    {
	//noContent() isn't applicable here, we should be able to leave the directory, even if it doesn't have any content
	if (current == null)
	    return false;
	final Path parent = current.getParent();
	if (parent == null)
	    return false;
	open(parent, current.getFileName().toString());
	appearance.introduceLocation(current);
	return true;
    }

    private boolean onInsert(KeyboardEvent event)
    {
	if (!selecting)
	    return false;
	if (noContentCheck())
	    return true;
	if (hotPointY >= entries.size())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	entries.get (hotPointY).selected = !entries.get(hotPointY).selected;
	++hotPointY;
	onNewHotPointY(false);
	return true;
    }

    private boolean onOk(EnvironmentEvent event)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY >= entries.size())
	{
	    Path[] selected = selected();
	    if (selected == null || selected.length < 1)
		return false;
	    if (clickHandler == null)
		return false;
	    return clickHandler.onCommanderClick(null, selected);
	}
	final Entry entry = entries.get(hotPointY);
	if (clickHandler == null)
	    return false;
	return clickHandler.onCommanderClick(entry.path(), selected());
    }

    private boolean onChar(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	final char c = event.getCharacter();
	String beginning = "";
	if (hotPointY < entries.size())
	{
	    final String name = entries.get(hotPointY).baseName();
	    final int pos = hotPointX < name.length()?hotPointX:name.length();
	    beginning = name.substring(0, pos);
	}
	final String mustBegin = beginning + c;
	for(int i = 0;i < entries.size();++i)
	{
	    final String name = entries.get(i).baseName();
	    if (!name.startsWith(mustBegin))
		continue;
	    hotPointY = i;
	    ++hotPointX;
	    environment.onAreaNewHotPoint(this);
	    appearance.introduceEntry(entries.get(hotPointY), true);
	    return true;
	}
	return false;
    }

    private boolean onArrowDown(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY >= entries.size())
	{
	    environment.hint(Hints.NO_ITEMS_BELOW);
	    return true;
	}
	++hotPointY;
	onNewHotPointY(briefIntroduction);
	return true;
    }

    private boolean onArrowUp(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY <= 0)
	{
	    environment.hint(Hints.NO_ITEMS_ABOVE);
	    return true;
	}
	--hotPointY;
	onNewHotPointY(briefIntroduction);
	return true;
    }

    private boolean onPageDown(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY >= entries.size())
	{
	    environment.hint(Hints.NO_ITEMS_BELOW);
	    return true;
	}
	final int visibleHeight = environment.getAreaVisibleHeight(this);
	if (visibleHeight < 1)
	    return false;
	if (hotPointY + visibleHeight > entries.size())
	    hotPointY = entries.size(); else
	    hotPointY += visibleHeight;
	onNewHotPointY(briefIntroduction);
	return true;
    }

    private boolean onPageUp(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY <= 0)
	{
	    environment.hint(Hints.NO_ITEMS_ABOVE);
	    return true;
	}
	final int visibleHeight = environment.getAreaVisibleHeight(this);
	if (visibleHeight < 1)
	    return false;
	if (hotPointY < visibleHeight)
	    hotPointY = 0; else
	    hotPointY -= visibleHeight;
	onNewHotPointY(briefIntroduction);
	return true;
    }

    private boolean onHome(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	hotPointY = 0;
	onNewHotPointY(false);
	return true;
    }

    private boolean onEnd(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	hotPointY = entries.size();
	onNewHotPointY(false);
	return true;
    }

    private boolean onArrowRight(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY >= entries.size())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String name = entries.get(hotPointY).baseName();
	if (name == null || name.isEmpty())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX >= name.length())
	{
	    environment.hint(Hints.END_OF_LINE);
	    return true;
	}
	++hotPointX;
	if (hotPointX < 0)
	    hotPointX = 0;
	if (hotPointX < name.length())
	    environment.sayLetter(name.charAt(hotPointX)); else
	    environment.hint(Hints.END_OF_LINE);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onArrowLeft(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY >= entries.size())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String name = entries.get(hotPointY).baseName();
	if (name == null || name.isEmpty())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX > name.length())
	    hotPointX = name.length();
	if (hotPointX <= 0)
	{
	    environment.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	--hotPointX;
	environment.onAreaNewHotPoint(this);
	environment.sayLetter(name.charAt(hotPointX));
	return true;
    }

    private boolean onAltRight(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY >= entries.size())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String name = entries.get(hotPointY).baseName();
	if (name == null || name.isEmpty())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX >= name.length())
	{
	    environment.hint(Hints.END_OF_LINE);
	    return true;
	}
	final WordIterator it = new WordIterator(name, hotPointX);
	if (!it.stepForward())
	{
	    environment.hint(Hints.END_OF_LINE);
	    return true;
	}
	hotPointX = it.pos();
	if (it.announce().length() > 0)
	    environment.say(it.announce()); else
	    environment.hint(Hints.END_OF_LINE);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAltLeft(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY >= entries.size())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String name = entries.get(hotPointY).baseName();
	if (name == null || name.isEmpty())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX > name.length())
	    hotPointX = name.length();
	if (hotPointX <= 0)
	{
	    environment.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	final WordIterator it = new WordIterator(name, hotPointX);
	if (!it.stepBackward())
	{
	    environment.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	hotPointX = it.pos();
	environment.say(it.announce());
	environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAltHome(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	hotPointX = 0;
	if (hotPointY >= entries.size() || entries.get(hotPointY).baseName().isEmpty())
	    environment.hint(Hints.EMPTY_LINE); else
	    environment.sayLetter(entries.get(hotPointY).baseName().charAt(hotPointX));
	environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAltEnd(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY < entries.size())
	{
	    hotPointX = entries.get(hotPointY).baseName().length();
	    environment.hint(Hints.END_OF_LINE);
	} else
	{
	    hotPointX = 0;
	    environment.hint(Hints.EMPTY_LINE);
	}
	environment.onAreaNewHotPoint(this);
	return true;
    }

    //Doesn't produce any speech announcement
    public void open(Path path, String desiredSelected)
    {
	NullCheck.notNull(path, "path");
	if (!Files.isDirectory(path))
	    throw new IllegalArgumentException("path must address a directory");
	current = path;
	entries = constructEntries(current);
	hotPointX = 0;
	hotPointY = 0;
	if (isEmpty())
	{
	    notifyNewContent();
	    return;
	}
	if (desiredSelected != null && !desiredSelected.isEmpty())
	    for(hotPointY = 0;hotPointY < entries.size();++hotPointY)
		if (entries.get(hotPointY).baseName().equals(desiredSelected))
		    break;
	if (hotPointY >= entries.size())
	    hotPointY = 0;
	notifyNewContent();
    }

    private Vector<Entry> constructEntries(Path path)
    {
	NullCheck.notNull(path, "path");
	final LinkedList<Path> paths = new LinkedList<Path>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
		for (Path p : directoryStream) 
		    paths.add(p);
	    } 
	catch (IOException e) 
	{
	    e.printStackTrace();
	    return null;
	}
	final LinkedList<Entry> res = new LinkedList<Entry>();
	if (path.getParent() != null)
	    res.add(new Entry(path.resolve(PARENT_DIR), Entry.Type.DIR, false, true));
	for(Path p: paths)
	    try {
		res.add(new Entry(p));
	    }
	    catch (IOException e)
	    {
		e.printStackTrace();
		Log.warning("commander", "unable to get attributes of " + p.toString() + ":" + e.getMessage());
	    }
	if (filter == null)
	{
	    Entry[] toSort = res.toArray(new Entry[res.size()]);
	    Arrays.sort(toSort, comparator);
	    final Vector<Entry> r = new Vector<Entry>();
	    for(int i = 0;i < toSort.length;++i)
		entries.add(i, toSort[i]);
	    return entries;
	}
	final Vector<Entry> filtered = new Vector<Entry>();
	for (Entry ee: res)
	    if (filter.commanderEntrySuits(ee))
		filtered.add(ee);
	final Entry[] toSort = filtered.toArray(new Entry[filtered.size()]);
	Arrays.sort(toSort, comparator);
	for(int i = 0;i < toSort.length;++i)
	    filtered.set(i, toSort[i]);
	return filtered;
    }

    private void onNewHotPointY(boolean briefIntroduction)
    {
	hotPointX = 0;
	if (hotPointY < entries.size())
	{
	    final Entry entry = entries.get(hotPointY);
	    if (entry != null)
		appearance.introduceEntry(entry, briefIntroduction); else
		environment.hint(Hints.EMPTY_LINE);
	} else
	    environment.hint(Hints.EMPTY_LINE);
	environment.onAreaNewHotPoint(this);
    }

    protected String noContentStr()
    {
	return environment.staticStr(LangStatic.COMMANDER_NO_CONTENT);
    }

    private boolean noContentCheck()
    {
	if (entries == null)
	{
	    environment.hint(noContentStr(), Hints.NO_CONTENT);
	    return true;
	}
	return false;
    }

    private void notifyNewContent()
    {
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
	environment.onAreaNewName(this);
    }
}
