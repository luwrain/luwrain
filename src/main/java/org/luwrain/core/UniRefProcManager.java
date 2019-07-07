/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core;

import java.util.*;

import org.luwrain.core.events.*;
import org.luwrain.core.extensions.*;

class UniRefProcManager
{
    private final Map<String, Entry> uniRefProcs = new HashMap<String, Entry>();

    boolean add(Luwrain luwrain, UniRefProc uniRefProc)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(uniRefProc, "uniRefProc");
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
	NullCheck.notEmpty(uniRef, "uniRef");
	final String uniRefType = getUniRefType(uniRef);
	if (uniRefType == null || uniRefType.trim().isEmpty() ||
	    !uniRefProcs.containsKey(uniRefType))
	    return new UniRefInfo(uniRef);
	final Entry entry = uniRefProcs.get(uniRefType);
final UniRefInfo res = entry.uniRefProc.getUniRefInfo(uniRef);
if (res == null)
    return new UniRefInfo(uniRef);
return res;
    }

    boolean open(String uniRef)
    {
	NullCheck.notEmpty(uniRef, "uniRef");
	final String uniRefType = getUniRefType(uniRef);
	if (uniRefType == null || uniRefType.trim().isEmpty())
	    return false;
	if (!uniRefProcs.containsKey(uniRefType))
	    return false;
	final Entry entry = uniRefProcs.get(uniRefType);
	return entry.uniRefProc.openUniRef(uniRef, entry.luwrain);
    }

    private String getUniRefType(String uniRef)
    {
	final int pos = uniRef.indexOf(':');
	if (pos < 1)
	    return null;
	return uniRef.substring(0, pos);
    }

    static private class Entry 
    {
	final Luwrain luwrain;
	final String uniRefType;
	final UniRefProc uniRefProc;

	Entry(Luwrain luwrain, 
	      String uniRefType, UniRefProc uniRefProc)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(uniRefType, "uniRefType");
	    NullCheck.notNull(uniRefProc, "uniRefProc");
	    if (uniRefType.trim().isEmpty())
		throw new IllegalArgumentException("uniRefType may not be empty");
	    this.luwrain = luwrain;
	    this.uniRefType = uniRefType;
	    this.uniRefProc = uniRefProc;
	}
    }
}
