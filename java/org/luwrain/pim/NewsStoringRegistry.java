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

package org.luwrain.pim;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.registry.Registry;

abstract class NewsStoringRegistry implements NewsStoring
{
    public final static String GROUPS_PATH = "/org/luwrain/pim/news/groups/";

    private Registry registry;

    public NewsStoringRegistry(Registry registry)
    {
	this.registry = registry;
    }

    public StoredNewsGroup[] loadNewsGroups() throws Exception
    {
	String[] groupsNames = registry.getDirectories(GROUPS_PATH);
	if (groupsNames == null || groupsNames.length == 0)
	    return new StoredNewsGroup[0];
	ArrayList<StoredNewsGroup> groups = new ArrayList<StoredNewsGroup>();
	for(String s: groupsNames)
	{
	    StoredNewsGroupRegistry g = readNewsGroup(s);
	    if (g != null)
		groups.add(g);
	}
	Log.debug("pim", "found " + groups.size() + " news groups");
	return groups.toArray(new StoredNewsGroup[groups.size()]);
    }

    private StoredNewsGroupRegistry readNewsGroup(String name)
    {
	if (name == null || name.isEmpty())
	    return null;
	StoredNewsGroupRegistry g = new StoredNewsGroupRegistry(registry);
	try {
	    g.id = Integer.parseInt(name.trim());
	}
	catch(NumberFormatException e)
	{
	    Log.warning("pim", "registry directory \'" + GROUPS_PATH + "\' contains illegal subdirectory \'" + name + "\'");
	    return null;
	}
	final String path = GROUPS_PATH + name;
	if (registry.getTypeOf(path + "/name") != Registry.STRING)
	{
	    Log.warning("pim", "registry directory \'" + path + "\' has no proper value \'title\'");
	    return null;
	}
	g.name = registry.getString(path + "/name");
	if (registry.getTypeOf(path + "/expire-days") == Registry.INTEGER)
	    g.expireAfterDays = registry.getInteger(path + "expire-days");
	if (registry.getTypeOf(path + "/order-index") == Registry.INTEGER)
	    g.orderIndex = registry.getInteger(path + "order-index");
	if (registry.getTypeOf(path + "/media-content-type") == Registry.STRING)
	    g.mediaContentType = registry.getString(path + "media-content-type");
	String[] values = registry.getValues(path);
	if (values == null)
	    return null;
	ArrayList<String> urls = new ArrayList<String>();
	for(String s: values)
	{
	    if (s == null || s.indexOf("url") < 0)
		continue;
	    if (registry.getTypeOf(path + "/" + s) != Registry.STRING)
	    {
		Log.warning("pim", "registry directory \'" + path + "\' has incorrect value \'" + s + "\'");
		return null;
	    }
	    urls.add(registry.getString(path + "/" + s));
	}
	g.urls = urls.toArray(new String[urls.size()]);
	return g;
    }
}
