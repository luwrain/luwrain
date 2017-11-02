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
import org.luwrain.controls.CommanderArea.EntryType;

public class CommanderUtilsFile
{
    static private final String LOG_COMPONENT = "commander-file";

    static public class Model implements CommanderArea.Model<java.io.File>
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

    static public class Appearance implements CommanderArea.Appearance<File>
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

	@Override public String getEntryText(File entry, EntryType type, boolean marked)
	{
	    NullCheck.notNull(entry, "entry");
	    //type may be null
	    if (type != null  && type == EntryType.PARENT)
		return "..";
	    return entry.getName();
	}

	@Override public void announceEntry(File entry, CommanderArea.EntryType type, boolean marked)
	{
	    NullCheck.notNull(entry, "entry");
	    NullCheck.notNull(type, "type");
	    final String name = entry.getName();
	    CommanderUtils.defaultEntryAnnouncement(environment, name, type, marked);
    }
    }

    static public CommanderArea.Params<File> createParams(ControlEnvironment environment)
    {
	NullCheck.notNull(environment, "environment");
	final CommanderArea.Params<File> params = new CommanderArea.Params<File>();
	params.environment = environment;
	params.model = new Model();
	params.appearance = new Appearance(environment);
	params.filter = new CommanderUtils.AllEntriesFilter();
	params.comparator = new CommanderUtils.ByNameComparator();
	return params;
    }

    static public File prepareLocation(String path)
    {
	NullCheck.notEmpty(path, "path");
	return new File(path);
    }
}
