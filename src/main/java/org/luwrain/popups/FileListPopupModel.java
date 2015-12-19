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
import java.nio.file.attribute.*;

import org.luwrain.core.NullCheck;

class FileListPopupModel extends DynamicListPopupModel
{
    private Path defPath;

    FileListPopupModel(Path defPath)
    {
	this.defPath = defPath;
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
	//	System.out.println("base=" + base.toString());
	//	System.out.println(base.getNameCount());
	//	System.out.println("path=" + path.toString());
	if (!from.isEmpty() && !hadTrailingSlash)
		path = path.getParent();
	if (!Files.exists(path) || !Files.isDirectory(path))
	    return new EditListPopupItem[0];
	final LinkedList<EditListPopupItem> items = new LinkedList<EditListPopupItem>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
		for (Path pp : directoryStream) 
		    if (base != null)
		    items.add(new EditListPopupItem(base.relativize(pp).toString(), pp.getFileName().toString())); else
		    items.add(new EditListPopupItem(pp.toString(), pp.getFileName().toString()));
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
	    if (Files.exists(path) && Files.isDirectory(path))
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
