/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import org.luwrain.core.*;
import org.luwrain.util.*;

class UniRefList
{
    private Luwrain luwrain;
    private Registry registry;
    private final RegistryKeys registryKeys = new RegistryKeys();
    private UniRefInfo[] uniRefs = new UniRefInfo[0];

    UniRefList(Luwrain luwrain)
    {
	this.luwrain = luwrain;
	NullCheck.notNull(luwrain, "luwrain");
	this.registry = luwrain.getRegistry();
    }

    UniRefInfo[] get()
    {
	return uniRefs;
    }

    void load()
    {
	final String[] values = registry.getValues(registryKeys.desktopUniRefs());
	final LinkedList<UniRefInfo> res = new LinkedList<UniRefInfo>();
	for(String v: values)
	{
	    final String path = Registry.join(Settings.DESKTOP_UNIREFS_PATH, v);
	    if (registry.getTypeOf(path) != Registry.STRING)
		continue;
	    final String s = registry.getString(path);
	    if (s.isEmpty())
		continue;
	    final UniRefInfo uniRef = luwrain.getUniRefInfo(s);
	    if (uniRef != null && !res.contains(uniRef))
		res	   .add(uniRef);
	}
	uniRefs = res.toArray(new UniRefInfo[res.size()]);
    }

    void add(int pos, String[] values)
    {
	if (values == null)
	    return;
	final LinkedList<UniRefInfo> toAdd = new LinkedList<UniRefInfo>();
	for(String v: values)
	{
	    if (v == null)
		continue;
	    final UniRefInfo uniRef = luwrain.getUniRefInfo(v);
	    if (uniRef != null)
		toAdd.add(uniRef);
	}
	if (toAdd.isEmpty())
	    return;
	final UniRefInfo[] newItems = toAdd.toArray(new UniRefInfo[toAdd.size()]);
	final int newPos = (pos >= 0 && pos <= uniRefs.length)?pos:0;
	final LinkedList<UniRefInfo> res = new LinkedList<UniRefInfo>();
	for(int i = 0;i < newPos;++i)
	    res.add(uniRefs[i]);
	for(UniRefInfo u: newItems)
	    res.add(u);
	for(int i = newPos;i < uniRefs.length;++i)
	    res.add(uniRefs[i]);
	uniRefs = res.toArray(new UniRefInfo[res.size()]);
    }

    boolean delete(int pos)
    {
	if (uniRefs == null || uniRefs.length < 1 ||
	    pos < 0 || pos >= uniRefs.length)
	    return false;
	    final UniRefInfo[] n = new UniRefInfo[uniRefs.length - 1];
	for(int i = 0;i < pos;++i)
	    n[i] = uniRefs[i];
	for(int i = pos + 1;i < uniRefs.length;++i)
	    n[i - 1] = uniRefs[i];
	uniRefs = n;
	return true;
    }

    void save()
    {
	final String[] values = registry.getValues(registryKeys.desktopUniRefs());
	if (values != null)
	    for(String v: values)
		registry.deleteValue(Registry.join(registryKeys.desktopUniRefs(), v));
	for(int i = 0;i < uniRefs.length;++i)
	{
	    String name = "" + (i + 1);
	    while (name.length() < 6)
		name = "0" + name;
	    registry.setString(Registry.join(registryKeys.desktopUniRefs(), name), uniRefs[i].value());
	}
    }
}
