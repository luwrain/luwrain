/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.controls;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.os.*;

public class CommanderArea implements Area
{
    public static final String PARENT_DIR = "..";

    class Entry
    {
	public static final int REGULAR = 0;
	public static final int DIRECTORY = 1;

	private File file;
	private int type;
	boolean selected;

	public Entry(File file,
		     int type,
		     boolean selected)
	{
	    this.file = file;
	    this.type = type;
	    this.selected = selected;
	    if (file == null)
		throw new NullPointerException("file may not be null");
	}

	public File file()
	{
	    if (file == null)
		throw new NullPointerException("file may not be null");
	    return file;
	}

	public int type()
	{
	    return type;
	}

	public boolean selected()
	{
	    return selected;
	}
    }

    private File current;
    private Vector<Entry> entries;//null means the content is inaccessible;
    protected ControlEnvironment environment;
    protected OperatingSystem os;
    private Location[] importantLocations;
    private org.luwrain.core.Strings strings;

    private CommanderFilter filter;
    private Comparator comparator;
    private boolean selecting;
    protected int visualShift = 2;
    private int hotPointX = 0;
    private int hotPointY = 0;

    public CommanderArea(ControlEnvironment environment, OperatingSystem os)
    {
	this.environment = environment;
	this.os = os;
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
	if (os == null)
	    throw new NullPointerException("os may not be null");
	current = environment.launchContext().userHomeDirAsFile();
	filter = new NoHiddenCommanderFilter();
	comparator = new ByNameCommanderComparator();
	selecting = false;
	importantLocations = getImportantLocations();
	strings = environment.environmentStrings();
	refresh();
    }

    public CommanderArea(ControlEnvironment environment, 
			 OperatingSystem os,
			 File current)
    {
	this.environment = environment;
	this.os = os;
	this.current = current;
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
	if (os == null)
	    throw new NullPointerException("os may not be null");
	if (current == null)
	    throw new NullPointerException("current may not be null");
	if (!current.isDirectory())
	    throw new IllegalArgumentException("current must address a directory");
	filter = new NoHiddenCommanderFilter();
	comparator = new ByNameCommanderComparator();
	selecting = false;
	importantLocations = getImportantLocations();
	strings = environment.environmentStrings();
	refresh();
    }

    public CommanderArea(ControlEnvironment environment, 
			 OperatingSystem os,
			 File current,
			 boolean selecting,
			 CommanderFilter filter,
			 Comparator comparator)
    {
	this.environment = environment;
	this.os = os;
	this.current = current;
	this.selecting = selecting;
	this.filter = filter;
	this.comparator = comparator;
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
	if (os == null)
	    throw new NullPointerException("os may not be null");
	if (current == null)
	    throw new NullPointerException("current may not be null");
	if (comparator == null)
	    throw new NullPointerException("comparator may not be null");
	if (!current.isDirectory())
	    throw new IllegalArgumentException("current must address a directory");
	importantLocations = getImportantLocations();
	strings = environment.environmentStrings();
	refresh();
    }

    /*
     * Returns the list of currently selected files. If user marked some
     * files or directories this method returns their list, regardless what
     * entry is under the cursor. Otherwise, this method returns exactly the
     * entry under the current cursor position or an empty array if the cursor is at
     * the empty string in the bottom of the area. The parent directory entry
     * is always ignored.
     *
     * @return The list of currently selected entries 
     */
    public File[] selected()
    {
	if (entries == null)
	    return new File[0];
	if (selecting)
	{
	    Vector<File> files = new Vector<File>();
	    for(Entry e: entries)
		if (e.selected() && !e.file().getName().equals(PARENT_DIR))
		    files.add(e.file());
	    if (!files.isEmpty())
		return files.toArray(new File[files.size()]);
	}
	final File f = cursorAt();
	if (f == null || f.getName().equals(PARENT_DIR))
	    return new File[0];
	return new File[]{f};
    }

    /**
     * Returns the location being currently observed.  In general, this
     * method may return null if the object isn't associated with any
     * particular location but in practice this should happen quite rarely.  
     *
     * @return The location being observed
     */
    public File opened()
    {
	return current;
    }

    /**
     * Returns the entry exactly under the cursor. This method returns the
     * entry without taking into account where there are the user marks. If the cursor is at
     * the empty line in the bottom of the area this method returns null. The parent directory entry is returned
     * as well.
     *
     * @return The entry under the cursor
     */
    public File cursorAt()
    {
	return entries != null && hotPointY >= 0 && hotPointY < entries.size()?entries.get(hotPointY).file():null;
    }

