/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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
    protected String[] getItems(String context)
    {
	if (context == null || context.isEmpty())
	    return new String[0];
	File current = new File(context);
	if (context.charAt(context.length() - 1) == '/' && current.exists() && current.isDirectory())//FIXME:System dependent slash;
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
		String[] res = new String[items.length];
		for(int i = 0;i < items.length;++i)
		    res[i] = items[i].getAbsolutePath();
		Arrays.sort(res);
		return res;
	    }
	}
	File parent = current.getParentFile();
	if (parent == null || !parent.exists())
	    return new String[0];
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
	    String[] res = new String[items.length];
	    for(int i = 0;i < items.length;++i)
		res[i] = items[i].getAbsolutePath();
	    Arrays.sort(res);
	    return res;
	}
	return new String[0];
    }

    protected String getEmptyItem(String context)
    {
	if (context == null || context.isEmpty())
	    return "";
	File current = new File(context);
	if (context.charAt(context.length() - 1) == '/' && current.exists() && current.isDirectory())//FIXME:System dependent slash;
	    return context;
	File parent = current.getParentFile();
	if (parent != null)
	    return parent.getAbsolutePath() + "/";//FIXME:System dependent slash;
	return context;
    }
}
