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

package org.luwrain.core;

import java.util.*;

import org.luwrain.core.events.*;
import org.luwrain.core.extensions.*;

class UniRefProcManager
{
    static private class Entry 
    {
	Luwrain luwrain;
	String uniRefType;
	UniRefProc uniRefProc;

	Entry(Luwrain luwrain, String uniRefType,
	      UniRefProc uniRefProc)
	{
	    this.luwrain = luwrain;
	    this.uniRefType = uniRefType;
	    this.uniRefProc = uniRefProc;
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(uniRefType, "uniRefType");
	    NullCheck.notNull(uniRefProc, "uniRefProc");
	    if (uniRefType.trim().isEmpty())
		throw new IllegalArgumentException("uniRefType may not be empty");
	}
    }

    private final TreeMap<String, Entry> uniRefProcs = new TreeMap<String, Entry>();

    boolean add(Luwrain luwrain, UniRefProc uniRefProc)
    {
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (uniRefProc == null)
	    throw new NullPointerException("uniRefProc may not be null");
	final String uniRefType = uniRefProc.getUniRefType();
	if (uniRefType == null || uniRefType.trim().isEmpty())
	    return false;
	if (uniRefProcs.containsKey(uniRefType))
	    return false;
	uniRefProcs.put(uniRefType, new Entry(luwrain, uniRefType, uniRefProc));
	return true;
    }

    UniRefInfo getInfo(String uniRef)
    {
	NullCheck.notNull(uniRef, "uniRef");
	if (uniRef.trim().isEmpty())
	    throw new IllegalArgumentException("uniRef may not be empty");
	final String uniRefType = getUniRefType(uniRef);
	if (uniRefType == null || uniRefType.trim().isEmpty() ||
!uniRefProcs.containsKey(uniRefType))
	    return new UniRefInfo(uniRef);
	final Entry entry = uniRefProcs.get(uniRefType);
	return entry.uniRefProc.getUniRefInfo(uniRef);
    }

    boolean open(String uniRef)
    {
	if (uniRef == null)
	    throw new NullPointerException("uniRef may not be null");
	if (uniRef.trim().isEmpty())
	    throw new IllegalArgumentException("uniRef may not be empty");
	final String uniRefType = getUniRefType(uniRef);
	if (uniRefType == null || uniRefType.trim().isEmpty())
	    return false;
	if (!uniRefProcs.containsKey(uniRefType))
	    return false;
	final Entry entry = uniRefProcs.get(uniRefType);
	entry.uniRefProc.openUniRef(uniRef, entry.luwrain);
	return true;
    }

    private String getUniRefType(String uniRef)
    {
	final int pos = uniRef.indexOf(':');
	if (pos < 1)
	    return null;
	return uniRef.substring(0, pos);
    }
}
