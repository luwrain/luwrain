/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.popups;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class FilePopup extends EditListPopup
{
    static public final int SKIP_HIDDEN = 2048;

    public interface Acceptance 
    {
	boolean pathAcceptable(Path path);
    }

    private Path defPath;
    private Acceptance acceptance;

    public FilePopup(Luwrain luwrain, String name,
		     String prefix, Acceptance acceptance,
		     Path path, Path defPath,
		     int popupFlags)
    {
	super(luwrain, new Model(defPath, (popupFlags & SKIP_HIDDEN) != 0), 
name, prefix, Model.getPathWithTrailingSlash(path), popupFlags);
	//	this.path = path;
	this.defPath = defPath;
	this.acceptance = acceptance;
	//	NullCheck.notNull(file, "file");
	NullCheck.notNull(defPath, "defPath");
    }

    public Path result()
    {
	final Path res = Paths.get(text());
	if (res.isAbsolute())
	    return res;
	return defPath.resolve(res);
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isCommand() && event.withShiftOnly())
	    switch(event.getCommand())
	    {
	    case 	    KeyboardEvent.ENTER:
		return openCommanderPopup();
	    }
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onOk()
    {
	if (acceptance == null)
	    return true;
	return acceptance.pathAcceptable(result());
    }

    private boolean openCommanderPopup()
    {
	File file = result().toFile();
	if (file == null)
	    return false;
	if (!file.isDirectory())
	    file = file.getParentFile();
	if (file == null || !file.isDirectory())
	    return false;
	final Path res = Popups.commanderSingle(luwrain, getAreaName() + ": ", file.toPath(), CommanderPopup.ACCEPT_ALL, 0);
	if (res != null)
	    setText(res.toAbsolutePath().toString(), "");
	return true;
    }

    static private class Model extends DynamicEditListPopupModel
    {
	private Path defPath;
	private boolean skipHidden = false;

	Model(Path defPath, boolean skipHidden)
	{
	    this.defPath = defPath;
	    this.skipHidden = skipHidden;
	    NullCheck.notNull(defPath, "defPath");
	}

	@Override protected EditListPopupItem[] getItems(String context)
	{
	    Path path = null;
	    Path base = null;
	    final String from = context != null?context:"";
	    final Path fromPath = Paths.get(from);
	    final boolean hadTrailingSlash = from.endsWith(separator());
	    if (!from.isEmpty() && fromPath.isAbsolute())
	    {
		base = null;
		path = fromPath;
	    } else
		if (from.isEmpty())
		{
		    base = defPath;
		    path = defPath;
		} else
		{
		    base = defPath;
		    path = defPath.resolve(fromPath);
		}
	    if (!from.isEmpty() && !hadTrailingSlash)
		path = path.getParent();
	    if (!Files.exists(path) || !Files.isDirectory(path))
		return new EditListPopupItem[0];
	    final LinkedList<EditListPopupItem> items = new LinkedList<EditListPopupItem>();
	    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
		    for (Path pp : directoryStream) 
			if (!skipHidden || !Files.isHidden(pp))
			{
			if (base != null)
			    items.add(new EditListPopupItem(base.relativize(pp).toString(), pp.getFileName().toString())); else
			    items.add(new EditListPopupItem(pp.toString(), pp.getFileName().toString()));
			}
		} 
	    catch (IOException e) 
	    {
		e.printStackTrace();
		return new EditListPopupItem[0];
	    }
	    final EditListPopupItem[] res = items.toArray(new EditListPopupItem[items.size()]);
	    Arrays.sort(res);
	    return res;
	}

	@Override protected EditListPopupItem getEmptyItem(String context)
	{
	    if (context == null || context.isEmpty())
		return new EditListPopupItem();
	    Path base = null;
	    Path path = Paths.get(context);
	    if (!path.isAbsolute())
	    {
		base = defPath;
		path = base.resolve(path);
	    }
	    if (context.endsWith(separator()) && Files.exists(path) && Files.isDirectory(path))
		return new EditListPopupItem(context);
	    path = path.getParent();
	    if (path != null)
	    {
		String suffix = "";
		//We don't want double slash in root designation and at the top of relative enumeration
		if (Files.exists(path) && Files.isDirectory(path) && 
		    !path.equals(path.getRoot()) &&
		    (base == null || !base.equals(path)))
		    suffix = separator();
		if (base != null)
		    return new EditListPopupItem(base.relativize(path).toString() + suffix);
		return new EditListPopupItem(path.toString() + suffix);
	    }
	    return new EditListPopupItem(context);
	}

	@Override public String getCompletion(String beginning)
	{
	    final String res = super.getCompletion(beginning);
	    final String path = beginning + (res != null?res:"");
	    if (!path.isEmpty() && path.endsWith(separator()))
		return res;
	    final Path pp = Paths.get(path);
	    if (Files.exists(pp) && Files.isDirectory(pp))
		return res + separator();
	    return res;
	}

	static String getPathWithTrailingSlash(Path p)
	{
	    NullCheck.notNull(p, "p");
	    if (Files.exists(p) && Files.isDirectory(p))
		return p.toString() + separator();
	    return p.toString();
	}

	static private String separator()
	{
	    return FileSystems.getDefault().getSeparator();
	}
    }
}
