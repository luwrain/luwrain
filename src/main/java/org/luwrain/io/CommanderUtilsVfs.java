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
import java.nio.file.*;

import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.auth.*;
import org.apache.commons.vfs2.impl.*;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.controls.CommanderArea.EntryType;

public class CommanderUtilsVfs
{
    static private final String LOG_COMPONENT = "commander-vfs";

    static public class Model implements CommanderArea.Model<FileObject>
    {
	protected final FileSystemManager manager;

	public Model(FileSystemManager manager)
	{
	    NullCheck.notNull(manager, "manager");
	    this.manager = manager;
	}

	public FileSystemManager getFileSystemManager()
	{
	    return manager;
	}

	@Override public EntryType getEntryType(FileObject currentLocation, FileObject entry)
	{
	    NullCheck.notNull(entry, "entry");
	    try {
		if (currentLocation.getParent() != null && currentLocation.getParent().equals(entry))
		    return EntryType.PARENT;
		if (entry instanceof org.apache.commons.vfs2.provider.local.LocalFile)
		{
		    final Path path = Paths.get(entry.getName().getPath());
		    if (Files.isSymbolicLink(path))
			return Files.isDirectory(path)?EntryType.SYMLINK_DIR:EntryType.SYMLINK;
		    if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))
			return EntryType.DIR;
		    if (Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
			return EntryType.REGULAR;
		    return EntryType.SPECIAL;
		}
		return entry.getType().hasChildren()?EntryType.DIR:EntryType.REGULAR;
	    }
	    catch(org.apache.commons.vfs2.FileSystemException e)
	    {
		Log.error("vfs", "unable to get type of " + entry.toString() + ":" + e.getClass().getName() + ":" + e.getMessage());
		return EntryType.REGULAR;
	    }
	}

	@Override public FileObject[] getEntryChildren(FileObject entry)
	{
	    NullCheck.notNull(entry, "entry");
	    try {
		entry.refresh();
		final FileObject[]children = entry.getChildren();
		final FileObject parent = entry.getParent();
		if (parent == null)
		    return children;
		final LinkedList<FileObject> res = new LinkedList<FileObject>();
		res.add(parent);
		for(FileObject f: children)
		    res.add(f);
		return res.toArray(new FileObject[res.size()]);
	    }
	    catch(Throwable e)
	    {
		Log.error(LOG_COMPONENT, "unable to get children of " + entry + ":" + e.getClass().getName() + ":" + e.getMessage());
		return null;
	    }
	}

	@Override public FileObject getEntryParent(FileObject entry)
	{
	    NullCheck.notNull(entry, "entry");
	    try {
		return entry.getParent();
	    }
	    catch(org.apache.commons.vfs2.FileSystemException e)
	    {
		Log.error(LOG_COMPONENT, "unable to get parent of " + entry + ":" + e.getClass().getName() + ":" + e.getMessage());
		return null;
	    }
	}
    }

    static public class Appearance implements CommanderArea.Appearance<FileObject>
    {
	protected final ControlEnvironment environment;
	protected final FileSystemManager manager;

	public Appearance(ControlEnvironment environment, FileSystemManager manager)
	{
	    NullCheck.notNull(environment, "environment");
	    NullCheck.notNull(manager, "manager");
	    this.environment = environment;
	    this.manager = manager;
	}

	@Override public String getCommanderName(FileObject entry)
	{
	    NullCheck.notNull(entry, "entry");
	    return entry.getName().getPath();
	}

	@Override public void announceLocation(FileObject entry)
	{
	    NullCheck.notNull(entry, "entry");
	    environment.silence();
	    environment.playSound(Sounds.COMMANDER_LOCATION);
	    if (entry.getName().getPath().equals("/"))
		environment.say(environment.getStaticStr("PartitionsPopupItemRoot")); else
		environment.say(entry.getName().getBaseName());
	}

	@Override public String getEntryText(FileObject entry, EntryType type, boolean marked)
	{
	    NullCheck.notNull(entry, "entry");
	    //type may be null
	    if (type != null  && type == EntryType.PARENT)
		return "..";
	    return entry.getName().getBaseName();
	}

	@Override public void announceEntry(FileObject entry, CommanderArea.EntryType type, boolean marked)
	{
	    NullCheck.notNull(entry, "entry");
	    NullCheck.notNull(type, "type");
	    final String name = entry.getName().getBaseName();
	    CommanderUtils.defaultEntryAnnouncement(environment, name, type, marked);
	}
    }

    static public class NoHiddenFilter implements CommanderArea.Filter<FileObject>
    {
	@Override public boolean commanderEntrySuits(FileObject entry)
	{
	    NullCheck.notNull(entry, "entry");
	    try {
	    return !entry.isHidden();
	    }
	    catch(java.io.IOException e)
	    {
		Log.error(LOG_COMPONENT, "Unable to get attributes of " + entry.toString() + ":" + e.getClass().getName() + ":" + e.getMessage());
		return true;
	    }
	}
    }


    static public CommanderArea.Params<FileObject> createParams(ControlEnvironment environment) throws org.apache.commons.vfs2.FileSystemException
    {
	NullCheck.notNull(environment, "environment");
	final CommanderArea.Params<FileObject> params = new CommanderArea.Params<FileObject>();
	final FileSystemManager manager = VFS.getManager();
	params.environment = environment;
	params.model = new Model(manager);
	params.appearance = new Appearance(environment, manager);
	params.filter = new CommanderUtils.AllEntriesFilter();
	params.comparator = new CommanderUtils.ByNameComparator();
	return params;
    }

    static public FileObject prepareLocation(Model model, String path) throws org.apache.commons.vfs2.FileSystemException
    {
	NullCheck.notNull(model, "model");
	NullCheck.notEmpty(path, "path");
	FileSystemOptions opts = new FileSystemOptions();                                                                              
	FtpFileSystemConfigBuilder.getInstance().setPassiveMode(opts, true); 
	FtpFileSystemConfigBuilder.getInstance( ).setUserDirIsRoot(opts,true);                                                                            
	return model.getFileSystemManager().resolveFile(path, opts);
    }
}
