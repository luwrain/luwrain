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

package org.luwrain.popups;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class FilePopup extends EditListPopup
{
    public enum Flags { SKIP_HIDDEN };
    static final String SEPARATOR = System.getProperty("file.separator");

    public interface Acceptance 
    {
	boolean isPathAcceptable(File file, boolean announce);
    }

    protected final File defaultDir;
    protected final Acceptance acceptance;

    public FilePopup(Luwrain luwrain, String name, String prefix,
		     Acceptance acceptance, File startFrom, File defaultDir,
		     Set<Flags> flags, Set<Popup.Flags> popupFlags)
    {
	super(luwrain,
	      new Model(defaultDir.toPath(), flags .contains(Flags.SKIP_HIDDEN)), 
	      name, prefix, getPathWithEndingSeparator(startFrom), popupFlags);
	this.defaultDir = defaultDir;
	this.acceptance = acceptance;
	if (!defaultDir.isDirectory())
	    throw new IllegalArgumentException("" + defaultDir.toString() + " must address a directory");
    }

    public File result()
    {
	final File res = new File(text());
	if (res.isAbsolute())
	    return res;
	return new File(defaultDir, text());
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != EnvironmentEvent.Type.REGULAR)
	    return super.onEnvironmentEvent(event);
	switch(event.getCode())
	{
	case PROPERTIES:
	    return openCommanderPopup();
	default:
	    return super.onEnvironmentEvent(event);
	}
    }

    @Override public boolean onOk()
    {
	if (result() == null)
	    return false;
	return acceptance != null?acceptance.isPathAcceptable(result(), true):true;
    }

    protected boolean openCommanderPopup()
    {
	Path path = result().toPath();
	if (path == null)
	    return false;
	if (!Files.isDirectory(path))
	    path = path.getParent();
	if (path == null || !Files.isDirectory(path))
	    return false;
	final File res = Popups.commanderSingle(luwrain, getAreaName(), path.toFile(), popupFlags);
	if (res != null)
	    setText(res.getAbsolutePath(), "");
	return true;
    }

    static String getPathWithEndingSeparator(File file)
    {
	NullCheck.notNull(file, "file");
	final String str = file.toString();
	//Checking if there is nothing to do
	if (str.endsWith(SEPARATOR))
	    return str;
	if (file.exists() && file.isDirectory())
	    return str + SEPARATOR;
	return str;
    }

    static protected class Model extends EditListPopupUtils.DynamicModel
    {
	protected final Path defPath;
	protected final boolean skipHidden;

	Model(Path defPath, boolean skipHidden)
	{
	    NullCheck.notNull(defPath, "defPath");
	    this.defPath = defPath;
	    if (!defPath.isAbsolute())
		throw new IllegalArgumentException("defPath must be absolute");
	    this.skipHidden = skipHidden;
	}

	@Override protected EditListPopup.Item[] getItems(String context)
	{
	    NullCheck.notNull(context, "context");
	    if (context.isEmpty())
		return readDirectory(defPath, defPath);
	    final Path contextPath = Paths.get(context);
	    NullCheck.notNull(contextPath, "contextPath");
	    final Path base;
	    Path path;
	    if (contextPath.isAbsolute())
	    {
		base = null;
		path = contextPath;
	    } else
	    {
		base = defPath;
		path = defPath.resolve(contextPath);
	    }
	    if (!context.endsWith(SEPARATOR) && path.getParent() != null)
		path = path.getParent();
	    if (!Files.exists(path) || !Files.isDirectory(path))
		return new Item[0];
	    return readDirectory(path, base);
	}

	@Override protected EditListPopup.Item getEmptyItem(String context)
	{
	    NullCheck.notNull(context, "context");
	    if (context.isEmpty())
		return new EditListPopup.Item();
	    Path base = null;
	    Path path = Paths.get(context);
	    if (!path.isAbsolute())
	    {
		base = defPath;
		path = defPath.resolve(path);
	    }
	    if (context.endsWith(SEPARATOR) && Files.exists(path) && Files.isDirectory(path))
		return new EditListPopup.Item(context);
	    path = path.getParent();
	    if (path != null)
	    {
		String suffix = "";
		//We don't want double slash in root designation and at the top of relative enumeration
		if (Files.exists(path) && Files.isDirectory(path) && 
		    !path.equals(path.getRoot()) &&
		    (base == null || !base.equals(path)))
		    suffix = SEPARATOR;
		if (base != null)
		    return new EditListPopup.Item(base.relativize(path).toString() + suffix);
		return new EditListPopup.Item(path.toString() + suffix);
	    }
	    return new EditListPopup.Item(context);
	}

	//Just adds ending slash, if necessary
	@Override public String getCompletion(String beginning)
	{
	    final String res = super.getCompletion(beginning);
	    NullCheck.notNull(res, "res");
	    final String path = beginning + res;
					     //We already have the slash, doing nothing
	    if (!path.isEmpty() && path.endsWith(SEPARATOR))
		return res;
	    Path pp = Paths.get(path);
	    if (!pp.isAbsolute())
		pp = defPath.resolve(pp);
					     final boolean withSlash;
	    if (!Files.exists(pp) || !Files.isDirectory(pp))
		withSlash = false; else
		withSlash = true;
	    if (withSlash && !hasWithSameBeginningNearby(pp))
		return res + SEPARATOR;
	    return res;
	}

	protected Item[] readDirectory(Path path, Path base)
	{
	    NullCheck.notNull(path, "path");
	    final LinkedList<Item> items = new LinkedList<Item>();
	    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
		    for (Path pp : directoryStream) 
			if (!skipHidden || !Files.isHidden(pp))
			{
			    if (base != null)
				items.add(new Item(base.relativize(pp).toString(), pp.getFileName().toString())); else
				items.add(new Item(pp.toString(), pp.getFileName().toString()));
			}
		    final Item[] res = items.toArray(new Item[items.size()]);
		    Arrays.sort(res);
		    return res;
		}
	    catch (IOException e) 
	    {
		Log.error("core", "unable to read content of " + path.toString() + ":" + e.getClass().getName() + ":" + e.getMessage());
		e.printStackTrace();
		return new Item[0];
	    }
	}

	protected boolean hasWithSameBeginningNearby(Path path)
	{
	    NullCheck.notNull(path, "path");
	    final Path parent = path.getParent();
	    if (parent == null)
		return false;
	    final String fileName = path.getFileName().toString();
	    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(parent)) {
		    for (Path pp : directoryStream) 
			if (!skipHidden || !Files.isHidden(pp))
			{
			    final Path f = pp.getFileName();
			    if (f == null)
				continue;
			    final String name = f.toString();
			    if (name.length() > fileName.length() && name.startsWith(fileName))
				return true;
			}
		    return false;
	}
	    catch (IOException e) 
	    {
		Log.error("core", "unable to read content of " + path.toString() + ":" + e.getClass().getName() + ":" + e.getMessage());
		e.printStackTrace();
		return false;
	    }
	}
    }
}
