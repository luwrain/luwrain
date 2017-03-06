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

package org.luwrain.io;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.controls.NgCommanderArea.EntryType;

public class CommanderUtilsFile
{
    static private final String LOG_COMPONENT = "commander-file";

    static public class Model implements NgCommanderArea.Model<java.io.File>
    {
	@Override public EntryType getEntryType(File currentLocation, File entry)
	{
	    NullCheck.notNull(entry, "entry");
		if (currentLocation.getParent() != null && currentLocation.getParent().equals(entry))
		    return EntryType.PARENT;
		final Path path = entry.toPath();
		    if (Files.isSymbolicLink(path))
			return Files.isDirectory(path)?EntryType.SYMLINK_DIR:EntryType.SYMLINK;
		    if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))
			return EntryType.DIR;
		    if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
			return EntryType.REGULAR;
		    return EntryType.SPECIAL;
	    }

	@Override public File[] getEntryChildren(File entry)
	{
	    NullCheck.notNull(entry, "entry");
	    try {
		final File[] children = entry.listFiles();
		final File parent = entry.getParentFile();
		if (parent == null)
		    return children;
		final LinkedList<File> res = new LinkedList<File>();
		res.add(parent);
		for(File f: children)
		    res.add(f);
		return res.toArray(new File[res.size()]);
	    }
	    catch(Throwable e)
	    {
		Log.error(LOG_COMPONENT, "unable to get children of " + entry + ":" + e.getClass().getName() + ":" + e.getMessage());
		return null;
	    }
	}

	@Override public File getEntryParent(File entry)
	{
	    NullCheck.notNull(entry, "entry");
		return entry.getParentFile();
	}
    }

    static public class Appearance implements NgCommanderArea.Appearance<File>
    {
	protected final ControlEnvironment environment;

	public Appearance(ControlEnvironment environment)
	{
	    NullCheck.notNull(environment, "environment");
	    this.environment = environment;
	}

	@Override public String getCommanderName(File entry)
	{
	    NullCheck.notNull(entry, "entry");
	    return entry.getName();
	}

	@Override public void announceLocation(File entry)
	{
	    NullCheck.notNull(entry, "entry");
	    environment.silence();
	    environment.playSound(Sounds.COMMANDER_LOCATION);
	    if (entry.getAbsolutePath().equals("/"))
		environment.say(environment.getStaticStr("PartitionsPopupItemRoot")); else
		environment.say(entry.getName());
	}

	@Override public String getEntryTextAppearance(File entry, EntryType type, boolean marked)
	{
	    NullCheck.notNull(entry, "entry");
	    //type may be null
	    if (type != null  && type == EntryType.PARENT)
		return "..";
	    return entry.getName();
	}

	@Override public void announceEntry(File entry, NgCommanderArea.EntryType type, boolean marked)
	{
	    NullCheck.notNull(entry, "entry");
	    NullCheck.notNull(type, "type");
	    final String name = entry.getName();
	    if (name.trim().isEmpty() && type != EntryType.PARENT)
	    {
		environment.hint(Hints.EMPTY_LINE);
		return;
	    }
	    final StringBuilder b = new StringBuilder();
	    if (marked)
		b.append(environment.getStaticStr("CommanderSelected") + " ");
	    b.append(name);
	    switch(type)
	    {
	    case PARENT:
		environment.hint(environment.getStaticStr("CommanderParentDirectory"));
		return;
	    case DIR:
		b.append(environment.getStaticStr("CommanderDirectory"));
		break;
	    case SYMLINK:
	    case SYMLINK_DIR:
		b.append(environment.getStaticStr("CommanderSymlink"));
		break;
	    case SPECIAL:
		b.append(environment.getStaticStr("CommanderSpecial"));
		break;
	    }
	    environment.playSound(Sounds.LIST_ITEM);
	    environment.say(new String(b));
	}
    }

    static public class ByNameComparator implements java.util.Comparator
    {
	@Override public int compare(Object o1, Object o2)
	{
	    if (!(o1 instanceof NgCommanderArea.Wrapper) || !(o2 instanceof NgCommanderArea.Wrapper))
		return 0;
	    final NgCommanderArea.Wrapper w1 = (NgCommanderArea.Wrapper)o1;
	    final NgCommanderArea.Wrapper w2 = (NgCommanderArea.Wrapper)o2;
	    if (w1.type == EntryType.PARENT)
		return w2.type == EntryType.PARENT?0:-1;
	    if (w2.type == EntryType.PARENT)
		return w1.type == EntryType.PARENT?0:1;
	    final String name1 = ((File)w1.obj).getName().toLowerCase();
	    final String name2 = ((File)w2.obj).getName().toLowerCase();
	    if (w1.isDirectory() && w2.isDirectory())
		return name1.compareTo(name2);
	    if (w1.isDirectory())
		return -1;
	    if (w2.isDirectory())
		return 1;
	    return name1.compareTo(name2);
	}
    }

    static public class AllEntriesFilter implements NgCommanderArea.Filter<File>
    {
	@Override public boolean commanderEntrySuits(File entry)
	{
	    return true;
	}
    }

    static public NgCommanderArea.Params<File> createParams(ControlEnvironment environment)
    {
	NullCheck.notNull(environment, "environment");
	final NgCommanderArea.Params<File> params = new NgCommanderArea.Params<File>();
	params.environment = environment;
	params.model = new Model();
	params.appearance = new Appearance(environment);
	params.filter = new AllEntriesFilter();
	params.comparator = new ByNameComparator();
	return params;
    }

    static public File prepareLocation(String path)
    {
	NullCheck.notEmpty(path, "path");
	return new File(path);
    }
}
