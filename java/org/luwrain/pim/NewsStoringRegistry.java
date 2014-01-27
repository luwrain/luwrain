/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

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

public abstract class NewsStoringRegistry implements NewsStoring
{
    private Registry registry;//FIXME:;

    public StoredNewsGroup[] loadNewsGroups() throws Exception
    {
	//FIXME:Should be completely rewritten;
	String[] names = splitNames("cnews:lenta-ru:forbs:rt:reuters:shanghai-daily:scmp:gulf-news:addthingsd");
	if (names == null || names.length < 1)
	    return new StoredNewsGroupRegistry[0];
	StoredNewsGroupRegistry[] groups = new StoredNewsGroupRegistry[names.length];
	for(int i = 0;i < names.length;i++)
	{
	    StoredNewsGroupRegistry g = new StoredNewsGroupRegistry();
	    groups[i] = g;
	    g.id = i;
	    if (registry.getTypeOf("/org/luwrain/news/groups/" + names[i] + "/title") == Registry.STRING)
		g.name = registry.getString("/org/luwrain/news/groups/" + names[i] + "/title");
	    ArrayList<String> urls = new ArrayList<String>();
	    int k = 1;
	    while(registry.getTypeOf("/org/luwrain/news/groups/" + names[i] + "/url" + k) == Registry.STRING)
	    {
		urls.add(registry.getString("/org/luwrain/news/groups/" + names[i] + "/url" + k));
		k++;
	    }
g.urls = urls.toArray(new String[urls.size()]);
	}
	return groups;
    }

    private static String[] splitNames(String names)
    {
	ArrayList<String> a = new ArrayList<String>();
	String s = "";
	for(int i = 0;i < names.length();i++)
	{
	    if (names.charAt(i) == ':')
	    {
		s = s.trim();
		if (!s.isEmpty())
		    a.add(s);
		s = "";;
	    } else
		s += names.charAt(i);
	}
	s = s.trim();
	if (!s.isEmpty())
	    a.add(s);
	return a.toArray(new String[a.size()]);
    }
}
