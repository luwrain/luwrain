/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.io;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.controls.CommanderArea.EntryType;

public final class CommanderUtilsFile
{
    static private final String LOG_COMPONENT = "core";

    static public class Model implements CommanderArea.Model<java.io.File>
    {
	@Override public EntryType getEntryType(File currentLocation, File entry)
	{
	    NullCheck.notNull(entry, "entry");
			if (currentLocation.getParentFile() != null && entry.getAbsolutePath().equals(currentLocation.getParentFile().getAbsolutePath()))
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
	protected final ControlContext context;
	public Appearance(ControlContext context)
	{
	    NullCheck.notNull(context, "context");
	    this.context = context;
	}
	@Override public String getCommanderName(File entry)
	{
	    NullCheck.notNull(entry, "entry");
	    return entry.getName();
	}
	@Override public void announceLocation(File entry)
	{
	    NullCheck.notNull(entry, "entry");
	    if (entry.getAbsolutePath().equals("/"))//FIXME: Windows style
		context.say(context.getStaticStr("CommanderRoot"), Sounds.COMMANDER_LOCATION); else
		context.say(context.getSpeakableText(entry.getName(), Luwrain.SpeakableTextType.PROGRAMMING), Sounds.COMMANDER_LOCATION);
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
	    CommanderUtils.defaultEntryAnnouncement(context, name, type, marked);
    }
    }

static     public class Filter implements CommanderArea.Filter<File>
    {
	public enum Flags {NO_HIDDEN, NO_NON_DIREXCLUDE_HIDDEN, DIR_ONLY};

	private final Set<Flags> flags;

	public Filter()
	{
	    this.flags = EnumSet.noneOf(Flags.class);
	}

	public Filter(Set<Flags> flags)
	{
	    NullCheck.notNull(flags, "flags");
	    this.flags = flags;
	}

		@Override public boolean commanderEntrySuits(File file)
	{
	    NullCheck.notNull(file, "file");
	    if (flags.contains(Flags.NO_HIDDEN) && file.isHidden())
		return false;
	    if (flags.contains(Flags.DIR_ONLY) && file.isFile())
		return false;
	    return true;
	    	}
    }

    static public CommanderArea.Params<File> createParams(ControlContext context)
    {
	NullCheck.notNull(context, "context");
	final CommanderArea.Params<File> params = new CommanderArea.Params<File>();
	params.context = context;
	params.model = new Model();
	params.appearance = new Appearance(context);
	params.filter = new CommanderUtils.AllEntriesFilter<>();
	params.comparator = new CommanderUtils.ByNameComparator<>();
	return params;
    }

    static public File prepareLocation(String path)
    {
	NullCheck.notEmpty(path, "path");
	return new File(path);
    }
}
