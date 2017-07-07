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
//import java.nio.file.*;

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
	      new Model(defaultDir, flags .contains(Flags.SKIP_HIDDEN)), 
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
File file = result();
	if (file == null)
	    return false;
	if (!file.isDirectory())
	    file = file.getParentFile();
	if (file == null || !file.isDirectory())
	    return false;
	final File res = Popups.commanderSingle(luwrain, getAreaName(), file, popupFlags);
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
	protected final File defPath;
	protected final boolean skipHidden;

	Model(File defPath, boolean skipHidden)
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
	    final File contextPath = new File(context);
	    NullCheck.notNull(contextPath, "contextPath");
	    final File base;
	    File path;
	    if (contextPath.isAbsolute())
	    {
		base = null;
		path = contextPath;
	    } else
	    {
		base = defPath;
		path = new File(defPath, contextPath.toString());
	    }
	    if (!context.endsWith(SEPARATOR) && path.getParent() != null)
		path = path.getParentFile();
	    if (!path.exists() || !path.isDirectory())
		return new Item[0];
	    return readDirectory(path, base);
	}

	@Override protected EditListPopup.Item getEmptyItem(String context)
	{
	    NullCheck.notNull(context, "context");
	    if (context.isEmpty())
		return new EditListPopup.Item();
	    File base = null;
	    File path = new File(context);
	    if (!path.isAbsolute())
	    {
		base = defPath;
		path = new File(defPath, path.toString());
	    }
	    if (context.endsWith(SEPARATOR) &&
path.exists() && path.isDirectory())
		return new EditListPopup.Item(context);
	    path = path.getParentFile();
	    if (path != null)
	    {
		String suffix = "";
		//We don't want double slash in root designation and at the top of relative enumeration
		if (path.exists() && path.isDirectory() && 
		    !isRoot(path) &&
		    (base == null || !base.equals(path)))
		    suffix = SEPARATOR;
		if (base != null)
		    return new EditListPopup.Item(relativize(path, base) + suffix);
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
	    File pp = new File(path);
	    if (!pp.isAbsolute())
		pp = new File(defPath, pp.toString());
					     final boolean withSlash;
	    if (!pp.exists() || !pp.isDirectory())
		withSlash = false; else
		withSlash = true;
	    if (withSlash && !hasWithSameBeginningNearby(pp))
		return res + SEPARATOR;
	    return res;
	}

protected Item[] readDirectory(File dir, File base)
	{
	    NullCheck.notNull(dir, "dir");
	    final LinkedList<Item> items = new LinkedList<Item>();
	    for (File pp: dir.listFiles())
			if (!skipHidden || !pp.isHidden())
			{
			    if (base != null)
				items.add(new Item(relativize(pp, base), pp.getName())); else
				items.add(new Item(pp.toString(), pp.getName()));
			}
		    final Item[] res = items.toArray(new Item[items.size()]);
		    Arrays.sort(res);
		    return res;
		}

	protected boolean hasWithSameBeginningNearby(File path)
	{
	    NullCheck.notNull(path, "path");
	    final File parent = path.getParentFile();
	    if (parent == null)
		return false;
	    final String fileName = path.getName();
	    for (File pp: parent.listFiles())
			if (!skipHidden || !pp.isHidden())
			{
			    final String name = pp.getName();
			    if (name.isEmpty())
				continue;
			    if (name.length() > fileName.length() && name.startsWith(fileName))
				return true;
			}
		    return false;
	}

	static protected String relativize(File file, File base)
	{
	    NullCheck.notNull(file, "file");
	    final String itemStr = file.toString();
	    final String baseStr = base.toString();
	    if (itemStr.startsWith(baseStr + SEPARATOR))
		return itemStr.substring(baseStr.length() + 1);
	    return itemStr;
	}

    static protected boolean isRoot(File file)
    {
	NullCheck.notNull(file, "file");
	for(File root: File.listRoots())
	    if (file.equals(root))
		return true;
	return false;
    }
}
}
