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

package org.luwrain.desktop;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.util.RegistryAutoCheck;
import org.luwrain.util.RegistryPath;

class Model implements ListModel
{
    private Luwrain luwrain;
    private Registry registry;
    private RegistryKeys registryKeys = new RegistryKeys();
    private Object[] items;
    private String[] introduction;

    public Model(Luwrain luwrain)
    {
	this.luwrain = luwrain;
	if (luwrain == null)
	    throw new NullPointerException("registry may not be null");
	this.registry = luwrain.getRegistry();
    }

    @Override public int getItemCount()
    {
	return items != null?items.length:0;
    }

    @Override public Object getItem(int index)
    {
	if (items == null)
	    return null;
	return index < items.length?items[index]:null;
    }

    @Override public void refresh()
    {
	final RegistryAutoCheck check = new RegistryAutoCheck(registry);
	final String[] values = registry.getValues(registryKeys.desktopUniRefs());
	final LinkedList<Object> res = new LinkedList<Object>();
	if (introduction != null)
	    for(String s: introduction)
		res.add(s);
	for(String v: values)
	{
	    final String s = check.stringAny(RegistryPath.join(registryKeys.desktopUniRefs(), v), "");
	    if (s.isEmpty())
		continue;
	    final UniRefInfo uniRefInfo = luwrain.getUniRefInfo(s);
	    res.add(uniRefInfo != null?uniRefInfo:s);
	}
	items = res.toArray(new Object[res.size()]);
    }

    @Override public boolean toggleMark(int index)
    {
	return false;
    }

    public void readIntroduction(String fileName)
    {
	try {
	    LinkedList<String> a = new LinkedList<String>();
	Path path = Paths.get(fileName);
	try (Scanner scanner =  new Scanner(path, StandardCharsets.UTF_8.name()))
	{
	    while (scanner.hasNextLine())
		a.add(scanner.nextLine());
	    }
introduction = a.toArray(new String[a.size()]);
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	    introduction = null;
	}
    }
}
