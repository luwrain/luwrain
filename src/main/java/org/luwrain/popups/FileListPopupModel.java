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

package org.luwrain.popups;

import java.util.*;
import java.io.*;

class FileListPopupModel extends DynamicListPopupModel
{
    protected EditListPopupItem[] getItems(String context)
    {
	if (context == null || context.isEmpty())
	    return new EditListPopupItem[0];
	File current = new File(context);
	if (context.charAt(context.length() - 1) == pathDelimiter() && current.exists() && current.isDirectory())
	{
	    File[] items = null;
	    try {
		items = current.listFiles();
	    }
	    catch (SecurityException e)
	    {
		e.printStackTrace();
	    }
	    if (items != null && items.length > 0)
	    {
		EditListPopupItem[] res = new EditListPopupItem[items.length];
		for(int i = 0;i < items.length;++i)
		    res[i] = new EditListPopupItem(items[i].getAbsolutePath(), items[i].getName());
		Arrays.sort(res);
		return res;
	    }
	}
	File parent = current.getParentFile();
	if (parent == null || !parent.exists())
	    return new EditListPopupItem[0];
	File[] items = null;
	try {
	    items = parent.listFiles();
	}
	catch (SecurityException e)
	{
	    e.printStackTrace();
	}
	if (items != null && items.length > 0)
	{
	    EditListPopupItem[] res = new EditListPopupItem[items.length];
	    for(int i = 0;i < items.length;++i)
		res[i] = new EditListPopupItem(items[i].getAbsolutePath(), items[i].getName());
	    Arrays.sort(res);
	    return res;
	}
	return new EditListPopupItem[0];
    }

    protected EditListPopupItem getEmptyItem(String context)
    {
	if (context == null || context.isEmpty())
	    return new EditListPopupItem();
	File current = new File(context);
	if (context.charAt(context.length() - 1) == pathDelimiter() && current.exists() && current.isDirectory())
	    return new EditListPopupItem(context);
	File parent = current.getParentFile();
	if (parent != null)
	    return new EditListPopupItem(parent.getAbsolutePath() + pathDelimiter());
	return new EditListPopupItem(context);
    }

    private char pathDelimiter()
    {
	return '/';
    }

    //Just to add slashes to directories;
    @Override public String getCompletion(String beginning)
    {
	final String res = super.getCompletion(beginning);
	final String path = beginning + (res != null?res:"");
	if (!path.isEmpty() && path.charAt(path.length() - 1) == pathDelimiter())
	    return res;
	if (new File(path).isDirectory())
	    return res + pathDelimiter(); else
	    return res;
    }
}