    /**
     * Updates the content of the current location. This method just rereads 
     * list of files from the disk.
     */
    public void refresh()
    {
	if (current == null)//What very strange;
	{
	    entries = null;
	    return;
	}
	final File c = cursorAt();
	open(current, c != null?c.getName():null);
    }

    protected void introduceEntry(Entry entry, boolean brief)
    {
	if (entry == null)
	    return;
	if (brief)
	{
	    final String name = entry.file().getName();
	    if (name.equals(PARENT_DIR))
		environment.hint(environment.staticStr(LangStatic.COMMANDER_PARENT_DIRECTORY)); else
		if (name.trim().isEmpty())
		    environment.hint(Hints.EMPTY_LINE); else
		    environment.say(entry.file().getName());
	    return;
	}
	final boolean selected = entry.selected();
	final boolean dir = entry.type() == Entry.DIRECTORY;
	final String name = entry.file().getName();
	if (name.equals(PARENT_DIR))
	{
	    environment.hint(environment.staticStr(LangStatic.COMMANDER_PARENT_DIRECTORY));
	    return;
	}
	if (selected && dir)
	    environment.say(environment.staticStr(LangStatic.COMMANDER_SELECTED_DIRECTORY) + " " + name); else
	    if (selected)
		environment.say(environment.staticStr(LangStatic.COMMANDER_SELECTED) + " " + name); else
		if (dir)
		    environment.say(environment.staticStr(LangStatic.COMMANDER_DIRECTORY) + " " + name); else
		{
		    if (name.trim().isEmpty())
			environment.hint(Hints.EMPTY_LINE); else
			environment.say(name);
		}
    }

    protected void introduceLocation(File file)
    {
	if (file == null)
	    return;
	for(Location l: importantLocations)
	    if (l.file().equals(file))
	    {
		environment.say(strings.locationTitle(l));
		return;
	    }
	    environment.say(file.getName());
    }

    protected String getScreenLine(Entry entry)
    {
	if (entry == null)
	    throw new NullPointerException("entry may not be null");
	final boolean selected = entry.selected();
	final boolean dir = entry.type() == Entry.DIRECTORY;
	if (selected && dir)
	    return "*[" + entry.file().getName() + "]";
	if (selected)
	    return "* " + entry.file().getName();
	if (dir)
	    return " [" + entry.file().getName() + "]";
	return "  " + entry.file().getName();
    }

    protected boolean onClick(File current, File[] selected)
    {
	return false;
    }

    @Override public int getLineCount()
    {
	return entries != null && !entries.isEmpty()?entries.size() + 1:1;
    }

    @Override public String getLine(int index)
    {
	if (entries == null)
	    return environment.staticStr(LangStatic.COMMANDER_INACCESSIBLE_DIRECTORY_CONTENT);
	return index < entries.size()?getScreenLine(entries.get(index)):"";
    }

    @Override public int getHotPointX()
    {
	if (entries == null)
	    return 0;
	if (hotPointY > entries.size())
	    return 0;
	return hotPointX >= 0?hotPointX + visualShift:visualShift;
    }

