/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.app.registry;

import java.util.*;

import org.luwrain.controls.ListArea;
import org.luwrain.core.Registry;

class ValuesListModel implements ListArea.Model
{
    private Registry registry;
    private String dirPath = null;
    private Value[] values = new Value[0];

    public ValuesListModel(Registry registry)
    {
	this.registry = registry;
	if (registry == null)
	    throw new NullPointerException("registry may not be null");
    }

    public void openDirectory(String path)
    {
	if (path == null || path.isEmpty())
	    return;
	dirPath = path;
	refresh();
    }

    @Override public int getItemCount()
    {
	return values != null?values.length:0;
    }

    @Override public Object getItem(int index)
    {
	if (values == null)
	    return null;
	return index < values.length?values[index]:null;
    }

    @Override public void refresh()
    {
	if (dirPath == null || dirPath.isEmpty())
	{
	    values = new Value[0];
	    return;
	}
	final String[] names = registry.getValues(dirPath);
	if (names == null || names.length < 1)
	{
	    values = new Value[0];
	    return;
	}
	final LinkedList<Value> res = new LinkedList<Value>();
	for(String s: names)
	{
	    final Value v = new Value();
	    v.type = registry.getTypeOf(s);
	    v.name = s;
	    v.parentDir = dirPath;
	    final String valuePath = Registry.join(dirPath, s);
	    switch(v.type)
	    {
	    case Registry.INTEGER:
		v.intValue = registry.getInteger(valuePath);
		break;
	    case Registry.STRING:
		v.strValue = registry.getString(valuePath);
		break;
	    case Registry.BOOLEAN:
		v.boolValue = registry.getBoolean(valuePath);
		break;
	    default:
		continue;
	    }
	    res.add(v);
	}
	values = res.toArray(new Value[res.size()]);
	Arrays.sort(values);
    }
}