    @Override public int getHotPointY()
    {
	if (entries == null)
	    return 0;
	return hotPointY >= 0?hotPointY:0;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	if (!event.isCommand())
	{
	    switch(event.getCharacter())
	    {
	    case '~':
		open(environment.launchContext().userHomeDirAsFile(), null);
		if (current != null)
		    introduceLocation(current);
		return true;
	    case '/':
		open(os.getRoot(current != null?current:environment.launchContext().userHomeDirAsFile()), null);
		if (current != null)
		    introduceLocation(current);
		return true;
	    default:
		return false;
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
	if (event == null)
	    throw new NullPointerException("event may not be null");
	switch(event.getCode())
	{
	case EnvironmentEvent.INTRODUCE:
	    return onIntroduce(event);
	case EnvironmentEvent.REFRESH:
	    refresh();
	    return true;
	case EnvironmentEvent.OK:
	    return onOk(event);
	default:
	    return false;
	}
    }

    @Override public String getName()
    {
	if (current == null)
	    return "-";
	for(Location l: importantLocations)
	    if (l.file().equals(current))
		return strings.locationTitle(l);
	return current.getAbsolutePath();
    }

    private boolean onEnter(KeyboardEvent event)
    {
	if (entries == null)
	{
	    noContentHint();
	    return true;
	}
	if (hotPointY >= entries.size())
	{
	    File[] selected = selected();
	    if (selected == null || selected.length < 1)
		return false;
	    return onClick(null, selected);
	}
	final Entry entry = entries.get(hotPointY);
	if (entry.type() == Entry.DIRECTORY)
	{
	    File parent = current.getParentFile();
	    if (entry.file().getName().equals(PARENT_DIR) && parent != null)
		open(parent, current.getName()); else
		open(entry.file(), null);
	    introduceLocation(current);
	    return true;
	}
	return onClick(entry.file(), selected());
    }

    private boolean onBackspace(KeyboardEvent event)
    {
	if (current == null)
	    return false;
	File parent = current.getParentFile();
	if (parent == null)
	    return false;
	open(parent, current.getName());
	introduceLocation(current);
	return true;
    }

    private boolean onArrowDown(KeyboardEvent event, boolean briefIntroduction)
    {
	if (entries == null)
	{
	    noContentHint();
	    return true;
	}
	if (hotPointY + 1> entries.size())
	{
	    environment.hint(Hints.NO_ITEMS_BELOW);
	    return true;
	}
	hotPointX = 0;
	++hotPointY;
	environment.onAreaNewHotPoint(this);
	if (hotPointY < entries.size())
	    introduceEntry(entries.get(hotPointY), briefIntroduction); else
	    environment.hint(Hints.EMPTY_LINE);
	return true;
    }

    private boolean onArrowUp(KeyboardEvent event, boolean briefIntroduction)
    {
	if (entries == null)
	{
	    noContentHint();
	    return true;
	}
	if (hotPointY < 1)
	{
	    environment.hint(Hints.NO_ITEMS_ABOVE);
	    return true;
	}
	hotPointX = 0;
	hotPointY--;
	environment.onAreaNewHotPoint(this);
	introduceEntry(entries.get(hotPointY), briefIntroduction);
	return true;
    }

    private boolean onArrowRight(KeyboardEvent event)
    {
	if (entries == null)
	{
	    noContentHint();
	    return true;
	}
	if (hotPointY >= entries.size())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String name = entries.get(hotPointY).file().getName();
	if (name == null)
	    return true;
	if (hotPointX >= name.length())
	{
	    environment.hint(Hints.END_OF_LINE);
	    return true;
	}
	++hotPointX;
	if (hotPointX < 0)
	    hotPointX = 0;
	environment.onAreaNewHotPoint(this);
	if (hotPointX < name.length())
	    environment.sayLetter(name.charAt(hotPointX)); else
	    environment.hint(Hints.END_OF_LINE);
	return true;
    }

    private boolean onArrowLeft(KeyboardEvent event)
    {
	if (entries == null)
	{
	    noContentHint();
	    return true;
	}
	if (hotPointY >= entries.size())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String name = entries.get(hotPointY).file().getName();
	if (name == null)
	    return false;
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
	//FIXME:
	return false;
    }

    private boolean onAltLeft(KeyboardEvent event)
    {
	//FIXME:
	return true;
    }

    private boolean onPageDown(KeyboardEvent event, boolean briefIntroduction)
    {
	if (entries == null)
	{
	    noContentHint();
	    return true;
	}
	final int visibleHeight = environment.getAreaVisibleHeight(this);
	if (visibleHeight < 1)
	    return false;
	if (hotPointY + visibleHeight > entries.size())
	    hotPointY = entries.size(); else
	    hotPointY += visibleHeight;
	hotPointX = 0;
	environment.onAreaNewHotPoint(this);
	if (hotPointY < entries.size())
	    introduceEntry(entries.get(hotPointY), briefIntroduction); else
	    environment.hint(Hints.EMPTY_LINE);
	return true;
    }

    private boolean onPageUp(KeyboardEvent event, boolean briefIntroduction)
    {
	if (entries == null)
	{
	    noContentHint();
	    return true;
	}
	final int visibleHeight = environment.getAreaVisibleHeight(this);
	if (visibleHeight < 1)
	    return false;
	if (hotPointY < visibleHeight)
	    hotPointY = 0; else
		hotPointY -= visibleHeight;
	hotPointX = 0;
	environment.onAreaNewHotPoint(this);
	if (hotPointY < entries.size())
    introduceEntry(entries.get(hotPointY), briefIntroduction); else
    environment.hint(Hints.EMPTY_LINE);
	return true;
    }

    private boolean onHome(KeyboardEvent event)
    {
	if (entries == null)
	{
	    noContentHint();
	    return true;
	}
	hotPointY = 0;
	hotPointX = 0;
	if (hotPointY < entries.size())
	    introduceEntry(entries.get(hotPointY), false); else
	    environment.hint(Hints.EMPTY_LINE);
environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAltHome(KeyboardEvent event)
    {
	if (entries == null)
	{
	    noContentHint();
	    return true;
	}
	hotPointX = 0;
	if (hotPointY >= entries.size() || entries.get(hotPointY).file().getName().isEmpty())
	    environment.hint(Hints.EMPTY_LINE); else
	    environment.sayLetter(entries.get(hotPointY).file().getName().charAt(hotPointX));
	environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onEnd(KeyboardEvent event)
    {
	if (entries == null)
	{
	    noContentHint();
	    return true;
	}
	hotPointY = entries.size();
	hotPointX = 0;
	environment.hint(Hints.EMPTY_LINE);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAltEnd(KeyboardEvent event)
    {
	if (entries == null)
	{
	    noContentHint();
	    return true;
	}
	if (hotPointY < entries.size())
	{
	    hotPointX = entries.get(hotPointY).file().getName().length();
	    environment.hint(Hints.END_OF_LINE);
	} else
	{
	    hotPointX = 0;
	    environment.hint(Hints.EMPTY_LINE);
	}
environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onInsert(KeyboardEvent event)
    {
	//FIXME:
	return false;
    }

    private boolean onOk(EnvironmentEvent event)
    {
	/*
	if (current == null || !current.isDirectory())
	    return false;
	File f = luwrain.openPopup(null, null, current);
	if (f == null)
	    return true;
	if (f.isDirectory())
	    openByFile(f); else
	    luwrain.openFile(f.getAbsolutePath());
	*/
	return true;
    }

    private boolean onIntroduce(EnvironmentEvent event)
    {
	//FIXME:
	return false;
    }

    //Doesn't produce any speech announcement;
    public void open(File file, String desiredSelected)
    {
	if (file == null)
	    throw new NullPointerException("file may not be null");
	if (!file.isDirectory())
	    throw new IllegalArgumentException("File must address a directory");
	current = file;
	entries = constructEntries(current);
	hotPointX = 0;
	hotPointY = 0;
	if (entries == null || entries.isEmpty())
	{
	    environment.onAreaNewContent(this);
	    environment.onAreaNewHotPoint(this);
	    environment.onAreaNewName(this);
	    return;
	}
	if (desiredSelected != null)
	    for(hotPointY = 0;hotPointY < entries.size();++hotPointY)
		if (entries.get(hotPointY).file().getName().equals(desiredSelected))
		    break;
	if (hotPointY >= entries.size())
	    hotPointY = 0;
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
	environment.onAreaNewName(this);
    }

    private Vector<Entry> constructEntries(File f)
    {
	if (f == null)
	    throw new NullPointerException("f may not be null");
	File[] files = f.listFiles();
	if (files == null)
	    return null;
	Vector<Entry> res = new Vector<Entry>();
	if (f.getParent() != null)
	    res.add(new Entry(new File(f, PARENT_DIR), Entry.DIRECTORY, false));
	for(File ff: files)
	    res.add(new Entry(ff, ff.isDirectory()?Entry.DIRECTORY:Entry.REGULAR, false));
	if (filter == null)
	{
	    Entry[] toSort = res.toArray(new Entry[res.size()]);
	    Arrays.sort(toSort, comparator);
	    for(int i = 0;i < toSort.length;++i)
		res.set(i, toSort[i]);
	    return res;
	}
	Vector<Entry> filtered = new Vector<Entry>();
	for (Entry ee: res)
	    if (filter.commanderEntrySuits(ee))
		filtered.add(ee);
	Entry[] toSort = filtered.toArray(new Entry[filtered.size()]);
	Arrays.sort(toSort, comparator);
	for(int i = 0;i < toSort.length;++i)
	    filtered.set(i, toSort[i]);
	return filtered;
    }

    private void noContentHint()
    {
	environment.hint("no content");
    }

    private Location[] getImportantLocations()
    {
	Vector<Location> res = new Vector<Location>();
	final Location[] l = os.getImportantLocations();
	res.add(new Location(Location.USER_HOME, environment.launchContext().userHomeDirAsFile(), environment.launchContext().userHomeDir()));
	for(Location ll: l)
	    res.add(ll);
	return res.toArray(new Location[res.size()]);
    }
}
